package dev.local.gtm.api.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.local.gtm.api.config.AppProperties;
import dev.local.gtm.api.domain.Captcha;
import dev.local.gtm.api.domain.JWTToken;
import dev.local.gtm.api.domain.User;
import dev.local.gtm.api.service.AuthService;
import dev.local.gtm.api.web.exception.ExceptionTranslator;
import dev.local.gtm.api.web.rest.AuthResource;
import dev.local.gtm.api.web.rest.vm.UserVM;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {AuthResource.class}, secure = false)
public class AuthResourceTest {

    @MockBean
    private AppProperties appProperties;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Autowired
    private HttpMessageConverter[] httpMessageConverters;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Before
    public void setup() {
        AuthResource authResource = new AuthResource(authService, appProperties);
        mockMvc = MockMvcBuilders.standaloneSetup(authResource)
            .setMessageConverters(httpMessageConverters)
            .setControllerAdvice(exceptionTranslator)
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView()).build();
    }

    @Test
    public void testCaptchaRequestSuccessfully() throws Exception {
        Captcha captcha = new Captcha();
        captcha.setUrl("http://someplace/somepic.jpg");
        captcha.setToken("someToken");
        given(this.authService.requestCaptcha())
            .willReturn(captcha);
        mockMvc.perform(get("/api/auth/captcha"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.captcha_url").isNotEmpty())
            .andExpect(jsonPath("$.captcha_token").isNotEmpty());
    }

    @Test
    public void testCaptchaVerificationSuccessfully() throws Exception {
        String code = "testCode";
        String token = "testToken";
        AuthResource.CaptchaVerification verification = new AuthResource.CaptchaVerification();
        verification.setCode(code);
        verification.setToken(token);
        given(this.authService.verifyCaptcha(code, token))
            .willReturn("testValidateToken");
        mockMvc.perform(post("/api/auth/captcha")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(verification)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.validate_token").isNotEmpty());
    }

    @Test
    public void testRegisterSuccess() throws Exception {
        String validateToken = "testValidateToken";
        String password = "12345";
        UserVM user = UserVM.builder()
            .login("test1")
            .mobile("13000000000")
            .email("test1@local.dev")
            .name("test 1")
            .password(password)
            .validateToken(validateToken)
            .build();
        AppProperties.Security security = new AppProperties.Security();
        doNothing()
            .when(this.authService)
            .verifyCaptchaToken(validateToken);
        doNothing()
            .when(this.authService)
            .registerUser(user.toUserDTO(), password);
        given(this.authService.login(user.getLogin(), user.getPassword()))
            .willReturn(new JWTToken("idToken", "refreshToken"));
        given(this.appProperties.getSecurity())
            .willReturn(security);

        mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(user)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id_token").isNotEmpty());
    }

//     @Test
//     public void testRegisterFailDup() throws Exception {
//         User user = User.builder()
//             .login("test1")
//             .mobile("13000000000")
//             .email("test1@local.dev")
//             .name("test 1")
//             .password("123456")
//             .build();
//         mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(user)))
//         .andExpect(status().isBadRequest());
//     }

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
