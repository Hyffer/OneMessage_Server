package xyz.hyffer.onemessage_server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

/**
 * A one-to-one map of a chat in an account,
 * whether it is a person or a group
 * <p>
 * Changes of attributes will be synced from agent software,
 * but they are not editable in OneMessage.
 * Use {@link Contact} to manage them, like pinning in chat list.
 */
@Data
@NoArgsConstructor
@Entity
@Table(indexes = {
        @Index(name = "contact_instance_unique_key", columnList = "_SID, id", unique = true)
})
public class ContactInstance {

    // foreign key
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    // ONLY maintained by Contact.addInstance, updateInstance and removeInstance
    @Setter(AccessLevel.PACKAGE)
    Contact attachedContact;

    // primary key
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contact_instance_seq")
    @SequenceGenerator(name = "contact_instance_seq", allocationSize = 1)
    @Setter(AccessLevel.PACKAGE)
    int _CiID;

    // unique constraint {_SID, id}
    // point at the specific chat in an account
    int _SID;
    String id;

    String name;
    String remark;
    String type;
    String avatar;

    // Most agent software(wechaty, eatmoreapple, mirai, onebot)
    // do not support set/get unread and pinned attribute.
    // So these are not implemented here.

    // if you delete a friend or leave a group, it will be marked as deleted
    boolean deleted;

    @Builder
    public ContactInstance(int _SID, String id,
                           String name, String remark, String type, String avatar) {
        this._SID = _SID;
        this.id = id;
        this.name = name;
        this.remark = remark;
        this.type = type;
        this.avatar = avatar;
        this.deleted = false;
    }

    @Builder(builderClassName = "ContactInstanceRawBuilder",
            builderMethodName = "rawBuilder", access = AccessLevel.PACKAGE)
    ContactInstance(int _CiID,
                    int _SID, String id,
                    String name, String remark, String type, String avatar,
                    boolean deleted) {
        this._CiID = _CiID;
        this._SID = _SID;
        this.id = id;
        this.name = name;
        this.remark = remark;
        this.type = type;
        this.avatar = avatar;
        this.deleted = deleted;
    }

    /**
     * Check if two objects refer to the same record.
     * (`_SID` and `id` fields are the same)
     * @param other another object
     * @return whether refer to the same record
     */
    boolean referEquals(ContactInstance other) {
        return this._SID == other._SID && this.id.equals(other.id);
    }
}
