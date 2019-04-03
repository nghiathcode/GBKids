package vn.android.thn.gbkids.views.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import vn.android.thn.gbfilm.views.listener.ListItemListener
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.model.db.KeyWordHistory


//
// Created by NghiaTH on 4/2/19.
// Copyright (c) 2019

class HistorySearchAdapter (private val mContext: Context, var list: MutableList<KeyWordHistory>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listener: ListItemListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(mContext)
            .inflate(R.layout.row_history_keword_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as HistorySearchAdapter.ViewHolder).bindData(list.get(position) )
    }

    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var key_word:TextView
        init {
            key_word = itemView.findViewById(R.id.key_word)
            itemView.setOnClickListener {
                if (listener!= null){
                    listener!!.onItemClick(list.get(layoutPosition),layoutPosition)
                }
            }
        }
        fun bindData(obj:KeyWordHistory){
            key_word.text = obj.keyword
        }
    }
}
