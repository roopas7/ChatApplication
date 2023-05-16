package com.example.chatapplication.model

class UserData {
    var email: String? = null
    var uName: String? = null
    var userID: String? = null
    var profileImage: String? = null

    constructor()

    constructor(email: String,uName: String, userID: String?, profileImage: String ){

        this.email = email
        this.uName = uName
        this.profileImage = profileImage
        this.userID = userID
    }

}