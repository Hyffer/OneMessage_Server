package xyz.hyffer.onemessage_server.source_api.service.storage_maintainer;

import xyz.hyffer.onemessage_server.storage.component.Contact;

import java.util.List;

public class ContactMaintainer {

    public static void migrateContacts(String sourceName, List<Contact> contacts) {
        List<Contact> originalContacts = StaticStorage.contactMapper.getContacts(0);

        // TODO: sort contact list first, to improve performance
        for (Contact contact : contacts) {
            boolean exist = false;

            for (Contact c : originalContacts) {
                if (contact.getType().equals(c.getType()) && contact.getId() == c.getId()) {
                    // contact exists
                    exist = true;
                    // update information
                    if (!contact.getRemark().equals(c.getRemark()) || !contact.getName().equals(c.getName())) {
                        c.setName(contact.getName());
                        c.setRemark(contact.getRemark());
                        StaticStorage.contactMapper.updateContact(c);
                    }
                    break;
                }
            }

            if (!exist) {
                // contact not exists
                StaticStorage.contactMapper.addContact(contact);
                int _CID = contact.get_CID();
                if (contact.getType().equals("Group")) {
                    StaticStorage.messageMapper.createGroupTable(_CID);
                } else {
                    StaticStorage.messageMapper.createUserTable(_CID);
                }
            }
        }

    }

    public static void updateContact(String sourceName, Contact contact) {

    }

}
