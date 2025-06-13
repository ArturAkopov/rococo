package anbrain.qa.rococo.data;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "user_profile")
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileEntity {
    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(columnDefinition = "BINARY(16)")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(length = 100)
    private String firstname;

    @Column(length = 100)
    private String lastname;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] avatar;
}
