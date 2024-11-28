package com.mythovac.configtemplate.component

import jakarta.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component


/**
 * 创建表格，保证表格存在
 * */
@Component
class TableManager @Autowired constructor( private val jdbcTemplate: JdbcTemplate ) : CommandLineRunner {

    /**
     * 获取application.properties中的自定义参数
     * 控制是否在结束时删除数据表
     * true or false
     * */
    @Value("\${drop.tables.on.close}")
    private lateinit var dropTablesOnClose: String

    /**
     * 程序启动时，如果表不存在，自动创建
     * */
    override fun run(vararg args: String?) {
        createTableUser()
        createTableUserProfile()
        createTableBook()
        createTableCart()
        createTableOperation()
        createTableBill()
    }

    /**
     * 程序结束时，删除表
     * 可不启用
     * */
    @PreDestroy
    fun onShutdown() {
        if(dropTablesOnClose.toBoolean()){
            dropTableBill()
            dropTableOperation()
            dropTableCart()
            dropTableBook()
            dropTableUserProfile()
            dropTableUser()
            println("已删除表。")
        }
        else{
            println("当前选择不删除表。")
        }
    }


    /**
     * 执行SQL语句
     * 创建和删除表
     * */
    private fun createTableUser() { jdbcTemplate.execute(createTableUserSQL) }
    private fun dropTableUser() { jdbcTemplate.execute(dropTableUserSQL) }

    private fun createTableUserProfile(){ jdbcTemplate.execute(createTableUserProfileSQL) }
    private fun dropTableUserProfile() { jdbcTemplate.execute(dropTableUserProfileSQL) }

    private fun createTableBook(){ jdbcTemplate.execute(createTableBookSQL) }
    private fun dropTableBook() { jdbcTemplate.execute(dropTableBookSQL) }

    private fun createTableCart(){ jdbcTemplate.execute(createTableCartSQL) }
    private fun dropTableCart() { jdbcTemplate.execute(dropTableCartSQL) }

    private fun createTableOperation(){ jdbcTemplate.execute(createTableOperationSQL) }
    private fun dropTableOperation() { jdbcTemplate.execute(dropTableOperationSQL) }

    private fun createTableBill(){ jdbcTemplate.execute(createTableBillSQL) }
    private fun dropTableBill() { jdbcTemplate.execute(dropTableBillSQL) }



    /**
     * 具体的创建和删除的SQL语句
     * */
    // 账号基本信息表
    private val createTableUserSQL = """
        CREATE TABLE IF NOT EXISTS users (
        account CHAR(23) PRIMARY KEY,
        phone CHAR(13) NOT NULL,
        password CHAR(23) NOT NULL,
        role CHAR(7) NOT NULL
        );
    """

    // 个性化信息表
    private val createTableUserProfileSQL = """
        CREATE TABLE IF NOT EXISTS userProfile (
        account CHAR(23) PRIMARY KEY,
        gender CHAR(7),
        address CHAR(47),
        username CHAR(23),
        email CHAR(47),
        profile CHAR(47),
        CONSTRAINT FK_userProfile FOREIGN KEY (account) REFERENCES users (account)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        );
    """

    // 书籍信息表
    private val createTableBookSQL = """
        CREATE TABLE IF NOT EXISTS book (
        bid INT AUTO_INCREMENT PRIMARY KEY,
        bookname CHAR(23) NOT NULL,
        stock INT NOT NULL,
        price INT NOT NULL,
        sales INT NOT NULL,
        author CHAR(23) NOT NULL,
        profile CHAR(47)
        );
    """

    // 购物车表
    private val createTableCartSQL = """
        CREATE TABLE IF NOT EXISTS cart (
        account CHAR(23) NOT NULL,
        bid INT NOT NULL,
        amount INT NOT NULL,
        PRIMARY KEY (account, bid),
        CONSTRAINT KF_cart_account FOREIGN KEY (account) REFERENCES users (account)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
        CONSTRAINT KF_cart_bid FOREIGN KEY (bid) REFERENCES book (bid)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        );
    """

    // 下单操作表
    private val createTableOperationSQL = """
        CREATE TABLE IF NOT EXISTS operation (
        operid BIGINT AUTO_INCREMENT PRIMARY KEY,
        account CHAR(23) NOT NULL,
        bid INT NOT NULL,
        amount INT NOT NULL,
        status CHAR(7) NOT NULL,
        CONSTRAINT KF_operation_account FOREIGN KEY (account) REFERENCES users (account)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
        CONSTRAINT KF_operation_bid FOREIGN KEY (bid) REFERENCES book (bid)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        );
    """

    // 已完成的记录表
    private val createTableBillSQL = """
        CREATE TABLE IF NOT EXISTS bill (
        billid BIGINT AUTO_INCREMENT PRIMARY KEY,
        operid BIGINT NOT NULL UNIQUE,
        account CHAR(23) NOT NULL,
        bid INT NOT NULL,
        amount INT NOT NULL,
        CONSTRAINT KF_bill_account FOREIGN KEY (account) REFERENCES users (account) 
        ON DELETE CASCADE
        ON UPDATE CASCADE,
        CONSTRAINT KF_bill_bid FOREIGN KEY (bid) REFERENCES book (bid)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        );
    """

    private val dropTableUserSQL = "DROP TABLE IF EXISTS users;"

    private val dropTableUserProfileSQL = "DROP TABLE IF EXISTS userProfile;"

    private val dropTableBookSQL = "DROP TABLE IF EXISTS book;"

    private val dropTableCartSQL = "DROP TABLE IF EXISTS cart;"

    private val dropTableOperationSQL = "DROP TABLE IF EXISTS operation;"

    private val dropTableBillSQL = "DROP TABLE IF EXISTS bill;"

}