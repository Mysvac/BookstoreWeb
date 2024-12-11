package com.mythovac.configtemplate.impl

import com.mythovac.configtemplate.entity.UserInfo
import com.mythovac.configtemplate.entity.Users
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

/**
 * Dao 类 的实现
 * 通过SQL语句和Entity
 * 直接实现数据库操作
 * */
@Repository
class UserInfoImpl(private val jdbcTemplate: JdbcTemplate) {

    // 映射，方便查询的结果存入List<Entity>
    private val rowMapper = RowMapper<UserInfo> { rs, _ ->
        UserInfo(
            uid = rs.getString("uid"),
            grade = rs.getString("grade"),
            gender = rs.getString("gender"),
            address = rs.getString("address")?:"无",
            username = rs.getString("username")?:"无",
            email = rs.getString("email")?:"无",
            profile = rs.getString("profile")?:"无"
        )
    }

    // 查询所以用户的详细信息
    fun findAllUserIndo(): List<UserInfo> {
        val sql = """
            SELECT 
            users.uid AS uid,
            grade,
            gender,
            address,
            username,
            email,
            profile
            FROM users 
            JOIN userProfile 
            ON users.uid = userProfile.uid
            """
        return jdbcTemplate.query(sql, rowMapper)
    }

    // 查询根据特征查询用户的详细信息
    fun findUserIndoByAttr(uid: String, username: String): List<UserInfo> {
        val sql = """
            SELECT 
            users.uid AS uid,
            grade,
            gender,
            address,
            username,
            email,
            profile
            FROM users 
            JOIN userProfile 
            ON users.uid = userProfile.uid
            WHERE userProfile.uid LIKE ? OR username LIKE ?
            """
        return jdbcTemplate.query(sql, rowMapper, "%$uid%", "%$username%")
    }
}