package vn.android.thn.gbkids.views.view

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

import vn.android.thn.gbkids.R
import vn.android.thn.library.utils.GBUtils



//
// Created by NghiaTH on 1/16/19.
// Copyright (c) 2019

class ImageLoader {
    companion object {
        fun loadImage(imageView: ImageView, url:String?=null){
//            imageView.background = ContextCompat.getDrawable( imageView.context,R.drawable.spinner)
//            val spinner = imageView.background as AnimationDrawable
//
//            spinner.start()
            if (GBUtils.isEmpty(url)){
//                imageView.background = null
                imageView.setImageResource(R.drawable.ico_no_image_row)
            } else {
                Glide.with(imageView.context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.ico_no_image_row)
//                    .placeholder(R.drawable.spinner)
//                    .listener(object :RequestListener<String, GlideDrawable>{
//                        override fun onException(
//                            e: Exception?,
//                            model: String?,
//                            target: Target<GlideDrawable>?,
//                            isFirstResource: Boolean
//                        ): Boolean {
//                            imageView.background = null
//                            return false
//                        }
//
//                        override fun onResourceReady(
//                            resource: GlideDrawable?,
//                            model: String?,
//                            target: Target<GlideDrawable>?,
//                            isFromMemoryCache: Boolean,
//                            isFirstResource: Boolean
//                        ): Boolean {
//                            imageView.background = null
//                            return false
//                        }
//
//                    })
                    .into(imageView)
            }
        }
        fun loadImageThumb(imageView: ImageView, url:String?=null){

//            imageView.background = (ContextCompat.getDrawable( imageView.context,R.drawable.spinner))
//            val spinner = imageView.background as AnimationDrawable
//            spinner.start()
            if (GBUtils.isEmpty(url)){
//                imageView.background = null
                imageView.setImageResource(R.drawable.ico_no_image_row)
            } else {
                Glide.with(imageView.context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.ico_no_image_row)
//                    .placeholder(R.drawable.spinner)
//                    .listener(object :RequestListener<String, GlideDrawable>{
//                        override fun onException(
//                            e: Exception?,
//                            model: String?,
//                            target: Target<GlideDrawable>?,
//                            isFirstResource: Boolean
//                        ): Boolean {
//                            imageView.background = null
//                            return false
//                        }
//
//                        override fun onResourceReady(
//                            resource: GlideDrawable?,
//                            model: String?,
//                            target: Target<GlideDrawable>?,
//                            isFromMemoryCache: Boolean,
//                            isFirstResource: Boolean
//                        ): Boolean {
//                            imageView.background = null
//                            return false
//                        }
//
//                    })

                    .into(imageView)
            }
        }
    }
}
