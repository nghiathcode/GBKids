package vn.android.thn.gbkids.utils

import android.content.Context
import android.widget.Toast
import vn.android.thn.gbkids.App
import vn.android.thn.library.utils.GBLog
import vn.android.thn.library.utils.GBUtils

object LogUtils {
    fun error(tags: String, msg: String, prefix: String = "GB_") {
        GBLog.error(tags,msg, App.getInstance().isDebugMode(),prefix)
    }

    fun info(tags: String, msg: String, isDebug: Boolean = false, prefix: String = "GB_") {
        GBLog.info(tags,msg, App.getInstance().isDebugMode(),prefix)
    }
    fun debug(tags: String, msg: String, isDebug: Boolean = false, prefix: String = "GB_") {
        GBLog.debug(tags,msg, App.getInstance().isDebugMode(),prefix)
    }

    fun showToast(context: Context, sms: String, isDebug: Boolean = false, prefix: String = "GB_") {
        GBLog.showToast(context,sms, App.getInstance().isDebugMode(),prefix)
    }
}
