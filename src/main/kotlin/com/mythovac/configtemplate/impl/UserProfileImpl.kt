package com.mythovac.configtemplate.impl

import com.mythovac.configtemplate.dao.UserProfileDao
import com.mythovac.configtemplate.entity.UserProfile
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
class UserProfileImpl(private val jdbcTemplate: JdbcTemplate) : UserProfileDao {
    // 映射，方便查询的结果存入List<Entity>
    private val rowMapper = RowMapper<UserProfile> { rs, _ ->
        UserProfile(
            uid = rs.getString("uid"),
            gender = rs.getString("gender"),
            address = rs.getString("address")?:"无",
            username = rs.getString("username")?:"无",
            email = rs.getString("email")?:"无",
            profile = rs.getString("profile")?:"无"
        )
    }

    // 查询单用户个性化数据
    override fun findByUid(uid: String): UserProfile? {
        val sql = "SELECT * FROM userProfile WHERE uid = ?"
        try{
            val res = jdbcTemplate.queryForObject(sql, rowMapper, uid)
            return res
        }
        catch(e: EmptyResultDataAccessException){
            return null
        }
    }

    // 插入
    override fun insert(userProfile: UserProfile){
        val sql = "INSERT INTO userProfile(uid, gender, address, username, email, profile) VALUES (?, ?, ?, ?, ?, ?)"
        jdbcTemplate.update(sql,
            userProfile.uid,
            userProfile.gender,
            userProfile.address,
            userProfile.username,
            userProfile.email,
            userProfile.profile
        )
    }

    // 修改
    override fun update(userProfile: UserProfile) {
        val sql = "UPDATE userProfile SET gender = ? , address = ? , username = ? ,email = ? , profile = ? WHERE uid = ?"
        jdbcTemplate.update(sql,
            userProfile.gender,
            userProfile.address,
            userProfile.username,
            userProfile.email,
            userProfile.profile,
            userProfile.uid)
    }

}