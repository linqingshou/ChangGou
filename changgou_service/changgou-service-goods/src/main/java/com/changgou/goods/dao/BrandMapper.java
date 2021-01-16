package com.changgou.goods.dao;

import com.changgou.goods.pojo.Brand;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;


public interface BrandMapper extends Mapper<Brand> {

    @Select("SELECT *  FROM tb_brand where id in (SELECT brand_id FROM tb_category_brand where category_id in (select id FROM tb_category where `name` = #{categoryName})) ORDER BY seq")
    public List<Brand> findByCategoryName(@Param("categoryName") String categoryName);
}
