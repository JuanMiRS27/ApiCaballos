package com.example.apisencilla;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.apisencilla.dto.AuthRequest;
import com.example.apisencilla.dto.HorseRequest;
import com.example.apisencilla.dto.RaceRegistrationRequest;
import com.example.apisencilla.dto.RegisterRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.jwt.secret=ZXN0YS1lcy11bmEtY2xhdmUtZGUtbWFzLWRlLTMyLWJ5dGVzLXBhcmEtand0"
})
class ApiSencillaApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void healthEndpointShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    void authEndpointsShouldRegisterAndLogin() throws Exception {
        String username = uniqueValue("jinete");
        RegisterRequest registerRequest = new RegisterRequest(username, username + "@mail.com", "Clave1234*");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.role").value("USER"));

        AuthRequest loginRequest = new AuthRequest(username, "Clave1234*");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void horseEndpointShouldRequireAdminForCreation() throws Exception {
        String userToken = registerUserAndGetToken(uniqueValue("usuario"));

        mockMvc.perform(post("/api/horses")
                        .header("Authorization", bearer(userToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new HorseRequest("Rayo", "Pura sangre", 5, "Luis"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminShouldCreateHorseAndUserShouldRegisterHorseInRace() throws Exception {
        String adminToken = loginAndGetToken("admin", "Admin123*");
        Long horseId = createHorseAndExtractId(adminToken, new HorseRequest("Centella", "Arabe", 4, "Maria"));

        String username = uniqueValue("corredor");
        String userToken = registerUserAndGetToken(username);

        RaceRegistrationRequest registrationRequest = new RaceRegistrationRequest(
                horseId,
                "Clasico Andino",
                "Bogota",
                LocalDate.now().plusDays(10)
        );

        mockMvc.perform(post("/api/registrations")
                        .header("Authorization", bearer(userToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.horseId").value(horseId))
                .andExpect(jsonPath("$.horseName").value("Centella"))
                .andExpect(jsonPath("$.registeredBy").value(username));

        mockMvc.perform(get("/api/registrations/mine")
                        .header("Authorization", bearer(userToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].registeredBy").value(username));

        mockMvc.perform(get("/api/registrations")
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].horseName").value("Centella"));
    }

    @Test
    void protectedEndpointsShouldRejectMissingToken() throws Exception {
        mockMvc.perform(get("/api/horses"))
                .andExpect(status().isForbidden());
    }

    private String registerUserAndGetToken(String username) throws Exception {
        RegisterRequest request = new RegisterRequest(username, username + "@mail.com", "Clave1234*");
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
        return extractField(result, "token");
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        AuthRequest request = new AuthRequest(username, password);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        return extractField(result, "token");
    }

    private Long createHorseAndExtractId(String token, HorseRequest request) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/horses")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asLong();
    }

    private String extractField(MvcResult result, String field) throws Exception {
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get(field).asText();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private String uniqueValue(String prefix) {
        return prefix + System.nanoTime();
    }
}
