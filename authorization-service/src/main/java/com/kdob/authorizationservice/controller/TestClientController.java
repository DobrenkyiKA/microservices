package com.kdob.authorizationservice.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class TestClientController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/userinfo")
    @ResponseBody
    public Map<String, Object> userInfo(@AuthenticationPrincipal OidcUser user) {
        if (user != null) {
            return Map.of(
                    "username", user.getPreferredUsername(),
                    "email", user.getEmail() != null ? user.getEmail() : "N/A",
                    "authorities", user.getAuthorities(),
                    "claims", user.getClaims()
            );
        }
        return Map.of("error", "Not authenticated");
    }

    @GetMapping("/secured")
    public String secured(Model model, @AuthenticationPrincipal OidcUser user) {
        if (user != null) {
            model.addAttribute("username", user.getPreferredUsername());
            model.addAttribute("authorities", user.getAuthorities());
        }
        return "secured";
    }
}