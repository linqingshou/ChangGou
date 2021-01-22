package com.changgou.content.service;

import com.changgou.content.pojo.Content;

import java.util.List;

public interface ContentService {
    /***
     * 根据分类id查询
     * @param id
     * @return
     */
    List<Content> findByCategoryId(Long id);
}
