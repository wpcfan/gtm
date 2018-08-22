package dev.local.gtm.api.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.local.gtm.api.Application;
import dev.local.gtm.api.config.AppProperties;
import dev.local.gtm.api.repository.mongo.TaskRepository;
import dev.local.gtm.api.repository.mongo.UserRepository;
import dev.local.gtm.api.web.exception.ExceptionTranslator;
import dev.local.gtm.api.web.rest.TaskResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class TaskResourceTest {

    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HttpMessageConverter[] httpMessageConverters;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private AppProperties appProperties;

    @Before
    public void setup() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        TaskResource taskResource = new TaskResource(taskRepository, userRepository);

        mockMvc = MockMvcBuilders.standaloneSetup(taskResource)
            .setMessageConverters(httpMessageConverters)
            .setControllerAdvice(exceptionTranslator)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((ViewResolver) (viewName, locale) -> new MappingJackson2JsonView()).build();
    }

    @Test
    @WithMockUser
    public void testGetTasks() throws Exception {
        mockMvc.perform(get("/api/tasks").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

}
