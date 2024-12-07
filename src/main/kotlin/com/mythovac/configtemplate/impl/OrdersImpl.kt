package com.mythovac.configtemplate.impl

import com.mythovac.configtemplate.dao.OrdersDao
import com.mythovac.configtemplate.entity.Orders
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

/**
 * Dao 类 的实现
 * 通过SQL语句和Entity
 * 直接实现数据库操作
 * */
@Repository
class OrdersImpl(private val jdbcTemplate: JdbcTemplate) : OrdersDao {
    // 映射，方便查询的结果存入List<Entity>
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

    // 查询全部
    override fun findAll(): List<Orders>{
        val sql = "SELECT * FROM orders WHERE status = 'ongoing'"
        return jdbcTemplate.query(sql, rowMapper)
    }

    // 清空已经完成的订单
    override fun clearStatus() {
        val sql = "DELETE FROM orders WHERE status != 'ongoing'"
        jdbcTemplate.update(sql)
    }

    // 特征查询
    override fun findByAttr(billid: Long, uid: String, bookid: Long): List<Orders> {
        val sql = "SELECT * FROM orders WHERE ((billid = ? OR uid = ? OR bookid = ?) AND status = 'ongoing')"
        return jdbcTemplate.query(sql, rowMapper, billid, uid, bookid)
    }

    // 修改
    override fun update(orders: Orders) {
        val sql = "UPDATE orders SET status = ? WHERE billid = ?"
        jdbcTemplate.update(sql,orders.status,orders.billid)
    }

    // 插入
    override fun insert(order: Orders) {
        val sql = "INSERT INTO orders (uid, bookid, amount, status,otime,sumprice) VALUES (?, ?, ?, ?, ?, ?)"
        jdbcTemplate.update(sql,order.uid,order.bookid,order.amount,order.status,order.otime,order.sumprice)
    }

    // 删除
    override fun deleteByBillid(billid: Long) {
        val sql = "DELETE FROM orders WHERE billid = ?"
        jdbcTemplate.update(sql,billid)
    }


}