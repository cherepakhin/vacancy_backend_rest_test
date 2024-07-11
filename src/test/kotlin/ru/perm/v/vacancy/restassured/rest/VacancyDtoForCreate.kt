package ru.perm.v.vacancy.restassured.rest

class VacancyDtoForCreate(
    var name: String = "-",
    var comment: String = "-",
    var company_n: Long  =  0
) {
    override fun toString(): String {
        return "VacancyDto(name='$name', comment='$comment', company_n=$company_n)"
    }
}