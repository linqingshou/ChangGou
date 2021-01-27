package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.Result;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuEsMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuService;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private SkuEsMapper skuEsMapper;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

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
    /***
     * 搜索
     * @param searchMap
     * @return
     */
    @Override
    public Map search(Map<String, String> searchMap) {
        String keywords = searchMap.get("keywords");
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        if (!StringUtils.isEmpty(keywords)) {
            queryBuilder.withQuery(QueryBuilders.matchQuery("name", keywords));
        }

        queryBuilder.addAggregation(AggregationBuilders.terms("categoryName").field("categoryName"));

        NativeSearchQuery build = queryBuilder.build();
        AggregatedPage<SkuInfo> skuPage = elasticsearchTemplate.queryForPage(build, SkuInfo.class);

        List<String> categoryList = getCategoryList(skuPage);

        Map map = new HashMap<>();
        map.put("rows", skuPage.getContent());
        map.put("total",skuPage.getTotalElements());
        map.put("totalPages",skuPage.getTotalPages());
        map.put("categoryList",categoryList);
        return map;
    }

    private List<String> getCategoryList(AggregatedPage<SkuInfo> skuPage) {
        StringTerms stringTerms = (StringTerms) skuPage.getAggregation("categoryName");
        List<String> categoryList = new ArrayList<>();
        if (stringTerms != null){
            for (StringTerms.Bucket bucket : stringTerms.getBuckets()){
                String key = bucket.getKeyAsString();
                categoryList.add(key);
            }
        }
        return categoryList;
    }
}
