package com.demo.demoproject.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureListener implements ApplicationListener<AbstractAuthenticationFailureEvent> {

    private static final Logger authLogger = LoggerFactory.getLogger("AUTH_LOGGER");
    private static final Logger errorLogger = LoggerFactory.getLogger("ERROR_LOGGER");

    @Override
    public void onApplicationEvent(AbstractAuthenticationFailureEvent event) {
        String username = event.getAuthentication() != null ? event.getAuthentication().getName() : "unknown";
        String remote = "";
        if(event.getAuthentication() != null && event.getAuthentication().getDetails() instanceof WebAuthenticationDetails wad) {
            remote = wad.getRemoteAddress();
        }
        authLogger.warn("[AUTH FAILURE] user={} remoteIp={} reason={}", username, remote, event.getException().getMessage());
        errorLogger.warn("[AUTH FAILURE TRACE] user={} reason={}", username, event.getException().toString());
    }

}
