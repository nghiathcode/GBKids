package vn.android.thn.gbkids.views.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vn.android.thn.commons.App
import vn.android.thn.commons.listener.ListItemListener
import vn.android.thn.commons.listener.LoadMoreListener
import vn.android.thn.commons.realm.RealmVideo
import vn.android.thn.commons.view.ImageLoader
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.views.view.ProcessPlayView

class HomeAdapter(private val mContext: Context, var list: ArrayList<RealmVideo>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listener: ListItemListener? = null
    var isLoadMore = false
    var loadMoreListener: LoadMoreListener? = null
    private val TYPE_DATA = 0
    private val TYPE_LOAD_MORE = 1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_DATA) {
            val itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.row_home_item, parent, false)

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
            (holder as HomeAdapter.ViewHolder).bindData(list.get(position) )
        } else {
            (holder as HomeAdapter.ViewHolderLoadMore).onLoadMore()
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
        var img_channel:ImageView
        var txt_name: TextView
        var txt_info:TextView
        var processPlay:ProcessPlayView
        init {
            img_thumbnail = itemView.findViewById(R.id.img_thumbnail)
            txt_name = itemView.findViewById(R.id.txt_name)
            txt_info = itemView.findViewById(R.id.txt_info)
            img_channel = itemView.findViewById(R.id.img_channel)
            processPlay = itemView.findViewById(R.id.processPlay)
            itemView.setOnClickListener {
                if (listener!= null){
                    listener!!.onItemClick(list.get(layoutPosition),layoutPosition)
                }
            }
            var layoutParams =  itemView.layoutParams
            layoutParams.height = App.getInstance().heightRowLarger()
            itemView.layoutParams = layoutParams
            itemView.requestLayout()
        }
        fun bindData(obj:RealmVideo){
            txt_name.text = obj.title
            ImageLoader.loadImage(img_thumbnail, obj.imageLarger,obj.videoID)
            ImageLoader.loadImageCricle(img_channel, obj.channelImage)
            txt_info.text = obj.channelTitle
            if (obj.videoTime>0) {
                processPlay.updatePersent( (obj.videoTimeCurrent * 100 / obj.videoTime).toFloat())
            } else {
                processPlay.updatePersent( 0f)
            }
        }
    }
}