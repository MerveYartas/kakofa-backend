package com.example.kakofa_backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class KakofaBackendApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
		// Spring konteksi yükleniyor, boş test yeterli
	}

	@Test
	void loginUser() throws Exception {
		String loginJson = "{\"email\":\"test1@test1.com\",\"password\":\"password123\"}";
		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(loginJson));
	}

	@Test
	@WithMockUser(username = "test1@test1.com")
	void getDoctors() throws Exception {
		mockMvc.perform(get("/api/users/doctors")
				.param("query", "test"))
				.andExpect(status().isOk());
	}

	@Test
	void registerUser() throws Exception {
		String registerJson = "{\"email\":\"newuser@test.com\",\"password\":\"password123\",\"firstname\":\"Test\",\"lastname\":\"User\"}";
		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(registerJson));
	}

	@Test
	@WithMockUser(username = "test1@test1.com")
	void updateUser() throws Exception {
		String updateJson = "{\"email\":\"test1@test1.com\",\"firstname\":\"Updated\",\"lastname\":\"User\"}";
		mockMvc.perform(put("/api/users/update")
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateJson));
	}

	@Test
	@WithMockUser(username = "test1@test1.com")
	void sendTextForAnalysis() throws Exception {
		String textJson = "{\"text\":\"Bu bir test metnidir\"}";
		mockMvc.perform(post("/api/analysis")
				.contentType(MediaType.APPLICATION_JSON)
				.content(textJson));
	}

	@Test
	@WithMockUser(username = "test1@test1.com")
	void saveAnalysisResult() throws Exception {
		String resultJson = "{\"userId\":1,\"analysisResult\":\"Pozitif\"}";
		mockMvc.perform(post("/api/analysis/save")
				.contentType(MediaType.APPLICATION_JSON)
				.content(resultJson));
	}

	@Test
	@WithMockUser(username = "test1@test1.com")
	void getCurrentDoctor() throws Exception {
		mockMvc.perform(get("/api/users/current-doctor"));
	}

	@Test
	@WithMockUser(username = "test1@test1.com")
	void sendMessage() throws Exception {
		String messageJson = "{\"sender\":\"test1@test1.com\",\"recipient\":\"test2@test2.com\",\"content\":\"Merhaba\"}";
		mockMvc.perform(post("/api/messages/send")
				.contentType(MediaType.APPLICATION_JSON)
				.content(messageJson))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "test1@test1.com")
	void getMessageHistory() throws Exception {
		mockMvc.perform(get("/api/messages/history/test1@test1.com/test2@test2.com"))
				.andExpect(status().isOk());
	}
}