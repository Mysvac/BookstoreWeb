package com.mythovac.configtemplate.entity

/**
 * Entity 实体类
 * Cartbook 购物车详情
 * 用于多表连接时的纯储
 * cart + book
 * */
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
