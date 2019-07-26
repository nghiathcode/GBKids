package vn.android.thn.gbkids.views.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v17.leanback.app.BackgroundManager
import android.support.v17.leanback.app.BrowseSupportFragment
import android.support.v17.leanback.widget.*
import android.support.v4.content.ContextCompat
import android.transition.Transition
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import vn.android.thn.gbkids.App
import vn.android.thn.gbkids.MovieList
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.model.db.VideoTable
import vn.android.thn.gbkids.presenter.CardPresenter
import vn.android.thn.gbkids.presenter.IconHeaderItemPresenter
import vn.android.thn.gbkids.presenter.MainPresenter
import vn.android.thn.gbkids.views.activity.PlaybackActivity

class GBKidsFragment: BrowseSupportFragment() ,MainPresenter.MainMvp{
    override fun onNew(result: MutableList<VideoTable>, offset: Int) {
        mCategoryRowAdapter.clear()
        val cardPresenter = CardPresenter()
        val list =result
        val listRowAdapter = ArrayObjectAdapter(cardPresenter)
        for (obj in list) {
            listRowAdapter.add(obj)
        }
        val header = HeaderItem("new")
        val row = ListRow(header, listRowAdapter)
        mCategoryRowAdapter.add(row)
//        startEntranceTransition()
    }
    override fun onRegister() {
        presenter.loadNew()
    }

    override fun apiError() {

    }

    override fun onNetworkFail() {

    }
    var app = App.getInstance()
    private val BACKGROUND_UPDATE_DELAY = 300
    private val mHandler = Handler()
    private lateinit var mCategoryRowAdapter: ArrayObjectAdapter
    private var mDefaultBackground: Drawable? = null
    private lateinit var mMetrics: DisplayMetrics
    private var mBackgroundTask: Runnable? = null
    private var mBackgroundURI: Uri? = null
    private lateinit var mBackgroundManager: BackgroundManager
    private lateinit var presenter:MainPresenter
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        prepareBackgroundManager()
        setupUIElements()
        setupEventListeners()
        prepareEntranceTransition()
        mCategoryRowAdapter = ArrayObjectAdapter(ListRowPresenter())
//        val cardPresenter = CardPresenter()
//        val list = MovieList.list
//        val listRowAdapter = ArrayObjectAdapter(cardPresenter)
//        listRowAdapter.add(list[0])
//        val header = HeaderItem("new")
//        val row = ListRow(header, listRowAdapter)
//        mCategoryRowAdapter.add(row)
        adapter = mCategoryRowAdapter
        presenter = MainPresenter(this,activity)
        if (app.appSetting() == null) {
            presenter.register()
        } else{
            presenter.loadNew()
        }
    }
    fun prepareBackgroundManager(){
        mBackgroundManager = BackgroundManager.getInstance(activity)
        mBackgroundManager.attach(activity!!.getWindow())
        mDefaultBackground = resources.getDrawable(R.drawable.default_background, null)
        mBackgroundTask = UpdateBackgroundTask()
        mMetrics = DisplayMetrics()
        activity!!.getWindowManager().defaultDisplay.getMetrics(mMetrics)
    }
    fun setupUIElements(){
        badgeDrawable = activity!!.getResources().getDrawable(R.drawable.app_icon_your_company, null)
        title = getString(R.string.browse_title) // Badge, when set, takes precedent over title
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        // Set fastLane (or headers) background color
        brandColor = ContextCompat.getColor(activity!!, R.color.fastlane_background)

        // Set search icon color.
        searchAffordanceColor = ContextCompat.getColor(activity!!, R.color.search_opaque)

        setHeaderPresenterSelector(object : PresenterSelector() {
            override fun getPresenter(o: Any): Presenter {
                return IconHeaderItemPresenter()
            }
        })
    }
    fun setupEventListeners(){
//        setOnSearchClickedListener {
//            val intent = Intent(activity, SearchActivity::class.java)
//            startActivity(intent)
//        }

        onItemViewClickedListener = ItemViewClickedListener()
        onItemViewSelectedListener = ItemViewSelectedListener()
    }
    private inner class UpdateBackgroundTask : Runnable {

        override fun run() {
            if (mBackgroundURI != null) {
                updateBackground(mBackgroundURI.toString())
            }
        }
    }

    private fun updateBackground(uri: String) {
        val width = mMetrics.widthPixels
        val height = mMetrics.heightPixels

        val options = RequestOptions()
            .centerCrop()
            .error(mDefaultBackground)

        Glide.with(this)
            .asBitmap()
            .load(uri)
            .apply(options)
            .into(object : SimpleTarget<Bitmap>(width, height) {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap>
                ) {
                    mBackgroundManager.setBitmap(resource)
                }
            })
    }

    private fun startBackgroundTimer() {
        mHandler.removeCallbacks(mBackgroundTask)
        mHandler.postDelayed(mBackgroundTask, BACKGROUND_UPDATE_DELAY.toLong())
    }
    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder?, item: Any?,
            rowViewHolder: RowPresenter.ViewHolder?, row: Row?
        ) {

            if (item!= null)
            if (item is VideoTable) {
                val video = item as VideoTable
                val intent = Intent(activity, PlaybackActivity::class.java)
                intent.putExtra("videoId", video.videoID)
                startActivity(intent)
            }
        }
    }

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(
            itemViewHolder: Presenter.ViewHolder?, item: Any?,
            rowViewHolder: RowPresenter.ViewHolder?, row: Row?
        ) {
            if (item!= null)
            if (item is VideoTable) {
                mBackgroundURI = Uri.parse((item as VideoTable).urlImage)
                startBackgroundTimer()
            }

        }
    }
}