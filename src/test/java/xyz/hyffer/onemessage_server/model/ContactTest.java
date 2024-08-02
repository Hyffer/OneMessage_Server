package xyz.hyffer.onemessage_server.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ContactTest {
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
