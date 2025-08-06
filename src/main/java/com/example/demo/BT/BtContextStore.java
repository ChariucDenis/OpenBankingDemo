package com.example.demo.BT;

import org.springframework.stereotype.Service;

@Service
public class BtContextStore {

    private String clientId;
    private String clientSecret;
    private String consentId;
    private String scaOauthHref;

    /* token-uri */
    private String accessToken;
    private String refreshToken;
    private long   expiresIn;

    /* --- setters existente --- */
    public void setClient(String id, String secret) {
        this.clientId = id;
        this.clientSecret = secret;
    }
    public void setConsent(String consentId, String scaHref) {
        this.consentId = consentId;
        this.scaOauthHref = scaHref;
    }

    /* --- setters pentru token --- */
    public void setTokens(String access, String refresh, long exp) {
        this.accessToken  = access;
        this.refreshToken = refresh;
        this.expiresIn    = exp;
    }

    /* --- getters --- */
    public String getClientId()     { return clientId; }
    public String getClientSecret() { return clientSecret; }
    public String getConsentId()    { return consentId; }
    public String getAccessToken()  { return accessToken; }
    public boolean hasClient()  { return clientId != null && clientSecret != null; }
    public boolean hasConsent() { return consentId != null; }
    /* (alte getters la nevoie) */
}
