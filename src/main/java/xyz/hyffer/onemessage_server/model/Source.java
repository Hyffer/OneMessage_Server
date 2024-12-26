package xyz.hyffer.onemessage_server.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Source {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "source_seq")
    @SequenceGenerator(name = "source_seq", allocationSize = 1)
    int _SID;
    @Column(unique = true, nullable = false)
    String name;

    public Source() {}

    public Source(String name) {
        this.name = name;
    }
}
