package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.Result;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuEsMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuService;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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

    /***
     * 导入SKU数据
     */
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
    public Map<String, Object> search(Map<String, String> searchMap) {

        NativeSearchQuery build = getNativeSearchQuery(searchMap);

        AggregatedPage<SkuInfo> skuPage = elasticsearchTemplate.queryForPage(build, SkuInfo.class, new SearchResultMapperImpl());

        List<String> categoryList = getList(skuPage, "skuCategoryGroup");
        List<String> brandList = getList(skuPage, "skuBrandGroup");
        Map<String, Set<String>> skuList = getStringSetMap(skuPage, "skuSpecGroup");


        Map<String, Object> resultMap = getMap(skuPage, categoryList, brandList, skuList);
        return resultMap;
    }

    private Map<String, Object> getMap(AggregatedPage<SkuInfo> skuPage, List<String> categoryList, List<String> brandList, Map<String, Set<String>> skuList) {
        Map<String, Object> map = new HashMap<>();
        map.put("rows", skuPage.getContent());
        map.put("total", skuPage.getTotalElements());
        map.put("totalPages", skuPage.getTotalPages());
        map.put("categoryList", categoryList);
        map.put("brandList", brandList);
        map.put("skuList", skuList);
        return map;
    }

    private NativeSearchQuery getNativeSearchQuery(Map<String, String> searchMap) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //设置高亮条件
        HighlightBuilder.Field field = new HighlightBuilder.Field("name");
        field.preTags("<em style=\"color:red\">");
        field.postTags("</em>");
        queryBuilder.withHighlightFields(field);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (searchMap != null) {
            String keywords = searchMap.get("keywords");
            if (!StringUtils.isEmpty(keywords)) {
                queryBuilder.withQuery(QueryBuilders.matchQuery("name", keywords));
            }
            if (!StringUtils.isEmpty(searchMap.get("brand"))) {
                boolQueryBuilder.filter(QueryBuilders.termQuery("brandName", searchMap.get("brand")));
            }
            if (!StringUtils.isEmpty(searchMap.get("category"))) {
                boolQueryBuilder.filter(QueryBuilders.termQuery("categoryName", searchMap.get("category")));
            }
            for (String key : searchMap.keySet()) {
                if (key.startsWith("spec_")) {
                    boolQueryBuilder.filter(QueryBuilders.termQuery("spec." + key.substring(5) + ".keyword", searchMap.get(key)));
                }
            }
            String price = searchMap.get("price");
            if (!StringUtils.isEmpty(price)) {
                String[] split = price.split("-");
                if (split.length == 2) {
                    boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").from(split[0], true).to(split[1], true));
                } else {
                    boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(split[0]));
                }
            }
            String sortRule = searchMap.get("sortRule");
            String sortField = searchMap.get("sortField");
            if (!StringUtils.isEmpty(sortField) && !StringUtils.isEmpty(sortRule)) {
                queryBuilder.withSort(SortBuilders.fieldSort(sortField).order(sortRule.equalsIgnoreCase("DESC") ? SortOrder.DESC : SortOrder.ASC));
            }
        }
        queryBuilder.withFilter(boolQueryBuilder);

        // 分页
        Integer pageNum = 1;
        Integer pageSize = 3;
        if (!StringUtils.isEmpty(searchMap.get("pageNum"))) {
            pageNum = Integer.valueOf(searchMap.get("pageNum"));
        }
        queryBuilder.withPageable(PageRequest.of(pageNum, pageSize));

        //设置分组条件并构建查询对象
        NativeSearchQuery build = getNativeSearchQuery(queryBuilder);
        return build;
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
