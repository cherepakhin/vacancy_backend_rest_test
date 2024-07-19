package ru.perm.v.vacancy.restassured.rest

import io.qameta.allure.Epic
import io.restassured.RestAssured.baseURI
import io.restassured.RestAssured.given
import org.apache.http.HttpStatus
import org.hamcrest.core.IsEqual.equalTo
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory


@DisplayName("Init tests")
class InitRestTest {
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    companion object {
        @BeforeAll
        @JvmStatic
        fun setupAll(): Unit {
            baseURI = CONSTS.INIT_PATH
        }
    }

    @Test
    @Epic("Init REST API Echo")
    @DisplayName("GET Init REST Request is status=200")
    fun getEchoHttpStatusIsOK() {
        logger.info("GET Init REST Request is status=200")
        val MESSAGE = "message"
        given().`when`().get("/echo/" + MESSAGE).then()
            .statusCode(HttpStatus.SC_OK)
    }

    @Test
    @Epic("Init REST API Echo")
    @DisplayName("GET Init REST Request check message")
    fun getEchoCheckMessage() {
        val MESSAGE = "message"
        given().`when`().get("/echo/" + MESSAGE).then()
            .statusCode(HttpStatus.SC_OK)
            .contentType("text/plain")
            .body(equalTo(MESSAGE))
    }
}