package ru.perm.v.shop_kotlin.restassured

import io.qameta.allure.Epic
import io.restassured.RestAssured
import io.restassured.RestAssured.get
import io.restassured.RestAssured.given
import org.apache.http.HttpStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Echo tests")
class EchoRestTest {
    val MESSAGE = "MESSAGE"

    companion object {
        @BeforeAll
        @JvmStatic
        fun setupAll(): Unit {
            RestAssured.baseURI = CONSTS.ECHO_PATH
        }
    }

    @Test
    @Epic("REST API Echo")
    @DisplayName("GET Request with message is status=200")
    fun getMessage_HttpStatusIsOK() {
        given().`when`().get(CONSTS.ECHO_PATH + MESSAGE).then()
            .statusCode(HttpStatus.SC_OK)
    }

    @Test
    @Epic("REST API Echo")
    @DisplayName("GET Request with message is status=200")
    fun getMessage_CheckMessage() {
        val response = get(CONSTS.ECHO_PATH + MESSAGE)
        val responseBody = response.asString()
        assertEquals(MESSAGE, responseBody)
    }
}