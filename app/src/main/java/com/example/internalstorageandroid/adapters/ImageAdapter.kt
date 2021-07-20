package com.example.internalstorageandroid.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.internalstorageandroid.databinding.ImageCardBinding
import com.example.internalstorageandroid.model.InternalSotorageImage
import com.example.internalstorageandroid.ui.ImageOnClick

class ImageAdapter (val imageOnClick: ImageOnClick): ListAdapter<InternalSotorageImage, ImageAdapter.PhotoViewHolder>(ImageCardCallBack()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val binding=ImageCardBinding.inflate(inflater,parent,false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo=getItem(position)
        holder.binding.apply {
            image.setImageBitmap(photo.bitmap)
        }
        holder.binding.image.setOnClickListener {
            imageOnClick.getImagePosition(position)
        }

    }

    inner class PhotoViewHolder (val binding:ImageCardBinding): RecyclerView.ViewHolder(binding.root){

    }

    class ImageCardCallBack: DiffUtil.ItemCallback<InternalSotorageImage>() {
        override fun areItemsTheSame(oldItem: InternalSotorageImage, newItem: InternalSotorageImage): Boolean {
            return oldItem.name==newItem.name
        }

        override fun areContentsTheSame(oldItem: InternalSotorageImage, newItem: InternalSotorageImage): Boolean {
            return oldItem==newItem
        }
    }


}