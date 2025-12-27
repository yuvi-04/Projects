package com.demo.demoproject.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {
    private static final Logger authLogger = LoggerFactory.getLogger("AUTH_LOGGER");

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        String details = "";
        if(event.getAuthentication().getDetails() instanceof WebAuthenticationDetails wad) {
            details = wad.getRemoteAddress();
        }
        authLogger.info("[AUTH SUCCESS] user={} remoteIp={}", username, details);
    }

}
