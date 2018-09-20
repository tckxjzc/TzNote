package com.tckxjzc.tz.note.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.tckxjzc.tz.note.R
import com.tckxjzc.tz.note.database.Note
import org.litepal.LitePal

class EditActivity : AppCompatActivity() {
    //view----------------
    @BindView(R.id.title)
    lateinit var title: EditText

    @BindView(R.id.content)
    lateinit var content: EditText

    @BindView(R.id.tool_bar)
    lateinit var toolbar: Toolbar
    //----------------view


    //properties-----------
//    companion object {
//        object MODE {
//            val ADD = 1
//            val EDIT = 2
//        }
//    }

//    var mode = MODE.ADD
    var id:Long=-1

    //-----------properties

    //lifecycle----------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        ButterKnife.bind(this)
        init()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                if (save()) {
                    goBack()
                    return true
                }

            }
            android.R.id.home -> {
                goBack()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }
    //----------lifecycle


    //method---------------
    fun save(): Boolean {
        val title = title.text.toString()
        if (title.isEmpty()) {
            Toast.makeText(this, "标题不能为空", Toast.LENGTH_LONG).show()
            return false
        }
        val note=Note(title, content.text.toString())
        if(id>0){
            note.update(id)
        }else{
            note.save()
        }
        MainActivity.needRefresh=true
        return true
    }


    private fun goBack() {
        finish()
    }

    private fun init() {
        initNoteItem()
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);

    }
    private fun initNoteItem(){
        id = intent.getIntExtra("id", -1).toLong()
        if(id<0){
            toolbar.setTitle(R.string.new_text)
            return
        }
        toolbar.setTitle(R.string.modify)
//        mode=MODE.EDIT
        val note = LitePal.find(Note::class.java, id)
        title.setText(note.title)
        content.setText(note.content)
    }
    //---------------method
}
