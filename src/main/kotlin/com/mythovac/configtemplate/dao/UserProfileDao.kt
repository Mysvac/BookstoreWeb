package com.mythovac.configtemplate.dao

import com.mythovac.configtemplate.entity.UserProfile
import org.springframework.stereotype.Repository

/**
 * Dao 类
 * 仅接口
 * */
@Repository
interface UserProfileDao {
    fun findByUid(uid: String): UserProfile?
    fun update(userProfile: UserProfile)
    fun insert(userProfile: UserProfile)
}