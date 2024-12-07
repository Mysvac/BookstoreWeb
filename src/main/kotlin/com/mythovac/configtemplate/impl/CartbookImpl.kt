package com.mythovac.configtemplate.impl

import com.mythovac.configtemplate.dao.CartbookDao
import com.mythovac.configtemplate.entity.Cartbook
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper

/**
 * Dao 类 的实现
 * 通过SQL语句和Entity
 * 直接实现数据库操作
 * */
class CartbookImpl(private val jdbcTemplate: JdbcTemplate) : CartbookDao  {
    // 映射，方便查询的结果存入List<Entity>
    private val rowMapper = RowMapper<Cartbook> { rs, _ ->
        Cartbook(
            uid = rs.getString("uid"),
            bookid = rs.getLong("bookid"),
            stock = rs.getInt("stock"),
            amount = rs.getInt("amount"),
            bookname = rs.getString("bookname"),
            price = rs.getInt("price"),
            author = rs.getString("author"),
            available = rs.getInt("available")
        )
    }

    // 根据uid查询对应用户的购物车内容
    override fun findCartbookByUid(uid: String): List<Cartbook> {
        val sql = """
            SELECT uid,cart.bookid AS bookid,stock,amount,bookname,price,author,available FROM cart
            JOIN book ON cart.bookid = book.bookid
            WHERE uid = ?
        """
        return jdbcTemplate.query(sql, rowMapper, uid)
    }
}