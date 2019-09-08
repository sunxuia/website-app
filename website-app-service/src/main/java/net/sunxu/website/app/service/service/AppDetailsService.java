package net.sunxu.website.app.service.service;

import net.sunxu.website.app.dto.AppInfoCreationResultDTO;
import net.sunxu.website.app.service.entity.AppInfo;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AppDetailsService extends UserDetailsService {

    String DEFAULT_SERVICE_ROLE = "ROLE_SERVICE";

    long DEFAULT_SERVICE_OWNER_ID = 1;

    void updateLastLoginTime(AppInfo appInfo);

    AppInfoCreationResultDTO createAppInfo(String appName);
}
