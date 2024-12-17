package xyz.hyffer.onemessage_server.client_api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import xyz.hyffer.onemessage_server.client_api.service.cs_api.ClientRequestBody;
import xyz.hyffer.onemessage_server.client_api.service.cs_api.ClientResponse;
import xyz.hyffer.onemessage_server.model.Contact;
import xyz.hyffer.onemessage_server.model.DataGenerator;
import xyz.hyffer.onemessage_server.storage.ContactRepository;
import xyz.hyffer.onemessage_server.storage.MessageRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class ClientServiceTest {

    ClientService clientService;

    ClientCustomQuery clientCustomQuery;

    ContactRepository contactRepository;
    MessageRepository messageRepository;

    @BeforeEach
    void setupMocks() {
        clientCustomQuery = mock(ClientCustomQuery.class);

        messageRepository = mock(MessageRepository.class);
        given(messageRepository.findByRank(anyInt())).willReturn(Optional.empty());

        contactRepository = mock(ContactRepository.class);

        clientService = new ClientService(clientCustomQuery, contactRepository, messageRepository);
    }

    // TODO: more elaborate tests
    @Test
    void getContacts1_1() {
        given(clientCustomQuery.catchupContacts(any(), any(), any(), any(), anyInt()))
                .willReturn(List.of());

        clientService.getContacts(new ClientRequestBody.GetContacts1());

        verify(clientCustomQuery).catchupContacts(any(), any(), any(), any(), anyInt());
        verify(messageRepository, times(0)).findByRank(anyInt());
    }

    @Test
    void getContacts1_2() {
        List<Contact> contacts = List.of(DataGenerator.getTestContacts().get(0));
        given(clientCustomQuery.catchupContacts(any(), any(), any(), any(), anyInt()))
                .willReturn(contacts);

        ClientRequestBody.GetContacts reqBody = new ClientRequestBody.GetContacts1();
        ReflectionTestUtils.setField(reqBody, "all_attr", true);
        ClientResponse.GetContacts resp = clientService.getContacts(reqBody);

        verify(clientCustomQuery).catchupContacts(any(), any(), any(), any(), anyInt());

        // Verify `Transient` fields are loaded
        ArgumentCaptor<Integer> rankCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(messageRepository, times(contacts.size()))
                .findByRank(rankCaptor.capture());
        List<Integer> capturedRanks = rankCaptor.getAllValues();
        assertThat(capturedRanks).usingRecursiveComparison().ignoringCollectionOrder()
                .isEqualTo(contacts.stream().map(Contact::getLastMsgRank).collect(Collectors.toSet()));

        assertThat(resp.getContacts().size()).isEqualTo(contacts.size());
    }
}
