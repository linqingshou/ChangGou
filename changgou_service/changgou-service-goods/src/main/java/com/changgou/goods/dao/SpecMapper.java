package com.changgou.goods.dao;

import com.changgou.goods.pojo.Spec;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpecMapper extends Mapper<Spec> {

    @Select("SELECT * from tb_spec WHERE template_id in (SELECT template_id from tb_category WHERE `name` = #{ctegoryName}) ORDER BY seq")
    public List<Spec> findByCategoryName(@Param("ctegoryName") String ctegoryName);
}
