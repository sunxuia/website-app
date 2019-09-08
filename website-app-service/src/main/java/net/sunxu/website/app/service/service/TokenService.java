package net.sunxu.website.app.service.service;

import java.util.List;
import java.util.Map;
import net.sunxu.website.app.dto.PublicKeyDTO;
import net.sunxu.website.app.service.entity.AppInfo;

public interface TokenService {

    PublicKeyDTO getPublicKey();

    String createServiceToken(AppInfo appInfo);

    Map<String, String> createUserToken(AppInfo appInfo, List<Map<String, Object>> paras);

}
