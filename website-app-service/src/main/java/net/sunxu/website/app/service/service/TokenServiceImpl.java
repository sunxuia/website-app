package net.sunxu.website.app.service.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import net.sunxu.website.app.dto.PublicKeyDTO;
import net.sunxu.website.app.service.config.KeyProperties;
import net.sunxu.website.app.service.entity.AppInfo;
import net.sunxu.website.help.exception.BizValidationException;
import net.sunxu.website.help.util.ObjectHelpUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Log4j2
@Service
public class TokenServiceImpl implements TokenService {

    private final PublicKeyDTO publicKeyDTO;

    private final PrivateKey privateKey;

    @Value("${spring.application.name}")
    private String applicationName;

    public TokenServiceImpl(KeyProperties keyProperties) throws Exception {
        var pubResource = findResource(keyProperties.getPublicKeyPath(), "public key");
        publicKeyDTO = new PublicKeyDTO();
        byte[] pubBytes = pubResource.getInputStream().readAllBytes();
        publicKeyDTO.setPublicKey(Base64.getEncoder().encodeToString(pubBytes));
        publicKeyDTO.setType(keyProperties.getPublicKeyType());

        var priResource = findResource(keyProperties.getPrivateKeyPath(), "private key");
        KeyStore keyStore = KeyStore.getInstance(keyProperties.getPrivateKeyType());
        var password = keyProperties.getPassword().toCharArray();
        keyStore.load(priResource.getInputStream(), password);
        privateKey = (PrivateKey) keyStore.getKey(keyProperties.getAlias(), password);
    }

    private Resource findResource(String path, String resourceName) {
        Resource resource = new ClassPathResource(path);
        if (!resource.exists()) {
            resource = new FileSystemResource(path);
        }
        Assert.isTrue(resource.exists(), String.format("[%s] resource not found", resourceName));
        return resource;
    }

    @Override
    public PublicKeyDTO getPublicKey() {
        return publicKeyDTO;
    }

    @Override
    public String createServiceToken(AppInfo appInfo) {
        var builder = Jwts.builder()
                .signWith(SignatureAlgorithm.RS256, privateKey);
        builder.claim("id", appInfo.getId());
        builder.claim("name", appInfo.getAppName());
        builder.claim("roles", appInfo.getRoles());
        builder.claim("service", true);

        var now = new Date();
        builder.setIssuedAt(now);
        builder.setExpiration(new Date(now.getTime() + 1000 * 60 * 60 * 24));
        builder.setIssuer(applicationName);
        builder.setId(UUID.randomUUID().toString());
        return builder.compact();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> createUserToken(AppInfo appInfo, List<Map<String, Object>> paras) {
        Map<String, String> res = new HashMap<>(paras.size());
        for (Map<String, Object> para : paras) {
            String tokenName = (String) para.get("tokenName");
            BizValidationException.assertNotNull(tokenName, "token name not exist");
            Object expire = para.get("expire");
            BizValidationException.assertNotNull(tokenName, "expire not exist");

            var builder = Jwts.builder().signWith(SignatureAlgorithm.RS256, privateKey);
            var now = new Date();
            builder.setIssuedAt(now);
            builder.setExpiration(new Date(now.getTime() + Long.parseLong("" + expire)));
            builder.setIssuer(applicationName);
            builder.setId(UUID.randomUUID().toString());

            for (Entry<String, Object> entry : para.entrySet()) {
                String keyName = entry.getKey();
                Object value = entry.getValue();
                if (ObjectHelpUtils.inRange(keyName, "tokenName", "expire")) {
                    continue;
                }
                builder.claim(keyName, value);
            }

            res.put(tokenName, builder.compact());
        }
        return res;
    }
}
