package com.mythovac.configtemplate.controller


import com.mythovac.configtemplate.entity.Book
import com.mythovac.configtemplate.entity.Cartbook
import com.mythovac.configtemplate.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller("page-controller")
@RequestMapping("/page")
class PageController(private val userService: UserService) {
    @GetMapping("/sign-in")
    fun signInPage(request: HttpServletRequest): String {
        val session: HttpSession = request.getSession(false) ?: return "sign_in.html"
        session.invalidate()
        return "sign_in.html"
    }

    @GetMapping("/main")
    fun main(request: HttpServletRequest, model: Model): String {
        // 模板model
        val session = request.getSession(false) ?: return "redirect:/page/sign-in"
        model.addAttribute("uid",session.getAttribute("uid"))
        val books: List<Book> = userService.findAllBook()
        model.addAttribute("books",books)
        return "main_page.html"
    }

    @GetMapping("/search")
    fun search(request: HttpServletRequest, model: Model): String {
        // 模板model
        val session = request.getSession(false) ?: return "redirect:/page/sign-in"
        model.addAttribute("uid",session.getAttribute("uid"))
        val info: String? = request.getParameter("info")
        if(info == null) return "redirect:/page/main"
        val books: List<Book> = userService.findBookByAttr(author = info, bookname = info, booktype = info)
        model.addAttribute("books",books)
        return "main_page.html"
    }

    @GetMapping("/info")
    fun info(request: HttpServletRequest, model: Model): String{
        // 模板model
        val session = request.getSession(false) ?: return "redirect:/page/sign-in"
        model.addAttribute("uid",session.getAttribute("uid"))
        val bookid: Long = request.getParameter("bookid")?.toLong() ?: return "redirect:/page/main"

        val book: Book = userService.findBookByAttr(bookid = bookid).firstOrNull() ?: return "redirect:/page/main"
        model.addAttribute("book",book)
        return "info_page.html"
    }

    @GetMapping("/cart")
    fun cart(request: HttpServletRequest, model: Model): String{
        // 模板model
        val session = request.getSession(false) ?: return "redirect:/page/sign-in"
        model.addAttribute("uid",session.getAttribute("uid"))
        val uid: String = session.getAttribute("uid") as String
        val cartbooks: List<Cartbook> = userService.findCartbookByUid(uid)

        model.addAttribute("cartbooks",cartbooks)
        return "cart_page.html"
    }
}