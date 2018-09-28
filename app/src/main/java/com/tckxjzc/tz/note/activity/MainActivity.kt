package com.tckxjzc.tz.note.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.tckxjzc.tz.note.R
import com.tckxjzc.tz.note.adapter.NoteListAdapter
import com.tckxjzc.tz.note.database.NoteManager
import com.tckxjzc.tz.note.tools.FileManageTools
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

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

    private var backTime: Long = -1

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
        if (keyCode == KeyEvent.KEYCODE_BACK && event!!.action == KeyEvent.ACTION_UP) {
            if (System.currentTimeMillis() - backTime > 2000) {
                backTime = System.currentTimeMillis()
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show()
                return true
            }
            android.os.Process.killProcess(android.os.Process.myPid())
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_backup -> {
                backup()
            }
            R.id.menu_recovery -> {
                recovery()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onRestart() {
        super.onRestart()
        if (needRefresh) {
            refresh()
            needRefresh = false
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Test", "授权被允许")
                } else {
                    AlertDialog.Builder(this)
                            .setMessage("拒绝后,部分功能无法使用")
                            .setPositiveButton("确定") { dialog, which -> dialog.dismiss() }
                            .create().show()
                }

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                if (data != null) {
                    val uri = data.data
                    Log.e("test",File(uri.path).exists().toString())
                    Log.e("test",uri.path)
                    Log.e("test",uri.authority)
                    Log.e("test",DocumentsContract.isDocumentUri(this,uri).toString())
                    Toast.makeText(this, "文件路径：" + uri.path, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //---------------lifecycle

    //method-----------------

    private fun init() {
        initRecyclerView()
        setSupportActionBar(toolbar)
        add.setOnClickListener {
            needRefresh = true
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

    fun backup() {
        if (!verifyStoragePermissions()) {
            return
        }
        val noteStoreFile = getDatabasePath("NoteStore.db").absolutePath
        val backupFilePath = "${Environment.getExternalStorageDirectory().absolutePath}/tckxjzc/TzNote/backup/"
        try {
            val file = File(backupFilePath)
            if (!file.exists()) {
                file.mkdirs()
            }
            val flag = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            FileManageTools.fileCopy(noteStoreFile, backupFilePath, "note_${flag}.db")
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            return
        }
        Toast.makeText(this, "已经备份至:${backupFilePath}", Toast.LENGTH_LONG).show()
    }

    private fun verifyStoragePermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //检测是否有写的权限
            val permission = this.checkSelfPermission(
                    "android.permission.WRITE_EXTERNAL_STORAGE")
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                this.requestPermissions(arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        1)
                return false
            }
        }
        return true
    }

    fun recovery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
//        startActivityForResult(intent, 1)
        startActivityForResult(Intent.createChooser(intent,"选择文件"),1)
    }

    //-----------------method
}
