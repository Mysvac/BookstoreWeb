package com.mythovac.configtemplate.service

import com.mythovac.configtemplate.entity.*
import com.mythovac.configtemplate.impl.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.crypto.password.PasswordEncoder

import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 数据库操作的服务层
 * */
@Service
class UserService(private val jdbcTemplate: JdbcTemplate, private val passwordEncoder: PasswordEncoder) {
    private var userProfileImpl: UserProfileImpl = UserProfileImpl(jdbcTemplate)
    private var usersImpl: UsersImpl = UsersImpl(jdbcTemplate)
    private var bookImpl: BookImpl = BookImpl(jdbcTemplate)
    private var cartImpl: CartImpl = CartImpl(jdbcTemplate)
    private var ordersImpl: OrdersImpl = OrdersImpl(jdbcTemplate)
    private var billImpl: BillImpl = BillImpl(jdbcTemplate)
    private var cartbookImpl: CartbookImpl = CartbookImpl(jdbcTemplate)

    /**
     * 注册普通账号
     * */
    fun signUp(uid: String, password: String): Boolean {
        if( usersImpl.findByUid(uid) == null ) {
            val newPassword = passwordEncoder.encode(password)
            usersImpl.insert(Users(uid = uid, password = newPassword, grade = "vip"))
            userProfileImpl.insert(UserProfile(uid,"secrecy","","","",""))
            return true
        }
        return false
    }
    /**
     * 登入
     * */
    fun signIn(uid: String, password: String): Boolean {
        val res: Users = usersImpl.findByUid(uid) ?: return signUp(uid, password)

        return passwordEncoder.matches(password, res.password)
    }
    fun findGradeByUid(uid: String): String{
        val res: Users? = usersImpl.findByUid(uid)
        return res?.grade ?: "banned"
    }
    /**
     * 设置用户权限
     * */
    fun setUserGrade(uid: String,grade: String): Boolean {
        if(grade != "admin" && grade != "vip" && grade != "banned") return false
        val res: Users = usersImpl.findByUid(uid) ?: return false
        usersImpl.update(Users(uid = uid, password = res.password, grade = grade))
        return true
    }
    /**
     * 查询用户
     * */
    fun findAllUsers(): List<Users> {
        return usersImpl.findAll()
    }
    fun findUsersByUid(uid: String): Users? {
        return usersImpl.findByUid(uid)
    }
    /**
     * 查询图书，加入和删除
     * */
    fun findCartbookByUid(uid: String): List<Cartbook> {
        return cartbookImpl.findCartbookByUid(uid)
    }
    fun findAllBook(): List<Book> {
        return bookImpl.findAll()
    }
    fun findBookByAttr(bookid: Long = -1, author: String = "鎿乸", booktype: String = "鎿乸", bookname: String = "鎿乸"): List<Book> {
        return bookImpl.findByAttr(bookid, author, booktype, bookname)
    }
    fun insertBook(book: Book) {
        bookImpl.insert(book)
    }
    fun setBookInfo(book: Book) {
        bookImpl.update(book)
    }
    fun findBillAndOrderByUid(uid : String): List<BillDetail>{
        val res: MutableList<BillDetail> = mutableListOf()
        val orders = ordersImpl.findByAttr(uid=uid)
        if(orders.isNotEmpty()){
            for(order in orders){
                val bookname = bookImpl.findByBookid(order.bookid)!!.bookname
                res.add(BillDetail(
                    billid = order.billid,
                    uid = order.uid,
                    bookid = order.bookid,
                    amount = order.amount,
                    status = order.status,
                    otime = order.otime,
                    sumprice = order.sumprice,
                    bookname = bookname
                ))
            }
        }
        val bills = billImpl.findByAttr(uid=uid)
        if(bills.isNotEmpty()){
            for (bill in bills){
                val bookname = bookImpl.findByBookid(bill.bookid)!!.bookname
                res.add(BillDetail(
                    billid = bill.billid,
                    uid = bill.uid,
                    bookid = bill.bookid,
                    amount = bill.amount,
                    status = bill.status,
                    otime = bill.otime,
                    sumprice = bill.sumprice,
                    bookname = bookname
                ))
            }
        }
        return res
    }

    /**
     * 删除相关
     * */
    fun deleteBook(bookid: Long): Boolean {
        // 有订单，不能删除
        if(billImpl.findByAttr(bookid=bookid).isNotEmpty() || ordersImpl.findByAttr(bookid=bookid).isNotEmpty()) {
            return false
        }
        bookImpl.deleteByBookid(bookid)
        return true
    }
    fun deleteUsers(uid: String): Boolean {
        // 存在订单，不能删除
        if(billImpl.findByAttr(uid = uid).isNotEmpty() || ordersImpl.findByAttr(uid = uid).isNotEmpty()) {
            return false
        }
        usersImpl.deleteByUid(uid)
        return true
    }
    fun deleteCart(uid: String,bookid: Long): Boolean {
        cartImpl.deleteByUidAndBookid(uid,bookid)
        return true
    }
    fun deleteCartByUid(uid: String): Boolean {
        cartImpl.deleteByUid(uid)
        return true
    }

    /**
     * 加入购物车，下单
     * */
    fun insertCart(uid: String, bookid: Long, amount: Int = -1) {
        val cart: Cart? = cartImpl.findByUidAndBookid(uid,bookid)
        if(cart == null) {
            cartImpl.insert(Cart(uid = uid, bookid = bookid, amount = 1))
            return
        }
        if(amount == -1){
            cartImpl.update(Cart(uid = uid, bookid = bookid, amount = cart.amount+1))
        }
        if(amount == 0 || amount<-1){
            deleteCart(uid,bookid)
        }
        if(amount > 0){
            cartImpl.update(Cart(uid = uid, bookid = bookid, amount = amount))
        }
    }
    fun insertOrders(orders: Orders) {
        ordersImpl.insert(orders)
    }
    fun setOrders(orders: Orders) {
        ordersImpl.update(orders)
    }
    fun insertOrdersByAttr(uid: String, bookid: Long, amount: Int = -1): String {
        if(amount<=0) return "购买数量异常"  // 数量错误
        val book = bookImpl.findByBookid(bookid) ?: return "书籍不存在" // 书籍不存在
        if(book.stock < amount) return "书籍数量不足" // 数量不足
        if(book.available == 0) return "书籍不可出售" // 书籍不可售

        book.stock -= amount
        bookImpl.update(book)
        val sumprice = book.price.toLong()*amount
        val time = getCurrentDateTime()
        val order = Orders(billid=0,uid=uid,bookid=bookid,amount = amount,otime = time,sumprice = sumprice,status = "ongoing")
        ordersImpl.insert(order)
        deleteCart(uid=uid,bookid=bookid)
        return "购买成功"
    }
    fun insertOneOrdersByAttr(uid: String, bookid: Long, amount: Int = -1): String {
        if(amount<=0) return "购买数量异常"  // 数量错误
        val book = bookImpl.findByBookid(bookid) ?: return "书籍不存在" // 书籍不存在
        if(book.stock < amount) return "数据数量不住" // 数量不足
        if(book.available == 0) return "书籍不可出售" // 书籍不可售

        book.stock -= amount
        bookImpl.update(book)
        val sumprice = book.price.toLong()*amount
        val time = getCurrentDateTime()
        val order = Orders(billid=0,uid=uid,bookid=bookid,amount = amount,otime = time,sumprice = sumprice,status = "ongoing")
        ordersImpl.insert(order)
        return "购买成功"
    }

    fun getCurrentDateTime(): String {
        val currentDateTime = LocalDateTime.now() // 获取当前时间
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") // 定义格式
        return currentDateTime.format(formatter) // 格式化时间
    }
}