package vn.android.thn.gbkids.views.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vn.android.thn.commons.listener.ListItemListener
import vn.android.thn.commons.listener.LoadMoreListener
import vn.android.thn.commons.view.ImageLoader
import vn.android.thn.gbkids.R
import android.content.Intent
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.net.Uri
import android.text.style.ClickableSpan
import android.widget.FrameLayout
import com.google.android.gms.ads.AdView
import vn.android.thn.commons.App
import vn.android.thn.commons.GBRealm
import vn.android.thn.commons.listener.DownloadVideoListener
import vn.android.thn.commons.realm.RealmFollow
import vn.android.thn.commons.realm.RealmVideo
import vn.android.thn.gbkids.views.view.ProcessPlayView
import vn.android.thn.library.utils.GBUtils
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize


class ListVideoPlayAdapter(private val mContext: Context, var list: ArrayList<RealmVideo>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listener: ListItemListener? = null
    var isLoadMore = false
    var loadMoreListener: LoadMoreListener? = null
    private val TYPE_DATA = 0
    private val TYPE_LOAD_MORE = 1
    private val TYPE_HEADER = 2
    var downloadListener: DownloadVideoListener? = null
    var showDescription = false
    var headerData: RealmVideo? = null
    var adView: AdView
    init {
        adView = AdView(mContext)
        adView!!.setAdSize(AdSize.BANNER);
        adView!!.setAdUnitId(mContext.getString(R.string.AD_UNIT_ID))
    }
    fun loadAD(){
        if (App.getInstance().isDebugMode()) {
            adView!!.loadAd(AdRequest.Builder().addTestDevice("BCB68136B98CF003B0B4965411508000").build())
        } else {
            adView!!.loadAd(AdRequest.Builder().build())
        }
    }
    fun resumeAD(){
        adView.resume()
    }
    fun pauseAD(){
        adView.pause()
    }
    fun destroyAD(){
        adView.destroy()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_HEADER) {
            val itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.row_header_list_play, parent, false)
            return ViewHolderHeader(itemView)
        }
        if (viewType == TYPE_DATA) {
            val itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.row_video_item, parent, false)
            return ViewHolder(itemView)
        } else {
            val itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.row_load_more, parent, false)
            return ViewHolderLoadMore(itemView)
        }
    }
    override fun getItemViewType(position: Int): Int {
//        if (position == list.size ){
//            return TYPE_LOAD_MORE
//        } else {
//            return TYPE_DATA
//        }
        //
        if (position == 0) return TYPE_HEADER
        if (isLoadMore) {
            if (position == (list.size + 1)) {
                return TYPE_LOAD_MORE
            } else {
                return TYPE_DATA
            }
        } else {
            return TYPE_DATA
        }
    }
    override fun getItemCount(): Int {
        if (isLoadMore){
            return list.size +2
        } else {
            return list.size+1
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_HEADER) {
            (holder as ListVideoPlayAdapter.ViewHolderHeader).bindData()
            return
        }
        if (getItemViewType(position) == TYPE_DATA) {
            (holder as ListVideoPlayAdapter.ViewHolder).bindData(list.get(position-1) )
        } else {
            (holder as ListVideoPlayAdapter.ViewHolderLoadMore).onLoadMore()
        }
    }
    internal inner class ViewHolderHeader(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var row_title:View
        lateinit var title:TextView
        lateinit var img_channel:ImageView
        lateinit var txt_channel_title:TextView
        lateinit var img_expand_detail:ImageView
        lateinit var row_description:View
        lateinit var txt_description:TextView
        lateinit var btn_download:ImageView
        var ad_card_view: FrameLayout
        var img_follow:ImageView
        var btn_like:ImageView
        var btn_ignore:ImageView
        init {
            ad_card_view = itemView.findViewById(R.id.ad_card_view)
            row_title = itemView.findViewById(R.id.row_title)
            title = itemView.findViewById(R.id.title)
            img_follow  = itemView.findViewById(R.id.img_follow)
            btn_download = itemView.findViewById(R.id.btn_download)
            img_channel = itemView.findViewById(R.id.img_channel)
            txt_channel_title = itemView.findViewById(R.id.txt_channel_title)
            btn_ignore = itemView.findViewById(R.id.btn_ignore)
            img_expand_detail = itemView.findViewById(R.id.img_expand_detail)
            row_description = itemView.findViewById(R.id.row_description)
            txt_description = itemView.findViewById(R.id.txt_description)
            btn_like = itemView.findViewById(R.id.btn_like)
            row_title.setOnClickListener {
                showDescription = !showDescription
                if (showDescription){
                    row_description.visibility = View.GONE
                    img_expand_detail.setImageResource(R.drawable.ico_up_detail)
                } else {
                    row_description.visibility = View.GONE
                    img_expand_detail.setImageResource(R.drawable.ico_down_detail)
                }
            }
            img_follow.setOnClickListener {
                var followTable = RealmFollow()
                followTable.channelId = headerData!!.channelId
                followTable.channelTitle = headerData!!.channelTitle
                followTable.channelImage = headerData!!.channelImage
                if (RealmFollow.isFollow(headerData!!.channelId)){
                    RealmFollow.unFollow(followTable.channelId)
                    img_follow.setImageResource(R.drawable.ico_un_following)
                } else {
                    GBRealm.save(followTable)
                    img_follow.setImageResource(R.drawable.ico_following)
                }
            }
            btn_like.setOnClickListener {
                if (RealmVideo.isLike(headerData!!.videoID)){
                    headerData!!.isLike =0
                    GBRealm.save(headerData!!)
                    btn_like.setImageResource(R.drawable.ico_un_like)
                } else{
                    headerData!!.isLike =1
                    GBRealm.save(headerData!!)
                    btn_like.setImageResource(R.drawable.ico_like)
                }
            }
            btn_ignore.setOnClickListener {
                if (headerData!= null) {
                    if (downloadListener!= null){
                        downloadListener!!.onIgnoreVideo(headerData!!)
                    }

                }
            }
            if (App.getInstance().downLoadAllow ==1){
                btn_download.setOnClickListener {
                    if (RealmVideo.isDownLoaded(headerData!!.videoID)){
                        btn_download.setImageResource(R.drawable.ico_un_download)
                    } else{
                        if (downloadListener!= null){
                            downloadListener!!.onDownload(headerData!!.videoID)
                            btn_download.setImageResource(R.drawable.ico_un_download)
                        }
                    }

                }
            }

        }
        fun bindData() {
            if (ad_card_view.childCount > 0) {
                ad_card_view.removeAllViews()
            }
            ad_card_view.addView(adView)
            title.text = headerData!!.title
            txt_channel_title.text = headerData!!.channelTitle

            ImageLoader.loadImageCricle(img_channel,headerData!!.channelImage)
            val sequence = Html.fromHtml(if (GBUtils.isEmpty(headerData!!.description)) "" else headerData!!.description)
            val strBuilder = SpannableStringBuilder(sequence)
            val urls = strBuilder.getSpans(0, sequence.length, URLSpan::class.java)
            for (span in urls) {
                makeLinkClickable(strBuilder, span)
            }
            txt_description.text = strBuilder
            txt_description.setMovementMethod(LinkMovementMethod.getInstance())
            if (showDescription){
                row_description.visibility = View.VISIBLE
                img_expand_detail.setImageResource(R.drawable.ico_up_detail)
            } else {
                row_description.visibility = View.GONE
                img_expand_detail.setImageResource(R.drawable.ico_down_detail)
            }
            if (RealmFollow.isFollow(headerData!!.channelId)){
                img_follow.setImageResource(R.drawable.ico_following)
            } else {
                img_follow.setImageResource(R.drawable.ico_un_following)
            }
            if (RealmVideo.isLike(headerData!!.videoID)){
                btn_like.setImageResource(R.drawable.ico_like)
            } else {
                btn_like.setImageResource(R.drawable.ico_un_like)
            }
            if (RealmVideo.isDownLoaded(headerData!!.videoID)){
                btn_download.setImageResource(R.drawable.ico_un_download)
            } else {
                btn_download.setImageResource(R.drawable.ico_download)
            }
            if (App.getInstance().downLoadAllow ==1){
                btn_download.visibility = View.VISIBLE
            } else{
                btn_download.visibility = View.INVISIBLE
            }
        }
        protected fun makeLinkClickable(strBuilder: SpannableStringBuilder, span: URLSpan) {
            val start = strBuilder.getSpanStart(span)
            val end = strBuilder.getSpanEnd(span)
            val flags = strBuilder.getSpanFlags(span)
            val clickable = object : ClickableSpan() {
                override fun onClick(view: View) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(span.url))
                    mContext.startActivity(intent)
                }
            }
            strBuilder.setSpan(clickable, start, end, flags)
            strBuilder.removeSpan(span)
        }
    }
    internal inner class ViewHolderLoadMore(itemView: View) : RecyclerView.ViewHolder(itemView){
        var data_loading: ProgressBar
        init {
            data_loading = itemView.findViewById(R.id.data_loading)
        }
        fun onLoadMore(){
            if (loadMoreListener!= null){
                loadMoreListener!!.onLoadMore()
            }
        }
    }
    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var img_thumbnail: ImageView
        var txt_name: TextView
        var txt_info:TextView
        var processPlay: ProcessPlayView
        init {
            img_thumbnail = itemView.findViewById(R.id.img_thumbnail)
            txt_name = itemView.findViewById(R.id.txt_name)
            txt_info = itemView.findViewById(R.id.txt_info)
            itemView.setOnClickListener {
                if (listener!= null){
                    listener!!.onItemClick(list.get(layoutPosition-1),layoutPosition-1)
                }
            }
            processPlay = itemView.findViewById(R.id.processPlay)
            var layoutParams =  itemView.layoutParams
            layoutParams.height = App.getInstance().heightRowSmall()
            itemView.layoutParams = layoutParams
            itemView.requestLayout()
        }
        fun bindData(obj:RealmVideo){
            txt_name.text = obj.title
            ImageLoader.loadImage(img_thumbnail, obj.imageLarger,obj.videoID)
            txt_info.text = obj.channelTitle
            if (obj.videoTime>0) {
                processPlay.updatePersent( (obj.videoTimeCurrent * 100 / obj.videoTime).toFloat())
            } else {
                processPlay.updatePersent( 0f)
            }
        }
    }
}