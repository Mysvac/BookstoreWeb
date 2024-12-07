package com.mythovac.configtemplate.dao

import com.mythovac.configtemplate.entity.Book
import org.springframework.stereotype.Repository

/**
 * Dao 类
 * 仅接口
 * */
@Repository
interface BookDao {
    fun findAll(): List<Book>
    fun findAllAble(): List<Book>
    fun findByBookid(bookid: Long): Book?
    fun findByAttr(bookid: Long = -1, author: String = "鎿", booktype: String = "鎿", bookname: String = "鎿") : List<Book>
    fun insert(book: Book)
    fun update(book: Book)
    fun deleteByBookid(bookid: Long)
}