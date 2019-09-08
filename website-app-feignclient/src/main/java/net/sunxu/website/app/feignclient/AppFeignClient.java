package net.sunxu.website.app.feignclient;

import java.util.List;
import java.util.Map;
import net.sunxu.website.app.dto.PublicKeyDTO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

public interface AppFeignClient {

    @RequestMapping("/app/publickey")
    PublicKeyDTO getPublicKey();

    @RequestMapping("/app/service-token")
    String getServiceToken();

    @RequestMapping("/app/user-token")
    Map<String, String> getUserToken(@RequestBody List<Map<String, Object>> paras);

}
