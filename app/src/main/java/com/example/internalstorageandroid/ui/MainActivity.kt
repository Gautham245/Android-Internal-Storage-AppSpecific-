package com.example.internalstorageandroid.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.internalstorageandroid.adapters.ImageAdapter
import com.example.internalstorageandroid.databinding.ActivityMainBinding
import com.example.internalstorageandroid.model.InternalSotorageImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import androidx.activity.result.launch
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity(),ImageOnClick {

    lateinit var activityMainBinding:ActivityMainBinding
    lateinit var imageAdapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        imageAdapter=ImageAdapter(this)

        val takePicture= registerForActivityResult(ActivityResultContracts.TakePicturePreview()){
            bitmap->
            val isSAvedSucces= saveImageToInternalStorage(bitmap,UUID.randomUUID().toString())
            if (isSAvedSucces){
                Toast.makeText(this,"DONE",Toast.LENGTH_SHORT).show()
                loadImagesIntoRecycyclerView()
            }
            else
                Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()

        }

        activityMainBinding.addImageBtn.setOnClickListener {
            takePicture.launch()
        }

        setUpRecyclerView()
        loadImagesIntoRecycyclerView()
    }

    fun saveImageToInternalStorage(bitmap: Bitmap, fileName: String): Boolean{
        return try {
            openFileOutput("$fileName.jpg", MODE_PRIVATE).use { stream->
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG,90,stream))
                    throw IOException("Can't save photo")
            }
            true
        }catch (e: IOException){
            e.printStackTrace()
            false
        }

    }

    suspend fun loadImagesFromInternalStorage():List<InternalSotorageImage>{
        return withContext(Dispatchers.IO){
            val files=filesDir.listFiles()
            files?.filter {
                it.canRead() && it.isFile && it.name.endsWith(".jpg")
            }?.map {
                val bytes = it.readBytes()
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                InternalSotorageImage(it.name, bmp)
            }?: listOf()
        }
    }

    fun setUpRecyclerView()= activityMainBinding.rvImages.apply {
        adapter=imageAdapter
        layoutManager=StaggeredGridLayoutManager(3,RecyclerView.VERTICAL)
    }

    fun loadImagesIntoRecycyclerView(){
        lifecycleScope.launch {
            val imagesList=loadImagesFromInternalStorage()
            imageAdapter.submitList(imagesList)
        }
    }

    fun deletefromInternalStorage(fileName: String):Boolean{
        return try {
            deleteFile(fileName)
        }catch (e:IOException){
            e.printStackTrace()
            false
        }
    }

    override fun getImagePosition(position: Int) {
        MaterialAlertDialogBuilder(this )
            .setTitle("Alert Dialog")
            .setMessage("Do want to delete")
            .setNegativeButton("Delete"){dialog,where->
                lifecycleScope.launch {
                    deletefromInternalStorage(loadImagesFromInternalStorage().get(position).name)
                    loadImagesIntoRecycyclerView()
                    dialog.dismiss()
                }
            }
            .setNeutralButton("Cancel"){dialog,where->
                dialog.dismiss()
            }
            .show()
    }

}