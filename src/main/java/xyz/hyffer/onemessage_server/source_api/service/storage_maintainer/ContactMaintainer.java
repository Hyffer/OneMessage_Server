package xyz.hyffer.onemessage_server.source_api.service.storage_maintainer;

import xyz.hyffer.onemessage_server.model.Contact;

import java.util.Comparator;
import java.util.List;

public class ContactMaintainer {

    // TODO: migrate or split when remark changes, and more cases
    public static void migrateContacts(int _SID, List<Contact> contacts) {
//        // padding and setting _SID
//        for (Contact contact : contacts) {
//            for (int i = 0; i < _SID - 1; i++)
//                contact.getContactInfos().add(i, null);
//            contact.getContactInfos().get(_SID - 1).set_SID(_SID);
//        }
//
//        List<Contact> originalContacts = StaticStorage.contactMapper.getContacts(0);
//        // padding
//        for (Contact c : originalContacts) {
//            for (int i = 0; i < c.getContactInfos().size(); i++) {
//                if (c.getContactInfos().get(i).get_SID() > i + 1)
//                    c.getContactInfos().add(i, null);
//            }
//            for (int i = c.getContactInfos().size(); i < _SID; i++) {
//                c.getContactInfos().add(i, null);
//            }
//        }
//
//        ArrayList<Contact> sortedContacts = new ArrayList<>(originalContacts);
//        sortedContacts.removeIf(c -> c.getContactInfos().get(_SID - 1) == null);
//        ContactComparator comparator = new ContactComparator(_SID);
//        sortedContacts.sort(comparator);
//
//        Contact[] sortedContactsArray = sortedContacts.toArray(new Contact[0]);
//
//        for (Contact contact : contacts) {
//            int index = Arrays.binarySearch(sortedContactsArray, contact, comparator);
//            if (index >= 0) {
//                // have saved this source of the contact earlier
//                // update name and remark
//                if (!contact.getRemark().equals(sortedContactsArray[index].getRemark()) ||
//                        !contact.getContactInfos().get(_SID - 1).getName().equals(sortedContactsArray[index].getContactInfos().get(_SID - 1).getName())) {
//                    contact.setRemark(contact.getRemark());
//                    contact.getContactInfos().get(_SID - 1).setName(contact.getContactInfos().get(_SID - 1).getName());
//                    StaticStorage.contactMapper.updateContact(contact, contact.getContactInfos().get(_SID - 1));
//                }
//            }
//            else {
//                boolean contactExist = false;
//                for (Contact originalContact : originalContacts) {
//                    if (originalContact.getRemark().equals(contact.getRemark())) {
//                        // new source of an already exist contact
//                        StaticStorage.contactMapper.addContactInfo(originalContact, contact.getContactInfos().get(_SID - 1));
//                        contactExist = true;
//                        break;
//                    }
//                }
//                if (!contactExist) {
//                    // new contact
//                    StaticStorage.contactMapper.addContact(contact);
//                    StaticStorage.contactMapper.addContactInfo(contact, contact.getContactInfos().get(_SID - 1));
//                    int _CID = contact.get_CID();
//                    if (contact.getType().equals("Group")) {
//                        StaticStorage.messageMapper.createGroupTable(_CID);
//                    } else {
//                        StaticStorage.messageMapper.createUserTable(_CID);
//                    }
//                }
//            }
//        }

    }

    public static void updateContact(String sourceName, Contact contact) {

    }

}

class ContactComparator implements Comparator<Contact> {

    private final int index;

    public ContactComparator(int _SID) {
        this.index = _SID - 1;
    }

    @Override
    public int compare(Contact o1, Contact o2) {
//        return Long.compare(o1.getContactInfos().get(index).getId(), o2.getContactInfos().get(index).getId());
        return 1;
    }
}