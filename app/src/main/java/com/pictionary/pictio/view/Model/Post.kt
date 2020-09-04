package com.pictionary.pictio.view.Model

class Post {
    var postid: String? = null
    var postimage: String? = null
    var description: String? = null
    var publisher: String? = null

    constructor(
        postid: String?,
        postimage: String?,
        description: String?,
        publisher: String?
    ) {
        this.postid = postid
        this.postimage = postimage
        this.description = description
        this.publisher = publisher
    }

    constructor() {}


}