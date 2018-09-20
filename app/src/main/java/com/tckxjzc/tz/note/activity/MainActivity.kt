package com.tckxjzc.tz.note.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.tckxjzc.tz.note.R
import com.tckxjzc.tz.note.adapter.NoteListAdapter
import com.tckxjzc.tz.note.database.NoteManager

class MainActivity : BaseActivity() {
    //view -------------
    @BindView(R.id.add)
    lateinit var add: Button

    @BindView(R.id.List_view)
    lateinit var recyclerView: RecyclerView

    @BindView(R.id.swipe_refresh)
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    @BindView(R.id.tool_bar)
    lateinit var toolbar: Toolbar
    //----------------view
    //properties----------
    companion object {
        var needRefresh = false
    }
    private var backTime:Long=-1

    private val adapter = NoteListAdapter(this)

    private val noteManager = NoteManager()
    //----------properties

    //lifecycle---------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        init()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode==KeyEvent.KEYCODE_BACK&&event!!.action==KeyEvent.ACTION_UP){
            if(System.currentTimeMillis()-backTime>2000){
                backTime=System.currentTimeMillis()
                Toast.makeText(this,"再按一次退出",Toast.LENGTH_SHORT).show()
                return true
            }
            android.os.Process.killProcess(android.os.Process.myPid())
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onRestart() {
        super.onRestart()
        if (needRefresh) {
            refresh()
            needRefresh=false
        }

    }
    //---------------lifecycle

    //method-----------------

    private fun init() {
        initRecyclerView()
        setSupportActionBar(toolbar)
        add.setOnClickListener {
            needRefresh=true
            startActivity(Intent(this, EditActivity::class.java))
        }

        swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }

    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.list.addAll(noteManager.first())
        adapter.notifyDataSetChanged()

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val count = recyclerView!!.adapter.itemCount
                val visibleItemCount = recyclerView.childCount
                val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition == count - 1 && visibleItemCount > 0) {
//                    Log.i("MainActivity", "loadMore")
                    loadMore()
                }
            }
        })
    }


    private fun refresh() {
        swipeRefreshLayout.isRefreshing = true
        noteManager.reset()
        adapter.list.clear()
        adapter.list.addAll(noteManager.first())
        adapter.notifyDataSetChanged()
        swipeRefreshLayout.isRefreshing = false
    }


    fun loadMore() {
        if (noteManager.hasNext) {
            adapter.list.addAll(noteManager.next())
            adapter.notifyDataSetChanged()
        }
    }
    //-----------------method
}
