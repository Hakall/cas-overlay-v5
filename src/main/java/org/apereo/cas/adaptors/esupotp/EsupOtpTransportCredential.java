package org.apereo.cas.adaptors.esupotp;

import java.io.Serializable;

/**
 * This is {@link EsupOtpCredential}.
 *
 * @author Alex Bouskine
 * @since 5.0.0
 */
public class EsupOtpTransportCredential implements Serializable {
    private static final long serialVersionUID = 6182390898687671093L;

    private String transport;
    
    private String method;

    /**
     * Instantiates a new Esup otp token credential.
     */
    public EsupOtpTransportCredential() {
    }

    public String getTransport() {
        return this.transport;
    }

    public void setTransport(final String transport) {
        this.transport = transport;
    }
    
    public String getMethod() {
        return this.method;
    }

    public void setMethod(final String method) {
        this.method = method;
    }
}
