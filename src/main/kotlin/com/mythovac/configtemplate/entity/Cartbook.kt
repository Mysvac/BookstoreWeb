package com.mythovac.configtemplate.entity

data class Cartbook(
    var uid: String,
    var bookid: Long,
    var amount: Int,
    var stock: Int,
    var bookname: String,
    var price: Int,
    var author : String,
    var available: Int
)
