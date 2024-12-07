package com.mythovac.configtemplate.entity

/**
 * Entity 实体类
 * Bill 账单信息
 * 对应数据表 bill
 * */
data class Bill(
    var billid: Long,
    var uid: String,
    var bookid: Long,
    var amount: Int,
    var status: String,
    var otime: String,
    var sumprice: Long
)
