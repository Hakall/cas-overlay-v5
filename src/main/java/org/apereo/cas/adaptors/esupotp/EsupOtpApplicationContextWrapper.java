package org.apereo.cas.adaptors.esupotp;

import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationMetaDataPopulator;
import org.apereo.cas.web.BaseApplicationContextWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * This is {@link EsupOtpApplicationContextWrapper}.
 *
 * @author Alex Bouskine
 * @since 5.0.0
 */
@Component("esupotpApplicationContextWrapper")
public class EsupOtpApplicationContextWrapper extends BaseApplicationContextWrapper {
    @Autowired
    @Qualifier("esupotpAuthenticationHandler")
    private AuthenticationHandler authenticationHandler;

    @Autowired
    @Qualifier("esupotpAuthenticationMetaDataPopulator")
    private AuthenticationMetaDataPopulator populator;

    /**
     * Initialize root application context.
     */
    @PostConstruct
    protected void initializeRootApplicationContext() {
        addAuthenticationHandler(this.authenticationHandler);
        addAuthenticationMetadataPopulator(this.populator);
    }
}
