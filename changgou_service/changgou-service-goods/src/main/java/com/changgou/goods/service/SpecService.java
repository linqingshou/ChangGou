package com.changgou.goods.service;

import com.changgou.goods.pojo.Para;
import com.changgou.goods.pojo.Spec;

import java.util.List;

public interface SpecService {

    /***
     * 根据商品分类名称查询规格列表
     * @param ctegoryName
     * @return
     */
    List<Spec> findByCategoryName( String ctegoryName);
}
