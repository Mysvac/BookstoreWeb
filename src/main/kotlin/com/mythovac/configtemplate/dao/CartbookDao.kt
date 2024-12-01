package com.mythovac.configtemplate.dao

import com.mythovac.configtemplate.entity.Cartbook
import org.springframework.stereotype.Repository

@Repository
interface CartbookDao {
    fun findCartbookByUid(uid: String): List<Cartbook>
}