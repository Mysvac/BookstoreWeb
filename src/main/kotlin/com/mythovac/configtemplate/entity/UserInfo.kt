package com.mythovac.configtemplate.entity

/**
 * Entity 实体类
 * UserInfo 个人详情
 * 用于多表连接
 * users + userProfile
 * */
data class UserInfo(
    var uid : String,
    var grade: String,
    var gender: String,
    var address : String,
    var username : String,
    var email : String,
    var profile : String
)
