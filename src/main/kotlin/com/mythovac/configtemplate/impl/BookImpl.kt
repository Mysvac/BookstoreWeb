package com.mythovac.configtemplate.impl

import com.mythovac.configtemplate.dao.BookDao
import com.mythovac.configtemplate.entity.Book
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository


@Repository
class BookImpl(private val jdbcTemplate: JdbcTemplate) : BookDao {
    private val rowMapper = RowMapper<Book> { rs, _ ->
        Book(
            bookid = rs.getLong("bookid"),
            booktype = rs.getString("booktype"),
            bookname = rs.getString("bookname"),
            stock = rs.getInt("stock"),
            price = rs.getInt("price"),
            sales = rs.getInt("sales"),
            author = rs.getString("author"),
            profile = rs.getString("profile"),
            available = rs.getInt("available")
        )
    }

    override fun findAll(): List<Book> {
        val sql = "SELECT * FROM book"
        return jdbcTemplate.query(sql, rowMapper)
    }

    override fun findAllAble(): List<Book> {
        val sql = "SELECT * FROM book WHERE available = 1"
        return jdbcTemplate.query(sql, rowMapper)
    }

    override fun findByBookid(bookid: Long): Book? {
        val sql = "SELECT * FROM book WHERE bookid = ?"
        try{
            val res = jdbcTemplate.queryForObject(sql, rowMapper, bookid)
            return res
        }
        catch(e: EmptyResultDataAccessException){
            return null
        }
    }

    override fun findByAttr(bookid: Long, author: String, booktype: String, bookname: String): List<Book> {
        val sql = "SELECT * FROM book WHERE bookid = ? OR author LIKE ? OR booktype LIKE ? OR bookname LIKE ?"
        return jdbcTemplate.query(sql, rowMapper, bookid, "%$author%", "%$booktype%", "%$bookname%")
    }

    override fun insert(book: Book) {
        val sql = "INSERT INTO book(booktype, bookname, stock, price, sales, author, profile, available) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
        jdbcTemplate.update(sql,
            book.booktype,
            book.bookname,
            book.stock,
            book.price,
            book.sales,
            book.author,
            book.profile,
            book.available)
    }
    override fun update(book: Book) {
        val sql = "UPDATE book SET booktype = ?, bookname = ?, stock = ?, price = ?, sales = ?, author = ?, profile = ?, available = ? WHERE bookid = ?"
        jdbcTemplate.update(sql,
            book.booktype,
            book.bookname,
            book.stock,
            book.price,
            book.sales,
            book.author,
            book.profile,
            book.available,
            book.bookid)
    }

    override fun deleteByBookid(bookid: Long) {
        val sql = "DELETE FROM book WHERE bookid = ?"
        jdbcTemplate.update(sql, bookid)
    }

}