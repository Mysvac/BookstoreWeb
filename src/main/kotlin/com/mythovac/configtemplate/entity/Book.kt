package com.mythovac.configtemplate.entity

data class Book(
    var bookid: Long,
    var bookname: String,
    var booktype: String,
    var stock: Int,
    var price: Int,
    var sales: Int,
    var author : String,
    var profile: String,
    var available: Int
)
