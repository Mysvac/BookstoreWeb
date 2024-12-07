package com.mythovac.configtemplate.entity

/**
 * Entity 实体类
 * Users 用户账号密码和权限
 * 对应数据表 users
 * */
data class Users(
    var uid : String,
    var password : String,
    var grade: String
)
