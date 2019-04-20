package vn.android.thn.gbkids.views.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import vn.android.thn.gbfilm.views.listener.ListItemListener
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.model.db.FollowTable
import vn.android.thn.gbkids.model.db.VideoDownLoad
import vn.android.thn.gbkids.views.view.ImageLoader

class FollowListAdapter(private val mContext: Context, var list: MutableList<FollowTable>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listener: ListItemListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.row_follow_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as FollowListAdapter.ViewHolder).bindData(list.get(position ))
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