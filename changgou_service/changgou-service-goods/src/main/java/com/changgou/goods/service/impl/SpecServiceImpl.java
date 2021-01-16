package com.changgou.goods.service.impl;

import com.changgou.goods.dao.SpecMapper;
import com.changgou.goods.pojo.Spec;
import com.changgou.goods.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class SpecServiceImpl implements SpecService {
    @Autowired
    private SpecMapper specMapper;

    /***
     * 根据商品分类名称查询规格列表
     * @param ctegoryName
     * @return
     */
    @Override
    public List<Spec> findByCategoryName(String ctegoryName) {
        List<Spec> specs = specMapper.findByCategoryName(ctegoryName);
        return specs;
    }
}
