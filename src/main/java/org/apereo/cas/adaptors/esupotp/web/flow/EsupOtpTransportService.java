package org.apereo.cas.adaptors.esupotp.web.flow;

import org.apereo.cas.adaptors.esupotp.EsupOtpTransportCredential;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.action.EventFactorySupport;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * This is {@link EsupOtpSencCodeAction}.
 *
 * @author Alex Bouskine
 * @since 5.0.0
 */
@RefreshScope
@Component("esupotpTransportService")
public class EsupOtpTransportService {

	public String sendCode(EsupOtpTransportCredential transportCredential) {

		return "success";
	}
}
