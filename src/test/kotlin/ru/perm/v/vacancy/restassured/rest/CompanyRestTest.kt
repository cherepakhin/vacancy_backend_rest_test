package ru.perm.v.vacancy.restassured.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.qameta.allure.Epic
import io.restassured.RestAssured.*
import org.apache.http.HttpStatus
import org.hamcrest.core.IsEqual.equalTo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory


@DisplayName("Company tests")
class CompanyRestTest {
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    companion object {
        @BeforeAll
        @JvmStatic
        fun setupAll(): Unit {
            baseURI = CONSTS.COMPANY_PATH
        }
    }

    @Test
    @Epic("Company REST API Echo")
    @DisplayName("GET Company REST Request is status=200")
    fun getEchoHttpStatusIsOK() {
        val MESSAGE = "message"
        given().`when`().get("/echo/" + MESSAGE).then()
            .statusCode(HttpStatus.SC_OK)
    }

    @Test
    @Epic("Company REST API Echo")
    @DisplayName("GET Company REST Request check message")
    fun getEchoCheckMessage() {
        val MESSAGE = "message"
        given().`when`().get("/echo/" + MESSAGE).then()
            .statusCode(HttpStatus.SC_OK)
            .contentType("text/plain")
            .body(equalTo(MESSAGE))
    }

    @Test
    @Epic("Company REST API Get Company 1")
    @DisplayName("GET Company N=1. Check HttpStatus.SC_OK.")
    fun getCompanyByN_HttpStatusIsOK() {
        val N = 1
        given().`when`().get("/" + N).then()
            .statusCode(HttpStatus.SC_OK)
    }

    @Test
    @Epic("Company REST API Get Company 1")
    @DisplayName("GET Company N=1. Check JSON.")
    fun getCompanyByN() {
        val N = 1
        given().`when`().get("/" + N).then()
            .contentType("application/json")
            .body("n", equalTo(N))
            .and()
            .body("name", equalTo("COMPANY_1"))
    }

    @Test
    @DisplayName("GET All Company. Check OK.")
    fun getAllCompany_HttpStatusIsOK() {
        given().`when`().get("/").then()
            .statusCode(HttpStatus.SC_OK)
            .contentType("application/json")
    }

    @Test
    @DisplayName("GET All Company. Check body.")
    fun getAllCompany_checkBody() {
        val json = get("/").body.asString()
        val companies: List<CompanyDto> = ObjectMapper().readValue(json)

        assertEquals(4, companies.size)
        assertEquals(CompanyDto(-1L, "-"), companies[0])
        assertEquals(CompanyDto(1L, "COMPANY_1"), companies[1])
        assertEquals(CompanyDto(2L, "COMPANY_2"), companies[2])
        assertEquals(CompanyDto(3L, "3_COMPANY"), companies[3])
    }

    @Test
    @DisplayName("Create Company.")
    fun createCompany() {
        val N = 4L
        //before test
        delete("/" + N) // delete if exist

        //test
        val newCompany = CompanyDto(N, "NEW_COMPANY")
        given().body(newCompany).contentType("application/json").`when`().post("/").then().statusCode(200)

        //check created company
        given().`when`().get("/" + N).then()
            .statusCode(HttpStatus.SC_OK)

        //clean after test
        delete("/" + N)
    }

    @Test
    @DisplayName("Update EXIST Company.")
    fun updateExistCompany() {
        val N = 1L
        // check exist company before test
        given().`when`().get("/" + N).then()
            .statusCode(HttpStatus.SC_OK)

        //test
        val changedCompany = CompanyDto(N, "CHANGED_NAME_COMPANY")
        val answer = given().body(changedCompany).contentType("application/json").`when`().post("/" + N)

        assertEquals(200, answer.statusCode())
        //verify changed company
        given().`when`().get("/" + N).then()
            .contentType("application/json")
            .body("n", equalTo(N.toInt()))
            .body("name", equalTo("CHANGED_NAME_COMPANY"))

        //reset after test
        val resetCompany = CompanyDto(N, "COMPANY_1")
        given().body(resetCompany).contentType("application/json").`when`().post("/" + N)
    }

    @Test
    @DisplayName("Update NOT EXIST Company.")
    fun updateNotExistCompany() {
        val N = 100L
        // verify not exist company
        given().`when`().get("/" + N).then()
            .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)

        //test
        val changedCompany = CompanyDto(N, "CHANGED_NAME_COMPANY")
        val answer = given().body(changedCompany).contentType("application/json").`when`().post("/" + N)

        assertEquals(500, answer.statusCode())
    }

    @Test
    @DisplayName("Delete NOT EXIST Company.")
    fun deleteNotExistCompany() {
        val N = 100L
        // verify NOT exist company
        given().`when`().get("/" + N).then()
            .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)

        //test
        val changedCompany = CompanyDto(N, "CHANGED_NAME_COMPANY")
        val answer = given().body(changedCompany).contentType("application/json").`when`().delete("/" + N)

        assertEquals(500, answer.statusCode())
    }

    @Test
    @DisplayName("Delete EXIST Company.")
    fun deleteExistCompany() {
        val N = 100L
        // verify NOT EXIST company. Company will be created next
        given().`when`().get("/" + N).then()
            .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)

        // before test create company for delete
        val newCompany = CompanyDto(N, N.toString() + "_NEW_COMPANY")
        val createdForDeleteCompany =
            given().body(newCompany).contentType("application/json").`when`().post("/").body.asString()

        logger.info("------------------------------------createdForDeleteCompany: " + createdForDeleteCompany)

        val companyDto = ObjectMapper().readValue<CompanyDto>(createdForDeleteCompany)
        logger.info(companyDto.toString())

        //test
        val answer = given().`when`().delete("/" + companyDto.n)
        logger.info("BODY TEST:" + answer.body.asString())

        // check what deleted and GET by id is status error
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, given().`when`().get("/" + N).statusCode())
    }
}