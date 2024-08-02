package xyz.hyffer.onemessage_server.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.generator.EventType;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.List;

@Data
@Entity
@Table(indexes = {
        @Index(columnList = "changeOrder", unique = true),
        @Index(name = "message_retrieve_key", columnList = "_CiID, _MID", unique = true)
})
public class Message {
    // TODO: table partition by _CiID,
    //  and use (_CiID, _CiMID) as index?
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_seq")
    @SequenceGenerator(name = "message_seq", allocationSize = 1)
    @Setter(AccessLevel.PACKAGE)
    int _MID;

    /**
     * `changeOrder` is used for synchronization purpose.
     * <p>
     * Message record insert/update happens one by one.
     * This `changeOrder` number represents the modification sequence,
     * for easy data synchronization.
     */
    @Column(columnDefinition="serial")
    @Generated(event = {EventType.INSERT, EventType.UPDATE}, sql = "nextval('message_change_order_seq')")
    @Setter(AccessLevel.PACKAGE)
    int changeOrder;

    int _CiID;
    String direction;

    // message send/receive time
    // one with later `time` has larger `_MID`
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Timestamp time;
    // message modify/withdraw time
    Timestamp modifiedTime;
    boolean deleted;

    // TODO: metadata to refer to a specific message
    //  for reply and withdraw.
    //  these diverge among different agents.
//    int messageId;
//    int internalId;
//    int quoteId;

    // these properties only used in group message
    String type;
    String senderId;
    String senderName;

    @JdbcTypeCode(SqlTypes.JSON)
    List<MessageSegment> segments;

    public Message() {
    }

    @Builder(builderClassName = "MessageRawBuilder",
            builderMethodName = "rawBuilder", access = AccessLevel.PACKAGE)
    public Message(int _MID, int changeOrder,
                   int _CiID, String direction, Timestamp time, Timestamp modifiedTime, boolean deleted,
                   String type, String senderId, String senderName,
                   @Singular List<MessageSegment> segments) {
        this._MID = _MID;
        this.changeOrder = changeOrder;
        this._CiID = _CiID;
        this.direction = direction;
        this.time = time;
        this.modifiedTime = modifiedTime;
        this.deleted = deleted;
        this.type = type;
        this.senderId = senderId;
        this.senderName = senderName;
        this.segments = segments;
    }
}
