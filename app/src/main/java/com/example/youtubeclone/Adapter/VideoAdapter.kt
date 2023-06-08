package com.example.youtubeclone.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.youtubeclone.MainActivity
import com.example.youtubeclone.Model.VideoModel
import com.example.youtubeclone.R
import com.example.youtubeclone.databinding.ItemVideoBinding

class VideoAdapter(val callback:(String,String) ->Unit):ListAdapter<VideoModel,VideoAdapter.ViewHolder>(diffUtil){
    inner class ViewHolder(val binding:ItemVideoBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(videoModel:VideoModel){
            binding.titleTextView.text = videoModel.title
            binding.subTitleTextView.text = videoModel.subtitle
            Glide.with(binding.thumbnail.context).load(videoModel.thumb).into(binding.thumbnail)

            itemView.setOnClickListener {
                callback(videoModel.sources,videoModel.title)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(ItemVideoBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(currentList[position])
    }

    companion object{
        private val diffUtil = object :DiffUtil.ItemCallback<VideoModel>(){
            override fun areItemsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
            return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
            return oldItem == newItem
            }
        }
    }
}
