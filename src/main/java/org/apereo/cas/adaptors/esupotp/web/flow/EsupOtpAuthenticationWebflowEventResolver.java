package org.apereo.cas.adaptors.esupotp.web.flow;

import org.apereo.cas.web.flow.resolver.AbstractCasWebflowEventResolver;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import java.util.Set;

/**
 * This is {@link EsupOtpAuthenticationWebflowEventResolver}.
 *
 * @author Alex Bouskine
 * @since 5.0.0
 */
@RefreshScope
@Component("esupotpAuthenticationWebflowEventResolver")
public class EsupOtpAuthenticationWebflowEventResolver extends AbstractCasWebflowEventResolver {
    @Override
    protected Set<Event> resolveInternal(final RequestContext context) {
        return handleAuthenticationTransactionAndGrantTicketGrantingTicket(context);
    }

}
