package com.dark.aiagent.ephemeral.interfaces.rest;

import com.dark.aiagent.ephemeral.application.EphemeralRoomUseCase;
import com.dark.aiagent.ephemeral.domain.EphemeralRoom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ShortLinkController.class)
@AutoConfigureMockMvc(addFilters = false)
class ShortLinkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EphemeralRoomUseCase useCase;

    @Test
    void redirect_with_referer_should_redirect_absolutely_to_preview_branch() throws Exception {
        EphemeralRoom room = EphemeralRoom.create("room123", "PklcS100", "Test", 3600, "creator");
        when(useCase.findRoom("PklcS100")).thenReturn(Optional.of(room));

        mockMvc.perform(get("/s/PklcS100")
                .header("Referer", "https://feature-ephemerallink.ms-ng-view.pages.dev/s/PklcS100"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://feature-ephemerallink.ms-ng-view.pages.dev/room/PklcS100"))
                .andExpect(header().string("X-Robots-Tag", "noindex, nofollow"));
    }

    @Test
    void redirect_with_production_referer_should_redirect_absolutely() throws Exception {
        EphemeralRoom room = EphemeralRoom.create("room123", "PklcS100", "Test", 3600, "creator");
        when(useCase.findRoom("PklcS100")).thenReturn(Optional.of(room));

        mockMvc.perform(get("/s/PklcS100")
                .header("Referer", "https://122577.xyz/s/PklcS100"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://122577.xyz/room/PklcS100"))
                .andExpect(header().string("X-Robots-Tag", "noindex, nofollow"));
    }

    @Test
    void redirect_without_referer_should_redirect_relatively() throws Exception {
        EphemeralRoom room = EphemeralRoom.create("room123", "PklcS100", "Test", 3600, "creator");
        when(useCase.findRoom("PklcS100")).thenReturn(Optional.of(room));

        mockMvc.perform(get("/s/PklcS100"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/room/PklcS100"))
                .andExpect(header().string("X-Robots-Tag", "noindex, nofollow"));
    }

    @Test
    void redirect_with_expired_code_should_return_410() throws Exception {
        when(useCase.findRoom("PklcS100")).thenReturn(Optional.empty());

        mockMvc.perform(get("/s/PklcS100"))
                .andExpect(status().isGone());
    }
}
