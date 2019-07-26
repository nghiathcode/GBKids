package vn.android.thn.gbkids.views.activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import vn.android.thn.commons.App
import vn.android.thn.gbfilm.views.dialogs.YoutubeDialog
import vn.android.thn.gbkids.BuildConfig
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.constants.RequestCode
import vn.android.thn.library.views.GBViewManager
import vn.android.thn.library.views.activity.GBActivity
import vn.android.thn.library.views.dialogs.GBDialogContentEntity

abstract class ActivityBase : GBActivity() {
    var app = App.getInstance()

    override fun layoutFileCommon(): Int {
        return R.layout.activity_base
    }

    override fun isDebugMode(): Boolean {

        if (BuildConfig.DEBUG) {
            return true
        }
        return false
    }

    override fun contentId(): Int {
        return R.id.fragment_view
    }

    override fun attachBaseContext(newBase: Context?) {
        viewManager = GBViewManager(supportFragmentManager, contentId(), this, isDebugMode())
        super.attachBaseContext(newBase)
    }


    companion object{
        fun getScreenWidth(): Int {
            return Resources.getSystem().getDisplayMetrics().widthPixels
        }

        fun getScreenHeight(): Int {
            return Resources.getSystem().getDisplayMetrics().heightPixels
        }
        fun getStatusBarHeight():Int{
            return (24 * Resources.getSystem().getDisplayMetrics().density).toInt()
        }
    }
}