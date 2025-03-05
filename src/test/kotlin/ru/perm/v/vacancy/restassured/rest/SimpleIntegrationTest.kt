package ru.perm.v.vacancy.restassured.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.restassured.RestAssured
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.test.context.jdbc.Sql

//@SpringBootTest
class SimpleIntegrationTest {

    @Test
    @Sql("/import.sql")
    fun getAllVacancy() {
        RestAssured.baseURI = CONSTS.VACANCY_PATH
        val json = RestAssured.get("/").body.asString()
        val vacancies: List<VacancyDto> = ObjectMapper().readValue(json)

        Assertions.assertEquals(4, vacancies.size)

    }

}