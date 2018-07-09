package dev.local.gtm.api.rest;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.local.gtm.api.config.AppProperties;
import dev.local.gtm.api.domain.Captcha;
import dev.local.gtm.api.service.AuthService;
import dev.local.gtm.api.web.rest.AuthResource;
import dev.local.gtm.api.web.rest.vm.UserVM;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.BDDMockito.*;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = { AuthResource.class }, secure = false)
public class AuthResourceTest {

        @MockBean
        private AppProperties appPropterties;

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private AuthService authService;

        @Before
        public void setup() {
        }

        @Test
        public void testCaptchaRequestSuccessfully() throws Exception {
                val captcha = new Captcha();
                captcha.setUrl("http://someplace/somepic.jpg");
                captcha.setToken("someToken");
                given(this.authService.requestCaptcha()).willReturn(captcha);
                mockMvc.perform(get("/api/auth/captcha")).andExpect(status().isOk())
                                .andExpect(jsonPath("$.captcha_url").isNotEmpty())
                                .andExpect(jsonPath("$.captcha_token").isNotEmpty());
        }

        @Test
        public void testCaptchaVerificationSuccessfully() throws Exception {
                val code = "testCode";
                val token = "testToken";
                val verification = new AuthResource.CaptchaVerification();
                verification.setCode(code);
                verification.setToken(token);
                given(this.authService.verifyCaptcha(code, token)).willReturn("testValidateToken");
                mockMvc.perform(post("/api/auth/captcha").content(objectMapper.writeValueAsString(verification)))
                                .andExpect(status().isOk()).andExpect(jsonPath("$.validate_token").isNotEmpty());
        }

        @Test
        public void testRegisterSuccess() throws Exception {

                val validateToken = "testValidateToken";
                val user = UserVM.builder().login("test1").mobile("13000000000").email("test1@local.dev").name("test 1")
                                .password("12345").validateToken(validateToken).build();

                mockMvc.perform(post("/api/auth/register").content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(status().isOk())
                                .andExpect(jsonPath("$.id_token").isNotEmpty());
        }

        // @Test
        // public void testRegisterFailDup() throws Exception {
        // val user =
        // User.builder().login("test1").mobile("13000000000").email("test1@local.dev").name("test
        // 1")
        // .password("123456").build();
        // userRepository.save(user);
        // mockMvc.perform(post("/api/auth/register").content(objectMapper.writeValueAsString(user))
        // .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(status().isBadRequest());
        // }

        // @Test
        // public void testVerifyMobileSuccess() throws Exception {
        // val user =
        // User.builder().login("test1").mobile("13898810892").email("test1@local.dev").name("test
        // 1")
        // .password("123456").build();
        // userRepository.save(user);
        // val verification = new AuthResource.MobileVerification("13898810892",
        // "525798");
        // mockMvc.perform(post("/api/auth/mobile").content(objectMapper.writeValueAsString(verification))
        // .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(status().isOk())
        // .andExpect(jsonPath("$.reset_key").isNotEmpty());
        // }

        // @Test
        // public void testVerifyMobileFail() throws Exception {
        // val user =
        // User.builder().login("test1").mobile("13898810892").email("test1@local.dev").name("test
        // 1")
        // .password("123456").build();
        // userRepository.save(user);
        // val verification = new AuthResource.MobileVerification("13898810892",
        // "123456");
        // mockMvc.perform(post("/api/auth/mobile").content(objectMapper.writeValueAsString(verification))
        // .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(status().isBadRequest());
        // }

        // @Test
        // public void testSendSmsSuccess() throws Exception {
        // val user =
        // User.builder().login("test1").mobile("13898810892").email("test1@local.dev").name("test
        // 1")
        // .password("123456").build();
        // userRepository.save(user);
        // val params = new LinkedMultiValueMap<String, String>();
        // params.add("mobile", "13898810892");
        // params.add("token", "1234");
        // mockMvc.perform(get("/api/auth/mobile").params(params).contentType(MediaType.APPLICATION_JSON_UTF8))
        // .andExpect(status().isOk());
        // }
}
