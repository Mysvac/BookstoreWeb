package com.mythovac.configtemplate.impl

import com.mythovac.configtemplate.dao.OrdersDao
import com.mythovac.configtemplate.entity.Orders
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
class OrdersImpl(private val jdbcTemplate: JdbcTemplate) : OrdersDao {
    private val rowMapper = RowMapper<Orders> { rs, _ ->
        Orders(
            billid = rs.getLong("billid"),
            uid = rs.getString("uid"),
            bookid = rs.getLong("bookid"),
            amount = rs.getInt("amount"),
            status = rs.getString("status"),
            otime = rs.getString("otime"),
            sumprice = rs.getLong("sumprice"),
        )
    }

    override fun findAll(): List<Orders>{
        val sql = "SELECT * FROM orders WHERE status = 'ongoing'"
        return jdbcTemplate.query(sql, rowMapper)
    }

    override fun clearStatus() {
        val sql = "DELETE FROM orders WHERE status != 'ongoing'"
        jdbcTemplate.update(sql)
    }

    override fun findByAttr(billid: Long, uid: String, bookid: Long): List<Orders> {
        val sql = "SELECT * FROM orders WHERE ((billid = ? OR uid = ? OR bookid = ?) AND status = 'ongoing')"
        return jdbcTemplate.query(sql, rowMapper, billid, uid, bookid)
    }

    override fun update(orders: Orders) {
        val sql = "UPDATE orders SET status = ? WHERE billid = ?"
        jdbcTemplate.update(sql,orders.status,orders.billid)
    }
    override fun insert(order: Orders) {
        val sql = "INSERT INTO orders (uid, bookid, amount, status,otime,sumprice) VALUES (?, ?, ?, ?, ?, ?)"
        jdbcTemplate.update(sql,order.uid,order.bookid,order.amount,order.status,order.otime,order.sumprice)
    }

    override fun deleteByBillid(billid: Long) {
        val sql = "DELETE FROM orders WHERE billid = ?"
        jdbcTemplate.update(sql,billid)
    }


}