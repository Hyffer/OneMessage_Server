package xyz.hyffer.onemessage_server.storage.component;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    int unread;
    boolean pinned;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    Timestamp lastMsgTime;

    public Contact() {}

    public Contact(String type, long id, String name, String remark) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.remark = remark;
    }
}
