package tests;

import models.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static specs.TestSpec.*;

public class UserTests extends TestBase {

    // Позитивные тесты
    @Test
    @DisplayName("Проверка успешного создания пользователя")
    void createUserTest() {
        LoginRequestModel userData = new LoginRequestModel();
        userData.setName("Nata");
        userData.setJob("QA");
        CreateLoginResponseModel response = step("Создаем нового пользователя с именем и работой",
                () -> given(requestSpec)
                        .body(userData)
                        .when()
                        .post("/users")
                        .then()
                        .spec(statusCode201Spec)
                        .extract().as(CreateLoginResponseModel.class));
        step("Проверяем ответ на запрос", () -> {
            assertThat(response.getName()).isEqualTo("Nata");
            assertThat(response.getJob()).isEqualTo("QA");
            assertThat(response.getId()).isNotNull();
            assertThat(response.getCreatedAt()).isNotNull();
        });
    }

    @Test
    @DisplayName("Проверка успешного удаления пользователя")
    void deleteUserDataTest() {
        step("Удаляем пользователя", () -> {
            given(requestSpec)
                    .when()
                    .delete("users/2")
                    .then()
                    .spec(statusCode204Spec);
        });
    }

    @Test
    @DisplayName("Проверка успешного изменения пользователя")
    void updateUserTest() {
        LoginRequestModel userData = new LoginRequestModel();
        userData.setName("Nata");
        userData.setJob("Auto_QA");
        UpdateLoginResponseModel response = step("Отправляем запрос на изменение пользователя", () ->
                given(requestSpec)
                        .body(userData)
                        .when()
                        .put("/users/2")
                        .then()
                        .spec(statusCode200Spec)
                        .extract().as(UpdateLoginResponseModel.class));

        step("Проверяем ответ на запрос", () -> {
            assertThat(response.getName()).isEqualTo("Nata");
            assertThat(response.getJob()).isEqualTo("Auto_QA");
            assertThat(response.getUpdatedAt()).isNotNull();
        });

    }

    @Test
    @DisplayName("Проверка успешного получения списка пользователей")
    void successfulGetUserDataTest() {
        given(requestSpec)
                .when()
                .queryParam("page", "1")
                .get("/users")
                .then()
                .spec(statusCode200Spec)
                .body("total", is(12));
    }

    @Test
    @DisplayName("Проверка отсутствия данных пользователя")
    void checkMissingUserDataTest() {
        UpdateLoginResponseModel response = step("Отправляем запрос на проверку отсутствия пользователя",
                () -> given(requestSpec)
                        .when()
                        .get("users/23")
                        .then().log().all()
                        .statusCode(404)
                        .extract().as(UpdateLoginResponseModel.class));

        step("Проверяем отсутствие пользователя", () -> {
            assertThat(response.getName()).isNull();
            assertThat(response.getJob()).isNull();
            assertThat(response.getUpdatedAt()).isNull();
        });
    }

    @Test
    @DisplayName("Проверка создания пользователя с неполными данными")
    void createUserWithIncompleteDataTest() {
        LoginRequestModel userData = new LoginRequestModel();
        userData.setName("Nata");
        CreateLoginResponseModel response = step("Делаем запрос на создание нового пользователя с именем",
                () -> given(requestSpec)
                        .body(userData)
                        .when()
                        .post("/users")
                        .then()
                        .spec(statusCode201Spec)
                        .extract().as(CreateLoginResponseModel.class));
        step("Проверяем ответ на запрос", () -> {
            assertThat(response.getName()).isEqualTo("Nata");
            assertThat(response.getId()).isNotNull();
            assertThat(response.getCreatedAt()).isNotNull();
        });
    }

}

