package vn.android.thn.gbkids.utils

import com.activeandroid.Model
import com.activeandroid.annotation.Table

object Utils {
    fun <T : Model> getTableName(clazz: Class<T>): String {
        try {
            return clazz.getAnnotation(Table::class.java).name
        } catch (e: Exception) {
            return ""
        }

    }
}
