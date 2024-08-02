package xyz.hyffer.onemessage_server.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Source {
    @Id
    int _SID;
    @Column(unique = true, nullable = false)
    String name;

    public Source() {}

    public Source(String name) {
        this.name = name;
    }
}
