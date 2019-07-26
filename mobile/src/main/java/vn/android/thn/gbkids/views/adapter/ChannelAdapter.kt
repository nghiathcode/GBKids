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
import vn.android.thn.gbkids.model.ChannelEntity

class ChannelAdapter(private val mContext: Context, var list: ArrayList<ChannelEntity>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listener: ListItemListener? = null
    var isLoadMore = false
    var loadMoreListener: LoadMoreListener? = null
    private val TYPE_DATA = 0
    private val TYPE_LOAD_MORE = 1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_DATA) {
            val itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.row_channel_list, parent, false)
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
            (holder as ChannelAdapter.ViewHolder).bindData(list.get(position) )
        } else {
            (holder as ChannelAdapter.ViewHolderLoadMore).onLoadMore()
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
        init {
            img_thumbnail = itemView.findViewById(R.id.img_thumbnail)
            txt_name = itemView.findViewById(R.id.txt_info)
            itemView.setOnClickListener {
                if (listener!= null){
                    listener!!.onItemClick(list.get(layoutPosition),layoutPosition)
                }
            }
        }
        fun bindData(obj:ChannelEntity){
            txt_name.text = obj.channelTitle
            ImageLoader.loadImageCricle(img_thumbnail, obj.channelImage)
        }
    }
}