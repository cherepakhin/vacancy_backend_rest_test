package ru.perm.v.vacancy.restassured.rest

import io.qameta.allure.Epic
import io.restassured.RestAssured
import io.restassured.RestAssured.get
import io.restassured.RestAssured.given
import io.restassured.parsing.Parser
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
            RestAssured.registerParser("text/plain", Parser.HTML);
            RestAssured.baseURI = CONSTS.ECHO_PATH
        }
    }

    @Test
    @Epic("REST API Echo")
    @DisplayName("GET Echo Request with message is status=200")
    fun getMessage_HttpStatusIsOK() {
        given().`when`().get(MESSAGE).then()
            .statusCode(HttpStatus.SC_OK)
    }

    @Test
    @Epic("REST API Echo")
    @DisplayName("GET Echo Request check message")
    fun getMessage_CheckMessage() {
        val response = get(CONSTS.ECHO_PATH + MESSAGE)
        val responseBody = response.asString()
        assertEquals(MESSAGE, responseBody)
        val v = VacancyDTO()
    }
}