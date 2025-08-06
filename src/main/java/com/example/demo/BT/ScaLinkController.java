package com.example.demo.BT;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bt")
public class ScaLinkController {

    private final BtContextStore ctx;

    public ScaLinkController(BtContextStore ctx){ this.ctx = ctx; }

    @GetMapping("/sca-link")
    public ResponseEntity<String> scaLink() {
        if (!ctx.hasClient() || !ctx.hasConsent())
            return ResponseEntity.badRequest().body("Trebuie întâi /bt/register şi /bt/consent!");


        String redirect  = "https://google.com";
        String state     = "state123test";
        String nonce     = "nonce123test";
        String challenge = "atgQe5BY6w5NKbzt_Tdbeo_1MIbbK-hcnPZbbIw6f4Q";

        String url = "https://apistorebt.ro/auth/realms/psd2-sb/protocol/openid-connect/auth?"
                + "client_id=" + ctx.getClientId()
                + "&redirect_uri=" + encode(redirect)
                + "&response_type=code"
                + "&scope=AIS:" + ctx.getConsentId()
                + "&state=" + state
                + "&nonce=" + nonce
                + "&code_challenge=" + challenge
                + "&code_challenge_method=S256";

        return ResponseEntity.ok(url);
    }

    private static String encode(String v){
        return java.net.URLEncoder.encode(v, java.nio.charset.StandardCharsets.UTF_8);
    }
}
