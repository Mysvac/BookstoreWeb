package com.mythovac.configtemplate.entity

/**
 * Entity 实体类
 * BillDetail 账单细节信息
 * 整合数据，不直接对应数据表
 * 用于多表连接时的存储
 * */
data class BillDetail(
    var billid: Long,
    var uid: String,
    var bookid: Long,
    var bookname: String,
    var amount: Int,
    var status: String,
    var otime: String,
    var sumprice: Long
)
