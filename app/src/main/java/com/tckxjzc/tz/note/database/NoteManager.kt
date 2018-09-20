package com.tckxjzc.tz.note.database

import org.litepal.LitePal

class NoteManager(var size:Int=20,var page:Int=1) {
    var hasNext=true

    fun first():List<Note>{
        val list= LitePal
                .offset(0)
                .limit(size).order("id desc")
                .find(Note::class.java)
        hasNext=list.size>0
        return list
    }

    fun next():List<Note>{
        page++
        val list= LitePal
                .offset((page - 1)*size)
                .limit(size).order("id desc")
                .find(Note::class.java)
        hasNext=list.size>0
        return list
    }

    fun reset(){
        page=1
        hasNext=false
    }

}