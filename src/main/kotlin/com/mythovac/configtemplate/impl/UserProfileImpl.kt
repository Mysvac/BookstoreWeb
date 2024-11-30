package com.mythovac.configtemplate.impl

import com.mythovac.configtemplate.dao.UserProfileDao
import com.mythovac.configtemplate.entity.UserProfile
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository


@Repository
class UserProfileImpl(private val jdbcTemplate: JdbcTemplate) : UserProfileDao {
    private val rowMapper = RowMapper<UserProfile> { rs, _ ->
        UserProfile(
            uid = rs.getString("uid"),
            gender = rs.getString("gender"),
            address = rs.getString("address"),
            username = rs.getString("username"),
            email = rs.getString("email"),
            profile = rs.getString("profile")
        )
    }

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