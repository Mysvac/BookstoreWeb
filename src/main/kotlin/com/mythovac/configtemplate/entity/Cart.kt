package com.mythovac.configtemplate.entity

/**
 * Entity 实体类
 * Cart 购物车
 * 对应数据表 cart
 * */
data class Cart(
    var uid: String,
    var bookid: Long,
    var amount: Int
)
