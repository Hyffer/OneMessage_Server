package xyz.hyffer.onemessage_server.storage.mapper;

import org.apache.ibatis.annotations.Mapper;
import xyz.hyffer.onemessage_server.storage.component.Contact;
import xyz.hyffer.onemessage_server.storage.component.ContactInfo;

import java.util.List;

@Mapper
public interface ContactMapper {

    List<Contact> getContacts(int limit);

    List<Contact> searchContacts(String key, int limit);

    Contact findContactByCID(int _CID);

    Contact findContactById(int _SID, long id);

    ContactInfo findContactInfoByCID(int _CID);

    /**
     * Add a new contact, with UNIQUE id and remark
     * @param contact contact to be added
     *                auto increment key `_CID` will be assigned
     * @return affected rows
     */
    Integer addContact(Contact contact);

    /**
     * Add a new source of the contact
     * @param contact contact
     * @param info source to be added
     * @return affected rows
     */
    Integer addContactInfo(Contact contact, ContactInfo info);

    /**
     * Update name and remark
     * @param contact contact to be updated
     * @return affected rows
     */
    Integer updateContact(Contact contact, ContactInfo info);

    /**
     * Update total, unread, pinned and lastMsgTime
     * @param contact contact to be updated
     * @return affected rows
     */
    Integer updateContactStatus(Contact contact);

}
