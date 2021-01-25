package com.changgou.canal.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.changgou.content.feign.ContentFeign;
import com.changgou.content.pojo.Content;
import com.changgou.entity.Result;
import com.xpand.starter.canal.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

@CanalEventListener
public class ContentListener {
    @Autowired
    private ContentFeign contentFeign;
    //字符串
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /***
     *
     * @param eventType 操作类型
     * @param rowData   发生变更的数据
     */
    @ListenPoint(
            destination = "example",//指定实列地址
            eventType = {CanalEntry.EventType.UPDATE, CanalEntry.EventType.INSERT, CanalEntry.EventType.DELETE},//监听类型
            schema = "changgou_content",//监听的数据库
            table = {"tb_content"}//监听的表
    )
    public void contentUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        String categoryId = getColumnValue(eventType, rowData);
        Result<List<Content>> content = contentFeign.findByCategory(Long.valueOf(categoryId));
        List<Content> data = content.getData();
        stringRedisTemplate.boundValueOps("content_" + categoryId).set(JSON.toJSONString(data));
    }

    private String getColumnValue(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        String categoryId = "";
        //判断操作类型
        if (eventType == CanalEntry.EventType.DELETE) {
            List<CanalEntry.Column> columnList = rowData.getBeforeColumnsList();
            for (CanalEntry.Column column : columnList) {
                if (column.getValue().equalsIgnoreCase("category_id")) {
                    categoryId = column.getValue();
                    return categoryId;
                }
            }
        }
        // 更新、新增
        List<CanalEntry.Column> ColumnsList = rowData.getAfterColumnsList();
        for (CanalEntry.Column column : ColumnsList) {
            if (column.getName().equalsIgnoreCase("category_id")) {
                categoryId = column.getValue();
                break;
            }


        }
        return categoryId;
    }
}


