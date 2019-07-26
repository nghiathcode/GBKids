package vn.android.thn.gbkids.views.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vn.android.thn.commons.listener.ListItemListener
import vn.android.thn.commons.realm.RealmHistorySearch
import vn.android.thn.gbkids.R

class HistoryKeyWordAdapter(private val mContext: Context, var list: List<RealmHistorySearch>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listener: ListItemListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(mContext)
            .inflate(R.layout.row_history_keyword, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun itemIndex(pos:Int):RealmHistorySearch{
        return list.get(pos)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as HistoryKeyWordAdapter.ViewHolder).bindData(list.get(position) )
    }

    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var txt_keyword: TextView
        init {
            txt_keyword = itemView.findViewById(R.id.txt_keyword)
            itemView.setOnClickListener {
                if (listener!= null){
                    listener!!.onItemClick(list.get(layoutPosition),layoutPosition)
                }
            }
        }
        fun bindData(obj:RealmHistorySearch){
            txt_keyword.text = obj.keyword
        }
    }
}