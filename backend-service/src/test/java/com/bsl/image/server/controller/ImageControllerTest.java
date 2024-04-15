package com.bsl.image.server.controller;

import com.bsl.image.server.ImageServerApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = ImageServerApplication.class)
@AutoConfigureMockMvc
public class ImageControllerTest {

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    public void setUp() {

    }


    @Test
    public void shouldReturnImage() throws Exception {
        mvc.perform(get("/v1/image/show/thumbnail/dept-test?reference=person-sitting-at-a-desk-working.jpg")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.IMAGE_JPEG));
    }

    @Test
    public void shouldFlushImage() throws Exception {
        mvc.perform(delete("/v1/image/flush/thumbnail?reference=person-sitting-at-a-desk-working.jpg")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


}
