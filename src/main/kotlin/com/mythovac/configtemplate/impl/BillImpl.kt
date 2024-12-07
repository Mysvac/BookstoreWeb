package com.mythovac.configtemplate.impl

import com.mythovac.configtemplate.dao.BillDao
import com.mythovac.configtemplate.entity.Bill
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

/**
 * Dao 类 的实现
 * 通过SQL语句和Entity
 * 直接实现数据库操作
 * */
@Repository
class BillImpl(private val jdbcTemplate: JdbcTemplate) : BillDao {
    // 映射，方便查询的结果存入List<Entity>
    private val rowMapper = RowMapper<Bill> { rs, _ ->
        Bill(
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
    override fun findAll(): List<Bill> {
        val sql = "SELECT * FROM bill"
        return jdbcTemplate.query(sql, rowMapper)
    }
    // 根据billid删除
    override fun deleteByBillid(billid: Long) {
        val sql = "DELETE FROM bill WHERE billid = ?"
        jdbcTemplate.update(sql, billid)
    }
    // 根据特征查询
    override fun findByAttr(billid: Long, uid: String, bookid: Long): List<Bill> {
        val sql = "SELECT * FROM bill WHERE billid = ? OR uid = ? OR bookid = ? ORDER BY billid DESC "
        return jdbcTemplate.query(sql, rowMapper, billid, uid, bookid)
    }
}