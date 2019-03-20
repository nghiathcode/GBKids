package vn.android.thn.gbkids.views.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import vn.android.thn.gbkids.R


//
// Created by NghiaTH on 3/19/19.
// Copyright (c) 2019

class PlayerVideoListFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =inflater.inflate(R.layout.fragment_video_list_player,container,false)
        return view
    }
}
