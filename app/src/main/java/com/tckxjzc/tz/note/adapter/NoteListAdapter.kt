package com.tckxjzc.tz.note.adapter

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import com.tckxjzc.tz.note.R
import com.tckxjzc.tz.note.activity.EditActivity
import com.tckxjzc.tz.note.database.Note
import java.text.SimpleDateFormat
import java.util.*

class NoteListAdapter(var context:Activity): RecyclerView.Adapter<NoteListAdapter.Companion.ViewHolder>() {
    var list=ArrayList<Note>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return  ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=list[position]
        holder.title.text = item.title
        holder.date.text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(item.createTime)
        holder.view.setOnClickListener{
            val intent=Intent(context,EditActivity::class.java)
            intent.putExtra("id",item.id)
            context.startActivity(intent)

        }
    }

    companion object {
        class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
            @BindView(R.id.title)
            lateinit var title:AppCompatTextView
            @BindView(R.id.date)
            lateinit var date:AppCompatTextView

            init {
                ButterKnife.bind(this,view)

            }
        }
    }
}