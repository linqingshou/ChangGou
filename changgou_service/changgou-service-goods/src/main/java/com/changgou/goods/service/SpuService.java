package com.changgou.goods.service;

import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Spu;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface SpuService {

    /**
     * 恢复数据
     * @param id
     */
    public void restore(Long id);

    /**
     * 物理删除
     * @param id
     */
    public void realDelete(Long id);

    /**
     * 下架商品
     * @param id
     */
    public void pull(Long id);

    /**
     * 批量下架商品
     * @param id
     */
    public void pullMany(Long id);

    /**
     * 上架商品
     * @param id
     */
    public void put(Long id);

    /**
     * 批量上架商品
     * @param ids
     */
    public void putMany(Long[] ids);

    /**
     * 审核
     * @param id
     */
    public void audit(Long id);

    /***
     * 查询所有
     * @return
     */
    List<Spu> findAll();

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    Spu findById(Long id);

    /**
     * 根据ID查询商品
     * @param id
     * @return
     */
    Goods findGoodsById(Long id);


    /***
     * 新增
     * @param goods
     */
    void add(Goods goods);

    /***
     * 修改
     * @param goods
     */
    void update(Goods goods);

    /***
     * 删除
     * @param id
     */
    void delete(Long id);

    /***
     * 多条件搜索
     * @param searchMap
     * @return
     */
    List<Spu> findList(Map<String, Object> searchMap);

    /***
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    Page<Spu> findPage(int page, int size);

    /***
     * 多条件分页查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    Page<Spu> findPage(Map<String, Object> searchMap, int page, int size);




}
