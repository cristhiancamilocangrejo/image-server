package com.bsl.image.server.controller;

import com.bsl.image.server.ImageServerApplication;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import de.mkammerer.wiremock.WireMockExtension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
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

    private static WireMockServer wireMockServer;

    @Autowired
    private MockMvc mvc;


    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(8090);
        wireMockServer.start();

        wireMockServer.stubFor(
                WireMock.get("/image/configuration")
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                        .withBody("[\n" +
                                                "  {\n" +
                                                "    \"name\": \"thumbnail\",\n" +
                                                "    \"height\": 400,\n" +
                                                "    \"width\": 500,\n" +
                                                "    \"quality\": 50,\n" +
                                                "    \"scaleType\": \"CROP\",\n" +
                                                "    \"fillColor\": null,\n" +
                                                "    \"imageType\": \"JPG\"\n" +
                                                "  },\n" +
                                                "  {\n" +
                                                "    \"name\": \"gif\",\n" +
                                                "    \"height\": 400,\n" +
                                                "    \"width\": 500,\n" +
                                                "    \"quality\": 40,\n" +
                                                "    \"scaleType\": \"FILL\",\n" +
                                                "    \"fillColor\": \"#ff0000\",\n" +
                                                "    \"imageType\": \"PNG\"\n" +
                                                "  }\n" +
                                                "]")));
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
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
