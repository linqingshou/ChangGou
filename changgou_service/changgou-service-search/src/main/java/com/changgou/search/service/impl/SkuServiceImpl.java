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

import java.util.*;

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
        List<SkuInfo> skuInfoList = JSON.parseArray(JSON.toJSONString(skuList), SkuInfo.class);
        for (SkuInfo skuInfo : skuInfoList) {
            String spec = skuInfo.getSpec();
            Map<String, Object> specMap = JSON.parseObject(spec, Map.class);
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
        //设置分组条件并构建查询对象
        NativeSearchQuery build = getNativeSearchQuery(queryBuilder);

        AggregatedPage<SkuInfo> skuPage = elasticsearchTemplate.queryForPage(build, SkuInfo.class);

        List<String> categoryList = getList(skuPage, "skuCategoryGroup");
        List<String> brandList = getList(skuPage, "skuBrandGroup");
        Map<String, Set<String>> skuList = getStringSetMap(skuPage, "skuSpecGroup");


        Map map = new HashMap<>();
        map.put("rows", skuPage.getContent());
        map.put("total", skuPage.getTotalElements());
        map.put("totalPages", skuPage.getTotalPages());
        map.put("categoryList", categoryList);
        map.put("brandList", brandList);
        map.put("skuList", skuList);
        return map;
    }

    /***
     * 设置分组条件并构建查询对象
     * @param queryBuilder
     * @return
     */
    private NativeSearchQuery getNativeSearchQuery(NativeSearchQueryBuilder queryBuilder) {
        queryBuilder.addAggregation(AggregationBuilders.terms("skuCategoryGroup").field("categoryName"));
        queryBuilder.addAggregation(AggregationBuilders.terms("skuBrandGroup").field("brandName"));
        queryBuilder.addAggregation(AggregationBuilders.terms("skuSpecGroup").field("spec.keyword"));

        return queryBuilder.build();
    }

    private List<String> getList(AggregatedPage<SkuInfo> skuPage, String group) {
        StringTerms stringTerms = (StringTerms) skuPage.getAggregation(group);
        List<String> list = new ArrayList<>();
        if (stringTerms != null) {
            for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
                String key = bucket.getKeyAsString();
                list.add(key);
            }
        }
        return list;
    }

    /**
     * 获取规格列表数据
     *
     * @param skuPage
     * @param group
     * @return
     */
    private Map<String, Set<String>> getStringSetMap(AggregatedPage<SkuInfo> skuPage, String group) {
        StringTerms stringTerms = (StringTerms) skuPage.getAggregation(group);
        HashMap<String, Set<String>> specMap = new HashMap<>();
        Set<String> specList = new HashSet<>();
        if (stringTerms != null) {
            for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
                String key = bucket.getKeyAsString();
                specList.add(key);
            }
        }
        for (String specJson : specList) {
            Map<String, String> map = JSON.parseObject(specJson, Map.class);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                Set<String> stringSet = specMap.get(key);
                if (stringSet == null) {
                    stringSet = new HashSet<>();
                }
                stringSet.add(value);
                specMap.put(key, stringSet);
            }

        }
        return specMap;
    }
}
