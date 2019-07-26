package vn.android.thn.gbkids.views.dialogs

import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.Sort
import vn.android.thn.commons.listener.ListItemListener
import vn.android.thn.commons.realm.RealmHistorySearch
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.views.adapter.HistoryKeyWordAdapter
import vn.android.thn.library.utils.GBUtils
import vn.android.thn.library.views.dialogs.GBDialogFragment

class SearchDialog: GBDialogFragment() , ListItemListener {
    override fun onItemClick(obj: Any, pos: Int) {
        if (listener!= null) {
            listener!!.onKeyWord(adapter.itemIndex(pos).keyword.trim())
        }
    }

    lateinit var toolbar:Toolbar
    lateinit var txt_keyword:EditText
    var listener:SearchListener? = null
    var isClear =false
    private lateinit var mListView: RecyclerView
    private lateinit var adapter: HistoryKeyWordAdapter
    override fun initView() {
        isCancelable = true
        mListView = findViewById<RecyclerView>(R.id.list)!!
        mListView.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(activity!!)
        mListView.setLayoutManager(mLayoutManager)
        mListView.setItemAnimator(DefaultItemAnimator())
        txt_keyword = findViewById<EditText>(R.id.txt_keyword)!!
        toolbar = findViewById<Toolbar>(R.id.toolbar)!!
        toolbar.setNavigationIcon(null)

        findViewById<View>(R.id.btn_back_search)!!.setOnClickListener {
            dismiss()
        }
        txt_keyword.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (listener!= null) {
                        listener!!.onKeyWord(txt_keyword.text.toString().trim())
                    }
                    return true
                }
                return false
            }

        })

        if (isClear){
            txt_keyword.setText("")

        } else{
            if (listener!= null){
                if (!GBUtils.isEmpty(listener!!.loadKeyWord())){
                    txt_keyword.setText(listener!!.loadKeyWord())
                    txt_keyword.setSelection(listener!!.loadKeyWord().length)
                }
            }
        }
        val realm =Realm.getDefaultInstance()
        val list =realm.where(RealmHistorySearch::class.java).sort("dateUpdate",Sort.DESCENDING).findAll()
        adapter = HistoryKeyWordAdapter(activity!!,realm.copyFromRealm(list))
        adapter.listener = this
        mListView.adapter = adapter
    }

    override fun dialogName(): String {
        return "SearchDialog"
    }

    override fun layoutFileCommon(): Int {
        return R.layout.dialog_search
    }
    override fun styleDialog(): Int {
        return R.style.AppTheme_DialogFullScreen
    }
    interface SearchListener{
        fun onKeyWord(keyword:String)
        fun loadKeyWord():String
    }
}