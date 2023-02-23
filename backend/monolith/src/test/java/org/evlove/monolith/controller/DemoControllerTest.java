package org.evlove.monolith.controller;

import org.evlove.common.core.pojo.ro.BasePagingParam;
import org.evlove.common.core.rcode.CommonReturnCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
class DemoControllerTest {
    @Autowired
    private WebApplicationContext webContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webContext)
                .build();
    }

    @Test
    void demoGet() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/demo")
                .param("field1", "A")
                .param("field2", "B"))

                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(CommonReturnCode.SUCCESS.getCode()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void demoPost() throws Exception {
        BasePagingParam param = new BasePagingParam();
        param.setPageNum(1);
        param.setPageSize(10);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/demo/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(param.toJson())
                .param("yyy", "A")
                .header("xxx", "B"))

                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(CommonReturnCode.SUCCESS.getCode()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void demoDelete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/demo/100"))

                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(CommonReturnCode.SUCCESS.getCode()))
                .andDo(MockMvcResultHandlers.print());
    }
}