package org.esupportail.cas.adaptors.esupotp.web.flow;

import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * This is {@link EsupOtpAuthenticationWebflowAction}.
 *
 * @author Alex Bouskine
 * @since 5.0.0
 */
@RefreshScope
@Component("esupotpAuthenticationWebflowAction")
public class EsupOtpAuthenticationWebflowAction extends AbstractAction {
    @Autowired
    @Qualifier("esupotpAuthenticationWebflowEventResolver")
    private CasWebflowEventResolver yubikeyAuthenticationWebflowEventResolver;

    @Override
    protected Event doExecute(final RequestContext requestContext) throws Exception {
        return this.yubikeyAuthenticationWebflowEventResolver.resolveSingle(requestContext);
    }
}