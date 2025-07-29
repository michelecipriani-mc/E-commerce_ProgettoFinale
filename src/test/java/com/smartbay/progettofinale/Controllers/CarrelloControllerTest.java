package com.smartbay.progettofinale.Controllers;

import com.smartbay.progettofinale.DTO.CarrelloDTO;
import com.smartbay.progettofinale.Repositories.CareerRequestRepository;
import com.smartbay.progettofinale.Security.SecurityService;
import com.smartbay.progettofinale.Services.CarrelloService;
import com.smartbay.progettofinale.Config.NoSecurityConfig;
import com.smartbay.progettofinale.Config.NotificationInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarrelloController.class)
@Import(NoSecurityConfig.class)
class CarrelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SecurityService securityService;

    @MockBean
    private CarrelloService carrelloService;

    @MockBean
    private CareerRequestRepository careerRequestRepository;

    @MockBean
    private NotificationInterceptor notificationInterceptor; // ✅ Fixes missing bean

    private final Long mockUserId = 42L;

    @BeforeEach
    void setUp() {
        when(securityService.getActiveUserId()).thenReturn(mockUserId);
    }

    @Test
    void testGetCarrelloTotal() throws Exception {
        BigDecimal expectedTotal = new BigDecimal("123.45");
        when(carrelloService.getPrezzoTotaleCarrello(mockUserId)).thenReturn(expectedTotal);

        mockMvc.perform(get("/api/carrello/total")).andExpect(status().isOk())
                .andExpect(content().string("123.45"));
    }

    @Test
    void testGetCarrello() throws Exception {
        CarrelloDTO dto = new CarrelloDTO();
        when(carrelloService.getCarrelloDTOFromUtente(mockUserId)).thenReturn(dto);

        mockMvc.perform(get("/api/carrello")).andExpect(status().isOk());
    }
}
