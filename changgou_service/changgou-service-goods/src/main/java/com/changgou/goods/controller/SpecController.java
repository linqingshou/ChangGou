package com.changgou.goods.controller;


import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.goods.pojo.Spec;
import com.changgou.goods.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spec")
@CrossOrigin
public class SpecController {
    @Autowired
    private SpecService specService;

    /***
     * 根据商品分类名称查询规格列表
     * @param ctegoryName
     * @return
     */
    @GetMapping("/category/{category}")
    public Result findByCategoryName(@PathVariable("category") String ctegoryName){
        List<Spec> specs = specService.findByCategoryName(ctegoryName);
        return new Result(true, StatusCode.OK,"查询成功",specs);
    }
}
