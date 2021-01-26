package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.changgou.entity.Result;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuEsMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private SkuEsMapper skuEsMapper;

    @Override
    public void importSku() {
        Result<List<Sku>> result = skuFeign.findByStatus("1");
        List<Sku> skuList = result.getData();
        List<SkuInfo> skuInfoList = JSON.parseArray(JSON.toJSONString(skuList),SkuInfo.class);
        for (SkuInfo skuInfo:skuInfoList) {
            String spec = skuInfo.getSpec();
            Map<String,Object> specMap = JSON.parseObject(spec, Map.class);
            skuInfo.setSpecMap(specMap);
        }
        skuEsMapper.saveAll(skuInfoList);
    }
}
