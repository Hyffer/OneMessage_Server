package xyz.hyffer.onemessage_server.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ContactTest {
    @Test
    void addInstance_alreadyExist() {
        Contact contact = DataGenerator.getTestContacts().get(0);
        assert !contact.getInstances().isEmpty();
        ContactInstance instance = contact.getInstances().iterator().next();

        boolean added = contact.addInstance(instance);
        assertThat(added).isFalse();
    }

    @Test
    void updateInstance_notExist() {
        ContactInstance instance = new ContactInstance();
        instance.set_CiID(-1);
        instance.set_SID(9999);
        instance.setId("notExist");

        Contact contact = DataGenerator.getTestContacts().get(0);
        boolean updated = contact.updateInstance(instance);
        assertThat(updated).isFalse();
    }

    @Test
    void getInstanceIds_test() {
        Contact contact = DataGenerator.getTestContacts().get(0);
        Set<ContactInstance> instances = contact.getInstances();
        assert !instances.isEmpty();

        Collection<Integer> _CiIDs_expected = new ArrayList<>();
        for (ContactInstance ci : instances) {
            _CiIDs_expected.add(ci.get_CiID());
        }
        assertThat(contact.getInstanceIds()).usingRecursiveComparison().ignoringCollectionOrder()
                .isEqualTo(_CiIDs_expected);
    }
}
