package com.changgou.content.feign;

import com.changgou.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "content")
@RequestMapping("/content")
public interface ContentFeign {

    /***
     * 根据分类id查询
     * @param id
     * @return
     */
    @GetMapping("/list/category/{id}")
    Result findByCategoryId(@PathVariable Long id);
}
