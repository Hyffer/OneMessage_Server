package xyz.hyffer.onemessage_server.storage;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import xyz.hyffer.onemessage_server.model.Contact;
import xyz.hyffer.onemessage_server.model.ContactInstance;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ContactInstanceRepositoryTest extends RepositoryTest {

    @Test
    void findBy_SIDAndId() {
        Contact contact = data.contacts.stream().toList().get(0);
        Set<ContactInstance> instances = ReflectionTestUtils.invokeMethod(contact, "getInstances");
        assert instances != null;
        ContactInstance expected = instances.stream().toList().get(0);
        assert expected != null;

        int e_SID = expected.get_SID();
        String e_id = expected.getId();
        Optional<ContactInstance> r = instanceRepository.findBy_SIDAndId(e_SID, e_id);
        assert r.isPresent();
        assertThat(r.get()).isEqualTo(expected);
    }

    /**
     * Test if `ManyToOne` foreign field can be automatically acquired.
     */
    @Test
    void queryForeignField() {
        Contact expectedForeignField = data.contacts.stream().toList().get(0);
        int e_CiID = expectedForeignField.getInstanceIds().stream().toList().get(0);
        Optional<ContactInstance> byId = instanceRepository.findById(e_CiID);
        assert byId.isPresent();

        ContactInstance queryInstance = byId.get();
        Contact foreignField = queryInstance.getAttachedContact();
        assertThat(foreignField).isNotNull();
        System.out.println(foreignField);
        assertThat(foreignField).isEqualTo(expectedForeignField);
    }

    /**
     * Test if `ManyToOne` foreign field can cascaded update.
     */
    @Test
    void saveForeignField() {
        Contact contact = data.contacts.stream().toList().get(0);
        int e_CiID = contact.getInstanceIds().stream().toList().get(0);
        Optional<ContactInstance> byId = instanceRepository.findById(e_CiID);
        assert byId.isPresent();

        ContactInstance queryInstance = byId.get();
        Contact foreignField = queryInstance.getAttachedContact();
        assertThat(foreignField).isNotNull();

        int e_CID = foreignField.get_CID();
        int old_unread = foreignField.getUnread();
        int new_unread = old_unread + 1;
        int old_stateOrder = foreignField.getStateOrder();
        foreignField.setUnread(new_unread);
        instanceRepository.saveAndFlush(queryInstance);

        Optional<Contact> r = contactRepository.findById(e_CID);
        assert r.isPresent();
        Contact updatedContact = r.get();
        assertThat(updatedContact.getUnread()).isEqualTo(new_unread);
        assertThat(updatedContact.getStateOrder()).isGreaterThan(old_stateOrder);
    }
}
