package com.mythovac.configtemplate

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping


/**
 * Controller，实现了网站根目录的网页
 * */
@Controller("main-controller")
class MainController {
    @GetMapping("/")
    fun hello(request: HttpServletRequest, model: Model): String {
        // 模板model
        val session = request.getSession(false)
        if (session != null) { model.addAttribute(session.getAttribute("uid")) }
        return "main_page.html"
    }
}