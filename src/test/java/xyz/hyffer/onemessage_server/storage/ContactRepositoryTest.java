package xyz.hyffer.onemessage_server.storage;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.hyffer.onemessage_server.model.Contact;
import xyz.hyffer.onemessage_server.model.ContactInstance;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ContactRepositoryTest extends RepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Test
    void findAllContacts() {
        List<Contact> all = contactRepository.findAll();
        System.out.println(all);
        assertThat(all).usingRecursiveComparison().ignoringCollectionOrder()
                .ignoringFields("stateOrder", "changeOrder")    // these properties might have changed during other tests
                .ignoringFields("instances.attachedContact")    // prevent it considering `instances.attachedContact` different
                                                                // although assertj seems could deal with recursion
                .isEqualTo(data.contacts);
    }

    @Test
    void update_changeOrder_increment() {
        int e_CID = data.contacts.stream().toList().get(0).get_CID();
        Optional<Contact> result = contactRepository.findById(e_CID);
        assert result.isPresent();
        Contact contact = result.get();
        int old_changeOrder = contact.getChangeOrder();
        int old_stateOrder = contact.getStateOrder();

        // `changeOrder` increment when contact record updated
        contact.setPinned(true);
        contactRepository.save(contact);
        contactRepository.flush();
        int new_changeOrder = contact.getChangeOrder();
        assertThat(new_changeOrder).isGreaterThan(old_changeOrder);

        // `changeOrder` increment when related instances changed
        int e_CiID = contact.getInstanceIds().stream().toList().get(0);
        Optional<ContactInstance> result2 = instanceRepository.findById(e_CiID);
        assert result2.isPresent();
        ContactInstance ci = result2.get();
        ci.setRemark("new_remark");
        contact.updateInstance(ci);
        contactRepository.save(contact);
        contactRepository.flush();
        int new_changeOrder_2 = contact.getChangeOrder();
        assertThat(new_changeOrder_2).isGreaterThan(new_changeOrder);

        // meanwhile `stateOrder` remains the same
        int new_stateOrder = contact.getStateOrder();
        assertThat(new_stateOrder).isEqualTo(old_stateOrder);
    }

    @Test
    void update_stateOrder_increment() {
        int e_CID = data.contacts.stream().toList().get(0).get_CID();
        Optional<Contact> result = contactRepository.findById(e_CID);
        assert result.isPresent();
        Contact contact = result.get();
        int old_changeOrder = contact.getChangeOrder();
        int old_stateOrder = contact.getStateOrder();

        // `stateOrder` increment
        contact.setUnread(contact.getUnread() + 1);
        contactRepository.save(contact);
        contactRepository.flush();
        int new_stateOrder = contact.getStateOrder();
        assertThat(new_stateOrder).isGreaterThan(old_stateOrder);

        // meanwhile `changeOrder` remains the same
        int new_changeOrder = contact.getChangeOrder();
        assertThat(new_changeOrder).isEqualTo(old_changeOrder);
    }

}
