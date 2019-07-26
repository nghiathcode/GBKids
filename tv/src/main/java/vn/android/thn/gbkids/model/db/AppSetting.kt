package vn.android.thn.gbkids.model.db

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table


//
// Created by NghiaTH on 3/12/19.
// Copyright (c) 2019

@Table(name = "AppSetting")
class AppSetting: Model() {
    @Column(name = "token")
    var token = ""
    @Column(name = "appID", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    var appID = ""
}
