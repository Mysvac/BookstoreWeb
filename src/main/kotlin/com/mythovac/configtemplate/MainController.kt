package com.mythovac.configtemplate

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.time.LocalDateTime


/**
 * Controller，实现了网站根目录的网页
 * */
@Controller("main-controller")
class MainController {
    @GetMapping("/")
    fun hello(request: HttpServletRequest, model: Model): String {
        val current = LocalDateTime.now().toString()
        // 模板model
        model.addAttribute("time", current)
        return "main_page.html"
    }
}