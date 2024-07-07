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
import org.slf4j.LoggerFactory
import java.lang.String.format

@DisplayName("Echo tests")
class EchoRestTest {
    val logger = LoggerFactory.getLogger(this::class.java)

    val MESSAGE = "MESSAGE"

    companion object {
        val logger = LoggerFactory.getLogger(this::class.java)

        @BeforeAll
        @JvmStatic
        fun setupAll(): Unit {
            RestAssured.registerParser("text/plain", Parser.HTML);
            RestAssured.baseURI = CONSTS.ECHO_PATH
            logger.info(format("RestAssured.baseURI = ", RestAssured.baseURI))
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
        logger.info(responseBody)
        assertEquals(MESSAGE, responseBody)
    }
}

