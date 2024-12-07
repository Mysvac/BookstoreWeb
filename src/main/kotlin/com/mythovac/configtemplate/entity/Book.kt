package com.mythovac.configtemplate.entity

/**
 * Entity 实体类
 * Book 书籍信息
 * 对应数据表 book
 * */
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
