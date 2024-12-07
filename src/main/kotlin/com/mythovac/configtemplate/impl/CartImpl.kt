package com.mythovac.configtemplate.impl

import com.mythovac.configtemplate.dao.CartDao
import com.mythovac.configtemplate.entity.Cart
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.SQLException

/**
 * Dao 类 的实现
 * 通过SQL语句和Entity
 * 直接实现数据库操作
 * */
@Repository
class CartImpl(private val jdbcTemplate: JdbcTemplate) : CartDao {
    // 映射，方便查询的结果存入List<Entity>
    private val rowMapper = RowMapper<Cart> { rs, _ ->
        Cart(
            uid = rs.getString("uid"),
            bookid = rs.getLong("bookid"),
            amount = rs.getInt("amount")
        )
    }

    // 根据 uid 或 bookid 查询列表(多本查询）
    override fun findByAttr(uid: String, bookid: Long): List<Cart> {
        val sql = "SELECT * FROM cart WHERE uid = ? OR bookid = ?"
        return jdbcTemplate.query(sql, rowMapper, uid, bookid)
    }

    // 单本查询
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
    // 删除
    override fun deleteByUid(uid: String) {
        val sql = "DELETE FROM cart WHERE uid = ?"
        jdbcTemplate.update(sql, uid)
    }
    // 删除
    override fun deleteByUidAndBookid(uid: String, bookid: Long) {
        val sql = "DELETE FROM cart WHERE uid = ? AND bookid = ?"
        jdbcTemplate.update(sql, uid, bookid)
    }
    // 修改
    override fun update(cart: Cart) {
        if(cart.amount <=0 ){
            deleteByUidAndBookid(cart.uid, cart.bookid)
            return
        }
        val sql = "UPDATE cart SET amount = ? WHERE uid = ? AND bookid = ?"
        jdbcTemplate.update(sql, cart.amount, cart.uid, cart.bookid)
    }
    // 插入
    override fun insert(cart: Cart) {
        val sql = "INSERT INTO cart(uid, bookid, amount) VALUES(?, ?, ?)"
        jdbcTemplate.update(sql, cart.uid, cart.bookid, cart.amount)
    }
}