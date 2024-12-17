package xyz.hyffer.onemessage_server.client_api.service;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import xyz.hyffer.onemessage_server.model.Contact;
import xyz.hyffer.onemessage_server.storage.PopulateData;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Integration test for {@link ClientCustomQuery}
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ClientCustomQuery.class, PopulateData.class})
public class ClientCustomQueryITest {

    @Resource
    ClientCustomQuery clientCustomQuery;

    @Test
    void catchupContacts_case1() {
        List<Contact> contacts = clientCustomQuery.catchupContacts(1, null, null, null, 15);
        System.out.println(contacts);
    }

    // TODO: more elaborate integration tests
    @Test
    void searchContact() {
        List<Contact> contacts = clientCustomQuery.searchContacts("test", 15);
        System.out.println(contacts);
        assertThat(contacts.size()).isEqualTo(1);
    }
}
