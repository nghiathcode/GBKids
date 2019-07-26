package vn.android.thn.gbkids.utils

import android.content.Context
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

    fun getDensityName(context: Context): String {
        val density = context.getResources().getDisplayMetrics().density
        if (density >= 4.0) {
            return "xxxhdpi"//1080
        }
        if (density >= 3.0) {
            return "xxhdpi"  //1080
        }
        if (density >= 2.0) {
            return "xhdpi"//720
        }
        if (density >= 1.5) {
            return "hdpi"//480
        }
        return if (density >= 1.0) {
            "mdpi"               //260
        } else "ldpi"
    }

    fun getDensity(context: Context): Int {
        val density = context.resources.displayMetrics.density
        if (density >= 4.0) {
            return 5
        }
        if (density >= 3.0) {
            return 4
        }
        if (density >= 2.0) {
            return 3
        }
        if (density >= 1.5) {
            return 2
        }
        return if (density >= 1.0) {
            1
        } else 0
    }
    fun getScreen(context: Context):Int{
        val density = getDensity(context)
        if (density>=4) return 1080
        if (density>=3) return 720
        if (density>=2) return 480
        return  360
    }
}
