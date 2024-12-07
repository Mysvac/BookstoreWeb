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
        val session: HttpSession = request.getSession(false) ?: return "sign_in.html"
        session.invalidate()
        return "sign_in.html"
    }

    /**
     * 主界面 商品界面
     * */
    @GetMapping("/main")
    fun main(request: HttpServletRequest, model: Model): String {
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

    /**
     * 查询界面
     * */
    @GetMapping("/search")
    fun search(request: HttpServletRequest, model: Model): String {
        // 模板model
        val session = request.getSession(false)
        if(session != null) {
            val uid: String = session.getAttribute("uid") as String
            val grade: String = session.getAttribute("grade") as String
            model.addAttribute("uid",uid)
            model.addAttribute("grade",grade)
        }
        val info: String? = request.getParameter("info")
        if(info == null) return "redirect:/page/main"
        val books: List<Book> = userService.findBookByAttr(author = info, bookname = info, booktype = info)
        model.addAttribute("books",books)

        return "main_page.html"
    }

    /**
     * 商品详情页
     * */
    @GetMapping("/info")
    fun info(request: HttpServletRequest, model: Model): String{
        // 模板model
        val session = request.getSession(false)
        if(session != null) {
            val uid: String = session.getAttribute("uid") as String
            val grade: String = session.getAttribute("grade") as String
            model.addAttribute("uid",uid)
            model.addAttribute("grade",grade)
        }


        val bookid: Long = request.getParameter("bookid")?.toLong() ?: return "redirect:/page/main"
        val book: Book = userService.findBookByAttr(bookid = bookid).firstOrNull() ?: return "redirect:/page/main"
        model.addAttribute("book",book)

        return "info_page.html"
    }

    /**
     * 购物车界面
     * */
    @GetMapping("/cart")
    fun cart(request: HttpServletRequest, model: Model): String{
        // 模板model
        val session = request.getSession(false) ?: return "redirect:/page/sign-in"
        val uid: String = session.getAttribute("uid") as String
        val grade: String = session.getAttribute("grade") as String
        model.addAttribute("uid",uid)
        model.addAttribute("grade",grade)

        val cartbooks: List<Cartbook> = userService.findCartbookByUid(uid)
        model.addAttribute("cartbooks",cartbooks)

        return "cart_page.html"
    }


    /**
     * 用户自身订单界面
     * */
    @GetMapping("/bill")
    fun bill(request: HttpServletRequest, model: Model): String{
        // 模板model
        val session = request.getSession(false) ?: return "redirect:/page/sign-in"
        val uid: String = session.getAttribute("uid") as String
        val grade: String = session.getAttribute("grade") as String
        model.addAttribute("uid",uid)
        model.addAttribute("grade",grade)

        val bills: List<BillDetail> = userService.findBillAndOrderByUid(uid)

        model.addAttribute("bills",bills)

        return "bill_page.html"
    }


    /**
     * 商品分类
     * */
    @GetMapping("/classify")
    fun classifyPage(request: HttpServletRequest, model: Model): String{
        // 模板model
        val session = request.getSession(false)
        if(session != null) {
            val uid: String = session.getAttribute("uid") as String
            val grade: String = session.getAttribute("grade") as String
            model.addAttribute("uid",uid)
            model.addAttribute("grade",grade)
        }

        return "classify_page.html"
    }


    /**
     * 个人信息界面
     * */
    @GetMapping("/profile")
    fun profilePage(request: HttpServletRequest, model: Model): String{
        val session = request.getSession(false) ?: return "redirect:/page/sign-in"
        val uid: String = session.getAttribute("uid") as String
        val grade: String = session.getAttribute("grade") as String
        model.addAttribute("uid",uid)
        model.addAttribute("grade",grade)

        val userInfo = userService.findUserInfoByUid(uid)
        model.addAttribute("userInfo",userInfo)

        return "profile_page.html"
    }


    /**
     * 管理员管理面板
     * 默认查看账单
     * */
    @GetMapping("/management")
    fun managementPage(request: HttpServletRequest, model: Model): String{
        val session = request.getSession(false) ?: return "redirect:/page/sign-in"
        val uid: String = session.getAttribute("uid") as String
        val grade: String = session.getAttribute("grade") as String
        model.addAttribute("uid",uid)
        model.addAttribute("grade",grade)
        if(grade != "admin") return "redirect:/"

        val bills: List<BillDetail>  = userService.findAllOrdersAndBill()

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

        return "management_page.html"
    }


    /**
     * 管理员管理面板
     * 处理进行中的账单
     * */
    @GetMapping("/manage-orders")
    fun manageOrdersPage(request: HttpServletRequest, model: Model): String{
        val session = request.getSession(false) ?: return "redirect:/page/sign-in"
        val uid: String = session.getAttribute("uid") as String
        val grade: String = session.getAttribute("grade") as String
        model.addAttribute("uid",uid)
        model.addAttribute("grade",grade)
        if(grade != "admin") return "redirect:/"

        val orders: List<BillDetail>  = userService.findAllOrders()

        model.addAttribute("orders",orders)

        return "orders_manage_page.html"
    }


    /**
     * 管理员管理面板
     * 处理进行中的账单
     * */
    @GetMapping("/manage-books")
    fun manageBooksPage(request: HttpServletRequest, model: Model): String{
        val session = request.getSession(false) ?: return "redirect:/page/sign-in"
        val uid: String = session.getAttribute("uid") as String
        val grade: String = session.getAttribute("grade") as String
        model.addAttribute("uid",uid)
        model.addAttribute("grade",grade)
        if(grade != "admin") return "redirect:/"



        val books: List<Book>  = userService.findAllBook()

        model.addAttribute("books",books)



        return "books_manage_page.html"
    }

    /**
     * 管理员管理面板
     * 处理进行中的账单
     * */
    @GetMapping("/manage-users")
    fun manageUsersPage(request: HttpServletRequest, model: Model): String{
        val session = request.getSession(false) ?: return "redirect:/page/sign-in"
        val uid: String = session.getAttribute("uid") as String
        val grade: String = session.getAttribute("grade") as String
        model.addAttribute("uid",uid)
        model.addAttribute("grade",grade)
        if(grade != "admin") return "redirect:/"


        val userInfos: List<UserInfo>  = userService.findAllUserInfos()
        model.addAttribute("userInfos",userInfos)

        return "users_manage_page.html"
    }
}