package org.example;

public class Post {
    public int id;
    public String title;
    public String body;
    public int userId;

    // Construtor para facilitar a criação
    public Post(String title, String body, int userId, int id) {
        this.title = title;
        this.body = body;
        this.userId = userId;
        this.id = id;
    }

    public Post() {
    }
}