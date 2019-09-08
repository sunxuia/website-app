package net.sunxu.website.app.feignclient;

import feign.Client;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(FeignClientsConfiguration.class)
public class AppFeignConfiguration {

    @Value("${website.app.id}")
    private String id;

    @Value("${website.app.password}")
    private String password;

    @Bean
    public AppFeignClient appFeignClient(Decoder decoder, Encoder encoder, Client client) {
        String credential = id + ":" + password;
        String basicToken = "basic " + java.util.Base64.getEncoder().encodeToString(credential.getBytes());

        return Feign.builder().client(client)
                .encoder(encoder)
                .decoder(decoder)
                .contract(new SpringMvcContract())
                .requestInterceptor(request -> {
                    request.header("Authorization", basicToken);
                })
                .target(AppFeignClient.class, "http://app-service/");
    }
}
