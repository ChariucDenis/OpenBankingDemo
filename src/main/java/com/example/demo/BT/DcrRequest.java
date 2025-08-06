package com.example.demo.BT;

import java.util.List;

public class DcrRequest {
    private List<String> redirect_uris;
    private String client_name;

    // Getters + Setters
    public List<String> getRedirect_uris() { return redirect_uris; }
    public void setRedirect_uris(List<String> redirect_uris) { this.redirect_uris = redirect_uris; }

    public String getClient_name() { return client_name; }
    public void setClient_name(String client_name) { this.client_name = client_name; }
}