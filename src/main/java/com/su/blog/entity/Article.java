package com.su.blog.entity;

import com.su.blog.entity.vo.CommentBo;
import com.su.blog.entity.vo.PageBo;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Data
@Document(indexName = "blog")
public class Article extends PageBo implements Serializable {
    @Id
    private Integer id;

    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String title;

    private Integer viewCount ;

    private String createdAt;

    private String updatedAt;

    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String content;

    private List<Category> categories = new LinkedList<>();
    private List<Tag> tags = new LinkedList<>();
    private List<CommentBo> comments = new LinkedList<>();

    private static final long serialVersionUID = 1L;

    public Article() {
    }

    public Article(Integer id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public Article(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public List<Category> getCategories() {
        if (categories == null) {
            return new LinkedList<>();
        }
        return categories;
    }

    public void setCategories(List<Category> categories) {
        if (categories == null) {
            this.categories = new LinkedList<>();
        } else {
            this.categories = categories;
        }
    }

    public List<Tag> getTags() {
        if (tags == null) {
            return new LinkedList<>();
        }
        return tags;
    }

    public void setTags(List<Tag> tags) {
        if (tags == null) {
            this.tags = new LinkedList<>();
        } else {
            this.tags = tags;
        }

    }
}
