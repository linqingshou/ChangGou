package com.changgou.goods.service.impl;

import com.changgou.goods.dao.AlbumMapper;
import com.changgou.goods.pojo.Album;
import com.changgou.goods.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class AlbumServiceImpl implements AlbumService {
    @Autowired
    private AlbumMapper albumMapper;

    /***
     * 查询全部相册
     * @return
     */
    @Override
    public List<Album> findAll() {
        return albumMapper.selectAll();
    }

    /***
     * 根据id查询
     * @param id
     * @return
     */
    @Override
    public Album findById(Long id) {
        return albumMapper.selectByPrimaryKey(id);
    }

    /***
     * 新增Album
     * @param album
     */
    @Override
    public void add(Album album) {
        albumMapper.insert(album);
    }

    /***
     * 根据id删除
     * @param id
     * @return
     */
    @Override
    public void deleteById(Long id) {
        albumMapper.deleteByPrimaryKey(id);
    }

    /***
     * 修改数据
     * @param album
     */
    @Override
    public void update(Album album) {
        albumMapper.updateByPrimaryKey(album);
    }

    /***
     * 条件查询
     * @param album
     * @return
     */
    @Override
    public List<Album> findList(Album album) {
        Example example = createExample(album);
        List<Album> albums = albumMapper.selectByExample(example);
        return albums;
    }


    private Example createExample(Album album){
        Example example = new Example(Album.class);
        Example.Criteria criteria = example.createCriteria();
        if (album != null){
            if (StringUtils.isEmpty(album.getImage())){
                criteria.andLike("image","%" + album.getImage() + "%");
            }
            if (StringUtils.isEmpty(album.getImageItems())){
                criteria.andLike("image_items","%" + album.getImageItems() + "%");
            }
            if (StringUtils.isEmpty(album.getTitle())){
                criteria.andLike("title","%" + album.getTitle() + "%");
            }

        }
        return example;
    }
}
