package xyz.hyffer.onemessage_server.client_api.service.cs_api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.hyffer.onemessage_server.model.MessageSegmentContent;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static xyz.hyffer.onemessage_server.client_api.service.cs_api.ClientRequestBodyDeserializer.deserializeOfType;

/**
 * Test whether {@link ClientRequestBodyDeserializer#deserializeOfType} could deserialize request body properly. That is:
 * <p>1. Valid request body can be deserialized correctly.
 * <p>2. Request body violates the specification will cause a `FORMAT_INCORRECT` exception.
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
class ClientRequestBodyDeserializationTest {

    @Test
    void deserialize_type_correctly() {
        try {
            ClientRequestBody requestBody = deserializeOfType(ClientRequest.CMD.GET_CONTACTS, "{}");
            assertThat(requestBody).isInstanceOf(ClientRequestBody.GetContacts1.class);

            requestBody = deserializeOfType(ClientRequest.CMD.GET_CONTACTS, "{\"pinned\":true}");
            assertThat(requestBody).isInstanceOf(ClientRequestBody.GetContacts2.class);

        } catch (ClientException e) {
            fail();
        }
    }

    @Test
    void deserialize_data_correctly() {
        try {
            ClientRequestBody.GetContacts1 requestBody = (ClientRequestBody.GetContacts1) deserializeOfType(
                    ClientRequest.CMD.GET_CONTACTS,
                    """
                            {
                                "_CID_r": 100,
                                "pre_cOrd":2,
                                "limit": 50
                            }
                            """
            );
            assertThat(requestBody.get_CID_r()).isEqualTo(100);
            assertThat(requestBody.getPre_cOrd()).isEqualTo(2);
            assertThat(requestBody.getLimit()).isEqualTo(50);

            ClientRequestBody.PostMessage requestBody2 = (ClientRequestBody.PostMessage) deserializeOfType(
                    ClientRequest.CMD.POST_MESSAGE,
                    """
                            {
                                "_CID": 1,
                                "content": [
                                    {"type": "plaintext", "content": {"text": "a text message"} },
                                    {"type": "image", "content": {"url": "http://img"} }
                                ]
                            }
                            """
            );
            assertThat(requestBody2.getContent().size()).isEqualTo(2);
            assertThat(requestBody2.getContent().get(0).getContent()).isInstanceOf(MessageSegmentContent.Plaintext.class);
            assertThat(requestBody2.getContent().get(1).getContent()).isInstanceOf(MessageSegmentContent.Image.class);

        } catch (ClientException e) {
            fail();
        }
    }

    @Test
    void generate_default_value() {
        try {
            ClientRequestBody.GetContacts1 requestBody = (ClientRequestBody.GetContacts1)
                    deserializeOfType(ClientRequest.CMD.GET_CONTACTS, "{}");
            System.out.println(requestBody);
            assertThat(requestBody.get_CID_l()).isNull();
            assertThat(requestBody.getPre_cOrd()).isNull();
            assertThat(requestBody.getLimit()).isEqualTo(20);
            assertThat(requestBody.getAll_attr()).isEqualTo(false);

        } catch (ClientException e) {
            fail();
        }
    }

    @Test
    void get_contacts_invalid_format() {
        // `limit` is null
        ClientException e = assertThrows(ClientException.class, () -> deserializeOfType(
                ClientRequest.CMD.GET_CONTACTS,
                "{\"_CID_l\":10, \"limit\":null}"
        ));
        assertThat(e.getCode()).isEqualTo(400);

        // not belong to any type
        assertThrows(ClientException.class, () -> deserializeOfType(
                ClientRequest.CMD.GET_CONTACTS,
                "{\"pinned\":true, \"_CID_l\":1}"
        ));

        /*
            get_contacts type 1
         */
        // `_CID_l` >= `_CID_r`
        assertThrows(ClientException.class, () -> deserializeOfType(
                ClientRequest.CMD.GET_CONTACTS,
                "{\"_CID_l\":5, \"_CID_r\":5}"
        ));

        // `pre_cOrd` and `pre_sOrd` both exist, but `limit` is not 0
        assertThrows(ClientException.class, () -> deserializeOfType(
                ClientRequest.CMD.GET_CONTACTS,
                "{\"pre_cOrd\":1, \"pre_sOrd\":2}"
        ));

        /*
            get_contacts type 2
         */
        // `pinned` is null
        assertThrows(ClientException.class, () -> deserializeOfType(
                ClientRequest.CMD.GET_CONTACTS,
                "{\"pinned\":null}"
        ));

        /*
            get_contacts type 3
         */
        // `key` is empty
        assertThrows(ClientException.class, () -> deserializeOfType(
                ClientRequest.CMD.GET_CONTACTS,
                "{\"key\":\"\"}"
        ));
    }

    @Test
    void get_contacts_valid_format() {
        try {
            deserializeOfType(
                    ClientRequest.CMD.GET_CONTACTS,
                    "{\"_CID_l\":4, \"_CID_r\":5, \"pre_cOrd\":3, \"pre_sOrd\":4, \"limit\":0}"
            );
        } catch (ClientException e) {
            fail();
        }
    }

    @Test
    void get_messages_invalid_format() {
        /*
            get_messages type 1
         */
        // `_MID_l` >= `_MID_r`
        assertThrows(ClientException.class, () -> deserializeOfType(
                ClientRequest.CMD.GET_MESSAGES,
                "{\"_MID_l\":10, \"_MID_r\":10}"
        ));

        // `pre_rank` and `pre_cOrd` both exist, but `limit` is not 0
        assertThrows(ClientException.class, () -> deserializeOfType(
                ClientRequest.CMD.GET_MESSAGES,
                "{\"pre_rank\":1, \"pre_cOrd\":2, \"limit\":10}"
        ));

        /*
            get_messages type 2
         */
        // `_CID` is null
        assertThrows(ClientException.class, () -> deserializeOfType(
                ClientRequest.CMD.GET_MESSAGES,
                "{\"_CID\":null}"
        ));
    }

    @Test
    void post_message_invalid_format() {
        // `content` is empty
        assertThrows(ClientException.class, () -> deserializeOfType(
                ClientRequest.CMD.POST_MESSAGE,
                "{\"_CID\":1, \"content\":[]}"
        ));
    }
}