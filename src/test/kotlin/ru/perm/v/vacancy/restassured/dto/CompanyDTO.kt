package ru.perm.v.vacancy.restassured.dto

class CompanyDTO {
    var n: Long = -1L
    var name: String = ""

    constructor()  {

    }

    constructor(n: Long, name: String) {
        this.n = n
        this.name = name
    }


    override fun toString(): String {
        return "CompanyDTO(n=$n, name='$name')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CompanyDTO) return false

        if (n != other.n) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = n.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }


}