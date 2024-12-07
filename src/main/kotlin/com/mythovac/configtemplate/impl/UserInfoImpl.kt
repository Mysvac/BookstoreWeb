package com.mythovac.configtemplate.impl

import com.mythovac.configtemplate.entity.UserInfo
import com.mythovac.configtemplate.entity.Users
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
class UserInfoImpl(private val jdbcTemplate: JdbcTemplate) {
    private val usersImpl = UsersImpl(jdbcTemplate)
    private val userProfileImpl  = UserProfileImpl(jdbcTemplate)

    private val rowMapper = RowMapper<UserInfo> { rs, _ ->
        UserInfo(
            uid = rs.getString("uid"),
            grade = rs.getString("grade"),
            gender = rs.getString("gender"),
            address = rs.getString("address"),
            username = rs.getString("username"),
            email = rs.getString("email"),
            profile = rs.getString("profile")
        )
    }

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
}