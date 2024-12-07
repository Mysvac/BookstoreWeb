package com.mythovac.configtemplate.dao

import com.mythovac.configtemplate.entity.Users
import org.springframework.stereotype.Repository

/**
 * Dao 类
 * 仅接口
 * */
@Repository
interface UsersDao {
    fun findAll(): List<Users>
    fun findByUid(uid: String): Users?
    fun insert(users: Users)
    fun update(users: Users)
    fun deleteByUid(uid: String)
}