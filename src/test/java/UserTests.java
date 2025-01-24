import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class UserTests {

    private String userId; // Переменная для хранения id созданного пользователя

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://reqres.in";
        RestAssured.basePath = "/api";
    }

    // Позитивные тесты
    @Test
    @DisplayName("Проверка успешного создания и удаления пользователя")
    void createAndDeleteUserTest() {

        String userData = "{\"name\": \"nata\", \"job\": \"QA\"}";

        userId = given()
                .body(userData)
                .contentType(JSON)
                .log().uri()

                .when()
                .post("/users")

                .then()
                .log().status()
                .log().body()
                .statusCode(201)
                .body("name", is("nata"))
                .body("job", is("QA"))
                .body("id", notNullValue())
                .body("createdAt", notNullValue())
                .extract().path("id"); // Сохраняем id созданного пользователя


        given()
                .log().uri()

                .when()
                .delete("/users/" + userId)

                .then()
                .log().status()
                .statusCode(204);
    }

    @Test
    @DisplayName("Проверка успешного создания и частичного изменения пользователя")
    void createAndUpdateUserTest() {
                String userData = "{\"name\": \"nata\", \"job\": \"QA\"}";

        userId = given()
                .body(userData)
                .contentType(JSON)
                .log().uri()

                .when()
                .post("/users")

                .then()
                .log().status()
                .log().body()
                .statusCode(201)
                .body("name", is("nata"))
                .body("job", is("QA"))
                .body("id", notNullValue())
                .body("createdAt", notNullValue())
                .extract().path("id");


        String updateData = "{\"job\": \"AutoQA\"}";

        given()
                .body(updateData)
                .contentType(JSON)
                .log().uri()

                .when()
                .patch("/users/" + userId)

                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("job", is("AutoQA"))
                .body("updatedAt", notNullValue());
    }

    @Test
    @DisplayName("Проверка успешного получения списка пользователей")
    void successfulGetUserDataTest() {
        given()
                .when()
                .queryParam("page", "1")
                .get("/users/")

                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("total", is(12));
    }

    @Test
    @DisplayName("Проверка создания пользователя без передачи тела")
    void createUserWithoutBodyTest() {
        given()
                .contentType(JSON)
                .log().uri()

                .when()
                .post("/users")

                .then()
                .log().status()
                .log().body()
                .statusCode(201)
                .body("id", notNullValue())
                .body("createdAt", notNullValue());
    }

    @Test
    @DisplayName("Проверка создания пользователя с неполными данными")
    void createUserWithIncompleteDataTest() {
        String incompleteUserData = "{\"job\": \"QA\"}";

        given()
                .body(incompleteUserData)
                .contentType(JSON)
                .log().uri()

                .when()
                .post("/users")

                .then()
                .log().status()
                .log().body()
                .statusCode(201)
                .body("id", notNullValue())
                .body("job", is("QA"))
                .body("createdAt", notNullValue());
    }

    @Test
    @DisplayName("Проверка изменения несуществующего пользователя")
    void updateNonExistentUserTest() {
        String updateData = "{\"job\": \"AutoQA\"}";
        String nonExistentUserId = "999999"; // Несуществующий ID пользователя

        given()
                .body(updateData)
                .contentType(JSON)
                .log().uri() // Логируем URI запроса

                .when()
                .patch("/users/" + nonExistentUserId) // Пытаемся обновить несуществующего пользователя

                .then()
                .log().status() // Логируем статус ответа
                .log().body() // Логируем тело ответа
                .statusCode(200) // Ожидаем статус код 200 (OK)
                .body("job", is("AutoQA")) // Проверяем, что job обновлен
                .body("updatedAt", notNullValue()); // Проверяем, что updatedAt присутствует
    }
}

