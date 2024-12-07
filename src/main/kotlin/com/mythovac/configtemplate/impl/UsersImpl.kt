package com.mythovac.configtemplate.impl

import com.mythovac.configtemplate.dao.UsersDao
import com.mythovac.configtemplate.entity.Users
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
class UsersImpl(private val jdbcTemplate: JdbcTemplate) : UsersDao {
    // 映射，方便查询的结果存入List<Entity>
    private val rowMapper = RowMapper<Users> { rs,_ ->
        Users(
            uid = rs.getString("uid"),
            password = rs.getString("password"),
            grade = rs.getString("grade")
        )
    }

    // 查询全部用户数据（账号 密码 权限）
    override fun findAll(): List<Users> {
        val sql = "SELECT * FROM users"
        return jdbcTemplate.query(sql, rowMapper)
    }
    // 单个查询
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
    // 单个删除
    override fun deleteByUid(uid: String) {
        val sql = "DELETE FROM users WHERE uid = ?"
        jdbcTemplate.update(sql, uid)
    }
    // 单个插入
    override fun insert(users: Users) {
        val sql = "INSERT INTO users(uid, password, grade) VALUES (?, ?, ?)"
        jdbcTemplate.update(sql, users.uid, users.password, users.grade)
    }
    // 修改
    override fun update(users: Users) {
        val sql = "UPDATE users SET grade = ?, password = ? WHERE uid = ?"
        jdbcTemplate.update(sql, users.grade, users.password, users.uid)
    }
}