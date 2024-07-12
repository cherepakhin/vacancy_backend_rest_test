package ru.perm.v.vacancy.restassured.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.qameta.allure.Epic
import io.restassured.RestAssured.*
import io.restassured.module.kotlin.extensions.Extract
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
        val vacancies: List<VacancyDTO> = ObjectMapper().readValue(json)

        val companyDTO1 = CompanyDTO(1L, "COMPANY_1")
        val companyDTO2 = CompanyDTO(2L, "COMPANY_2")
        val companyDTO3 = CompanyDTO(3L, "3_COMPANY")

        assertEquals(4, vacancies.size)
        assertEquals(
            VacancyDTO(
                1L,
                "NAME_VACANCY_1_COMPANY_1",
                "COMMENT_VACANCY_1_COMPANY_1",
                companyDTO1
            ),
            vacancies[0]
        )
        assertEquals(
            VacancyDTO(
                2L,
                "NAME_VACANCY_2_COMPANY_1",
                "COMMENT_VACANCY_2_COMPANY_1",
                companyDTO1
            ),
            vacancies[1]
        )
        assertEquals(
            VacancyDTO(
                3L,
                "NAME_VACANCY_1_COMPANY_2",
                "COMMENT_VACANCY_1_COMPANY_2",
                companyDTO2
            ),
            vacancies[2]
        )
        assertEquals(
            VacancyDTO(
                4L,
                "NAME_VACANCY_1_COMPANY_3",
                "COMMENT_VACANCY_1_COMPANY_3",
                companyDTO3
            ),
            vacancies[3]
        )
    }

    @Test
    @DisplayName("GET NOT EXIST Vacancy.")
    fun getNotExistVacancy() {
        val answer = given().`when`().get("/-100").then()
            .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
            .contentType("application/json")
        answer.Extract { this.statusCode() == 500 }
    }

    @Test
    @DisplayName("Create Vacancy with VALID DTO.")
    fun createWithValidDTO() {
        val companyDTO1 = CompanyDTO(1L, "COMPANY_1")
        val vacancyDTO = VacancyDtoForCreate(
            "NAME_VACANCY_2_COMPANY_1",
            "COMMENT_VACANCY_2_COMPANY_1",
            companyDTO1.n
        )
        val answer = given().contentType(io.restassured.http.ContentType.JSON)
            .body(vacancyDTO).`when`().post(CONSTS.VACANCY_PATH)
        answer.Extract { this.statusCode() == 200 } // assert variant 1
        assertEquals(200,  answer.statusCode) // assert variant 2
    }
    @Test
    @DisplayName("Create Vacancy with NOT valid DTO (name short). Check message.")
    fun createWith_NOT_ValidDTO_NAME_SHORT_check_error_message() {
        val companyDTO1 = CompanyDTO(1L, "COMPANY_1")
        val vacancyDTO = VacancyDtoForCreate(
            "1234", // width  >=5
            "COMMENT_VACANCY_2_COMPANY_1",
            companyDTO1.n
        )
        val answer = given().contentType(io.restassured.http.ContentType.JSON)
            .body(vacancyDTO).`when`().post(CONSTS.VACANCY_PATH).andReturn().then().extract().path<String>("message")
        assertEquals("VacancyDto(name='1234', comment='COMMENT_VACANCY_2_COMPANY_1', company_n=1) has errors: размер должен находиться в диапазоне от 5 до 50\n",  answer)
    }

    @Test
    @DisplayName("Create Vacancy with NOT valid DTO (name short). Check message.")
    fun createWith_NOT_ValidDTO_NAME_SHORT_check_status_code() {
        val companyDTO1 = CompanyDTO(1L, "COMPANY_1")
        val vacancyDTO = VacancyDtoForCreate(
            "1234", // width  < 5 chars
            "COMMENT_VACANCY_2_COMPANY_1",
            companyDTO1.n
        )
        val answer = given().contentType(io.restassured.http.ContentType.JSON)
            .body(vacancyDTO).`when`().post(CONSTS.VACANCY_PATH).andReturn()
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR,  answer.statusCode)
    }

    //TODO: test create with NOT valid DTO
    //TODO: test validate message on create
    //TODO: test update
    //TODO: test delete exist
    //TODO: test delete NOT exist
    //TODO: test delete with check cache
}