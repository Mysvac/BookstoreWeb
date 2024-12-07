package com.mythovac.configtemplate.entity

/**
 * Entity 实体类
 * UserProfile 用户个性化信息
 * 对应数据表 userProfile
 * */
data class UserProfile(
    var uid : String,
    var gender: String,
    var address : String,
    var username : String,
    var email : String,
    var profile : String
)
