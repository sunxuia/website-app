package net.sunxu.website.app.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import net.sunxu.website.app.dto.AppInfoCreationResultDTO;
import net.sunxu.website.app.dto.PublicKeyDTO;
import net.sunxu.website.app.service.repo.AppInfoRepo;
import net.sunxu.website.test.dbunit.DbUnitRule;
import net.sunxu.website.test.dbunit.DbUnitRuleFactory;
import net.sunxu.website.test.dbunit.annotation.DbUnitClearTable;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AppControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DbUnitRuleFactory factory;

    @Autowired
    private AppInfoRepo appInfoRepo;

    @Autowired
    private DataSource dataSource;

    @Rule
    public DbUnitRule dbUnitRule;

    @PostConstruct
    public void initial() throws SQLException {
        dbUnitRule = factory.builder().connection(dataSource.getConnection()).build();
    }

    @Test
    @DbUnitClearTable({"app_info", "app_info_roles"})
    public void testPostApp() throws Exception {
        var res = createAppInfo();

        Assert.assertEquals("test-app", res.getName());
        Assert.assertNotNull(res.getPassword());
        Assert.assertTrue(appInfoRepo.findById(res.getId()).isPresent());
    }

    private AppInfoCreationResultDTO createAppInfo() throws Exception {
        String response = mockMvc.perform(post("/app?name=test-app"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, AppInfoCreationResultDTO.class);
    }

    @Test
    @DbUnitClearTable({"app_info", "app_info_roles"})
    public void testGetPublicKey() throws Exception {
        var app = createAppInfo();

        String token = Base64.getEncoder().encodeToString((app.getId() + ":" + app.getPassword()).getBytes());
        String ret = mockMvc.perform(get("/app/publickey")
                .header("Authorization", "basic " + token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PublicKeyDTO res = objectMapper.readValue(ret, PublicKeyDTO.class);
        Assert.assertEquals("X.509", res.getType());
        Assert.assertNotNull(res.getPublicKey());
    }

    @Test
    @DbUnitClearTable({"app_info", "app_info_roles"})
    public void testGetServiceToken() throws Exception {
        var app = createAppInfo();

        String token = Base64.getEncoder().encodeToString((app.getId() + ":" + app.getPassword()).getBytes());
        String res = mockMvc.perform(get("/app/service-token")
                .header("Authorization", "basic " + token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assert.assertFalse(res.isEmpty());
    }

    @Test
    @DbUnitClearTable({"app_info", "app_info_roles"})
    public void testGetUserServieToken() throws Exception {
        var app = createAppInfo();
        var entity = appInfoRepo.findById(app.getId()).get();
        entity.getRoles().add("ROLE_AUTH");
        appInfoRepo.save(entity);

        List<Map<String, Object>> body = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("tokenName", "test-token");
        map.put("expire", 100L);
        map.put("id", 100);
        body.add(map);
        map = new HashMap<>();
        map.put("tokenName", "test-token-2");
        map.put("expire", 100L);
        map.put("id", 100);
        body.add(map);

        String token = Base64.getEncoder().encodeToString((app.getId() + ":" + app.getPassword()).getBytes());
        String ret = mockMvc.perform(post("/app/user-token")
                .header("Authorization", "basic " + token)
                .header("Content-Type", "application/json")
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var res = (Map<String, String>) objectMapper.readValue(ret, Map.class);

        Assert.assertEquals(2, res.size());
        Assert.assertNotNull(res.get("test-token"));
        Assert.assertNotNull(res.get("test-token-2"));
    }
}
