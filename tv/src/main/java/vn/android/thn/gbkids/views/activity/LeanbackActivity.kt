package vn.android.thn.gbkids.views.activity

import android.support.v4.app.FragmentActivity

abstract class LeanbackActivity: FragmentActivity() {
    override fun onSearchRequested(): Boolean {
        return super.onSearchRequested()
    }
}