package com.mythovac.configtemplate.manager

import jakarta.servlet.http.HttpSession
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

/**
 * 单例，会话管理器
 * 用于强行终止指定uid的会话
 * */
object SessionManager {
    // 使用线程安全的 ConcurrentHashMap 存储会话
    private val sessionMap = ConcurrentHashMap<String, HttpSession>()

    fun containSession(uid: String): Boolean {
        return sessionMap.containsKey(uid)
    }

    // 添加会话到全局管理器
    fun addSession(uid: String, session: HttpSession) {
        sessionMap[uid] = session
    }

    // 移除会话
    fun invalidSession(uid: String) {
        sessionMap[uid]?.invalidate()
    }

    fun deleteSession(uid: String) {
        sessionMap.remove(uid)
    }
}