package com.mythovac.configtemplate.dao

import com.mythovac.configtemplate.entity.Orders
import org.springframework.stereotype.Repository

@Repository
interface OrdersDao {
    fun findAll(): List<Orders>
    fun findByAttr(billid:Long = -1,uid: String = "-1", bookid: Long = -1): List<Orders>
    fun deleteByBillid(billid: Long)
    fun update(orders: Orders)
    fun insert(order: Orders)
}