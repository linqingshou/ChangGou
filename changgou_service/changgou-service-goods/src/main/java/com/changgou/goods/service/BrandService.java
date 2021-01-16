package com.changgou.goods.service;

import com.changgou.goods.pojo.Brand;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

public interface BrandService {

    /***
     * 查询全部品牌
     * @return
     */
    List<Brand> findAll();

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    Brand findById(Integer id);

    /***
     * 新增品牌
     * @param brand
     */
    void add(Brand brand);

    /***
     * 修改品牌数据
     * @param brand
     */
    void update(Brand brand);

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    void delete(Integer id);

    /***
     * 多条件搜索品牌方法
     * @param brand
     * @return
     */
    List<Brand> findList(Brand brand);

    /***
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<Brand> findPage(int page, int size);

    /***
     * 多条件分页查询
     * @param brand
     * @param page
     * @param size
     * @return
     */
    PageInfo<Brand> findPage(Brand brand, int page, int size);

    /***
     * 根据商品分类名称查询品牌列表
     * @param categoryName
     * @return
     */
    List<Brand> findByCategoryName(String categoryName);
}
