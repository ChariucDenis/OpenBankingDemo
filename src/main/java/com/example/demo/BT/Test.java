package com.example.demo.BT;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class Test {
    @GetMapping("/posts")
    public ResponseEntity<String> getPosts() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://jsonplaceholder.typicode.com/posts";
        String response = restTemplate.getForObject(url, String.class);
        return ResponseEntity.ok(response);
    }
}
