package xyz.hyffer.onemessage_server.storage.mapper;

import org.apache.ibatis.annotations.Mapper;
import xyz.hyffer.onemessage_server.storage.component.Contact;

import java.util.List;

@Mapper
public interface ContactMapper {

    List<Contact> getContacts(int limit);

    List<Contact> searchContacts(String key, int limit);

    Contact findContactByCID(int _CID);

    Contact findContactById(long id);

    /**
     * Add a new contact, with UNIQUE id and remark
     * @param contact contact to be added
     *                auto increment key `_CID` will be assigned
     * @return affected rows
     */
    Integer addContact(Contact contact);

    /**
     * Update name and remark
     * @param contact contact to be updated
     * @return affected rows
     */
    Integer updateContact(Contact contact);

    /**
     * Update total, unread, pinned and lastMsgTime
     * @param contact contact to be updated
     * @return affected rows
     */
    Integer updateContactStatus(Contact contact);

}
