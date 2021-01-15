package com.changgou.goods.service;

import com.changgou.goods.pojo.Album;
import com.changgou.goods.pojo.Brand;

import java.util.List;

public interface AlbumService {

    /***
     * 查询全部相册
     * @return
     */
    List<Album> findAll();

    /***
     * 根据id查询
     * @param id
     * @return
     */
    Album findById(Long id);

    /***
     * 新增Album
     * @param album
     */
    void add(Album album);

    /***
     * 根据id删除
     * @param id
     * @return
     */
    void deleteById(Long id);

    /***
     * 修改数据
     * @param album
     */
    void update(Album album);

    /***
     * 多条件搜索
     * @param album
     * @return
     */
    List<Album> findList(Album album);
}
