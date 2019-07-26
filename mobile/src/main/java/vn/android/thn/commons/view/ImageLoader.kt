package vn.android.thn.commons.view

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.Target
import com.google.android.material.shape.RoundedCornerTreatment
import vn.android.thn.commons.App
import vn.android.thn.gbkids.R
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
                GBLog.info("ImageLoader:",url!!, App.getInstance().isDebugMode())
                Glide.with(imageView.context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.ico_no_image_row)
                    .placeholder(R.drawable.placeholder)
                    .listener(object : RequestListener<String, GlideDrawable> {
                        override fun onException(
                            e: Exception?,
                            model: String?,
                            target: Target<GlideDrawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            GBLog.info("ImageLoader_error:","error image:"+videoId, App.getInstance().isDebugMode())
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
                GBLog.info("ImageLoader:",url!!, App.getInstance().isDebugMode())
                Glide.with(imageView.context)
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.ico_no_image_row)
                    .crossFade()
                        .listener(object :RequestListener<String, GlideDrawable>{
                            override fun onException(
                                    e: Exception?,
                                    model: String?,
                                    target: Target<GlideDrawable>?,
                                    isFirstResource: Boolean
                            ): Boolean {
                                GBLog.info("ImageLoader_error:","error image:"+videoId, App.getInstance().isDebugMode())
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
        fun loadImageCricle(imageView: ImageView, url:String?=null){
            if (GBUtils.isEmpty(url)){
                imageView.setImageResource(R.drawable.ico_no_image_row)
            } else {
//                Glide.with(imageView.context).load(url).asBitmap().centerCrop().into(object : BitmapImageViewTarget(imageView) {
//                    override fun setResource(resource: Bitmap) {
//                        val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource)
//                        circularBitmapDrawable.isCircular = true
//                        imageView.setImageDrawable(circularBitmapDrawable)
//                    }
//                })

                Glide.with(imageView.context)
                        .load(url).asBitmap().centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.ico_no_image_row)
                        .placeholder(R.drawable.placeholder)
                        .into(object : BitmapImageViewTarget(imageView){
                            override fun setResource(resource: Bitmap?) {
                                val circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(imageView.context.getResources(), resource)
                                circularBitmapDrawable.setCircular(true);
                                imageView.setImageDrawable(circularBitmapDrawable)
                            }
                        })
            }
        }
    }
}
