import io.restassured.RestAssured;
import org.example.Post;
import org.example.PostService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class helloWorldTests {
    PostService postService = new PostService();

    @BeforeAll
    public static void setup(){
        baseURI = "https://jsonplaceholder.typicode.com";
        requestSpecification = given().contentType("application/json"); //.auth().oauth2("seu_token_aqui")
    }

    @Test
    void meuTeste(){
        given()
                .log().all()
                .when()
                .get("/users/1")
                .then()
                .log().all()
                .statusCode(200)
                .body("name", is("Leanne Graham"))
                .body("address.geo.lng", is("81.1496"));
    }


    @Test
    void meuTeste2(){
        given()
                .log().all()
                .when()
                .get("/users/0000")
                .then()
                .statusCode(404);
    }

    @Test
    void meuTeste3(){
        given()
                .log().all()
                .queryParam("userId", "1")
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .body("title[0]", is("sunt aut facere repellat provident occaecati excepturi optio reprehenderit"));

    }

    @Test
    void meuTeste4(){
        Post novoPost = new Post("Titulo", "Corpo", 1, 1);

        // Act (Ação)
        Post resposta = postService.criarPost(novoPost)
                .then()
                .time(lessThan(2000L))
                .statusCode(201)
                .extract()
                .as(Post.class);

        assertEquals(novoPost.title, resposta.title, "O título retornado está incorreto!");
    }
}
