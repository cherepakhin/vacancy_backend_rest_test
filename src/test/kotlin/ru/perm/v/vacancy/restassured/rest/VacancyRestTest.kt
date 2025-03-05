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
import org.junit.jupiter.api.BeforeEach
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

        }

    }

    @BeforeEach
    fun setup() {
        // Reimport DB for set basic state
        logger.info("SETUP. REIMPORT.")
        baseURI = CONSTS.HOST + "/init/reimport_db"
        given().`when`().get().then()
            .statusCode(HttpStatus.SC_OK)
        baseURI = CONSTS.VACANCY_PATH
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
        baseURI = CONSTS.VACANCY_PATH

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
        baseURI = CONSTS.VACANCY_PATH
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
        baseURI = CONSTS.VACANCY_PATH

        val json = get("/").body.asString()

        val vacancies: List<VacancyDto> = ObjectMapper().readValue(json)

        logger.info("All Vacancy:")
        for (v in vacancies) {
            logger.info(v.toString())
        }

        assertEquals(4, vacancies.size)

        val companyDto1 = CompanyDto(1L, "COMPANY_1")
        val companyDto2 = CompanyDto(2L, "COMPANY_2")
        val companyDto3 = CompanyDto(3L, "3_COMPANY")
        val contactDto1 = ContactDto(1L, "CONTACT_1_COMPANY_1", "CONTACT_1_EMAIL",
            "CONTACT_1_PHONE", "CONTACT_1_COMMENT")
        assertEquals(
            VacancyDto(
                1L,
                "NAME_VACANCY_1_COMPANY_1",
                "COMMENT_VACANCY_1_COMPANY_1",
                companyDto1,
                contactDto1
            ),
            vacancies[0]
        )

        val contactDto2 = ContactDto(2L, "CONTACT_2_COMPANY_1", "CONTACT_2_COMPANY_1_EMAIL",
            "CONTACT_2_COMPANY_1_PHONE", "CONTACT_2_COMPANY_1_COMMENT")
        assertEquals(
            VacancyDto(
                2L,
                "NAME_VACANCY_2_COMPANY_1",
                "COMMENT_VACANCY_2_COMPANY_1",
                companyDto1,
                contactDto2
            ),
            vacancies[1]
        )
        val contactDto3 = ContactDto(3L, "CONTACT_3_COMPANY_2", "CONTACT_3_EMAIL",
            "CONTACT_3_PHONE", "CONTACT_3_COMMENT")
        assertEquals(
            VacancyDto(
                3L,
                "NAME_VACANCY_1_COMPANY_2",
                "COMMENT_VACANCY_1_COMPANY_2",
                companyDto2,
                contactDto3
            ),
            vacancies[2]
        )
        val contactDto4 = ContactDto(4L, "CONTACT_4_COMPANY_3", "CONTACT_3_EMAIL",
            "CONTACT_3_PHONE", "CONTACT_3_COMMENT")
        assertEquals(
            VacancyDto(
                4L,
                "NAME_VACANCY_1_COMPANY_3",
                "COMMENT_VACANCY_1_COMPANY_3",
                companyDto3,
                contactDto4
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
    @DisplayName("Create Vacancy with VALID Dto.")
    fun createWithValidDto() {
        val companyDto1 = CompanyDto(1L, "COMPANY_1")
        val contactDto = ContactDto(1L, "NAME_CONTACT_1", "EMAIL_CONTACT1",
            "PHONE_CONTACT1", "COMMENT_CONTACT1")

        val vacancyDto = VacancyDtoForCreate(
            "TEST_NAME_VACANCY_2_COMPANY_1",
            "TEST_COMMENT_VACANCY_2_COMPANY_1",
            companyDto1.n,
            contactDto.n
        )
        val answer = given().contentType(io.restassured.http.ContentType.JSON)
            .body(vacancyDto).`when`().post(CONSTS.VACANCY_PATH)

        answer.Extract { this.statusCode() == 200 } // assert variant 1
        assertEquals(200, answer.statusCode) // assert variant 2
    }

    @Test
    @DisplayName("Create Vacancy with NOT valid Dto (name short). Check message.")
    fun createWith_NOT_ValidDto_NAME_SHORT_check_error_message() {
        val companyDto1 = CompanyDto(1L, "COMPANY_1")
        val vacancyDto = VacancyDtoForCreate(
            "1234", // width  >=5
            "COMMENT_VACANCY_2_COMPANY_1",
            companyDto1.n
        )
        val errorMessage = given().contentType(io.restassured.http.ContentType.JSON)
            .body(vacancyDto).`when`().post(CONSTS.VACANCY_PATH).andReturn().then().extract().path<String>("message")

        assertEquals(
            "VacancyDto(name='1234', comment='COMMENT_VACANCY_2_COMPANY_1', company_n=1, contact_n=0) has errors: размер должен находиться в диапазоне от 5 до 50",
            errorMessage
        )
    }

    @Test
    @DisplayName("Create Vacancy with NOT valid Dto (name short). Check status code.")
    fun createWith_NOT_ValidDto_NAME_SHORT_check_status_code() {
        val companyDto1 = CompanyDto(1L, "COMPANY_1")
        val vacancyDto = VacancyDtoForCreate(
            "1234", // width  < 5 chars
            "COMMENT_VACANCY_2_COMPANY_1",
            companyDto1.n
        )

        val answer = given().contentType(io.restassured.http.ContentType.JSON)
            .body(vacancyDto).`when`().post(CONSTS.VACANCY_PATH).andReturn()

        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, answer.statusCode)
    }

    @Test
    @DisplayName("Create Vacancy with NOT exist company. Check status code.")
    fun createWithNotExistCompanyCheckStatusCode() {
        baseURI = CONSTS.VACANCY_PATH

        val ID_NOT_EXIST_COMPANY = -100L
        val vacancyDto = VacancyDtoForCreate(
            "NAME_VACANCY",
            "COMMENT",
            ID_NOT_EXIST_COMPANY
        )
        val answer = given().contentType(io.restassured.http.ContentType.JSON)
            .body(vacancyDto).`when`().post(CONSTS.VACANCY_PATH).andReturn()

        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, answer.statusCode)
    }

    @Test
    @DisplayName("Create Vacancy with NOT exist company. Check status code.")
    fun createWithNotExistCompanyCheckMessage() {
        val ID_NOT_EXIST_COMPANY = -100L
        val vacancyDto = VacancyDtoForCreate(
            "NAME_VACANCY",
            "COMMENT",
            ID_NOT_EXIST_COMPANY
        )
        val errorMessage = given().contentType(io.restassured.http.ContentType.JSON)
            .body(vacancyDto).`when`().post(CONSTS.VACANCY_PATH).andReturn().then().extract().path<String>("message")


        assertEquals("Company with N=-100 not found", errorMessage)
    }

    @Test
    @DisplayName("Update Vacancy with NOT exist company. Check error message.")
    fun updateWithNotExistCompanyCheckMessage() {
        val ID_NOT_EXIST_COMPANY = -100L
        val vacancyDto = VacancyDtoForCreate(
            "NAME_VACANCY",
            "COMMENT",
            ID_NOT_EXIST_COMPANY
        )
        val errorMessage = given().contentType(io.restassured.http.ContentType.JSON)
            .body(vacancyDto).`when`().post(CONSTS.VACANCY_PATH).andReturn().then().extract().path<String>("message")


        assertEquals("Company with N=-100 not found", errorMessage)
    }

    //TODO: test update Not exist vacancy
    @Test
    @DisplayName("Update NOT exist vacancy. Check error message.")
    fun updateNotExistVacancyCheckMessage() {
        val ID_NOT_EXIST_VACANCY = -100L
        val contactDto = ContactDto(1L, "NAME_CONTACT_1", "EMAIL_CONTACT1",
            "PHONE_CONTACT1", "COMMENT_CONTACT1")
        val vacancyDto = VacancyDto(
            ID_NOT_EXIST_VACANCY,
            "NAME_VACANCY",
            "COMMENT",
            CompanyDto(1L, ""), // any EXIST!!! company
            contactDto
        )

        val errorMessage = given().contentType(io.restassured.http.ContentType.JSON)
            .body(vacancyDto).`when`().post(CONSTS.VACANCY_PATH + ID_NOT_EXIST_VACANCY).andReturn().then().extract()
            .path<String>("message")


        assertEquals("Vacancy with N=-100 not found", errorMessage)
    }

    @Test
    @DisplayName("Delete NOT exist vacancy. Check error message.")
    fun deleteNotExistVacancyCheckMessage() {
        val ID_NOT_EXIST_VACANCY = 100L

        val errorMessage = given().contentType(io.restassured.http.ContentType.JSON)
            .`when`().delete(CONSTS.VACANCY_PATH + ID_NOT_EXIST_VACANCY).andReturn().then().extract()
            .path<String>("message")


        assertEquals("Vacancy with N=100 not found", errorMessage)
    }

    @Test
    @DisplayName("Delete exist vacancy.")
    fun deleteExistVacancy() {
        val ID_EXIST_VACANCY = 4L
        given().`when`().delete("/" + ID_EXIST_VACANCY).then().statusCode(HttpStatus.SC_OK)

        // verify vacancy deleted?
        val answer = given().`when`().get("/" + ID_EXIST_VACANCY).then()
            .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
            .contentType("application/json")

        answer.Extract { this.statusCode() == 500 }
    }

    @Test
    @DisplayName("Update EXIST Vacancy with EXIST company. Verify result as string.")
    fun updateVacancy_withVerifyResultAsString() {
        val VACANCY_ID = 4L // vacancy with id=4 EXIST, see import.sql in vacancy project
        val NEW_COMPANY_ID = 1L
        val NEW_COMPANY_Dto = CompanyDto()
        NEW_COMPANY_Dto.n = NEW_COMPANY_ID
        val contactDto = ContactDto(1L, "NAME_CONTACT_1", "EMAIL_CONTACT1",
            "PHONE_CONTACT1", "COMMENT_CONTACT1")
        val vacancyDto = VacancyDto(
            VACANCY_ID,
            "NAME_VACANCY",
            "COMMENT",
            NEW_COMPANY_Dto,
            contactDto
        )
        val resultMessage = given().contentType(io.restassured.http.ContentType.JSON)
            .body(vacancyDto).`when`().post(CONSTS.VACANCY_PATH + VACANCY_ID).andReturn()

        logger.info(resultMessage.body.asString())
        assertEquals("{\"n\":4,\"name\":\"NAME_VACANCY\",\"comment\":\"COMMENT\",\"company\":{\"n\":1,\"name\":\"COMPANY_1\"}}", resultMessage.body.asString())
    }

    @Test
    @DisplayName("Update EXIST Vacancy with EXIST company. Verify result as object.")
    fun updateVacancy_withVerifyResultAsObject() {
        val VACANCY_ID = 4L // vacancy with id=4 EXIST, see import.sql in vacancy project
        val NEW_COMPANY_ID = 1L
        val NEW_COMPANY_Dto = CompanyDto()
        NEW_COMPANY_Dto.n = NEW_COMPANY_ID
        val contactDto = ContactDto(1L, "NAME_CONTACT_1", "EMAIL_CONTACT1",
            "PHONE_CONTACT1", "COMMENT_CONTACT1")

        val vacancyDto = VacancyDto(
            VACANCY_ID,
            "NAME_VACANCY",
            "COMMENT",
            NEW_COMPANY_Dto,
            contactDto
        )
        val resultMessage = given().contentType(io.restassured.http.ContentType.JSON)
            .body(vacancyDto).`when`().post(CONSTS.VACANCY_PATH + VACANCY_ID).andReturn()

        logger.info(resultMessage.body.asString())

        val json = resultMessage.body.asString()
        val receivedVacancyDto = ObjectMapper().readValue(json, VacancyDto::class.java)
        val expectedVacancyDto = VacancyDto(
            VACANCY_ID,
            "NAME_VACANCY",
            "COMMENT",
            CompanyDto(1, "COMPANY_1"),
            contactDto
        )

        assertEquals(expectedVacancyDto, receivedVacancyDto)
    }

    @Test
    @DisplayName("Delete EXIST vacancy by ID=4")
    fun deleteAndCheckDeletedInCache() {
        // plan :
        // 0. before ALL init db. see
        //       @BeforeEach
        //       fun setup() {...}
        // http http://127.0.0.1:8980/vacancy/api/init/reimport_db
        // 1. get by id for verify exist
        // 2. delete by id
        // 3. get by id for check deleted AND DELETED FROM CACHE
        // Run test: ./gradlew test --tests '*deleteAndCheckDeletedInCache*'
        val ID_EXIST_VACANCY = 4L
        // 0. Reimport
        baseURI = CONSTS.HOST + "/init/reimport_db"
        given().`when`().get().then()
            .statusCode(HttpStatus.SC_OK)

        baseURI = CONSTS.VACANCY_PATH
        // 1. get by id for verify exist
        given().`when`().get("/" + ID_EXIST_VACANCY).then()
            .statusCode(HttpStatus.SC_OK)
        // 2. delete by id
        given().`when`().delete("/" + ID_EXIST_VACANCY).then().statusCode(HttpStatus.SC_OK)
        // 3. get by id for check deleted AND DELETED FROM CACHE
//        given().`when`().get("/" + ID_EXIST_VACANCY).then()
//            .statusCode(HttpStatus.SC_OK)

    }
}