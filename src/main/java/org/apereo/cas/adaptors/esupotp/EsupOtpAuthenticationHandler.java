package org.apereo.cas.adaptors.esupotp;

import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

import javax.annotation.PostConstruct;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import java.security.GeneralSecurityException;

/**
 * An authentication handler that uses the token provided
 * to authenticator against google authN for MFA.
 *
 * @author Alex Bouskine
 * @since 5.0.0
 */
@RefreshScope
@Component("esupotpAuthenticationHandler")
public class EsupOtpAuthenticationHandler extends AbstractPreAndPostProcessingAuthenticationHandler {

    /**
     * Instantiates a new Esup otp authentication handler.
     */
    public EsupOtpAuthenticationHandler() {
    }

    /**
     * Init.
     */
    @PostConstruct
    public void init() {
    }

    @Override
    protected HandlerResult doAuthentication(final Credential credential) throws GeneralSecurityException, PreventedException {
        final EsupOtpCredential esupotpCredential = (EsupOtpCredential) credential;
        final String otp = esupotpCredential.getToken();
        final RequestContext context = RequestContextHolder.getRequestContext();
        final String uid = WebUtils.getAuthentication(context).getPrincipal().getId();
        //always accept auth for dev
        return createHandlerResult(esupotpCredential,this.principalFactory.createPrincipal(uid), null);
        //throw new FailedLoginException("Failed to authenticate code " + otp);
    }

    @Override
    public boolean supports(final Credential credential) {
        return EsupOtpCredential.class.isAssignableFrom(credential.getClass());
    }
}
