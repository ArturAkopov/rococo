package anbrain.qa.rococo.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "painting")
public class PaintingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "content", columnDefinition = "LONGBLOB")
    private byte[] content;

    @Column(name = "artist_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID artistId;

    @Column(name = "museum_id", columnDefinition = "BINARY(16)")
    private UUID museumId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaintingEntity that = (PaintingEntity) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(title, that.title) &&
               Objects.equals(description, that.description) &&
               Arrays.equals(content, that.content) &&
               Objects.equals(artistId, that.artistId) &&
               Objects.equals(museumId, that.museumId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, Arrays.hashCode(content), artistId, museumId);
    }

    @Override
    public String toString() {
        return "PaintingEntity{" +
               "id=" + id +
               ", title='" + title + '\'' +
               ", description='" + description + '\'' +
               ", content=[binary data]" +
               ", artistId=" + artistId +
               ", museumId=" + museumId +
               '}';
    }
}