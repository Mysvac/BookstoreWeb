package com.mythovac.configtemplate.impl

import com.mythovac.configtemplate.dao.BookDao
import com.mythovac.configtemplate.entity.Book
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository


/**
 * Dao 类 的实现
 * 通过SQL语句和Entity
 * 直接实现数据库操作
 * */
@Repository
class BookImpl(private val jdbcTemplate: JdbcTemplate) : BookDao {
    // 映射，方便查询的结果存入List<Entity>
    private val rowMapper = RowMapper<Book> { rs, _ ->
        Book(
            bookid = rs.getLong("bookid"),
            booktype = rs.getString("booktype"),
            bookname = rs.getString("bookname"),
            stock = rs.getInt("stock"),
            price = rs.getInt("price"),
            sales = rs.getInt("sales"),
            author = rs.getString("author"),
            profile = rs.getString("profile")?:"无",
            available = rs.getInt("available")
        )
    }

    // 查询全部
    override fun findAll(): List<Book> {
        val sql = "SELECT * FROM book"
        return jdbcTemplate.query(sql, rowMapper)
    }

    // 查询全部可售的书籍
    override fun findAllAble(): List<Book> {
        val sql = "SELECT * FROM book WHERE available = 1"
        return jdbcTemplate.query(sql, rowMapper)
    }

    // 根据 bookid 查询书籍
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

    // 根据特征查询书籍，宽泛查询
    override fun findByAttr(bookid: Long, author: String, booktype: String, bookname: String): List<Book> {
        val sql = "SELECT * FROM book WHERE bookid = ? OR author LIKE ? OR booktype LIKE ? OR bookname LIKE ?"
        return jdbcTemplate.query(sql, rowMapper, bookid, "%$author%", "%$booktype%", "%$bookname%")
    }

    // 插入
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

    // 修改数据
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

    // 删除对应书籍
    override fun deleteByBookid(bookid: Long) {
        val sql = "DELETE FROM book WHERE bookid = ?"
        jdbcTemplate.update(sql, bookid)
    }

}