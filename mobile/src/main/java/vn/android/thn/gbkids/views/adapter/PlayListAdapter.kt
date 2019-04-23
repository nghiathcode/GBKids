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
import com.google.gson.Gson
import jp.co.tss21.monistor.models.GBDataBase
import vn.android.thn.gbfilm.views.listener.ListItemListener
import vn.android.thn.gbfilm.views.listener.LoadMoreListener
import vn.android.thn.gbfilm.views.listener.PlayListItemListener
import vn.android.thn.gbkids.App
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.constants.Constants
import vn.android.thn.gbkids.model.db.FollowTable
import vn.android.thn.gbkids.model.db.VideoDownLoad
import vn.android.thn.gbkids.model.db.VideoTable
import vn.android.thn.gbkids.model.entity.ChannelLogoEntity
import vn.android.thn.gbkids.views.view.ImageLoader
import vn.android.thn.library.utils.GBUtils
import com.google.android.gms.ads.AdView
import vn.android.thn.gbkids.constants.Constants.SHOW_AD_START


//
// Created by NghiaTH on 2/27/19.
// Copyright (c) 2019

class PlayListAdapter(private val mContext: Context, var list: MutableList<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TYPE_DATA = 0
    private val TYPE_LOAD_MORE = 1
    private val TYPE_HEADER = 2
    var listener: PlayListItemListener? = null
    var isLoadMore = false
    var loadMoreListener: LoadMoreListener? = null
    var headerData: VideoTable? = null
    var channelLogoEntity: ChannelLogoEntity? = null
    var localList = false
    fun loadHeader(headerData: VideoTable) {
        this.headerData = headerData
        notifyDataSetChanged()
    }

    fun loadChannelLogo(channelLogoEntity: ChannelLogoEntity?) {
        this.channelLogoEntity = channelLogoEntity
        notifyDataSetChanged()
    }

    fun loadMore(isLoadMore: Boolean, loadMoreListener: LoadMoreListener) {
        this.isLoadMore = isLoadMore
        if (!isLoadMore) {
            this.loadMoreListener = null
        } else {
            this.loadMoreListener = loadMoreListener
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (!localList) {
            if (position == 0) return TYPE_HEADER
        }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_HEADER) {
            val itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.row_header_list, parent, false)
            return ViewHolderHeader(itemView)
        }
        if (viewType == TYPE_DATA) {
            val itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.row_search_list, parent, false)
            return ViewHolder(itemView)
        } else {
            val itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.row_load_more, parent, false)
            return ViewHolderLoadMore(itemView)
        }
    }

    override fun getItemCount(): Int {
        if (isLoadMore) {
            return list.size + 2
        } else {
            if (localList) {
                return list.size
            } else {
                return list.size + 1
            }

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_HEADER) {
            (holder as PlayListAdapter.ViewHolderHeader).bindData()
            return
        }
        if (getItemViewType(position) == TYPE_DATA) {
            if (localList) {
                (holder as PlayListAdapter.ViewHolder).bindData(list.get(position))
            } else {
                (holder as PlayListAdapter.ViewHolder).bindData(list.get(position - 1))
            }
        } else {
            (holder as PlayListAdapter.ViewHolderLoadMore).onLoadMore()
        }
    }

    internal inner class ViewHolderLoadMore(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onLoadMore() {
            if (loadMoreListener != null) {
                loadMoreListener!!.onLoadMore()
            }
        }
    }

    internal inner class ViewHolderHeader(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView
        var title_channel: TextView
        var img_channel: ImageView
        var action_download: ImageView
        var action_follow: ImageView

        var ad_card_view: FrameLayout
        var adView: AdView? = null

        init {
            adView = AdView(mContext)
            adView!!.setAdSize(AdSize.BANNER);
            adView!!.setAdUnitId(mContext.getString(R.string.AD_UNIT_ID));
            title = itemView.findViewById(R.id.title)
            ad_card_view = itemView.findViewById(R.id.ad_card_view)

            action_follow = itemView.findViewById(R.id.action_follow)
            action_download = itemView.findViewById(R.id.action_download)
            title_channel = itemView.findViewById(R.id.title_channel)
            img_channel = itemView.findViewById(R.id.img_channel)
            action_download.setOnClickListener {
                if (listener != null) {
                    if (headerData != null) {
                        listener!!.onDownload(headerData!!)
                        action_download.setImageResource(R.drawable.ico_download_complete)
                    }
                }
            }
            action_follow.setOnClickListener {
                if (headerData != null) {
                    var follow = GBDataBase.getObject(FollowTable::class.java, "channelID=?", *arrayOf(headerData!!.channelID))
                    if (follow == null) {
                        follow = FollowTable()
                        follow!!.channelID = headerData!!.channelID
                        follow!!.channelTitle = headerData!!.channelTitle
                        follow!!.thumbnails = Gson().toJson(channelLogoEntity)
                        follow!!.save()
                        action_follow.setImageResource(R.drawable.ico_follow_complete)
                    } else {
                        follow.delete()
                        action_follow.setImageResource(R.drawable.ico_follow)
                    }
                }
            }
        }

        fun bindData() {
            if (headerData != null) {
                title.text = headerData!!.title
                title_channel.text = headerData!!.channelTitle
                var follow = GBDataBase.getObject(FollowTable::class.java, "channelID=?", *arrayOf(headerData!!.channelID))
                if (follow == null) {
                    action_follow.setImageResource(R.drawable.ico_follow)
                } else {
                    action_follow.setImageResource(R.drawable.ico_follow_complete)
                }
                var downloaded = GBDataBase.getObject(VideoDownLoad::class.java, "videoID=?", *arrayOf(headerData!!.videoID))
                if (downloaded == null) {
                    action_download.setImageResource(R.drawable.ico_download)
                } else {
                    action_download.setImageResource(R.drawable.ico_download_complete)
                }

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
                if (App.getInstance().playCount > SHOW_AD_START && App.getInstance().appStatus == 1){
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
            } else {
                if (App.getInstance().playCount > SHOW_AD_START && App.getInstance().appStatus == 1){
                    if (ad_card_view.childCount > 0) {
                        ad_card_view.removeAllViews()
                    }
                }
                img_channel.visibility = View.GONE
            }

        }
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
                    if (!localList) {
                        listener!!.onItemClick(list.get(layoutPosition - 1), layoutPosition - 1)
                    } else {
                        listener!!.onItemClick(list.get(layoutPosition), layoutPosition)
                    }
                }
            }
        }

        fun bindData(obj: Any) {
            if (obj is VideoTable) {
                txt_name.text = obj.title
                if (App.getInstance().appStatus == 1) {
                    if (GBUtils.isEmpty(obj.urlImage)) {
                        var thumbnails = obj.toImage()
                        if (thumbnails != null) {
                            if (thumbnails.maxres != null) {
                                obj.urlImage = thumbnails!!.maxres!!.url
                            } else if (thumbnails.high != null) {
                                obj.urlImage = thumbnails!!.high!!.url
                            } else if (thumbnails.medium != null) {
                                obj.urlImage = thumbnails!!.medium!!.url
                            } else if (thumbnails.standard != null) {
                                obj.urlImage = thumbnails!!.standard!!.url
                            } else if (thumbnails.default != null) {
                                obj.urlImage = thumbnails!!.default!!.url
                            }
                        }
                    }
                    ImageLoader.loadImage(img_thumbnail, obj.urlImage, obj.videoID)
                } else {
                    ImageLoader.loadImage(img_thumbnail, Constants.DOMAIN + "/thumbnail_high/" + obj.videoID, obj.videoID)
                }

                txt_info.text = obj.channelTitle
                return
            }
            if (obj is VideoDownLoad) {
                txt_name.text = obj.title
                ImageLoader.loadImage(img_thumbnail, obj.thumbnails, obj.videoID)
                txt_info.text = obj.channelTitle
            }
        }
    }
}
