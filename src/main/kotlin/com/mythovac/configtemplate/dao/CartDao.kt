package com.mythovac.configtemplate.dao

import com.mythovac.configtemplate.entity.Cart
import org.springframework.stereotype.Repository

@Repository
interface CartDao {
    fun insert(cart: Cart)
    fun update(cart: Cart)
    fun deleteByUid(uid: String)
    fun deleteByUidAndBookid(uid: String, bookid: Long)
    fun findByUidAndBookid(uid: String, bookid: Long): Cart?
    fun findByAttr(uid: String = "-1", bookid: Long = -1): List<Cart>
}