package xyz.hyffer.onemessage_server.client_api.service.cs_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test {@link ClientRequest} deserialization.
 * <p>
 * More tests on {@link ClientRequestBody} can be found in {@link ClientRequestBodyDeserializationTest}.
 */
@ExtendWith(MockitoExtension.class)
public class ClientRequestConverterTest {

    @Spy
    ObjectMapper objectMapper;

    @InjectMocks
    ClientRequestConverter converter;

    @Test
    void deserialize_valid_request() {
        try {
            ClientRequest request = converter.deserialize("""
                    {
                        "cmd": "get_contacts",
                        "body": {
                            "pinned": true,
                            "limit": 50
                        }
                    }
                    """
            );
            assertThat(request.getCmd()).isEqualTo(ClientRequest.CMD.GET_CONTACTS);
            assertThat(request.getBody()).isInstanceOf(ClientRequestBody.GetContacts2.class);
        } catch (ClientException e) {
            fail();
        }
    }

    @Test
    void deserialize_invalid_request() {
        ClientException e = assertThrows(ClientException.class, () -> converter.deserialize("""
                {
                    "cmd": "abc",
                    "body": {
                        "pinned": true,
                        "limit": "50"
                    }
                }
                """
        ));
        assertThat(e.getCode()).isEqualTo(400);
        assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);

        assertThrows(ClientException.class, () -> converter.deserialize("""
                {
                    "cmd": false,
                    "body": {
                        "pinned": true,
                        "limit": "50"
                    }
                }
                """
        ));

        assertThrows(ClientException.class, () -> converter.deserialize("""
                {
                    "cmd": "get_contacts"
                }
                """
        ));

        assertThrows(ClientException.class, () -> converter.deserialize("""
                {
                    "cmd": "get_contacts",
                    "body": {
                        "pinned": true,
                        "limit" 50
                    }
                }
                """
        ));
    }
}
