package com.pictionary.pictio.view.Model

class User {
    var id: String? = null
    var username: String? = null
    var fullname: String? = null
    var imageurl: String? = null
    var bio: String? = null

    constructor(
        id: String?,
        username: String?,
        fullname: String?,
        imageurl: String?,
        bio: String?
    ) {
        this.id = id
        this.username = username
        this.fullname = fullname
        this.imageurl = imageurl
        this.bio = bio
    }

    constructor() {}

}
