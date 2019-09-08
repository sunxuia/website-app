package net.sunxu.website.app.service.bo;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import net.sunxu.website.app.service.entity.AppInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AppUserDetails implements UserDetails {

    private AppInfo appInfo;

    private Set<GrantedAuthority> authorities;

    public AppUserDetails(AppInfo appInfo) {
        this.appInfo = appInfo;
        this.authorities = appInfo.getRoles() == null ?
                Collections.emptySet() :
                appInfo.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return appInfo.getPassword();
    }

    @Override
    public String getUsername() {
        return appInfo.getAppName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return appInfo.getIsActive();
    }

    public AppInfo getAppInfo() {
        return appInfo;
    }
}
