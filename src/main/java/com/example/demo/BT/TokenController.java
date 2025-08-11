package com.example.demo.BT;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bt")
public class TokenController {

    private final RestTemplate   rest;
    private final BtContextStore ctx;
    private final ObjectMapper   mapper = new ObjectMapper();

    // code_verifier fix (exact ca în codul tău inițial)
    private static final String CODE_VERIFIER = "UVesdGMKR8LZgCzV4MH1Mcr6d4KQ0mVVl0owlxT0PLI";
    private static final String TOKEN_URL = "https://api.apistorebt.ro/bt/sb/oauth/token";

    public TokenController(BtContextStore ctx) {
        this.ctx = ctx;
        SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
        f.setConnectTimeout((int) Duration.ofSeconds(10).toMillis());
        f.setReadTimeout((int) Duration.ofSeconds(60).toMillis());
        this.rest = new RestTemplate(f);
    }

    @PostMapping("/token")
    public ResponseEntity<String> exchangeCode(@RequestBody Map<String,String> body) throws Exception {

        String authCode = body.get("auth_code");
        if (authCode == null || authCode.isBlank()) {
            return ResponseEntity.badRequest().body("Lipsește auth_code în body!");
        }

        String redirectUri = ctx.getRedirectUri();

        // ---------- Încercare #1: client_secret_basic + X-IBM-* ----------
        HttpHeaders h1 = new HttpHeaders();
        h1.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        h1.setAccept(List.of(MediaType.APPLICATION_JSON));
        h1.set("User-Agent", "bt-psd2-demo/1.0");
        h1.setBasicAuth(ctx.getClientId(), ctx.getClientSecret());
        h1.set("X-IBM-Client-ID", ctx.getClientId());
        h1.set("X-IBM-Client-Secret", ctx.getClientSecret());

        String form1 =
                "grant_type=authorization_code" +
                        "&code="           + enc(authCode) +
                        "&redirect_uri="   + enc("https://openbankingdemo.onrender.com") +
                        "&code_verifier="  + enc(CODE_VERIFIER);

        try {
            ResponseEntity<String> resp1 =
                    rest.exchange(TOKEN_URL, HttpMethod.POST, new HttpEntity<>(form1, h1), String.class);
            maybePersistTokens(resp1);
            return resp1;
        } catch (RestClientResponseException ex) {
            System.err.println("Token #1 failed: " + ex.getRawStatusCode() + " " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            System.err.println("Token #1 failed (exception): " + ex.getMessage());
        }

        // ---------- Încercare #2: client_secret_post + X-IBM-* ----------
        HttpHeaders h2 = new HttpHeaders();
        h2.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        h2.setAccept(List.of(MediaType.APPLICATION_JSON));
        h2.set("User-Agent", "bt-psd2-demo/1.0");
        h2.set("X-IBM-Client-ID", ctx.getClientId());
        h2.set("X-IBM-Client-Secret", ctx.getClientSecret());

        String form2 =
                "grant_type=authorization_code" +
                        "&code="           + enc(authCode) +
                        "&redirect_uri="   + enc(redirectUri) +
                        "&client_id="      + enc(ctx.getClientId()) +
                        "&client_secret="  + enc(ctx.getClientSecret()) +
                        "&code_verifier="  + enc(CODE_VERIFIER);

        ResponseEntity<String> resp2 =
                rest.exchange(TOKEN_URL, HttpMethod.POST, new HttpEntity<>(form2, h2), String.class);
        maybePersistTokens(resp2);
        return resp2;
    }

    private void maybePersistTokens(ResponseEntity<String> resp) throws Exception {
        if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
            JsonNode json = mapper.readTree(resp.getBody());
            String access  = json.path("access_token").asText(null);
            String refresh = json.path("refresh_token").asText(null);
            long   exp     = json.path("expires_in").asLong(0);
            if (access != null) {
                ctx.setTokens(access, refresh, exp);
            }
        }
    }

    private static String enc(String v) {
        return URLEncoder.encode(v, StandardCharsets.UTF_8);
    }
}
