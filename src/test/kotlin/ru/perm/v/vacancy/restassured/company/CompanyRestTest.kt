package ru.perm.v.vacancy.restassured.company

import io.qameta.allure.Epic
import io.restassured.RestAssured
import org.apache.http.HttpStatus
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import ru.perm.v.shop_kotlin.restassured.CONSTS


@DisplayName("Company tests")
class CompanyRestTest {
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    companion object {
        @BeforeAll
        @JvmStatic
        fun setupAll(): Unit {
            RestAssured.baseURI = CONSTS.COMPANY_PATH
        }
    }

    @Test
    @Epic("Company REST API Echo")
    @DisplayName("GET Company REST Request with message is status=200")
    fun getMessage_HttpStatusIsOK() {
        val MESSAGE = "message"
        RestAssured.given().`when`().get("/echo/" + MESSAGE).then()
            .statusCode(HttpStatus.SC_OK)
    }
}