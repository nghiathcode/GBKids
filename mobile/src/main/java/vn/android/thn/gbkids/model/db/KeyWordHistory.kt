package vn.android.thn.gbkids.model.db

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table


//
// Created by NghiaTH on 3/19/19.
// Copyright (c) 2019

@Table(name = "keyword_history")
class KeyWordHistory : Model() {
    @Column(name = "keyword", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    var keyword: String =""
}
