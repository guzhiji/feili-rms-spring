package com.feiliks.rms.entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.NamedQuery;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "rms_tag")
@NamedQuery(name = "Tag.findOrphaned", query = "select t from Tag t where size(t.requirements) = 0")
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(length = 16)
    private String color;

    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    private Collection<Requirement> requirements;

    public Tag() {
    }

    public Tag(String t) {
        name = t;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * @return the requirements
     */
    public Collection<Requirement> getRequirements() {
        return requirements;
    }

    /**
     * @param requirements the requirements to set
     */
    public void setRequirements(Collection<Requirement> requirements) {
        this.requirements = requirements;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        if (hash == 0) {
            hash += (name != null ? name.hashCode() : 0);
        }
        if (hash != 0) {
            return hash;
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Tag)) {
            return false;
        }
        Tag other = (Tag) object;
        if (this.id == null) {
            if (this.name == null) {
                return super.equals(object);
            }
            return this.name.equals(other.name);
        }
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName() + "[ id=" + id + ", name=" + name + " ]";
    }

}
