package com.mythovac.configtemplate.listener

import com.mythovac.configtemplate.manager.SessionManager
import jakarta.servlet.http.HttpSession
import jakarta.servlet.http.HttpSessionEvent
import jakarta.servlet.http.HttpSessionListener

/**
 * Session Listener
 * 监听会话的终止
 * 终止时，将会话从 SessionManager中移除
 * */
class MySessionListener : HttpSessionListener {

    override fun sessionDestroyed(se: HttpSessionEvent?) {
        // 会话销毁时执行的操作
        val session: HttpSession? = se?.session
        val uid = session?.getAttribute("uid") as String?
        println("Session destroyed for uid: $uid")

        // 假设这里需要从 SessionManager 移除会话
        uid?.let {
            SessionManager.deleteSession(it)
        }
        super.sessionDestroyed(se)
    }
}