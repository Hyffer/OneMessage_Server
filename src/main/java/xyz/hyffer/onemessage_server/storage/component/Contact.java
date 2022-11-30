package xyz.hyffer.onemessage_server.storage.component;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
public class Contact {
    int _CID;
    String type;
    String avatar;
    String remark;
    int total;
    int unread;
    boolean pinned;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    Timestamp lastMsgTime;

    List<ContactInfo> contactInfos = new ArrayList<>();

    public Contact() {}

    public Contact(String type, String id, String name, String remark) {
        this.type = type;
        this.remark = remark;
        this.contactInfos.add(new ContactInfo(id, name));
    }

    public ContactInfo getContactInfo(int _SID) {
        for (ContactInfo info : contactInfos) {
            if (info.get_SID() == _SID) return info;
        }
        return null;
    }
}
