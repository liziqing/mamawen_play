package models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ronald on 2015/3/10.
 */
@Entity
@Table(name = "tag", indexes = {@Index(name = "index_tag_tag", columnList = "tag")})
public class Tag {
    @Id
    @GeneratedValue
    Long id;

    @Column(name = "tag")
    public String tag;

    @ManyToMany(cascade = CascadeType.REFRESH, mappedBy = "tags", fetch = FetchType.EAGER)
    public List<Inquiry>inquiries = new ArrayList<>();

    public Tag() {
    }

    public Tag(String tag) {
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag1 = (Tag) o;

        if (tag != null ? !tag.equals(tag1.tag) : tag1.tag != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return tag != null ? tag.hashCode() : 0;
    }
}
