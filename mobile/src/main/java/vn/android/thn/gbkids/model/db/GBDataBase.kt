package jp.co.tss21.monistor.models

import android.database.Cursor
import com.activeandroid.ActiveAndroid
import com.activeandroid.Cache
import com.activeandroid.Model
import com.activeandroid.query.Delete
import com.activeandroid.query.Select
import vn.android.thn.gbkids.utils.Utils


//
// Created by NghiaTH on 9/13/18.
// Copyright (c) 2018

class GBDataBase {
    companion object {
        var SQL_UPDATE_SEQ = "UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME= '%s' ;"
        var SQL_DELETE_TABLE = "DELETE FROM %s ;"
        fun <T : Model> getList(tableClass: Class<T>, clause: String? = null, vararg args: Any): MutableList<T> {
            if (clause == null) {
                return Select().from(tableClass).execute<T>()
            } else {
                return Select().from(tableClass).where(clause, *args).execute<T>()
            }
        }

        fun <T : Model> getList(tableClass: Class<T>, sql: String): MutableList<T> {
            return Select().from(tableClass).where(sql).execute<T>()
        }

        fun <T : Model> insert(tableClass: T): Long {
            return tableClass.save()!!
        }

        fun <T : Model> insertList(listTable: List<T>) {
            ActiveAndroid.beginTransaction()
            try {
                for (table in listTable) {
                    table.save()
                }
                ActiveAndroid.setTransactionSuccessful()
            } finally {
                ActiveAndroid.endTransaction()
            }
        }

        fun <T : Model> getObject(tableClass: Class<T>, clause: String?, vararg args: Any?): T? {
            if (clause != null) {

                var model = Select().from(tableClass).where(clause, *args).executeSingle<Model>()
                if (model != null) {
                    return model as T
                }
                return null

            } else {
                var model = Select().from(tableClass).executeSingle<Model>()
                if (model != null) {
                    return model as T
                }
                return null
            }
        }

        fun <T : Model> deleteTable(tableClass: Class<T>, clause: String? = null, vararg args: Any) {
            if (clause == null) {
                Delete().from(tableClass).execute<Model>()
            } else {
                Delete().from(tableClass).where(clause, *args).execute<Model>()
            }

        }

        fun <T : Model> resetSeq(tableClass: Class<T>) {
            try {
                ActiveAndroid.beginTransaction()
                ActiveAndroid.execSQL(String.format(SQL_UPDATE_SEQ, Utils.getTableName(tableClass)))
                ActiveAndroid.setTransactionSuccessful()
            } finally {
                ActiveAndroid.endTransaction()
            }
        }

        fun isEmpty(tableName: String, clause: String? = null, vararg args: Any): Boolean {
            var sql = ""
            var c: Cursor? = null
            if (clause == null) {
                sql = "select * from %s"
                c = Cache.openDatabase().rawQuery(String.format(sql, tableName), null)
            } else {
                sql = "select * from %s where " + clause
                c = Cache.openDatabase().rawQuery(String.format(sql, tableName), args as Array<out String>?)
            }

            if (c != null) {
                if (c.moveToFirst()) {
                    return false
                }
            }
            return true
        }
    }
}
