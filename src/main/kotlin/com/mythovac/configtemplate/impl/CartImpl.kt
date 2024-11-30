package com.mythovac.configtemplate.impl

import com.mythovac.configtemplate.dao.CartDao
import com.mythovac.configtemplate.entity.Cart
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.SQLException

@Repository
class CartImpl(private val jdbcTemplate: JdbcTemplate) : CartDao {
    private val rowMapper = RowMapper<Cart> { rs, _ ->
        Cart(
            uid = rs.getString("uid"),
            bookid = rs.getLong("bookid"),
            amount = rs.getInt("amount")
        )
    }


    override fun findByAttr(uid: String, bookid: Long): List<Cart> {
        val sql = "SELECT * FROM cart WHERE uid = ? OR bookid = ?"
        return jdbcTemplate.query(sql, rowMapper, uid, bookid)
    }

    override fun findByUidAndBookid(uid: String, bookid: Long): Cart? {
        val sql = "SELECT * FROM cart WHERE  uid = ? AND bookid = ?"
        try{
            val res = jdbcTemplate.queryForObject(sql, rowMapper, uid, bookid)
            return res
        }
        catch(e: EmptyResultDataAccessException){
            return null
        }
    }
    override fun deleteByUid(uid: String) {
        val sql = "DELETE FROM cart WHERE uid = ?"
        jdbcTemplate.update(sql, uid)
    }

    override fun deleteByUidAndBookid(uid: String, bookid: Long) {
        val sql = "DELETE FROM cart WHERE uid = ? AND bookid = ?"
        jdbcTemplate.update(sql, uid, bookid)
    }

    override fun update(cart: Cart) {
        if(cart.amount <=0 ){
            deleteByUidAndBookid(cart.uid, cart.bookid)
            return
        }
        val sql = "UPDATE cart SET amount = ? WHERE uid = ? AND bookid = ?"
        jdbcTemplate.update(sql, cart.amount, cart.uid, cart.bookid)
    }
    override fun insert(cart: Cart) {
        val sql = "INSERT INTO cart(uid, bookid, amount) VALUES(?, ?, ?)"
        jdbcTemplate.update(sql, cart.uid, cart.bookid, cart.amount)
    }
}