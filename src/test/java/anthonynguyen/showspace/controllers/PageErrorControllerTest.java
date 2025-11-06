package anthonynguyen.showspace.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.RequestDispatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PageErrorController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PageErrorControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void testErrorPageWithStatus() throws Exception {
        mvc.perform(get("/error")
                .requestAttr("jakarta.servlet.error.status_code", 404)
                .requestAttr("jakarta.servlet.error.request_uri", "/non-existent"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("status", 404))
                .andExpect(model().attribute("path", "/non-existent"));
    }

    @Test
    public void test404ErrorPage() throws Exception {
        mvc.perform(get("/error")
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 404)
                .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/some-page"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("status"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("status", 404));
    }

    @Test
    public void test403ErrorPage() throws Exception {
        mvc.perform(get("/error")
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 403))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("status", 403));
    }

    @Test
    public void test500ErrorPage() throws Exception {
        mvc.perform(get("/error")
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 500))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("status", 500));
    }

    @Test
    public void testUnknownErrorCode() throws Exception {
        mvc.perform(get("/error")
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 999))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("status", 999));
    }
}
