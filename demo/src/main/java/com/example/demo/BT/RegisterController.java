package com.example.demo.BT;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;
@CrossOrigin(origins = {
        "http://localhost:3000", // pentru development local
        "https://tangerine-frangollo-da6fa4.netlify.app" // frontend Netlify
})
@RestController
@RequestMapping("/bt")
public class RegisterController {

    private final RestTemplate   rest = new RestTemplate();
    private final BtContextStore ctx;          // bean singleton injectat

    public RegisterController(BtContextStore ctx) {
        this.ctx = ctx;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerClient(@RequestBody Map<String, String> request) {
        String clientName  = request.get("clientName");
        String redirectUri = request.get("redirectUri");

        if (clientName == null || redirectUri == null) {
            return ResponseEntity.badRequest().body("Lipsește clientName sau redirectUri");
        }

        String url = "https://api.apistorebt.ro/bt/sb/oauth/register";

        DcrRequest payload = new DcrRequest();
        payload.setClient_name(clientName);
        payload.setRedirect_uris(Collections.singletonList(redirectUri));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<DcrResponse> resp = rest.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(payload, headers),
                DcrResponse.class
        );

        DcrResponse body = resp.getBody();
        if (body != null) {
            // salvăm doar client_id și client_secret în ctx
            ctx.setClient(body.getClientId(), body.getClientSecret());

            // dar returnăm și ce a fost trimis ca input
            return ResponseEntity.ok(Map.of(
                    "client_id",     body.getClientId(),
                    "client_secret", body.getClientSecret(),
                    "client_name",   clientName,
                    "redirect_uri",  redirectUri
            ));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("BT a răspuns fără corp JSON");
        }
    }



    /**  GET /bt/credentials – pentru debug rapid  */
    @GetMapping("/credentials")
    public ResponseEntity<?> showCredentials() {
        if (!ctx.hasClient()) {
            return ResponseEntity.badRequest()
                    .body("Nu există încă date; rulează /bt/register mai întâi.");
        }
        return ResponseEntity.ok(
                Map.of("client_id", ctx.getClientId(),
                        "client_secret", ctx.getClientSecret())
                );
    }
}