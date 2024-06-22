package ru.perm.v.vacancy.restassured

class CONSTS {
    companion object {
        val IP = System.getenv("VACANCY_KOTLIN_IP") ?: "127.0.0.1:8980"
        val HOST = "http://"+ IP +"/vacancy/api"
        val ECHO_PATH = HOST + "/echo/"
        val COMPANY_PATH = HOST + "/company/"
    }
}