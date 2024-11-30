package com.mythovac.configtemplate.controller

import ch.qos.logback.core.model.Model
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller("page-controller")
@RequestMapping("/page")
class PageController {
    @RequestMapping("/sign-in")
    fun signInPage(request: HttpServletRequest): String {
        val session: HttpSession = request.getSession(false) ?: return "sign_in.html"
        session.invalidate()
        return "sign_in.html"
    }
}