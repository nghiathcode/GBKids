package vn.android.thn.gbkids.views.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import vn.android.thn.gbfilm.views.listener.ListItemListener
import vn.android.thn.gbfilm.views.listener.LoadMoreListener
import vn.android.thn.gbkids.App
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.constants.Constants
import vn.android.thn.gbkids.model.db.VideoTable
import vn.android.thn.gbkids.views.view.ImageLoader
import vn.android.thn.library.utils.GBUtils


//
// Created by NghiaTH on 2/27/19.
// Copyright (c) 2019

class SearchListAdapter (private val mContext: Context, var list: MutableList<VideoTable>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TYPE_DATA = 0
    private val TYPE_LOAD_MORE = 1
    var listener: ListItemListener? = null
    var isLoadMore = false
    var loadMoreListener: LoadMoreListener? = null
    fun loadMore(isLoadMore:Boolean,loadMoreListener:LoadMoreListener){
        this.isLoadMore = isLoadMore
        if (!isLoadMore){
            this.loadMoreListener = null
        } else{
            this.loadMoreListener = loadMoreListener
        }
    }
    override fun getItemViewType(position: Int): Int {
        if (isLoadMore){
            if (position == list.size){
                return TYPE_LOAD_MORE
            } else {
                return TYPE_DATA
            }
        } else {
            return TYPE_DATA
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_DATA) {
            val itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.row_search_list, parent, false)
            return ViewHolder(itemView)
        } else {
            val itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.row_load_more, parent, false)
            return ViewHolderLoadMore(itemView)
        }
    }

    override fun getItemCount(): Int {
        if (isLoadMore){
            return list.size +1
        } else {
            return list.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_DATA) {
            (holder as SearchListAdapter.ViewHolder).bindData(list.get(position))
        } else {
            (holder as SearchListAdapter.ViewHolderLoadMore).onLoadMore()
        }
    }
    internal inner class ViewHolderLoadMore(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun onLoadMore(){
            if (loadMoreListener!= null){
                loadMoreListener!!.onLoadMore()
            }
        }
    }

    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var img_thumbnail:ImageView
        var txt_name:TextView
        var txt_info:TextView
        init {
            img_thumbnail = itemView.findViewById(R.id.img_thumbnail)
            txt_name = itemView.findViewById(R.id.txt_name)
            txt_info = itemView.findViewById(R.id.txt_info)
            itemView.setOnClickListener {
                if (listener!= null){
                    listener!!.onItemClick(list.get(layoutPosition),layoutPosition)
                }
            }
        }
        fun bindData(obj:VideoTable){
            txt_name.text = obj.title
            if (App.getInstance().appStatus == 1){
                if (GBUtils.isEmpty(obj.urlImage)){
                    var thumbnails =obj.toImage()
//                    if (thumbnails!= null) {
//                        if (thumbnails.default != null) {
//                            obj.urlImage = thumbnails!!.default!!.url
//                        } else if (thumbnails.medium != null) {
//                            obj.urlImage = thumbnails!!.medium!!.url
//                        } else if (thumbnails.standard != null) {
//                            obj.urlImage = thumbnails!!.standard!!.url
//                        }else if (thumbnails.high != null) {
//                            obj.urlImage = thumbnails!!.high!!.url
//                        } else{
//                            obj.urlImage = thumbnails!!.maxres!!.url
//                        }
//                    }
                    if (thumbnails!= null) {
                        if (thumbnails.maxres != null) {
                            obj.urlImage = thumbnails!!.maxres!!.url
                        } else if (thumbnails.high != null) {
                            obj.urlImage = thumbnails!!.high!!.url
                        } else if (thumbnails.medium != null) {
                            obj.urlImage =  thumbnails!!.medium!!.url
                        } else if (thumbnails.standard != null) {
                            obj.urlImage = thumbnails!!.standard!!.url
                        } else if (thumbnails.default != null) {
                            obj.urlImage = thumbnails!!.default!!.url
                        }
                    }
                }
                ImageLoader.loadImage(img_thumbnail, obj.urlImage,obj.videoID)
            } else {
                ImageLoader.loadImage(img_thumbnail, Constants.DOMAIN + "/thumbnail_high/" + obj.videoID,obj.videoID)
            }

            txt_info.text = obj.channelTitle
        }
    }
}
