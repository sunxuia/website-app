package net.sunxu.website.app.dto;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.util.Base64;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PublicKeyDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    private String type;

    private String publicKey;

    public PublicKey readPublicKey() {
        try {
            var inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(publicKey));
            CertificateFactory certificatefactory = CertificateFactory.getInstance(type);
            var cert = certificatefactory.generateCertificate(inputStream);
            return cert.getPublicKey();
        } catch (Exception err) {
            throw new RuntimeException(err);
        }
    }
}
