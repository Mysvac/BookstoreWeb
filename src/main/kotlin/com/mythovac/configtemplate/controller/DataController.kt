package com.mythovac.configtemplate.controller


import com.mythovac.configtemplate.entity.Orders
import com.mythovac.configtemplate.entity.UserProfile
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

    @PostMapping("/buy-one-book")
    fun buyOneBook(request: HttpServletRequest):ResponseEntity<Map<String, String>?> {
        val session = request.getSession(false)?:return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(mapOf("message" to "用户未登录"))

        val uid = session.getAttribute("uid") as String
        val bookid = request.getParameter("bookid")?.toLong() ?:return ResponseEntity.badRequest()
            .body(mapOf("message" to "书籍ID不能为空"))
        val amount = request.getParameter("amount")?.toInt() ?:return ResponseEntity.badRequest()
            .body(mapOf("message" to "购买数量不能为空"))

        val msg: String = userService.insertOneOrdersByAttr(uid = uid,bookid=bookid, amount = amount)

        if(msg == "购买成功") return ResponseEntity.ok(mapOf("message" to msg))
        return ResponseEntity.badRequest().body(mapOf("message" to msg))
    }

    @PostMapping("/buy-book")
    fun buyBook(request: HttpServletRequest):ResponseEntity<Map<String, String>?> {
        val session = request.getSession(false)?:return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(mapOf("message" to "用户未登录"))

        val uid = session.getAttribute("uid") as String
        val bookid = request.getParameter("bookid")?.toLong() ?:return ResponseEntity.badRequest()
            .body(mapOf("message" to "书籍ID不能为空"))
        val amount = request.getParameter("amount")?.toInt() ?:return ResponseEntity.badRequest()
            .body(mapOf("message" to "购买数量不能为空"))

        val msg: String = userService.insertOrdersByAttr(uid = uid,bookid=bookid, amount = amount)

        if(msg == "购买成功") return ResponseEntity.ok(mapOf("message" to msg))
        return ResponseEntity.badRequest().body(mapOf("message" to msg))
    }

    @PostMapping("/buy-all")
    fun buyAllBook(request: HttpServletRequest):ResponseEntity<Map<String, String>?> {
        val session = request.getSession(false)?:return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(mapOf("message" to "用户未登录"))

        val sure: String = request.getParameter("sure") ?:return ResponseEntity.badRequest()
            .body(mapOf("message" to "无确认码"))
        if(sure!="1") return ResponseEntity.badRequest().body(mapOf("message" to "无确认码"))

        val uid = session.getAttribute("uid") as String

        val cartbookList =  userService.findCartbookByUid(uid)

        for(cartbook in cartbookList){
            if(cartbook.amount>cartbook.stock){
                return ResponseEntity.badRequest()
                    .body(mapOf("message" to "有书籍库存不足，请检查"))
            }
            if(cartbook.available!=1){
                return ResponseEntity.badRequest()
                    .body(mapOf("message" to "有书籍已停售，请检查"))
            }
        }

        for(cartbook in cartbookList) {
            userService.insertOrdersByAttr(uid = uid,bookid=cartbook.bookid, amount = cartbook.amount)
        }

        return ResponseEntity.ok(mapOf("message" to "已全部购买"))
    }

    @RequestMapping("/del-all-cart")
    fun delAllCartBook(request: HttpServletRequest):ResponseEntity<Map<String, String>?> {
        val session = request.getSession(false)?:return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(mapOf("message" to "用户未登录"))

        val sure: String = request.getParameter("sure") ?:return ResponseEntity.badRequest()
            .body(mapOf("message" to "无确认码"))
        if(sure!="1") return ResponseEntity.badRequest().body(mapOf("message" to "无确认码"))

        val uid = session.getAttribute("uid") as String
        userService.deleteCartByUid(uid)

        return ResponseEntity.ok(mapOf("message" to "清空完成"))
    }

    @RequestMapping("/del-un-cart")
    fun delBookUn(request: HttpServletRequest):ResponseEntity<Map<String, String>?> {
        val session = request.getSession(false) ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(mapOf("message" to "用户未登录"))

        val sure: String = request.getParameter("sure") ?:return ResponseEntity.badRequest()
            .body(mapOf("message" to "无确认码"))
        if(sure!="1") return ResponseEntity.badRequest().body(mapOf("message" to "无确认码"))

        val uid = session.getAttribute("uid") as String

        val cartbookList =  userService.findCartbookByUid(uid)

        for(cartbook in cartbookList){
            if(cartbook.available!=1){
                userService.deleteCart(uid=uid,bookid=cartbook.bookid)
            }
        }

        return ResponseEntity.ok(mapOf("message" to "清空完成"))
    }


    @PostMapping("/bill-suspend")
    fun billSuspend(request: HttpServletRequest):ResponseEntity<Map<String, String>?> {
        val session = request.getSession(false) ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(mapOf("message" to "用户未登录"))


        val uid: String = session.getAttribute("uid") as String
        val billidStr: String = request.getParameter("billid") ?: return ResponseEntity.badRequest().body(mapOf("message" to "billid为空"))

        val billid: Long = billidStr.toLong()

        val orders: Orders = userService.findOrderByBillid(billid) ?: return ResponseEntity.badRequest().body(mapOf("message" to "书籍信息错误"))
        if(orders.uid != uid) return ResponseEntity.badRequest().body(mapOf("message" to "你没有权限修改别人的书"))
        if(orders.status!="ongoing") return ResponseEntity.badRequest().body(mapOf("message" to "书籍已处理"))

        orders.status = "suspend"
        userService.setOrders(orders)

        userService.deleteOrdersByBillid(billid)

        return ResponseEntity.ok(mapOf("message" to "处理成功"))
    }

    @PostMapping("/bill-finish")
    fun billFinished(request: HttpServletRequest):ResponseEntity<Map<String, String>?> {
        val session = request.getSession(false)?:return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(mapOf("message" to "用户未登录"))

        val uid: String = session.getAttribute("uid") as String
        val billidStr: String = request.getParameter("billid") ?: return ResponseEntity.badRequest().body(mapOf("message" to "书籍信息错误"))

        val billid: Long = billidStr.toLong()

        val orders: Orders = userService.findOrderByBillid(billid) ?: return ResponseEntity.badRequest().body(mapOf("message" to "书籍信息错误"))
        if(orders.uid != uid) return ResponseEntity.badRequest().body(mapOf("message" to "你没有权限修改别人的书"))
        if(orders.status!="ongoing") return ResponseEntity.badRequest().body(mapOf("message" to "书籍已处理"))

        orders.status = "finish"
        userService.setOrders(orders)

        return ResponseEntity.ok(mapOf("message" to "处理成功"))
    }

    @PostMapping("/change-userinfo")
    fun changeUserInfo(request: HttpServletRequest):String {
        val session = request.getSession(false)?: return "redirect:/page/sign-in"
        val uid = session.getAttribute("uid") as String

        val username = request.getParameter("username")?.take(23) ?:return "redirect:/page/profile"
        val email = request.getParameter("email")?.take(45) ?: ""
        val address = request.getParameter("address")?.take(45) ?: ""
        val gender = request.getParameter("gender") ?: "secrecy"
        if(gender != "male" && gender != "female" && gender != "secrecy") {
            return "redirect:/page/profile"
        }
        val profile = request.getParameter("profile")?.take(47) ?: ""
        val userProfile = UserProfile(
            uid=uid,
            username=username,
            email=email,
            address=address,
            gender=gender,
            profile=profile
        )
        userService.setUserProfile(userProfile)

        return "redirect:/page/profile"
    }
}