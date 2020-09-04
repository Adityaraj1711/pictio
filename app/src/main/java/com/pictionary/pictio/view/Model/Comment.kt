package com.pictionary.pictio.view.Model

class Comment {
    var comment: String? = null
    var publisher: String? = null

    constructor(
        comment: String?,
        publisher: String?
    ) {
        this.comment = comment
        this.publisher = publisher
    }

    constructor() {}

}