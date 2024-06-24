package ru.perm.v.vacancy.restassured.rest

import io.qameta.allure.Epic
import io.restassured.RestAssured
import org.apache.http.HttpStatus
import org.hamcrest.core.IsEqual.equalTo
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory


@DisplayName("Vacancy tests")
class VacancyRestTest {
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    companion object {
        @BeforeAll
        @JvmStatic
        fun setupAll(): Unit {
            RestAssured.baseURI = CONSTS.VACANCY_PATH
        }
    }

    @Test
    @Epic("Vacancy REST API Echo")
    @DisplayName("GET Vacancy REST Request is status=200")
    fun getEchoHttpStatusIsOK() {
        val MESSAGE = "message"
        RestAssured.given().`when`().get("/echo/" + MESSAGE).then()
            .statusCode(HttpStatus.SC_OK)
    }

    @Test
    @Epic("Vacancy REST API Echo")
    @DisplayName("GET Vacancy REST Request check message")
    fun getEchoCheckMessage() {
        val MESSAGE = "message"
        RestAssured.given().`when`().get("/echo/" + MESSAGE).then()
            .statusCode(HttpStatus.SC_OK)
            .contentType("text/plain")
            .body(equalTo(MESSAGE))
    }

    @Test
    @Epic("Vacancy REST API Get Vacancy 1")
    @DisplayName("GET Vacancy N=1. Check HttpStatus.SC_OK.")
    fun getVacancyByN_HttpStatusIsOK() {
        val N = 1
        RestAssured.given().`when`().get("/" + N).then()
            .statusCode(HttpStatus.SC_OK)
    }

    @Test
    @Epic("Vacancy REST API Get Vacancy 1")
    @DisplayName("GET Vacancy N=1. Check JSON.")
    fun getVacancyByN() {
        val N = 1
        RestAssured.given().`when`().get("/" + N).then()
            .contentType("application/json")
            .body("n", equalTo(N))
            .and()
            .body("name", equalTo("NAME_VACANCY_1_COMPANY_1"))
    }

    @Test
    @DisplayName("GET All Vacancy. Check OK.")
    fun getAllVacancy_HttpStatusIsOK() {
        RestAssured.given().`when`().get("/").then()
            .statusCode(HttpStatus.SC_OK)
            .contentType("application/json")
    }

//TODO: uncomment
//    @Test
//    @DisplayName("GET All Vacancy. Check body.")
//    fun getAllVacancy_checkBody() {
//        val json = RestAssured.get("/").body.asString()
//        val companies: List<VacancyDTO>  = ObjectMapper().readValue(json)
//
//        assertEquals(4, companies.size)
//        assertEquals(VacancyDTO(-1L,"-"), companies[0])
//        assertEquals(VacancyDTO(1L,"COMPANY_1"), companies[1])
//        assertEquals(VacancyDTO(2L,"COMPANY_2"), companies[2])
//        assertEquals(VacancyDTO(3L,"3_COMPANY"), companies[3])
//    }
}