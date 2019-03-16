package vn.android.thn.gbkids.model.db

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table
import com.google.gson.Gson
import vn.android.thn.gbkids.model.entity.ThumbnailEntity
import vn.android.thn.library.utils.GBUtils


//
// Created by NghiaTH on 3/12/19.
// Copyright (c) 2019
@Table(name = "videos")
class VideoTable : Model() {

    @Column(name = "videoID", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    var videoID: String =""
    @Column(name = "title")
    lateinit var title: String
    @Column(name = "description")
    lateinit var description: String
    @Column(name = "channelID")
    lateinit var channelID: String
    @Column(name = "thumbnails")
    var thumbnails: String = ""
    @Column(name = "channelTitle")
    lateinit var channelTitle: String
    @Column(name = "publishedAt")
    lateinit var publishedAt: String
    @Column(name = "tags")
    var tags: String = ""
    @Column(name = "statistics")
    var statistics = ""
    @Column(name = "isDelete")
    var isDelete: Int = 0
    @Column(name = "dateUpdate")
    var dateUpdate: String = ""

    fun toImage(): ThumbnailEntity? {
        if (!GBUtils.isEmpty(thumbnails)) {
            val thumbnails = Gson().fromJson<ThumbnailEntity>(thumbnails, ThumbnailEntity::class.java)
            return thumbnails
        }
        return null
    }
}
