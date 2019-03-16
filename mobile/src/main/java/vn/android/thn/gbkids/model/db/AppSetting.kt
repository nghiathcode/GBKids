package vn.android.thn.gbkids.model.db

import com.activeandroid.Model
import com.activeandroid.annotation.Column


//
// Created by NghiaTH on 3/12/19.
// Copyright (c) 2019

class AppSetting: Model() {
    @Column(name = "token", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    var token = ""
}
