package xyz.hyffer.onemessage_server.storage.component;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Contact {
    int _CID;
    String type;
    long id;
    String avatar;
    String name;
    String remark;
    int total;
    boolean unread;
    boolean pinned;
    Timestamp lastMsgTime;
}
