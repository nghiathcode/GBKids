package vn.android.thn.gbkids.views.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi

class ProcessPlayView : TextView {
    private var bg_color: Int = Color.RED
    private val paint = Paint()
    var width_temp = 0
    var height_temp = 0
    var persent = 50.toFloat()
    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet? = null) {
        if (attrs !=null){
//            var tArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.TagFind)
//            bg_color = tArray.getColor(R.styleable.TagFind_bg_color,Color.parseColor("#80FFFFFF"))
//            persent  = tArray.getFloat(R.styleable.TagFind_persent,0f)
        }
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        width_temp = View.MeasureSpec.getSize(widthMeasureSpec)
        height_temp = View.MeasureSpec.getSize(heightMeasureSpec)
        invalidate()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
    fun updatePersent(persent:Float){
        this.persent = persent
        invalidate()
    }
    override fun onDraw(canvas: Canvas) {
        paint.setColor(bg_color);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0f, 0f, ((width_temp.toFloat()*persent)/100), height_temp.toFloat(), paint);
    }
}
