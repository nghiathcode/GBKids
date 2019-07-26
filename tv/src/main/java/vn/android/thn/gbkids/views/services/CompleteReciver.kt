package vn.android.thn.gbkids.views.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import jp.co.tss21.monistor.models.GBDataBase
import vn.android.thn.gbkids.model.db.VideoDownLoad
import vn.android.thn.gbkids.utils.LogUtils

class CompleteReciver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val listDownLoad = GBDataBase.getList(VideoDownLoad::class.java,"isComplete=0")
        var pathDirectory = context!!.getExternalFilesDir(null).listFiles()
        for (video in listDownLoad){
            for (directory in pathDirectory){
                if (directory.name.equals(video.videoID,true)){
                    var isFull = 2
                    val listFile = directory.listFiles()
                    if (listFile.size >=2 ){
                        for (file in listFile){
                            if (file.name.equals(video.imageName)){
                                isFull = isFull-1
                                video.thumbnails = file.path
                            }
                            if (file.name.equals(video.videoName)){
                                isFull = isFull-1
                                video.videoPath = file.path
                            }
                            LogUtils.info("download complete",file.path)
                        }
                        if (isFull == 0) {
                            video.isComplete = 1
                            video.save()
                        } else {
                            video.delete()
                        }
                    }


                }

            }
        }
    }
}