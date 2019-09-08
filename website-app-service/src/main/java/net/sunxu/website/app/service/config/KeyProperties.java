package net.sunxu.website.app.service.config;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ToString
@Component
@ConfigurationProperties("website.key")
@Valid
public class KeyProperties {

    private String publicKeyPath = "client.crt";

    @NotEmpty
    private String publicKeyType;

    private String privateKeyPath = "key.keystore";

    @NotEmpty
    private String privateKeyType;

    @NotEmpty
    private String password;

    @NotEmpty
    private String alias;
}
