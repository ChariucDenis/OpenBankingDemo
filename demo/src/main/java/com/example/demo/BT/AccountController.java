package com.example.demo.BT;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
@CrossOrigin(origins = {
        "http://localhost:3000", // pentru development local
        "https://tangerine-frangollo-da6fa4.netlify.app" // frontend Netlify
})
@RestController
public class AccountController {

    private final BtContextStore ctx;
    private final RestTemplate rest = new RestTemplate();

    private static final String URL =
            "https://api.apistorebt.ro/bt/sb/bt-psd2-aisp/v2/accounts";

    public AccountController(BtContextStore ctx) {
        this.ctx = ctx;
    }

    @GetMapping("/bt/accounts")
    public ResponseEntity<String> getAccounts() {

        if (ctx.getAccessToken() == null || ctx.getConsentId() == null)
            return ResponseEntity.badRequest().body("Fără token sau consent. Refă flow-ul.");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + ctx.getAccessToken());
        headers.set("X-Request-ID", UUID.randomUUID().toString());
        headers.set("Consent-ID", ctx.getConsentId());
        headers.set("PSU-IP-Address", "192.168.0.10"); // dummy IP

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = rest.exchange(
                URL,
                HttpMethod.GET,
                request,
                String.class
        );

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }
}
