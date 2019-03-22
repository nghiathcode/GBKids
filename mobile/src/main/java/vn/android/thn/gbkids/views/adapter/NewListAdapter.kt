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
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.constants.Constants
import vn.android.thn.gbkids.model.db.VideoTable
import vn.android.thn.gbkids.views.view.ImageLoader

import com.google.android.gms.ads.AdView


//
// Created by NghiaTH on 2/27/19.
// Copyright (c) 2019

class NewListAdapter (private val mContext: Context, var list: MutableList<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TYPE_DATA = 0
    private val TYPE_LOAD_MORE = 1
    private val BANNER_AD_VIEW_TYPE = 2
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
                if (position  == 0){
                    return BANNER_AD_VIEW_TYPE
                } else {
                    return TYPE_DATA
                }

            }
        } else {
            if (position  == 0){
                return BANNER_AD_VIEW_TYPE
            } else {
                return TYPE_DATA
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_DATA) {
            val itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.row_new_list, parent, false)
            return ViewHolder(itemView)
        } else if (viewType == BANNER_AD_VIEW_TYPE){
            var bannerLayoutView = LayoutInflater.from(mContext)
                .inflate(R.layout.row_ad_new_list, parent, false)
            return AdViewHolder(bannerLayoutView)
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
            (holder as NewListAdapter.ViewHolder).bindData(list.get(position) as VideoTable)
        } else if (getItemViewType(position) == BANNER_AD_VIEW_TYPE){
            var bannerHolder = holder as AdViewHolder
            var adView = list.get(position) as AdView
            var adCardView = bannerHolder.itemView as ViewGroup
            if (adCardView.childCount > 0) {
                adCardView.removeAllViews()
            }
            if (adView.parent != null) {
                (adView.parent as ViewGroup).removeView(adView)
            }

            // Add the banner ad to the ad view.
            adCardView.addView(adView)
        }else {
            (holder as NewListAdapter.ViewHolderLoadMore).onLoadMore()
        }
    }
    internal inner class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }
    internal inner class ViewHolderLoadMore(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun onLoadMore(){
            if (loadMoreListener!= null){
                isLoadMore = false
                loadMoreListener!!.onLoadMore()
                loadMoreListener = null
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
//            val snippet = obj.snippet
            txt_name.text = obj.title
//            var thumbnails =obj.toImage()
//            if (thumbnails!= null) {
//                if (thumbnails.maxres != null) {
//                    ImageLoader.loadImage(img_thumbnail, thumbnails!!.maxres!!.url)
//                } else if (thumbnails.high != null) {
//                    ImageLoader.loadImage(img_thumbnail, thumbnails!!.high!!.url)
//                } else if (thumbnails.medium != null) {
//                    ImageLoader.loadImage(img_thumbnail, thumbnails!!.medium!!.url)
//                } else if (thumbnails.standard != null) {
//                    ImageLoader.loadImage(img_thumbnail, thumbnails!!.standard!!.url)
//                } else if (thumbnails.default != null) {
//                    ImageLoader.loadImage(img_thumbnail, thumbnails!!.default!!.url)
//                }
//            }
            ImageLoader.loadImage(img_thumbnail, Constants.DOMAIN+"/thumbnail_high/"+obj.videoID)
            txt_info.text = obj.channelTitle
        }

    }
}
