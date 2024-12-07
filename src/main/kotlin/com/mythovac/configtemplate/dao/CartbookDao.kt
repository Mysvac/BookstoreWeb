package com.mythovac.configtemplate.dao

import com.mythovac.configtemplate.entity.Cartbook
import org.springframework.stereotype.Repository

/**
 * Dao 类
 * 仅接口
 * */
@Repository
interface CartbookDao {
    fun findCartbookByUid(uid: String): List<Cartbook>
}