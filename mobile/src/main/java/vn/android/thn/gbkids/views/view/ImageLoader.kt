package vn.android.thn.gbkids.views.view

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.utils.LogUtils
import vn.android.thn.library.utils.GBLog
import vn.android.thn.library.utils.GBUtils



//
// Created by NghiaTH on 1/16/19.
// Copyright (c) 2019

class ImageLoader {
    companion object {
        fun loadImage(imageView: ImageView, url:String?=null){
            if (GBUtils.isEmpty(url)){
                imageView.setImageResource(R.drawable.ico_no_image_row)
            } else {
                LogUtils.info("ImageLoader:",url!!)
                Glide.with(imageView.context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.ico_no_image_row)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView)
            }
        }
        fun loadImageThumb(imageView: ImageView, url:String?=null){

            if (GBUtils.isEmpty(url)){
                imageView.setImageResource(R.drawable.ico_no_image_row)
            } else {
                Glide.with(imageView.context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.ico_no_image_row)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView)
            }
        }
    }
}
