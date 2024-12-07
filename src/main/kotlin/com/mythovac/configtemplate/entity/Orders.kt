package com.mythovac.configtemplate.entity

/**
 * Entity 实体类
 * Orders 订单信息
 * 对应数据表 orders
 * */
data class Orders(
    var billid: Long,
    var uid: String,
    var bookid: Long,
    var amount: Int,
    var status: String,
    var otime: String,
    var sumprice: Long
)
