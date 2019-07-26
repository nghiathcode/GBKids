package vn.android.thn.gbkids.views.fragment


import android.view.View
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.views.activity.MainActivity

class MyFragment:BaseFragment() {
    override fun titleView(): String {
        return ""
    }

    override fun initView() {
        if (activity is MainActivity){
            (activity as MainActivity).updateTab(4)
        }
        findViewById<View>(R.id.btn_follow)!!.setOnClickListener {
            val listFollow = FollowFragment()
            viewManager.pushViewChild(childFragmentManager,listFollow,null,null,R.id.content_view)
        }
        findViewById<View>(R.id.btn_watched)!!.setOnClickListener {
            val listWatched = WatchedFragment()
            viewManager.pushViewChild(childFragmentManager,listWatched,null,null,R.id.content_view)
        }
        //btn_like
        findViewById<View>(R.id.btn_like)!!.setOnClickListener {
            val listLike = LikeFragment()
            viewManager.pushViewChild(childFragmentManager,listLike,null,null,R.id.content_view)
        }
        //btn_ignore
        findViewById<View>(R.id.btn_ignore)!!.setOnClickListener {
            val listIgnore = IgnoreFragment()
            viewManager.pushViewChild(childFragmentManager,listIgnore,null,null,R.id.content_view)
        }

        hideNoData()
    }

    override fun loadData() {
    }

    override fun firstInit() {
    }

    override fun layoutFileResourceContent(): Int {
        return R.layout.fragment_my
    }

    override fun fragmentName(): String {
        return "MyFragment"
    }
}