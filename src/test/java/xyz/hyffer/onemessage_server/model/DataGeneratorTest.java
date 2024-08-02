package xyz.hyffer.onemessage_server.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DataGeneratorTest {
    @Test
    void generatedContactTest() {
        List<Contact> contacts = DataGenerator.getTestContacts();
        List<Contact> contacts_withoutGenValue = DataGenerator.getTestContacts_withoutGenValue();
        assert !contacts.isEmpty();
        assertThat(contacts.size()).isEqualTo((contacts_withoutGenValue.size()));

        Contact contact = contacts.get(0);
        Contact contact_withoutGenValue = contacts_withoutGenValue.get(0);
        System.out.println(contact);
        System.out.println(contact_withoutGenValue);
        assert !contact.getInstances().isEmpty();
        assertThat(contact.getInstances().size()).isEqualTo(contact_withoutGenValue.getInstances().size());
        ContactInstance contactInstance
                = (ContactInstance) contact.getInstances().toArray()[0];
        ContactInstance contactInstance_withoutGenValue
                = (ContactInstance) contact_withoutGenValue.getInstances().toArray()[0];

        // make sure test data are deeply copied
        assertThat(contact).isNotEqualTo(contact_withoutGenValue);
        assertThat(contact.getRemark()).isEqualTo(contact_withoutGenValue.getRemark());
        assertThat(contactInstance).isNotEqualTo(contactInstance_withoutGenValue);
        assertThat(contactInstance.getName()).isEqualTo(contactInstance_withoutGenValue.getName());

        // make sure relationship is maintained
        assertThat(contactInstance.getAttachedContact()).isSameAs(contact);

        // check generated value
        assertThat(contact._CID).isNotEqualTo(0);
        assertThat(contact_withoutGenValue._CID).isEqualTo(0);
        assertThat(contactInstance._CiID).isNotEqualTo(0);
        assertThat(contactInstance_withoutGenValue._CiID).isEqualTo(0);
    }
}
