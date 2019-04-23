package vn.android.thn.gbkids.views.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import jp.co.tss21.monistor.models.GBDataBase
import vn.android.thn.gbfilm.views.listener.ListItemListener
import vn.android.thn.gbkids.App
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.constants.Constants
import vn.android.thn.gbkids.model.db.FollowTable
import vn.android.thn.gbkids.model.db.VideoDownLoad
import vn.android.thn.gbkids.model.entity.ChannelLogoEntity
import vn.android.thn.gbkids.views.view.ImageLoader

class DownloadListAdapter(private val mContext: Context, var list: MutableList<VideoDownLoad>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TYPE_DATA = 0
    private val TYPE_LOAD_MORE = 1
    private val TYPE_HEADER = 2
    var listener: ListItemListener? = null
    var channelLogoEntity: ChannelLogoEntity? = null
    var headerData:VideoDownLoad? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_HEADER) {
            val itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.row_header_download_list, parent, false)
            return ViewHolderHeader(itemView)
        }
        val itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.row_search_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size + 1
    }
    override fun getItemViewType(position: Int): Int {
        if (position == 0) return TYPE_HEADER
        return TYPE_DATA
    }
    internal inner class ViewHolderHeader(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView
        var ad_card_view: FrameLayout
        var adView: AdView? = null
        var title_channel: TextView
        var img_channel: ImageView
        var action_follow: ImageView
        init {
            adView = AdView(mContext)
            adView!!.setAdSize(AdSize.BANNER);
            adView!!.setAdUnitId(mContext.getString(R.string.AD_UNIT_ID));
            title = itemView.findViewById(R.id.title)
            ad_card_view = itemView.findViewById(R.id.ad_card_view)
            title_channel = itemView.findViewById(R.id.title_channel)
            img_channel = itemView.findViewById(R.id.img_channel)
            action_follow = itemView.findViewById(R.id.action_follow)
        }

        fun bindData() {
            if (headerData != null) {
                itemView.visibility = View.VISIBLE
                title.text = headerData!!.title
                title_channel.text = headerData!!.channelTitle
                var follow = GBDataBase.getObject(FollowTable::class.java, "channelID=?", *arrayOf(headerData!!.channelID))
                if (follow == null) {
                    action_follow.setImageResource(R.drawable.ico_follow)
                } else {
                    action_follow.setImageResource(R.drawable.ico_follow_complete)
                }
                if (App.getInstance().playCount > Constants.SHOW_AD_START && App.getInstance().appStatus == 1) {
                    if (ad_card_view.childCount > 0) {
                        ad_card_view.removeAllViews()
                    }
                    ad_card_view.addView(adView)
                    adView!!.loadAd(AdRequest.Builder().build())
                    if (App.getInstance().isDebugMode()) {
                        adView!!.loadAd(AdRequest.Builder().addTestDevice("BCB68136B98CF003B0B4965411508000").build())
                    } else {
                        adView!!.loadAd(AdRequest.Builder().build())
                    }
                }
                title.visibility = View.VISIBLE
                ad_card_view.visibility = View.VISIBLE
                title_channel.visibility = View.VISIBLE
                action_follow.visibility = View.VISIBLE
            } else {
                action_follow.visibility = View.GONE
                title_channel.visibility = View.GONE
                itemView.visibility = View.GONE
                title.visibility = View.GONE
                ad_card_view.visibility = View.GONE
            }
            if (channelLogoEntity != null) {
                img_channel.visibility = View.VISIBLE
                var url = ""
                if (channelLogoEntity!!.high != null) {
                    url = channelLogoEntity!!.high!!.url
                } else if (channelLogoEntity!!.medium != null) {
                    url = channelLogoEntity!!.medium!!.url
                } else if (channelLogoEntity!!.default != null) {
                    url = channelLogoEntity!!.default!!.url
                }
                ImageLoader.loadImageCricle(img_channel, url)
            } else {
                img_channel.visibility = View.GONE
            }
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_HEADER) {
            (holder as DownloadListAdapter.ViewHolderHeader).bindData()
            return
        }
        (holder as DownloadListAdapter.ViewHolder).bindData(list.get(position -1 ))
    }
    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img_thumbnail: ImageView
        var txt_name: TextView
        var txt_info: TextView

        init {
            img_thumbnail = itemView.findViewById(R.id.img_thumbnail)
            txt_name = itemView.findViewById(R.id.txt_name)
            txt_info = itemView.findViewById(R.id.txt_info)
            itemView.setOnClickListener {
                if (listener != null) {
                    listener!!.onItemClick(list.get(layoutPosition - 1), layoutPosition - 1)
                }
            }
        }
        fun bindData(obj: VideoDownLoad) {
            txt_name.text = obj.title

            ImageLoader.loadImage(img_thumbnail, obj.thumbnails, obj.videoID)

            txt_info.text = obj.channelTitle
        }
    }
}