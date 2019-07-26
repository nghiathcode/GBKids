package vn.android.thn.gbkids.presenter

import android.content.Context
import android.support.v17.leanback.widget.ListRow
import android.support.v17.leanback.widget.Presenter
import android.support.v17.leanback.widget.RowHeaderPresenter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import vn.android.thn.gbkids.R

class IconHeaderItemPresenter: RowHeaderPresenter() {
    private var mUnselectedAlpha: Float = 0.toFloat()
    override fun onCreateViewHolder(viewGroup: ViewGroup?): Presenter.ViewHolder {
        mUnselectedAlpha = viewGroup!!.getResources()
            .getFraction(R.fraction.lb_browse_header_unselect_alpha, 1, 1)
        val inflater = viewGroup.context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.icon_header_item, null)
        view.setAlpha(mUnselectedAlpha) // Initialize icons to be at half-opacity.

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder?, item: Any?) {
        val headerItem = (item as ListRow).getHeaderItem()
        val rootView = viewHolder!!.view
        rootView.isFocusable = true

        val iconView = rootView.findViewById<View>(R.id.header_icon) as ImageView
        val icon = rootView.resources.getDrawable(R.drawable.android_header, null)
        iconView.setImageDrawable(icon)

        val label = rootView.findViewById<View>(R.id.header_label) as TextView
        label.setText(headerItem.getName())
    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder?) {
//        super.onUnbindViewHolder(viewHolder)
    }

    override fun onSelectLevelChanged(holder: ViewHolder?) {
        holder!!.view.alpha = mUnselectedAlpha + holder.getSelectLevel() * (1.0f - mUnselectedAlpha)
    }
}