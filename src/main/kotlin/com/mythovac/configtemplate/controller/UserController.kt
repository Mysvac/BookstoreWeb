package com.mythovac.configtemplate.controller

import com.mythovac.configtemplate.service.UserService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

@Controller("user-controller")
@RequestMapping("/user")
class UserController(private val userService: UserService) {

    @RequestMapping("/sign-in")
    fun userSignIn(request: HttpServletRequest,model: Model): String {
        val uid: String? = request.getParameter("uid")
        val password: String? = request.getParameter("password")
        if(uid == null || password == null || uid == "" || password == "") {
            model.addAttribute("error", "用户名和密码不能为空")
            return "sign_in.html"
        }
        if(uid.length<5 || uid.length > 23 || password.length<8 || password.length>29) {
            model.addAttribute("error", "用户名和密码长度应该在8~23之间")
            return "sign_in.html"
        }
        if(userService.signIn(uid,password)) {
            val session = request.getSession(true);
            session.setAttribute("uid", uid)
            println(uid)
            return "redirect:/"
        }
        model.addAttribute("error", "用户名或密码错误")
        return "sign_in.html"
    }
}