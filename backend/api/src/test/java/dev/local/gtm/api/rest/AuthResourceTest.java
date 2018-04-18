package dev.local.gtm.api.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.local.gtm.api.config.propsupport.SmsCaptchaProperties;
import dev.local.gtm.api.config.propsupport.SmsCodeProperties;
import dev.local.gtm.api.domain.MobileVerification;
import dev.local.gtm.api.domain.User;
import dev.local.gtm.api.repository.UserRepo;
import dev.local.gtm.api.web.exception.ExceptionTranslator;
import dev.local.gtm.api.web.rest.AuthResource;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthResourceTest {


    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HttpMessageConverter[] httpMessageConverters;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SmsCaptchaProperties captchaProperties;

    @Autowired
    private SmsCodeProperties codeProperties;


    @Before
    public void setup() {
        userRepo.deleteAll();
        val authResource = new AuthResource(userRepo, restTemplate, captchaProperties, codeProperties);
        mockMvc = MockMvcBuilders.standaloneSetup(authResource)
            .setMessageConverters(httpMessageConverters)
            .setControllerAdvice(exceptionTranslator)
            .build();
    }

    @Test
    public void testRegisterSuccess() throws Exception {
        val user = User.builder()
                .login("test1")
                .mobile("13000000000")
                .email("test1@local.dev")
                .name("test 1")
                .password("123456")
                .build();
        mockMvc.perform(post("/api/auth/register")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
        assertThat(userRepo.findOneByLogin("test1").isPresent()).isTrue();
    }

    @Test
    public void testRegisterFail() throws Exception {
        val user = User.builder()
                .login("test1")
                .mobile("13000000000")
                .email("test1@local.dev")
                .name("test 1")
                .password("123456")
                .build();
        userRepo.save(user);
        mockMvc.perform(post("/api/auth/register")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testVerifyMobileSuccess() throws Exception {
        val user = User.builder()
                .login("test1")
                .mobile("13898810892")
                .email("test1@local.dev")
                .name("test 1")
                .password("123456")
                .build();
        userRepo.save(user);
        val verification = new MobileVerification("13898810892","525798");
        mockMvc.perform(post("/api/auth/mobile")
                .content(objectMapper.writeValueAsString(verification))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void testVerifyMobileFail() throws Exception {
        val user = User.builder()
                .login("test1")
                .mobile("13898810892")
                .email("test1@local.dev")
                .name("test 1")
                .password("123456")
                .build();
        userRepo.save(user);
        val verification = new MobileVerification("13898810892","123456");
        mockMvc.perform(post("/api/auth/mobile")
                .content(objectMapper.writeValueAsString(verification))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }
}
