package ru.perm.v.vacancy.restassured.company

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.qameta.allure.Epic
import io.restassured.RestAssured
import org.apache.http.HttpStatus
import org.hamcrest.core.IsEqual.equalTo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import ru.perm.v.vacancy.restassured.CONSTS
import ru.perm.v.vacancy.restassured.dto.CompanyDTO


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
    @DisplayName("GET Company REST Request is status=200")
    fun getEchoHttpStatusIsOK() {
        val MESSAGE = "message"
        RestAssured.given().`when`().get("/echo/" + MESSAGE).then()
            .statusCode(HttpStatus.SC_OK)
    }

    @Test
    @Epic("Company REST API Echo")
    @DisplayName("GET Company REST Request check message")
    fun getEchoCheckMessage() {
        val MESSAGE = "message"
        RestAssured.given().`when`().get("/echo/" + MESSAGE).then()
            .statusCode(HttpStatus.SC_OK)
            .contentType("text/plain")
            .body(equalTo(MESSAGE))
    }

    @Test
    @Epic("Company REST API Get Company 1")
    @DisplayName("GET Company N=1. Check JSON.")
    fun getCompanyByN_HttpStatusIsOK() {
        val N = 1
        RestAssured.given().`when`().get("/" + N).then()
            .statusCode(HttpStatus.SC_OK)
    }

    @Test
    @Epic("Company REST API Get Company 1")
    @DisplayName("GET Company N=1. Check JSON.")
    fun getCompanyByN() {
        val N = 1
        RestAssured.given().`when`().get("/" + N).then()
            .contentType("application/json")
            .body("n", equalTo(N))
            .and()
            .body("name", equalTo("COMPANY_1"))
    }
    @Test
    @DisplayName("GET All Company. Check OK.")
    fun getAllCompany_HttpStatusIsOK() {
        RestAssured.given().`when`().get("/").then()
            .statusCode(HttpStatus.SC_OK)
            .contentType("application/json")
    }
    @Test
    @DisplayName("GET All Company. Check body.")
    fun getAllCompany_checkBody() {
        val json = RestAssured.get("/").body.asString()
        val companies: List<CompanyDTO>  = ObjectMapper().readValue(json)

        assertEquals(4, companies.size)
        assertEquals(CompanyDTO(-1L,"-"), companies[0])
        assertEquals(CompanyDTO(1L,"COMPANY_1"), companies[1])
        assertEquals(CompanyDTO(2L,"COMPANY_2"), companies[2])
        assertEquals(CompanyDTO(3L,"3_COMPANY"), companies[3])
    }
}