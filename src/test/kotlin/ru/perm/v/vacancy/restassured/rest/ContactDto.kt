package ru.perm.v.vacancy.restassured.rest

class ContactDto {
    var n: Long = -1L
    var name: String = ""
    var email: String = ""
    var phone: String = ""
    var comment: String = ""

    constructor() {
    }

    constructor(n: Long, name: String, email: String, phone: String, comment: String): this() {
        this.n = n
        this.name = name
        this.email = email
        this.phone = phone
        this.comment = comment
    }

    override fun hashCode(): Int {
        return this.n.hashCode() + this.name.hashCode() + this.email.hashCode() + this.phone.hashCode() + this.comment.hashCode()
    }

    override fun toString(): String {
        return "ContactDto(n=$n, name=$name, email=$email, phone=$phone, comment=$comment)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ru.perm.v.vacancy.restassured.rest.ContactDto

        if (n != other.n) return false
        if (name != other.name) return false
        if (email != other.email) return false
        if (phone != other.phone) return false
        if (comment != other.comment) return false

        return true
    }
}