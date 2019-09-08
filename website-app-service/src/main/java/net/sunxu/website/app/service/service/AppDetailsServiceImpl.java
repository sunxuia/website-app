package net.sunxu.website.app.service.service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import javax.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import net.sunxu.website.app.dto.AppInfoCreationResultDTO;
import net.sunxu.website.app.service.bo.AppUserDetails;
import net.sunxu.website.app.service.entity.AppInfo;
import net.sunxu.website.app.service.repo.AppInfoRepo;
import net.sunxu.website.help.exception.BizValidationException;
import net.sunxu.website.help.util.ThreadPoolHelpUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
public class AppDetailsServiceImpl implements AppDetailsService {

    @Autowired
    private AppInfoRepo appInfoRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private RandomStringGenerator passwordGenerator = new RandomStringGenerator.Builder()
            .withinRange(new char[]{'0', '9'}, new char[]{'a', 'z'})
            .build();

    private ExecutorService executorService = ThreadPoolHelpUtils.newSingleThreadExecutor("update-app-login-time");

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return appInfoRepo.findById(Long.parseLong(username))
                .map(AppUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("app info " + username + " not found"));
    }

    @Override
    public void updateLastLoginTime(AppInfo appInfo) {
        LocalDateTime now = LocalDateTime.now();
        appInfo.setLastLoginTime(now);
        executorService.execute(() -> appInfoRepo.updateLastLoginTime(appInfo.getId(), now));
    }

    @Override
    public AppInfoCreationResultDTO createAppInfo(String appName) {
        if (appInfoRepo.existsByAppNameIgnoreCase(appName)) {
            throw BizValidationException.newException("%s name already exist", appName);
        }

        AppInfo appInfo = new AppInfo();
        appInfo.setAppName(appName);
        appInfo.setCreateTime(LocalDateTime.now());
        String password = passwordGenerator.generate(10);
        appInfo.setPassword(passwordEncoder.encode(password));
        appInfo.setIsActive(true);
        appInfo.setCreatorId(DEFAULT_SERVICE_OWNER_ID);
        appInfo.setRoles(Set.of(DEFAULT_SERVICE_ROLE));
        appInfoRepo.save(appInfo);

        AppInfoCreationResultDTO dto = new AppInfoCreationResultDTO();
        dto.setId(appInfo.getId());
        dto.setName(appName);
        dto.setPassword(password);
        return dto;
    }

    @Value("${spring.application.name}")
    private String applicationName;

    @PostConstruct
    @Transactional(rollbackFor = Exception.class)
    public void initialData() {
        if (appInfoRepo.count() == 0) {
            var info = createAppInfo(applicationName);
            log.info(String.format("%s serivice created, id: %d, password: %s",
                    info.getName(), info.getId(), info.getPassword()));
        }
    }
}
