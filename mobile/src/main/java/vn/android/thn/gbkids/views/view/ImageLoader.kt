package vn.android.thn.gbkids.views.view

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.utils.LogUtils
import vn.android.thn.library.utils.GBLog
import vn.android.thn.library.utils.GBUtils
import java.lang.Exception


//
// Created by NghiaTH on 1/16/19.
// Copyright (c) 2019

class ImageLoader {
    companion object {
        fun loadImage(imageView: ImageView, url:String?=null,videoId:String){
            if (GBUtils.isEmpty(url)){
                imageView.setImageResource(R.drawable.ico_no_image_row)
            } else {
                LogUtils.info("ImageLoader:",url!!)
                Glide.with(imageView.context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.ico_no_image_row)
                    .placeholder(R.drawable.placeholder)
                    .listener(object :RequestListener<String, GlideDrawable>{
                        override fun onException(
                            e: Exception?,
                            model: String?,
                            target: Target<GlideDrawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            LogUtils.info("ImageLoader_error:","error image:"+videoId)
                            return false
                        }

                        override fun onResourceReady(
                            resource: GlideDrawable?,
                            model: String?,
                            target: Target<GlideDrawable>?,
                            isFromMemoryCache: Boolean,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                    })
                    .into(imageView)
            }
        }
        fun loadImagePlay(imageView: ImageView, url:String?=null,videoId:String){
            if (GBUtils.isEmpty(url)){
                imageView.setImageResource(R.drawable.ico_no_image_row)
            } else {
                LogUtils.info("ImageLoader:",url!!)
                Glide.with(imageView.context)
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.ico_no_image_row)
                        .placeholder(R.drawable.placeholder)
                        .listener(object :RequestListener<String, GlideDrawable>{
                            override fun onException(
                                    e: Exception?,
                                    model: String?,
                                    target: Target<GlideDrawable>?,
                                    isFirstResource: Boolean
                            ): Boolean {
                                LogUtils.info("ImageLoader_error:","error image:"+videoId)
                                return false
                            }

                            override fun onResourceReady(
                                    resource: GlideDrawable?,
                                    model: String?,
                                    target: Target<GlideDrawable>?,
                                    isFromMemoryCache: Boolean,
                                    isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }

                        })
                        .into(imageView)
            }
        }
        fun loadImageThumb(imageView: ImageView, url:String?=null,videoId:String){
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
