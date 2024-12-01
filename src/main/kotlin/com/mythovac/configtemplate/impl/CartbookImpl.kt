package com.mythovac.configtemplate.impl

import com.mythovac.configtemplate.dao.CartbookDao
import com.mythovac.configtemplate.entity.Cartbook
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper

class CartbookImpl(private val jdbcTemplate: JdbcTemplate) : CartbookDao  {
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
    override fun findCartbookByUid(uid: String): List<Cartbook> {
        val sql = """
            SELECT uid,cart.bookid AS bookid,stock,amount,bookname,price,author,available FROM cart
            JOIN book ON cart.bookid = book.bookid
            WHERE uid = ?
        """
        return jdbcTemplate.query(sql, rowMapper, uid)
    }
}