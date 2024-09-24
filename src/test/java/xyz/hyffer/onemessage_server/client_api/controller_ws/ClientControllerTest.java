package xyz.hyffer.onemessage_server.client_api.controller_ws;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import xyz.hyffer.onemessage_server.client_api.service.ClientService;
import xyz.hyffer.onemessage_server.client_api.service.cs_api.ClientResponse;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientController.class)
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    @Test
    void get_contacts() throws Exception {
        given(clientService.getContacts(any())).willReturn(new ClientResponse.GetContacts(new ArrayList<>()));
        mockMvc.perform(get("/app/get_contacts")
                        .content("{\"pinned\":true,\"limit\":50}").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"contacts\":[]}"))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    System.out.println(responseBody);
                    assertThat(responseBody).doesNotContain("code");
                    assertThat(responseBody).contains("contacts");
                });
        verify(clientService).getContacts(any());
    }

    @Test
    void get_contacts_error() throws Exception {
        mockMvc.perform(get("/app/get_contacts")
                        .content("{\"a\":1}").contentType("application/json"))
                .andExpect(status().is4xxClientError())
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    System.out.println(responseBody);
                    assertThat(responseBody).doesNotContain("code");
                    assertThat(responseBody).contains("msg");
                });
    }
}
