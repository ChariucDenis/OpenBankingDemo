package com.example.demo.BT;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
@CrossOrigin(origins = {
        "http://localhost:3000", // pentru development local
        "https://tangerine-frangollo-da6fa4.netlify.app" // frontend Netlify
})
@RestController
@RequestMapping("/bt")
public class TokenController {

    private final RestTemplate   rest = new RestTemplate();
    private final BtContextStore ctx;
    private final ObjectMapper   mapper = new ObjectMapper();

    private static final String code_verifier = "UVesdGMKR8LZgCzV4MH1Mcr6d4KQ0mVVl0owlxT0PLI";
    private static final String TOKEN_URL =
            "https://api.apistorebt.ro/bt/sb/oauth/token";

    public TokenController(BtContextStore ctx) {
        this.ctx = ctx;
    }

    /**
     *  POST /bt/token
     *  Body JSON: { "auth_code": "xxxxxxxx" }
     */
    @PostMapping("/token")
    public ResponseEntity<String> exchangeCode(@RequestBody Map<String,String> body) throws Exception {

        String authCode = body.get("auth_code");
        if (authCode == null || authCode.isBlank())
            return ResponseEntity.badRequest().body("Lipsește auth_code în body!");

        /* -------- form-urlencoded payload ----------------------------- */
        String form = "grant_type=authorization_code"
                + "&code="          + encode(authCode)
                + "&redirect_uri="  + encode("https://google.com")
                + "&client_id="     + encode(ctx.getClientId())      // client_secret_post
                + "&client_secret=" + encode(ctx.getClientSecret())
                + "&code_verifier=" + encode(code_verifier);
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> req = new HttpEntity<>(form, h);

        ResponseEntity<String> resp = rest.exchange(
                TOKEN_URL, HttpMethod.POST, req, String.class);

        if (resp.getStatusCode().is2xxSuccessful()) {
            /* extragem token-urile și le salvăm */
            JsonNode json = mapper.readTree(resp.getBody());
            String access  = json.path("access_token").asText();
            String refresh = json.path("refresh_token").asText();
            long   exp     = json.path("expires_in").asLong();
            ctx.setTokens(access, refresh, exp);
        }

        return resp;   // întoarcem payload-ul BT ca atare
    }

    private static String encode(String v){
        return URLEncoder.encode(v, StandardCharsets.UTF_8);
    }
}
