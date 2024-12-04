package com.mythovac.configtemplate.entity

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
