package com.changgou.content.pojo;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name="tb_content")
public class Content implements Serializable{
    @Id
    private Long id;//编号
    private Long categoryId;//内容类目ID
    private String title;//内容标题
    private String url;//链接
    private String pic;//图片绝对路径
    private String status;//状态,0无效，1有效
    private Integer sortOrder;//排序

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}





