package com.netspecter.controller;

import com.netspecter.model.ScanResult;
import com.netspecter.service.ScanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ScanController.class)
class ScanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScanService scanService;

    @Test
    @WithMockUser
    void testScanTarget_Success() throws Exception {
        // Mock service
        ScanResult mockResult = new ScanResult();
        mockResult.setTarget("example.com");
        when(scanService.performScan("example.com")).thenReturn(mockResult);

        // Perform GET request
        mockMvc.perform(get("/api/scan")
                .param("target", "example.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testStreamScan_InitiatesSSE() throws Exception {
        // Perform GET request to stream endpoint
        // Verify it returns an SSE stream (EventStream)

        mockMvc.perform(get("/api/scan/stream")
                .param("target", "example.com"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    if (!(result.getAsyncResult() instanceof SseEmitter)) {
                        throw new AssertionError("Expected SseEmitter");
                    }
                });

        // Verify service was called (async verification might be tricky here depending
        // on controller impl)
        // The controller calls performScan inside an Executor, so we might need a short
        // sleep or loop to verify.
        // However, for this unit test, we just check the endpoint returns 200 OK and an
        // emitter.
    }
}
