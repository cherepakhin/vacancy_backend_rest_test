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


@DisplayName("Vacancy tests")
class VacancyRestTest {
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    companion object {
        @BeforeAll
        @JvmStatic
        fun setupAll(): Unit {
            baseURI = CONSTS.VACANCY_PATH
        }
    }

    @Test
    @Epic("Vacancy REST API Echo")
    @DisplayName("GET Vacancy REST Request is status=200")
    fun getEchoHttpStatusIsOK() {
        val MESSAGE = "message"
        given().`when`().get("/echo/" + MESSAGE).then()
            .statusCode(HttpStatus.SC_OK)
    }

    @Test
    @Epic("Vacancy REST API Echo")
    @DisplayName("GET Vacancy REST Request check message")
    fun getEchoCheckMessage() {
        val MESSAGE = "message"
        given().`when`().get("/echo/" + MESSAGE).then()
            .statusCode(HttpStatus.SC_OK)
            .contentType("text/plain")
            .body(equalTo(MESSAGE))
    }

    @Test
    @Epic("Vacancy REST API Get Vacancy 1")
    @DisplayName("GET Vacancy N=1. Check HttpStatus.SC_OK.")
    fun getVacancyByN_HttpStatusIsOK() {
        val N = 1
        given().`when`().get("/" + N).then()
            .statusCode(HttpStatus.SC_OK)
    }

    @Test
    @Epic("Vacancy REST API Get Vacancy 1")
    @DisplayName("GET Vacancy N=1. Check JSON.")
    fun getVacancyByN() {
        val N = 1
        given().`when`().get("/" + N).then()
            .contentType("application/json")
            .body("n", equalTo(N))
            .and()
            .body("name", equalTo("NAME_VACANCY_1_COMPANY_1"))
    }

    @Test
    @DisplayName("GET All Vacancy. Check OK.")
    fun getAllVacancy_HttpStatusIsOK() {
        given().`when`().get("/").then()
            .statusCode(HttpStatus.SC_OK)
            .contentType("application/json")
    }

    @Test
    @DisplayName("GET All Vacancy. Check body.")
    fun getAllVacancy_checkBody() {
        val json = get("/").body.asString()
        val vacancies: List<VacancyDTO>  = ObjectMapper().readValue(json)

        val companyDTO1 = CompanyDTO(1L,"COMPANY_1")
        val companyDTO2 = CompanyDTO(2L,"COMPANY_2")
        val companyDTO3 = CompanyDTO(3L,"3_COMPANY")

        assertEquals(4, vacancies.size)
        assertEquals(
            VacancyDTO(
                1L,
                "NAME_VACANCY_1_COMPANY_1",
                "COMMENT_VACANCY_1_COMPANY_1",
                companyDTO1),
            vacancies[0])
        assertEquals(
            VacancyDTO(
                2L,
                "NAME_VACANCY_2_COMPANY_1",
                "COMMENT_VACANCY_2_COMPANY_1",
                companyDTO1),
            vacancies[1])
        assertEquals(
            VacancyDTO(
                3L,
                "NAME_VACANCY_1_COMPANY_2",
                "COMMENT_VACANCY_1_COMPANY_2",
                companyDTO2),
            vacancies[2])
        assertEquals(
            VacancyDTO(
                4L,
                "NAME_VACANCY_1_COMPANY_3",
                "COMMENT_VACANCY_1_COMPANY_3",
                companyDTO3),
            vacancies[3])
    }

    //TODO: test get NOT EXIST vacancy
    //TODO: test create with valid DTO
    //TODO: test create with NOT valid DTO
    //TODO: test update
    //TODO: test delete exist
    //TODO: test delete NOT exist
    //TODO: test delete with check cache
    //TODO: test validate message on create
}