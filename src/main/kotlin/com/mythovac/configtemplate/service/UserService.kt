package com.mythovac.configtemplate.service

import com.mythovac.configtemplate.entity.*
import com.mythovac.configtemplate.impl.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.crypto.password.PasswordEncoder

import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 服务层
 * 数据库操作的二次封装
 * 实现了控制层需要的方法
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
    private var userInfoImpl: UserInfoImpl = UserInfoImpl(jdbcTemplate)

    /**
     * 注册普通账号
     * */
    fun signUp(uid: String, password: String): Boolean {
        if( usersImpl.findByUid(uid) == null ) {
            // 密码加密存储
            val newPassword = passwordEncoder.encode(password)
            usersImpl.insert(Users(uid = uid, password = newPassword, grade = "vip"))
            return true
        }
        return false
    }

    /**
     * 登入 不存在就注册，存在就密码验证
     * */
    fun signIn(uid: String, password: String): Boolean {
        val res: Users = usersImpl.findByUid(uid) ?: return signUp(uid, password)

        // 因为数据库内的密码加密了，所以要使用编码器，验证密码
        return passwordEncoder.matches(password, res.password)
    }

    /**
     * 查询指定uid用户的权限
     * */
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
     * 查询用户详情信息
     * */
    fun findUserInfoByUid(uid: String): UserInfo? {
        val user: Users = usersImpl.findByUid(uid) ?: return null
        val profile: UserProfile = userProfileImpl.findByUid(uid) ?: return null

        val userInfo = UserInfo(
            uid=uid,
            grade=user.grade,
            gender = profile.gender,
            address = profile.address,
            username = profile.username,
            email = profile.email,
            profile = profile.profile
        )
        return userInfo
    }

    /**
     * 根据uid
     * 查询用户的 账号 密码 权限 信息
     * */
    fun findUsersByUid(uid: String): Users? {
        return usersImpl.findByUid(uid)
    }


    /**
     * 根据特征查询全部用户具体信息
     * */
    fun findUserInfoByAttr(uid: String = "屙蠺錒", username: String="屙蠺錒"): List<UserInfo>{
        return userInfoImpl.findUserIndoByAttr(uid=uid,username=username)
    }

    /**
     * 查询购物车数据 按照uid
     * */
    fun findCartbookByUid(uid: String): List<Cartbook> {
        return cartbookImpl.findCartbookByUid(uid)
    }

    /**
     * 查询全部图书
     * */
    fun findAllBook(): List<Book> {
        return bookImpl.findAll()
    }

    /**
     * 查询全部正在售卖的图书信息
     * */
    fun findAllAbleBook(): List<Book> {
        return bookImpl.findAllAble ()
    }

    /**
     * 根据特征查询图书
     * */
    fun findBookByAttr(bookid: Long = -1, author: String = "鎿乸", booktype: String = "鎿乸", bookname: String = "鎿乸"): List<Book> {
        return bookImpl.findByAttr(bookid, author, booktype, bookname)
    }

    /**
     * 设置(修改)图书信息
     * */
    fun setBookInfo(book: Book) {
        bookImpl.update(book)
    }

    /**
     * 查询指定用户的订单和账单
     * */
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
     * 查询指定用户的订单和账单
     * 根据特征
     * */
    fun findBillAndOrderByAttr(uid : String,billid: Long,bookname: String): List<BillDetail>{
        val res: MutableList<BillDetail> = mutableListOf()
        val orders = ordersImpl.findByAttr(uid=uid)
        if(orders.isNotEmpty()){
            for(order in orders){
                val tmpBookname = bookImpl.findByBookid(order.bookid)!!.bookname
                if(order.billid==billid || tmpBookname.contains(bookname)){
                    res.add(BillDetail(
                        billid = order.billid,
                        uid = order.uid,
                        bookid = order.bookid,
                        amount = order.amount,
                        status = order.status,
                        otime = order.otime,
                        sumprice = order.sumprice,
                        bookname = tmpBookname
                    ))
                }
            }
        }
        val bills = billImpl.findByAttr(uid=uid)
        if(bills.isNotEmpty()){
            for (bill in bills){
                val tmpBookname = bookImpl.findByBookid(bill.bookid)!!.bookname
                if(bill.billid==billid || tmpBookname.contains(bookname)){
                    res.add(BillDetail(
                        billid = bill.billid,
                        uid = bill.uid,
                        bookid = bill.bookid,
                        amount = bill.amount,
                        status = bill.status,
                        otime = bill.otime,
                        sumprice = bill.sumprice,
                        bookname = tmpBookname
                    ))
                }
            }
        }
        return res
    }

    /**
     * 查询指定的订单，根据订单号billid
     * */
    fun findOrderByBillid(billid: Long): Orders?{
        return ordersImpl.findByAttr(billid = billid).firstOrNull()
    }

    /**
     * 查询全部的订单
     * */
    fun findAllOrders(): List<BillDetail>{
        val res: MutableList<BillDetail> = mutableListOf()
        val orders = ordersImpl.findAll()
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
        return res
    }

    /**
     * 查询全部的订单和账单
     * */
    fun findAllOrdersAndBill(): List<BillDetail>{
        ordersImpl.clearStatus()
        val res: MutableList<BillDetail> = mutableListOf()
        val orders = ordersImpl.findAll()
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
        val bills = billImpl.findAll()
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
     * 根据特征查询订单
     * */
    fun findOrdersByAttr(billid: Long=-1, uid: String="琺鑪覭", bookid: Long=-1): List<BillDetail> {
        val res: MutableList<BillDetail> = mutableListOf()
        val orders = ordersImpl.findByAttr(uid = uid, bookid = bookid, billid = billid)
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
        return res
    }

    /**
     * 删除图书
     * */
    fun deleteBook(bookid: Long): Boolean {
        // 有订单，不能删除
        if(billImpl.findByAttr(bookid=bookid).isNotEmpty() || ordersImpl.findByAttr(bookid=bookid).isNotEmpty()) {
            return false
        }
        bookImpl.deleteByBookid(bookid)
        return true
    }

    /**
     * 删除用户
     * */
    fun deleteUsers(uid: String): Boolean {
        // 存在订单，不能删除
        if(billImpl.findByAttr(uid = uid).isNotEmpty() || ordersImpl.findByAttr(uid = uid).isNotEmpty()) {
            return false
        }
        usersImpl.deleteByUid(uid)
        return true
    }

    /**
     * 删除单条购物车数据
     * */
    fun deleteCart(uid: String,bookid: Long): Boolean {
        cartImpl.deleteByUidAndBookid(uid,bookid)
        return true
    }

    /**
     * 删除指定用户的全部购物车数据
     * */
    fun deleteCartByUid(uid: String): Boolean {
        cartImpl.deleteByUid(uid)
        return true
    }

    /**
     * 加入购物车
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

    /**
     * 修改订单数据
     * */
    fun setOrders(orders: Orders) {
        ordersImpl.update(orders)
    }

    /**
     * 插入一个新订单，同时减少库存数量
     * 去除购物车中同类项目（通过购物车购买时）
     * */
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

    /**
     * 插入一个新订单，同时减少库存数量
     * 不修改购物车（直接下单，未通过购物车购买）
     * */
    fun insertOneOrdersByAttr(uid: String, bookid: Long, amount: Int = -1): String {
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
        return "购买成功"
    }

    /**
     * 修改用户数据
     * */
    fun setUserProfile(userProfile: UserProfile){
        userProfileImpl.update(userProfile)
    }

    /**
     * 获取时间信息
     * */
    fun getCurrentDateTime(): String {
        val currentDateTime = LocalDateTime.now() // 获取当前时间
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") // 定义格式
        return currentDateTime.format(formatter) // 格式化时间
    }
}