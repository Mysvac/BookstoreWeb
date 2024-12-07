package com.mythovac.configtemplate

import com.mythovac.configtemplate.entity.Book
import com.mythovac.configtemplate.service.UserService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping



/**
 * Controller，实现了网站根目录的网页
 * */
@Controller("main-controller")
class MainController(private val userService: UserService) {
    @GetMapping("/")
    fun hello(request: HttpServletRequest, model: Model): String {
        // 模板model
        val session = request.getSession(false)
        if(session != null) {
            val uid: String = session.getAttribute("uid") as String
            val grade: String = session.getAttribute("grade") as String
            model.addAttribute("uid",uid)
            model.addAttribute("grade",grade)
        }

        val books: List<Book> = userService.findAllAbleBook()
        model.addAttribute("books",books)
        return "main_page.html"
    }

    // SET GLOBAL log_bin_trust_function_creators = 1;

}