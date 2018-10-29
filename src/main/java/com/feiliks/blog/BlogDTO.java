package com.feiliks.blog;

import com.feiliks.common.dto.UserDTO;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class BlogDTO {

    private Long id;
    private String slug;
    private String title;
    private boolean published;
    private Date created;
    private Date modified;
    private UserDTO owner;
    private String tags;
    private String content;

    public BlogDTO() {
    }

    public BlogDTO(Blog entity) {
        id = entity.getId();
        slug = entity.getSlug();
        title = entity.getTitle();
        published = entity.isPublished();
        created = entity.getCreated();
        modified = entity.getModified();
        content = entity.getContent();
        owner = entity.getOwner() == null ? null : new UserDTO(entity.getOwner());
        StringBuilder sb = new StringBuilder();
        for (Tag tag : entity.getTags()) {
            sb.append(tag.getName()).append(' ');
        }
        tags = sb.toString();
    }

    public Blog toEntity() {
        Blog entity = new Blog();
        entity.setId(id);
        entity.setSlug(slug);
        entity.setTitle(title);
        entity.setPublished(published);
        entity.setCreated(created);
        entity.setModified(modified);
        entity.setContent(content);
        entity.setOwner(owner == null ? null : owner.toEntity());
        Set<Tag> tagSet = new HashSet<>();
        if (this.tags != null) {
            for (String tag : this.tags.split("\\s+")) {
                Tag t = new Tag();
                t.setName(tag);
                tagSet.add(t);
            }
        }
        entity.setTags(tagSet);
        return entity;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getSlug() {
        return slug;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public boolean isPublished() {
        return this.published;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getCreated() {
        return created;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public Date getModified() {
        return modified;
    }

    public void setOwner(UserDTO owner) {
        this.owner = owner;
    }

    public UserDTO getOwner() {
        return owner;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTags() {
        return this.tags;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

}
