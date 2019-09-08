package net.sunxu.website.app.service.controller;

import java.util.List;
import java.util.Map;
import net.sunxu.website.app.dto.AppInfoCreationResultDTO;
import net.sunxu.website.app.dto.PublicKeyDTO;
import net.sunxu.website.app.service.bo.AppUserDetails;
import net.sunxu.website.app.service.service.AppDetailsService;
import net.sunxu.website.app.service.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
public class AppController {

    @Autowired
    private AppDetailsService detailsService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("")
    public AppInfoCreationResultDTO createAppInfo(@RequestParam("name") String name) {
        return detailsService.createAppInfo(name);
    }

    @GetMapping("/publickey")
    @PreAuthorize("isAuthenticated()")
    public PublicKeyDTO getPublicKey() {
        return tokenService.getPublicKey();
    }

    @GetMapping("/service-token")
    @PreAuthorize("isAuthenticated()")
    public String getAppToken() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var appInfo = ((AppUserDetails) principal).getAppInfo();
        return tokenService.createServiceToken(appInfo);
    }

    @PostMapping("/user-token")
    @PreAuthorize("isAuthenticated() && hasRole('AUTH')")
    public Map<String, String> getUserToken(@RequestBody List<Map<String, Object>> paras) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var appInfo = ((AppUserDetails) principal).getAppInfo();
        return tokenService.createUserToken(appInfo, paras);
    }
}
