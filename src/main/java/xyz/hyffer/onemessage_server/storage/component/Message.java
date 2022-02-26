package xyz.hyffer.onemessage_server.storage.component;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.sql.Timestamp;
import java.util.ArrayList;

@Data
public class Message {
    @Id // used for mongodb query, mapping primary key '_id' to attribute '_MID'
    int _MID;
    Timestamp time;
//    int messageId;
//    int internalId;
    String direction;
//    int quoteId;
    String type;
    long senderId;
    String senderName;
    ArrayList<MessageSegment> segments;
}
