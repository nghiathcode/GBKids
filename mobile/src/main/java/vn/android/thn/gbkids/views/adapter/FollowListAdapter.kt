package vn.android.thn.gbkids.views.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import vn.android.thn.gbfilm.views.listener.ListItemListener
import vn.android.thn.gbfilm.views.listener.LoadMoreListener
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.model.db.FollowTable
import vn.android.thn.gbkids.model.db.VideoDownLoad
import vn.android.thn.gbkids.views.view.ImageLoader

class FollowListAdapter(private val mContext: Context, var list: MutableList<FollowTable>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listener: ListItemListener? = null
    var isLoadMore = false
    var loadMoreListener: LoadMoreListener? = null
    private val TYPE_DATA = 0
    private val TYPE_LOAD_MORE = 1
    fun loadMore(isLoadMore: Boolean, loadMoreListener: LoadMoreListener) {
        this.isLoadMore = isLoadMore
        if (!isLoadMore) {
            this.loadMoreListener = null
        } else {
            this.loadMoreListener = loadMoreListener
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_DATA) {
            val itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.row_follow_list, parent, false)
            return ViewHolder(itemView)
        } else {
            val itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.row_load_more, parent, false)
            return ViewHolderLoadMore(itemView)
        }
    }
    override fun getItemViewType(position: Int): Int {
        if (position == list.size ){
            return TYPE_LOAD_MORE
        } else {
            return TYPE_DATA
        }
    }
    override fun getItemCount(): Int {
        if (isLoadMore){
            return list.size +1
        } else {
            return list.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_DATA) {
            (holder as FollowListAdapter.ViewHolder).bindData(list.get(position))
        } else {
            (holder as FollowListAdapter.ViewHolderLoadMore).onLoadMore()
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
    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img_thumbnail: ImageView
        var txt_name: TextView
//        var txt_info: TextView

        init {
            img_thumbnail = itemView.findViewById(R.id.img_thumbnail)
            txt_name = itemView.findViewById(R.id.txt_info)
//            txt_info = itemView.findViewById(R.id.txt_info)
            itemView.setOnClickListener {
                if (listener != null) {
                    listener!!.onItemClick(list.get(layoutPosition), layoutPosition)
                }
            }
        }
        fun bindData(obj: FollowTable) {
            txt_name.text = obj.channelTitle
//
            ImageLoader.loadImageCricle(img_thumbnail, obj.getUrlImage())
//
//            txt_info.text = obj.channelTitle
        }
    }
}