package com.mythovac.configtemplate.entity

data class Bill(
    var billid: Long,
    var uid: String,
    var bookid: Long,
    var amount: Int,
    var status: String,
    var otime: String,
    var sumprice: Long
)
