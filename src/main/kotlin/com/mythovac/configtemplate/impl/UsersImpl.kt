package com.mythovac.configtemplate.impl

import com.mythovac.configtemplate.dao.UsersDao
import com.mythovac.configtemplate.entity.Users
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.SQLException

@Repository
class UsersImpl(private val jdbcTemplate: JdbcTemplate) : UsersDao {

    private val rowMapper = RowMapper<Users> { rs,_ ->
        Users(
            uid = rs.getString("uid"),
            password = rs.getString("password"),
            grade = rs.getString("grade")
        )
    }

    override fun findAll(): List<Users> {
        val sql = "SELECT * FROM users"
        return jdbcTemplate.query(sql, rowMapper)
    }
    override fun findByUid(uid: String): Users? {
        val sql = "SELECT * FROM users WHERE uid = ?"
        try{
            val res = jdbcTemplate.queryForObject(sql, rowMapper, uid)
            return res
        }
        catch(e: EmptyResultDataAccessException){
            return null
        }
    }


    override fun deleteByUid(uid: String) {
        val sql = "DELETE FROM users WHERE uid = ?"
        jdbcTemplate.update(sql, uid)
    }
    override fun insert(users: Users) {
        val sql = "INSERT INTO users(uid, password, grade) VALUES (?, ?, ?)"
        jdbcTemplate.update(sql, users.uid, users.password, users.grade)
    }
    override fun update(users: Users) {
        val sql = "UPDATE users SET grade = ?, password = ? WHERE uid = ?"
        jdbcTemplate.update(sql, users.grade, users.password, users.uid)
    }
}