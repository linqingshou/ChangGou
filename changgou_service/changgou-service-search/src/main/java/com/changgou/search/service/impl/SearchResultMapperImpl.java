package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.search.pojo.SkuInfo;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;

import java.util.ArrayList;
import java.util.List;

public class SearchResultMapperImpl implements SearchResultMapper {
    @Override
    public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
        List<T> list = new ArrayList<>();
        SearchHits hits = searchResponse.getHits();
        if (hits == null || hits.getTotalHits() <= 0){
            return new AggregatedPageImpl<>(list);
        }
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
            SkuInfo skuInfo = JSON.parseObject(sourceAsString, SkuInfo.class);
            HighlightField field = hit.getHighlightFields().get("name");
            if (field != null){
                Text[] fragments = field.getFragments();
                StringBuffer stringBuffer = new StringBuffer();
                for (Text fragment : fragments) {
                    stringBuffer.append(fragment);
                }
                skuInfo.setName(stringBuffer.toString());
            }
            list.add((T) skuInfo);
        }
        return new AggregatedPageImpl<T>(list,pageable,hits.getTotalHits());
    }
}
