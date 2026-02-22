package org.example;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class PostService {
    public Response criarPost(Post post) {
        return given()
                .body(post)
                .when()
                .post("/posts");
    }

    public Response buscarPostPorId(int id) {
        return given()
                .pathParam("id", id)
                .when()
                .get("/posts/{id}");
    }
}
