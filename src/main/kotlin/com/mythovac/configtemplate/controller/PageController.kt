package com.mythovac.configtemplate.controller



import com.mythovac.configtemplate.entity.*
import com.mythovac.configtemplate.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping


/**
 * 控制层
 * 页面控制Controller
 * */
@Controller("page-controller")
@RequestMapping("/page")
class PageController(private val userService: UserService) {
    /**
     * 登入界面
     * */
    @GetMapping("/sign-in")
    fun signInPage(request: HttpServletRequest): String {
        // 进入登录界面时，退出当前账号
        val session: HttpSession = request.getSession(false) ?: return "sign_in.html"
        session.invalidate()

        // 转发至html文件
        return "sign_in.html"
    }

    /**
     * 主界面 ，即商品界面
     * 商品界面无需登录也可查看
     * 但是按钮点击无效（无法加入购物车和购买，会提示需要登录）
     * */
    @GetMapping("/main")
    fun main(request: HttpServletRequest, model: Model): String {
        // 获取Session，也就是检测是否登录
        val session = request.getSession(false)
        if(session != null) {
            val uid: String = session.getAttribute("uid") as String
            val grade: String = session.getAttribute("grade") as String
            model.addAttribute("uid",uid)
            model.addAttribute("grade",grade)
        }

        // 获取图书列表，从数据库
        val books: List<Book> = userService.findAllAbleBook()
        model.addAttribute("books",books)

        // 转发至html文件
        return "main_page.html"
    }

    /**
     * 查询界面
     * 无需登录也可查看
     * 但是按钮点击无效（无法加入购物车和购买，会提示需要登录）
     * */
    @GetMapping("/search")
    fun search(request: HttpServletRequest, model: Model): String {
        // 获取Session，也就是检测是否登录
        val session = request.getSession(false)
        if(session != null) {
            val uid: String = session.getAttribute("uid") as String
            val grade: String = session.getAttribute("grade") as String
            model.addAttribute("uid",uid)
            model.addAttribute("grade",grade)
        }

        // 获取查询的参数
        val info: String? = request.getParameter("info")
        if(info == null) return "redirect:/page/main"

        // 根据参数查询书籍，从数据库
        val books: List<Book> = userService.findBookByAttr(author = info, bookname = info, booktype = info)
        model.addAttribute("books",books)

        // 转发至html文件
        return "main_page.html"
    }

    /**
     * 商品详情页
     * 不登录也可以查看
     * 但是按钮点击无效（无法加入购物车和购买，会提示需要登录）
     * */
    @GetMapping("/info")
    fun info(request: HttpServletRequest, model: Model): String{
        // 获取Session，也就是检测是否登录
        val session = request.getSession(false)
        if(session != null) {
            val uid: String = session.getAttribute("uid") as String
            val grade: String = session.getAttribute("grade") as String
            model.addAttribute("uid",uid)
            model.addAttribute("grade",grade)
        }

        // 从参数获取图书编号，并且从数据库获取对应图书信息
        val bookid: Long = request.getParameter("bookid")?.toLong() ?: return "redirect:/page/main"
        val book: Book = userService.findBookByAttr(bookid = bookid).firstOrNull() ?: return "redirect:/page/main"
        model.addAttribute("book",book)

        // 转发至html文件
        return "info_page.html"
    }

    /**
     * 购物车界面
     * 必须登录
     * */
    @GetMapping("/cart")
    fun cart(request: HttpServletRequest, model: Model): String{
        // 获取Session，也就是检测是否登录，不登录则跳转至登录界面
        val session = request.getSession(false) ?: return "redirect:/page/sign-in"
        val uid: String = session.getAttribute("uid") as String
        val grade: String = session.getAttribute("grade") as String
        model.addAttribute("uid",uid)
        model.addAttribute("grade",grade)

        // 获取当前用户的购物车数据，从数据库中查询
        val cartbooks: List<Cartbook> = userService.findCartbookByUid(uid)
        model.addAttribute("cartbooks",cartbooks)

        // 转发至html文件
        return "cart_page.html"
    }


    /**
     * 用户自身订单界面
     * 必须登录
     * 包含进行中的订单，和已经结束/完成的账单
     * */
    @GetMapping("/bill")
    fun bill(request: HttpServletRequest, model: Model): String{
        // 获取Session，也就是检测是否登录，不登录则跳转至登录界面
        val session = request.getSession(false) ?: return "redirect:/page/sign-in"
        val uid: String = session.getAttribute("uid") as String
        val grade: String = session.getAttribute("grade") as String
        model.addAttribute("uid",uid)
        model.addAttribute("grade",grade)

        // 获取当前用户的订单/账单列表，从数据库
        val bills: List<BillDetail> = userService.findBillAndOrderByUid(uid)
        model.addAttribute("bills",bills)

        // 转发至html文件
        return "bill_page.html"
    }


    /**
     * 商品分类界面
     * 不登录也可以查看
     * */
    @GetMapping("/classify")
    fun classifyPage(request: HttpServletRequest, model: Model): String{
        // 获取Session，也就是检测是否登录
        val session = request.getSession(false)
        if(session != null) {
            val uid: String = session.getAttribute("uid") as String
            val grade: String = session.getAttribute("grade") as String
            model.addAttribute("uid",uid)
            model.addAttribute("grade",grade)
        }

        // 转发至html文件
        return "classify_page.html"
    }


    /**
     * 个人信息界面
     * 必须登录
     * */
    @GetMapping("/profile")
    fun profilePage(request: HttpServletRequest, model: Model): String{
        // 验证是否登录，未登录则跳转至登录界面
        val session = request.getSession(false) ?: return "redirect:/page/sign-in"
        val uid: String = session.getAttribute("uid") as String
        val grade: String = session.getAttribute("grade") as String
        model.addAttribute("uid",uid)
        model.addAttribute("grade",grade)

        // 获取个人信息，从数据库查询
        val userInfo = userService.findUserInfoByUid(uid)
        model.addAttribute("userInfo",userInfo)

        // 转发至html文件
        return "profile_page.html"
    }

    /**
     * 管理员界面
     * 修改和查看他人的个人信息
     * */
    @GetMapping("/user-profile")
    fun userProfilePage(request: HttpServletRequest, model: Model): String{
        // 验证权限
        val session = request.getSession(false) ?: return "redirect:/page/sign-in"
        val uid: String = session.getAttribute("uid") as String
        val grade: String = session.getAttribute("grade") as String
        if(grade!="admin"){ return "redirect:/" }
        model.addAttribute("uid",uid)
        model.addAttribute("grade",grade)

        // 获取目标个人信息界面
        val goal: String = request.getParameter("uid")
        val userInfo = userService.findUserInfoByUid(goal)
        model.addAttribute("userInfo",userInfo)

        return "profile_page.html"
    }

    /**
     * 管理员管理面板
     * 默认查看账单
     * 必须登录，而且要求有admin权限
     * */
    @GetMapping("/management")
    fun managementPage(request: HttpServletRequest, model: Model): String{
        // 检测是否登录并持有权限
        val session = request.getSession(false) ?: return "redirect:/page/sign-in"
        val uid: String = session.getAttribute("uid") as String
        val grade: String = session.getAttribute("grade") as String
        model.addAttribute("uid",uid)
        model.addAttribute("grade",grade)
        if(grade != "admin") return "redirect:/"

        // 加载总账单列表，从数据库
        val bills: List<BillDetail>  = userService.findAllOrdersAndBill()

        // 计算各类金额
        var sumMoney: Long = 0
        var finishMoney: Long = 0
        var goingMoney: Long = 0
        for(bill: BillDetail in bills) {
            sumMoney += bill.sumprice
            if(bill.status=="finish"){
                finishMoney += bill.sumprice
            }
            if(bill.status=="ongoing"){
                goingMoney += bill.sumprice
            }
        }
        model.addAttribute("bills",bills)
        model.addAttribute("sumMoney",sumMoney)
        model.addAttribute("finishMoney",finishMoney)
        model.addAttribute("goingMoney",goingMoney)

        // 转发至html文件
        return "management_page.html"
    }


    /**
     * 管理员管理面板
     * 处理进行中的账单
     * 必须登录并具有admin权限
     * */
    @GetMapping("/manage-orders")
    fun manageOrdersPage(request: HttpServletRequest, model: Model): String{
        // 验证登录与权限
        val session = request.getSession(false) ?: return "redirect:/page/sign-in"
        val uid: String = session.getAttribute("uid") as String
        val grade: String = session.getAttribute("grade") as String
        model.addAttribute("uid",uid)
        model.addAttribute("grade",grade)
        if(grade != "admin") return "redirect:/"

        // 获取进行中的订单信息，从数据库
        val orders: List<BillDetail>  = userService.findAllOrders()
        model.addAttribute("orders",orders)

        // 转发至html文件
        return "orders_manage_page.html"
    }


    /**
     * 管理员管理面板
     * 修改图书信息
     * 必须登录并具有admin权限
     * */
    @GetMapping("/manage-books")
    fun manageBooksPage(request: HttpServletRequest, model: Model): String{
        // 验证登录与权限
        val session = request.getSession(false) ?: return "redirect:/page/sign-in"
        val uid: String = session.getAttribute("uid") as String
        val grade: String = session.getAttribute("grade") as String
        model.addAttribute("uid",uid)
        model.addAttribute("grade",grade)
        if(grade != "admin") return "redirect:/"

        // 查询图书信息表，从数据库
        val books: List<Book>  = userService.findAllBook()
        model.addAttribute("books",books)

        // 转发至html文件
        return "books_manage_page.html"
    }

    /**
     * 管理员管理面板
     * 用户管理界面
     * 必须登录并具有admin权限
     * */
    @GetMapping("/manage-users")
    fun manageUsersPage(request: HttpServletRequest, model: Model): String{
        // 验证登录与权限
        val session = request.getSession(false) ?: return "redirect:/page/sign-in"
        val uid: String = session.getAttribute("uid") as String
        val grade: String = session.getAttribute("grade") as String
        model.addAttribute("uid",uid)
        model.addAttribute("grade",grade)
        if(grade != "admin") return "redirect:/"

        // 查询用户信息，从数据库
        val userInfos: List<UserInfo>  = userService.findAllUserInfos()
        model.addAttribute("userInfos",userInfos)

        // 转发至html文件
        return "users_manage_page.html"
    }
}