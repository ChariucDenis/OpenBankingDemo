package com.example.demo.BT;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
@CrossOrigin(origins = {
        "http://localhost:3000", // pentru development local
        "https://tangerine-frangollo-da6fa4.netlify.app" // frontend Netlify
})
@RestController
@RequestMapping("/bt")
public class ConsentController {

    private final RestTemplate rest = new RestTemplate();
    private final BtContextStore ctx;                 // injectăm contextul
    private final ObjectMapper mapper = new ObjectMapper();

    private static final String CONSENT_URL =
            "https://api.apistorebt.ro/bt/sb/bt-psd2-aisp/v2/consents";

    public ConsentController(BtContextStore ctx) {
        this.ctx = ctx;
    }

    /**  POST /bt/consent  – creează consent şi salvează consentId  */
    @PostMapping("/consent")
    public ResponseEntity<String> createConsent() throws Exception {

        /* ---------- HEADERS obligatorii ------------------------------ */
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("PSU-IP-Address", "192.168.0.10");      // IP dummy
        headers.add("X-Request-ID", UUID.randomUUID().toString());

        /* ---------- BODY exact după specs --------------------------- */
        Map<String,Object> body = new HashMap<>();
        body.put("access", Collections.singletonMap("availableAccounts", "allAccounts"));
        body.put("recurringIndicator", true);
        body.put("validUntil", "2025-08-28");
        body.put("combinedServiceIndicator", false);
        body.put("frequencyPerDay", 4);

        /* ---------- POST către BT sandbox --------------------------- */
        ResponseEntity<String> resp = rest.exchange(
                CONSENT_URL,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                String.class
        );

        /* ---------- Extragem consentId & scaOAuth.href -------------- */
        JsonNode root = mapper.readTree(resp.getBody());
        String consentId = root.path("consentId").asText();
        String scaHref   = root.path("_links").path("scaOAuth").path("href").asText();

        ctx.setConsent(consentId, scaHref);   // salvăm în BtContextStore

        return ResponseEntity.status(resp.getStatusCode()).body(resp.getBody());
    }
}
