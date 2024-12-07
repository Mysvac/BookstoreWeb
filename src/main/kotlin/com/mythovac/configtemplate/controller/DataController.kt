package com.mythovac.configtemplate.controller


import com.mythovac.configtemplate.entity.Book
import com.mythovac.configtemplate.entity.Orders
import com.mythovac.configtemplate.entity.UserProfile
import com.mythovac.configtemplate.manager.SessionManager
import com.mythovac.configtemplate.service.UserService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

/**
 * 控制层
 * 数据处理Controller
 * 响应各种Post提交操作
 * 登录除外
 * */
@Controller("data-controller")
@RequestMapping("/data")
class DataController(private val userService: UserService) {

    /**
     * 加入购物车
     * 每次点一下加一本
     * */
    @PostMapping("/insert-cart")
    fun insertCart(request: HttpServletRequest): ResponseEntity<Map<String, String>?> {
        // 要求登录
        val session = request.getSession(false) ?: return ResponseEntity.status(
            HttpStatus.UNAUTHORIZED
        )
            .body(mapOf("message" to "用户未登录"))

        // 获取用户和要加入的图书编号
        val uid = session.getAttribute("uid") as String
        val bookid =
            request.getParameter("bookid") ?: return ResponseEntity.badRequest()
                .body(mapOf("message" to "书籍ID不能为空"))

        // 加入
        userService.insertCart(uid, bookid.toLong())

        return ResponseEntity.ok(mapOf("message" to "书籍已加入购物车"))
    }

    /**
     * 修改购物车内某商品的数量
     * */
    @PostMapping("/cart-amount")
    fun cartAmount(request: HttpServletRequest): String {
        // 要求登录
        val session = request.getSession(false) ?: return "redirect:/page/cart"

        // 获取用户 书籍编号 以及数量
        val uid = session.getAttribute("uid") as String
        val bookid = request.getParameter("bookid")?.toLong()
            ?: return "redirect:/page/cart"
        var amount = request.getParameter("amount")?.toInt()
            ?: return "redirect:/page/cart"
        if (amount < 0) amount = 0

        // 修改数量
        userService.insertCart(uid = uid, bookid = bookid, amount = amount)

        return "redirect:/page/cart"
    }

    /**
     * 直接单本书，不影响购物车
     * */
    @PostMapping("/buy-one-book")
    fun buyOneBook(request: HttpServletRequest): ResponseEntity<Map<String, String>?> {
        // 要求登录
        val session = request.getSession(false) ?: return ResponseEntity.status(
            HttpStatus.UNAUTHORIZED
        )
            .body(mapOf("message" to "用户未登录"))

        // 获取用户 书籍编号
        val uid = session.getAttribute("uid") as String
        val bookid = request.getParameter("bookid")?.toLong()
            ?: return ResponseEntity.badRequest()
                .body(mapOf("message" to "书籍ID不能为空"))

        // 尝试购买（内部验证数量 可售性等内容）
        val msg: String = userService.insertOneOrdersByAttr(
            uid = uid,
            bookid = bookid,
            amount = 1
        )

        if (msg == "购买成功") return ResponseEntity.ok(mapOf("message" to msg))
        return ResponseEntity.badRequest().body(mapOf("message" to msg))
    }

    /**
     * 通过购物车买书，删除购物车内对应内容
     * 可一次性购买多本图书
     * */
    @PostMapping("/buy-book")
    fun buyBook(request: HttpServletRequest): ResponseEntity<Map<String, String>?> {
        // 要求登录
        val session = request.getSession(false) ?: return ResponseEntity.status(
            HttpStatus.UNAUTHORIZED
        )
            .body(mapOf("message" to "用户未登录"))

        // 获取用户 书籍编号 以及购买数量
        val uid = session.getAttribute("uid") as String
        val bookid = request.getParameter("bookid")?.toLong()
            ?: return ResponseEntity.badRequest()
                .body(mapOf("message" to "书籍ID不能为空"))
        val amount = request.getParameter("amount")?.toInt()
            ?: return ResponseEntity.badRequest()
                .body(mapOf("message" to "购买数量不能为空"))

        // 尝试购买（内部验证数量 可售性等内容）
        val msg: String = userService.insertOrdersByAttr(
            uid = uid,
            bookid = bookid,
            amount = amount
        )

        if (msg == "购买成功") return ResponseEntity.ok(mapOf("message" to msg))
        return ResponseEntity.badRequest().body(mapOf("message" to msg))
    }

    /**
     * 通过购物车，够买全部
     * */
    @PostMapping("/buy-all")
    fun buyAllBook(request: HttpServletRequest): ResponseEntity<Map<String, String>?> {
        // 要求登录
        val session = request.getSession(false) ?: return ResponseEntity.status(
            HttpStatus.UNAUTHORIZED
        )
            .body(mapOf("message" to "用户未登录"))

        // 增加确认码
        val sure: String =
            request.getParameter("sure") ?: return ResponseEntity.badRequest()
                .body(mapOf("message" to "无确认码"))
        if (sure != "1") return ResponseEntity.badRequest()
            .body(mapOf("message" to "无确认码"))

        // 获取用户数据
        val uid = session.getAttribute("uid") as String

        // 获取用户购物车数据
        val cartbookList = userService.findCartbookByUid(uid)

        // 检测购物车内的书是否都能买
        for (cartbook in cartbookList) {
            if (cartbook.amount > cartbook.stock) {
                return ResponseEntity.badRequest()
                    .body(mapOf("message" to "有书籍库存不足，请检查"))
            }
            if (cartbook.available != 1) {
                return ResponseEntity.badRequest()
                    .body(mapOf("message" to "有书籍已停售，请检查"))
            }
        }

        // 批量购买
        for (cartbook in cartbookList) {
            userService.insertOrdersByAttr(
                uid = uid,
                bookid = cartbook.bookid,
                amount = cartbook.amount
            )
        }

        return ResponseEntity.ok(mapOf("message" to "已全部购买"))
    }

    /**
     * 购物车，清空
     * */
    @RequestMapping("/del-all-cart")
    fun delAllCartBook(request: HttpServletRequest): ResponseEntity<Map<String, String>?> {
        // 要求登录
        val session = request.getSession(false) ?: return ResponseEntity.status(
            HttpStatus.UNAUTHORIZED
        )
            .body(mapOf("message" to "用户未登录"))

        // 获取确认码，也就是要通过点击按钮
        val sure: String =
            request.getParameter("sure") ?: return ResponseEntity.badRequest()
                .body(mapOf("message" to "无确认码"))
        if (sure != "1") return ResponseEntity.badRequest()
            .body(mapOf("message" to "无确认码"))

        // 获取用户id
        val uid = session.getAttribute("uid") as String

        // 清空购物车
        userService.deleteCartByUid(uid)

        return ResponseEntity.ok(mapOf("message" to "清空完成"))
    }

    /**
     * 购物车，批量删除停售的书籍
     * */
    @RequestMapping("/del-un-cart")
    fun delBookUn(request: HttpServletRequest): ResponseEntity<Map<String, String>?> {
        // 要求登录
        val session = request.getSession(false) ?: return ResponseEntity.status(
            HttpStatus.UNAUTHORIZED
        )
            .body(mapOf("message" to "用户未登录"))

        // 确认码
        val sure: String =
            request.getParameter("sure") ?: return ResponseEntity.badRequest()
                .body(mapOf("message" to "无确认码"))
        if (sure != "1") return ResponseEntity.badRequest()
            .body(mapOf("message" to "无确认码"))

        // 获取用户uid
        val uid = session.getAttribute("uid") as String
        // 获取购物车数据
        val cartbookList = userService.findCartbookByUid(uid)

        // 删除停售的书籍，也就是 available==0 的
        for (cartbook in cartbookList) {
            if (cartbook.available != 1) {
                userService.deleteCart(uid = uid, bookid = cartbook.bookid)
            }
        }

        return ResponseEntity.ok(mapOf("message" to "清空完成"))
    }


    /**
     * orders订单，
     * 终止订单，中断
     * */
    @PostMapping("/bill-suspend")
    fun billSuspend(request: HttpServletRequest): ResponseEntity<Map<String, String>?> {
        // 需要登录
        val session = request.getSession(false) ?: return ResponseEntity.status(
            HttpStatus.UNAUTHORIZED
        )
            .body(mapOf("message" to "用户未登录"))

        // 获取订单和个人信息
        val uid: String = session.getAttribute("uid") as String
        val grade: String = session.getAttribute("grade") as String
        val billidStr: String =
            request.getParameter("billid") ?: return ResponseEntity.badRequest()
                .body(mapOf("message" to "billid为空"))

        val billid: Long = billidStr.toLong()

        val orders: Orders = userService.findOrderByBillid(billid)
            ?: return ResponseEntity.badRequest()
                .body(mapOf("message" to "书籍信息错误"))

        // 权限要求，已经必须是自己的书籍，只能修改进行中的订单
        if (orders.uid != uid && grade != "admin") return ResponseEntity.badRequest()
            .body(mapOf("message" to "你没有权限修改别人的书"))
        if (orders.status != "ongoing") return ResponseEntity.badRequest()
            .body(mapOf("message" to "书籍已处理"))

        // 修改
        orders.status = "suspend"
        userService.setOrders(orders)

        return ResponseEntity.ok(mapOf("message" to "处理成功"))
    }

    /**
     * orders订单，设置为完成
     * */
    @PostMapping("/bill-finish")
    fun billFinished(request: HttpServletRequest): ResponseEntity<Map<String, String>?> {
        // 需要登录
        val session = request.getSession(false) ?: return ResponseEntity.status(
            HttpStatus.UNAUTHORIZED
        )
            .body(mapOf("message" to "用户未登录"))

        // 获取订单和个人信息
        val uid: String = session.getAttribute("uid") as String
        val grade: String = session.getAttribute("grade") as String
        val billidStr: String =
            request.getParameter("billid") ?: return ResponseEntity.badRequest()
                .body(mapOf("message" to "书籍信息错误"))

        val billid: Long = billidStr.toLong()

        // 权限要求，已经必须是自己的书籍，只能修改进行中的订单
        val orders: Orders = userService.findOrderByBillid(billid)
            ?: return ResponseEntity.badRequest()
                .body(mapOf("message" to "书籍信息错误"))
        if (orders.uid != uid && grade != "admin") return ResponseEntity.badRequest()
            .body(mapOf("message" to "你没有权限修改别人的书"))
        if (orders.status != "ongoing") return ResponseEntity.badRequest()
            .body(mapOf("message" to "书籍已处理"))

        // 修改
        orders.status = "finish"
        userService.setOrders(orders)

        return ResponseEntity.ok(mapOf("message" to "处理成功"))
    }

    /**
     * 修改用户个性化数据
     * */
    @PostMapping("/change-userinfo")
    fun changeUserInfo(request: HttpServletRequest): String {
        // 要求登录
        val session =
            request.getSession(false) ?: return "redirect:/page/sign-in"

        // 获取uid
        val uid = session.getAttribute("uid") as String

        // 获取提交的个人数据
        val username = request.getParameter("username")?.take(23)
            ?: return "redirect:/page/profile"
        val email = request.getParameter("email")?.take(45) ?: ""
        val address = request.getParameter("address")?.take(45) ?: ""
        val gender = request.getParameter("gender") ?: "secrecy"
        if (gender != "male" && gender != "female" && gender != "secrecy") {
            return "redirect:/page/profile"
        }
        val profile = request.getParameter("profile")?.take(47) ?: ""

        // 数据放入实体类中
        val userProfile = UserProfile(
            uid = uid,
            username = username,
            email = email,
            address = address,
            gender = gender,
            profile = profile
        )
        // 数据库修改
        userService.setUserProfile(userProfile)

        return "redirect:/page/profile"
    }

    /**
     * 修改图书价格和库存
     * */
    @PostMapping("/book-update")
    fun updateBook(request: HttpServletRequest): ResponseEntity<Map<String, String>?> {
        // 要求登录
        val session = request.getSession(false) ?: return ResponseEntity.status(
            HttpStatus.UNAUTHORIZED
        )
            .body(mapOf("message" to "用户未登录"))

        // 获取用户和权限，要求admin权限
        val uid: String = session.getAttribute("uid") as String
        val grade: String = session.getAttribute("grade") as String
        if (grade != "admin") return ResponseEntity.badRequest()
            .body(mapOf("message" to "你没有权限"))

        // 获取书籍信息
        val bookid: Long = request.getParameter("bookid").toLong()
        val price: Int = request.getParameter("price").toInt()
        val stock: Int = request.getParameter("stock").toInt()

        // 修改
        val book: Book =
            userService.findBookByAttr(bookid = bookid).firstOrNull()
                ?: return ResponseEntity.badRequest()
                    .body(mapOf("message" to "图书不存在"))
        book.stock = stock
        book.price = price
        userService.setBookInfo(book)

        return ResponseEntity.ok(mapOf("message" to "处理成功"))
    }

    /**
     * 修改图书状态图书
     * */
    @PostMapping("/book-able")
    fun ableBook(request: HttpServletRequest): ResponseEntity<Map<String, String>?> {
        // 要求登录
        val session = request.getSession(false) ?: return ResponseEntity.status(
            HttpStatus.UNAUTHORIZED
        )
            .body(mapOf("message" to "用户未登录"))

        // 获取用户和权限，要求admin权限
        val uid: String = session.getAttribute("uid") as String
        val grade: String = session.getAttribute("grade") as String
        if (grade != "admin") return ResponseEntity.badRequest()
            .body(mapOf("message" to "你没有权限"))

        // 获取书籍信息
        val bookid: Long = request.getParameter("bookid").toLong()

        // 修改
        val book: Book =
            userService.findBookByAttr(bookid = bookid).firstOrNull()
                ?: return ResponseEntity.badRequest()
                    .body(mapOf("message" to "图书不存在"))
        book.available = 1 - book.available
        userService.setBookInfo(book)

        return ResponseEntity.ok(mapOf("message" to "处理成功"))
    }

    /**
     * 删除书籍
     * */
    @PostMapping("/book-delete")
    fun deleteBook(request: HttpServletRequest): ResponseEntity<Map<String, String>?> {
        // 要求登录
        val session = request.getSession(false) ?: return ResponseEntity.status(
            HttpStatus.UNAUTHORIZED
        )
            .body(mapOf("message" to "用户未登录"))

        // 获取用户和权限，要求admin权限
        val uid: String = session.getAttribute("uid") as String
        val grade: String = session.getAttribute("grade") as String
        if (grade != "admin") return ResponseEntity.badRequest()
            .body(mapOf("message" to "你没有权限"))

        // 获取书籍信息
        val bookid: Long = request.getParameter("bookid").toLong()

        // 修改
        val res: Boolean = userService.deleteBook(bookid)
        return if (res) {
            ResponseEntity.ok(mapOf("message" to "处理成功"))
        } else {
            ResponseEntity.badRequest()
                .body(mapOf("message" to "图书存在订单/账单，不能删除，只能停售"))
        }
    }

    /**
     * 修改用户权限
     * */
    @PostMapping("/user-grade-change")
    fun changeUserGrade(request: HttpServletRequest): ResponseEntity<Map<String, String>?> {
        // 要求登录
        val session = request.getSession(false) ?: return ResponseEntity.status(
            HttpStatus.UNAUTHORIZED
        )
            .body(mapOf("message" to "用户未登录"))

        // 查询权限
        val uid: String = session.getAttribute("uid") as String
        val grade: String = session.getAttribute("grade") as String
        if (grade != "admin") return ResponseEntity.badRequest()
            .body(mapOf("message" to "你没有权限"))

        // 获取目标
        val goal: String = request.getParameter("uid") as String
        val newGrade: String = request.getParameter("grade") as String

        // 目标合法性检查
        if (goal.isEmpty()) return ResponseEntity.badRequest()
            .body(mapOf("message" to "缺少对象"))
        if (goal == uid) return ResponseEntity.badRequest()
            .body(mapOf("message" to "你不能修改自己的权限"))

        // 修改
        val res = userService.setUserGrade(goal, newGrade)

        // 强制下线，要求其重新登录（如今已登录）
        SessionManager.invalidSession(goal)

        return if (res) {
            ResponseEntity.ok(mapOf("message" to "处理成功"))
        } else {
            ResponseEntity.badRequest()
                .body(mapOf("message" to "对象不存在或目标值错误"))
        }

    }

    /**
     * 删除用户
     * */
    @PostMapping("/user-delete")
    fun deleteUser(request: HttpServletRequest): ResponseEntity<Map<String, String>?> {
        // 要求登录
        val session = request.getSession(false) ?: return ResponseEntity.status(
            HttpStatus.UNAUTHORIZED
        )
            .body(mapOf("message" to "用户未登录"))

        // 查询权限
        val uid: String = session.getAttribute("uid") as String
        val grade: String = session.getAttribute("grade") as String
        if (grade != "admin") return ResponseEntity.badRequest()
            .body(mapOf("message" to "你没有权限"))

        // 获取目标
        val goal: String = request.getParameter("uid") as String
        if (goal.isEmpty()) return ResponseEntity.badRequest()
            .body(mapOf("message" to "缺少对象"))
        if (goal == uid) return ResponseEntity.badRequest()
            .body(mapOf("message" to "你不能删除自己"))

        // 目标合法性检查
        val user = userService.findUsersByUid(goal)
            ?: return ResponseEntity.badRequest()
                .body(mapOf("message" to "目标不存在"))
        if (user.grade == "admin") {
            return ResponseEntity.badRequest()
                .body(mapOf("message" to "不能直接删除admin用户"))
        }

        // 修改
        val res = userService.deleteUsers(goal)
        return if (res) {
            // 断开对应用户的记录，如果存在
            SessionManager.invalidSession(goal)
            ResponseEntity.ok(mapOf("message" to "处理成功"))
        } else {
            ResponseEntity.badRequest()
                .body(mapOf("message" to "此用户存在购买记录，不能删除，只能禁用"))
        }
    }

}

