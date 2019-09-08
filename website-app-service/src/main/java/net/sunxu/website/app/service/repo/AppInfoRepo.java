package net.sunxu.website.app.service.repo;

import java.time.LocalDateTime;
import net.sunxu.website.app.service.entity.AppInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppInfoRepo extends CrudRepository<AppInfo, Long> {

    @Modifying
    @Query("update AppInfo set lastLoginTime = :prefix where id = :id")
    void updateLastLoginTime(@Param("id") Long id, @Param("prefix") LocalDateTime localDateTime);

    boolean existsByAppNameIgnoreCase(String appName);

}
