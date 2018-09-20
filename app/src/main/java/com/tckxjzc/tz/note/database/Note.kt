package com.tckxjzc.tz.note.database

import org.litepal.crud.LitePalSupport
import java.util.*

class Note() : LitePalSupport() {
    lateinit  var title:String
    lateinit  var content: String
    constructor( title: String,  content: String) : this() {
        this.title=title
        this.content=content
    }

    var createTime: Date = Date()
    var id:Int?=null

}