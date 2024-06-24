package ru.perm.v.vacancy.restassured.rest

class VacancyDto {
    var n: Long = -1L
    var name: String = "-"
    var comment: String = "-"
    var company: CompanyDTO = CompanyDTO(-1L, "-")

    constructor()

    constructor(n: Long, name: String, comment: String, company: CompanyDTO)  {
        this.n = n
        this.name = name
        this.comment = comment
        this.company = company
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VacancyDto) return false

        if (n != other.n) return false
        if (name != other.name) return false
        if (comment != other.comment) return false
        if (company != other.company) return false

        return true
    }

    override fun hashCode(): Int {
        var result = n.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + comment.hashCode()
        result = 31 * result + company.hashCode()
        return result
    }

    override fun toString(): String {
        return "VacancyDto(n=$n, name='$name', comment='$comment', company=$company)"
    }
}