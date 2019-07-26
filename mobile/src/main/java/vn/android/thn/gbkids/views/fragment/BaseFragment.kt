package vn.android.thn.gbkids.views.fragment

import android.view.View
import androidx.fragment.app.FragmentTransaction
import vn.android.thn.commons.App
import vn.android.thn.gbkids.BuildConfig
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.presenter.MVPBase
import vn.android.thn.gbkids.views.activity.MainActivity
import vn.android.thn.library.views.fragment.GBFragment

abstract class BaseFragment : GBFragment(), MVPBase {
    var app = App.getInstance()
    init {

    }
    override fun isDebugMode(): Boolean {

        if (BuildConfig.DEBUG) {
            return true
        }
        return false
    }

    override fun setAnimationCustom(animationCustom: FragmentTransaction) {
        animationCustom.setCustomAnimations(
            R.anim.fragment_slide_right_enter,
            R.anim.fragment_slide_left_exit,
            R.anim.fragment_slide_left_enter,
            R.anim.fragment_slide_right_exit
        )

    }

    override fun initViewCommon() {
        if (activity!= null){
            if (activity is MainActivity){
                (activity as MainActivity).updateTitle(titleView())
            }
        }
    }

    override fun layoutFileResourceCommon(): Int {
        return R.layout.fragment_base
    }

    override fun viewCommonID(): Int {
        return R.id.content_view
    }

    override fun contentId(): Int {
        return 0
    }

    override fun apiError() {

    }

    override fun onNetworkFail() {

    }
    fun updateNoDataText(message:String){
        if (activity!= null){
            if (activity is MainActivity){
                (activity as MainActivity).updateNoDataText(message)
            }
        }
    }
    fun hideNoData(){
        if (activity!= null){
            if (activity is MainActivity){
                (activity as MainActivity).view_no_data.visibility = View.GONE
            }
        }
    }
    abstract fun titleView():String
    fun onSearch(keyWord:String){
        val result = SearchResultFragment()
        val fragment = viewManager.getViewCurrent()
        if(fragment!= null){
            result.keyword = keyWord
            viewManager.pushViewChild(fragment.childFragmentManager,result,null,null,R.id.content_view)
        }

    }
}