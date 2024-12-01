package com.mythovac.configtemplate.controller

import com.mythovac.configtemplate.service.UserService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller("data-controller")
@RequestMapping("/data")
class DataController(private val userService: UserService){

    @PostMapping("/insert-cart")
    fun insertCart(request: HttpServletRequest): ResponseEntity<Map<String, String>?> {

        val session = request.getSession(false)?:return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(mapOf("message" to "用户未登录"))

        val uid = session.getAttribute("uid") as String
        val bookid = request.getParameter("bookid")?:return ResponseEntity.badRequest()
            .body(mapOf("message" to "书籍ID不能为空"))

        userService.insertCart(uid,bookid.toLong())
        return ResponseEntity.ok(mapOf("message" to "书籍已加入购物车"))
    }

    @PostMapping("/cart-amount")
    fun cartAmount(request: HttpServletRequest):String {

        val session = request.getSession(false)?:return "redirect:/page/cart"

        val uid = session.getAttribute("uid") as String
        val bookid = request.getParameter("bookid")?.toLong() ?:return "redirect:/page/cart"
        var amount = request.getParameter("amount")?.toInt()?:return "redirect:/page/cart"
        if(amount<0) amount = 0
        userService.insertCart(uid=uid, bookid = bookid, amount = amount)
        return "redirect:/page/cart"
    }
}