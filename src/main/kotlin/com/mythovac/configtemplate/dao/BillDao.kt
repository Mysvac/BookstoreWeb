package com.mythovac.configtemplate.dao

import com.mythovac.configtemplate.entity.Bill
import org.springframework.stereotype.Repository

@Repository
interface BillDao {
    fun findAll(): List<Bill>
    fun findByAttr(billid:Long = -1,uid: String = "-1", bookid: Long = -1): List<Bill>
    fun deleteByBillid(billid: Long)
}