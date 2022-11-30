package xyz.hyffer.onemessage_server.storage.component;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.sql.Timestamp;
import java.util.ArrayList;

@Data
public class Message {
    @Id // used for mongodb query, mapping primary key '_id' to attribute '_MID'
    int _MID;
    @Transient
    int _SID;
    @Transient  // make fields transparent to mongodb
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    Timestamp time;
//    int messageId;
//    int internalId;
    @Transient
    String direction;
//    int quoteId;
    @Transient
    String type;
    @Transient
    String senderId;
    @Transient
    String senderName;
    ArrayList<MessageSegment> segments;

    public Message() {}

    public Message(Timestamp time, String direction) {
        this.time = time;
        this.direction = direction;
    }

    public Message(Timestamp time, String direction, String type, String senderId, String senderName) {
        this.time = time;
        this.direction = direction;
        this.type = type;
        this.senderId = senderId;
        this.senderName = senderName;
    }
}
