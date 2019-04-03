package vn.android.thn.gbkids.model.db

import android.database.Cursor
import com.activeandroid.ActiveAndroid
import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table
import jp.co.tss21.monistor.models.GBDataBase


//
// Created by NghiaTH on 3/19/19.
// Copyright (c) 2019

@Table(name = "keyword_history")
class KeyWordHistory() : Model() {
    @Column(name = "keyword", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    var keyword: String =""
    @Column(name = "dateUpdate")
    var dateUpdate: String =""
    companion object {
        fun convertToObject(c: Cursor):KeyWordHistory{
            var obj :KeyWordHistory = KeyWordHistory()
            if (!c.isNull(c.getColumnIndex("keyword"))){
                obj.keyword = c.getString(c.getColumnIndex("keyword"))
            }
            if (!c.isNull(c.getColumnIndex("dateUpdate"))){
                obj.dateUpdate = c.getString(c.getColumnIndex("dateUpdate"))
            }

            return obj
        }
        fun allData():MutableList<KeyWordHistory>{
            var lst:MutableList<KeyWordHistory> = ArrayList<KeyWordHistory>()
            var sqlSelect = StringBuilder()
            sqlSelect.append(" select * from keyword_history ORDER BY dateUpdate DESC")
            val c = ActiveAndroid.getDatabase().rawQuery(sqlSelect.toString(),null)
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        lst.add( convertToObject(c))
                    } while (c.moveToNext())
                }

            }
            c.close()
            return lst
        }
    }
}
