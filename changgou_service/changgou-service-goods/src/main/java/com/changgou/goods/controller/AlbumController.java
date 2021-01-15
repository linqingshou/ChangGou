package com.changgou.goods.controller;


import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.goods.pojo.Album;
import com.changgou.goods.service.AlbumService;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/album")
@CrossOrigin
public class AlbumController {
    @Autowired
    private AlbumService albumService;

    /***
     * 查询全部相册
     * @return
     */
    @GetMapping
    public Result findAll(){
        List<Album> albums = albumService.findAll();
        return new Result(true, StatusCode.OK,"查询成功",albums);
    }

    /***
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable("id") Long id){
        Album album = albumService.findById(id);
        return new Result(true, StatusCode.OK,"查询成功",album);
    }

    /***
     * 新增Album
     * @param album
     */
    @PostMapping
    public Result add(@RequestBody Album album){
        albumService.add(album);
        return new Result(true, StatusCode.OK,"新增成功");
    }

    /***
     * 根据id删除
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable("id") Long id){
        albumService.deleteById(id);
        return new Result(true, StatusCode.OK,"删除成功");
    }

    @PutMapping("/{id}")
    public Result update(@RequestBody Album album,@PathVariable Long id){
        album.setId(id);
        albumService.update(album);
        return new Result(true, StatusCode.OK,"修改成功");
    }

    /***
     * 条件查询
     * @param album
     * @return
     */
    @PostMapping("/search")
    public Result findByExample(@RequestBody Album album){
        List<Album> albums = albumService.findList(album);
        return new Result(true, StatusCode.OK,"查询成功",albums);
    }
}
