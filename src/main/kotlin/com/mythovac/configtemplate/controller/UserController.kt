package com.mythovac.configtemplate.controller

import com.mythovac.configtemplate.manager.SessionManager
import com.mythovac.configtemplate.service.UserService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

/**
 * 控制层
 * 登录管理 ，验证登录
 * */
@Controller("user-controller")
@RequestMapping("/user")
class UserController(private val userService: UserService) {

    /**
     * 登录验证函数
     * 输入完账号密码，点击登入后，发生数据至此
     * 1.获取数据（账号密码）
     * 2.解密数据
     * 3.验证是否满足长度限制
     * 4.转交UserService.signIn进一步验证
     *  4.1 查询数据库，按照uid查询
     *      4.1.1 用户存在，就加密验证，检测密码是否正确
     *      4.1.2用户不存在，就添加记录，密码加密纯储
     * 5.跳转页面
     *  5.1密码正确，跳转主页
     *  5.2密码不正确，跳会登录页面，提示错误消息
     * */
    @PostMapping("/sign-in")
    fun userSignIn(request: HttpServletRequest,model: Model): String {

        // 1.获取账号密码数据
        var uid: String? = request.getParameter("uid")
        var password: String? = request.getParameter("password")
        if(uid == null || password == null || uid == "" || password == "") {
            model.addAttribute("error", "用户名和密码不能为空")
            return "sign_in.html"
        }

//        println("uid: ${uid}")
//        println("password: ${password}")

        // 2.解码 这不是数据库加密，是传输过程的加密
        uid = xorEncryptDecrypt(uid,10086)
        password = xorEncryptDecrypt(password,10086)

//        println("uid: ${uid}")
//        println("password: ${password}")

        // 3.用户名密码长度验证
        if(uid.length<5 || uid.length > 23 || password.length<8 || password.length>29) {
            model.addAttribute("error", "用户名和密码长度应该在8~23之间")
            return "sign_in.html"
        }

        // 4.密码验证 或 创建账号
        if(userService.signIn(uid,password)) {

            // 检查用户权限，如果被封禁，就跳回登录界面，显示警告信息
            val grade: String = userService.findGradeByUid(uid)
            if(grade != "admin" && grade != "vip"){
                model.addAttribute("error","用户被封禁")
                return "sign_in.html"
            }


            // 登入成功。检查是否有异地同时在线，如果有，下线其他地方的Session
            if(SessionManager.containSession(uid)){
                SessionManager.invalidSession(uid)
            }

            // 创建Session会话，记录账号 uid 和权限 grade
            val session = request.getSession(true)
            session.setAttribute("uid", uid)
            session.setAttribute("grade",grade)

            // 记录当前session，方便断开
            SessionManager.addSession(uid,session)

            // 跳转至主页面
            return "redirect:/"
        }
        // 密码错误，跳转登录界面，显示错误信息
        model.addAttribute("error", "用户名或密码错误")
        return "sign_in.html"
    }

    /**
     * 简单的加密解密函数
     * 这是传输过程的加密，不是数据库纯储的
     * 采用简单异或加密
     * */
    private fun xorEncryptDecrypt(str: String, key: Int): String {
        var result:StringBuilder = StringBuilder();

        for (i in str) {
            // XOR 运算
            result.append((i.code xor key).toChar())
        }
        return result.toString();
    }

}