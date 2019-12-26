package com.orange.eventframework.console.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.orange.eventframework.console.dal.entity.EventNameAlias;
import com.orange.eventframework.console.service.EventAliasService;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 事件框架的别名
 *
 * @author yajun.wu
 */
@Service
public class EventAliasServiceImpl implements EventAliasService {

    @Resource
    private RestHighLevelClient esClient;
    @Value("${eventframework.elastic.event_name_alias.index}")
    private String              eventNameAliasIndex;

    @Override
    public EventNameAlias getAlias(String code, String role) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder booleanQueryBuilder = QueryBuilders.boolQuery();
        booleanQueryBuilder.filter(QueryBuilders.termQuery("code", code));
        booleanQueryBuilder.filter(QueryBuilders.termQuery("role", role));
        searchSourceBuilder.query(booleanQueryBuilder);
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(1);

        // 搜索超时时间
        searchSourceBuilder.timeout(new TimeValue(10 * 1000, TimeUnit.MILLISECONDS));

        try {
            SearchRequest searchRequest = new SearchRequest(eventNameAliasIndex);
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            List<EventNameAlias> list = new LinkedList<>();
            SearchHit[] searchHitArr = searchHits.getHits();
            for (SearchHit searchHit : searchHitArr) {
                return new JSONObject(searchHit.getSourceAsMap()).toJavaObject(EventNameAlias.class);
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean save(EventNameAlias eventNameAlias) {
        try {
            IndexRequest request = new IndexRequest(eventNameAliasIndex).source(JSON.toJSONStringWithDateFormat(eventNameAlias, "yyyy-MM-dd'T'HH:mm:ss.SSSZ"), XContentType.JSON);
            if (eventNameAlias.getId() != null) {
                request.id(eventNameAlias.getId().toString());
            }

            IndexResponse response = esClient.index(request, RequestOptions.DEFAULT);
            switch (response.getResult()) {
                case CREATED:
                case UPDATED:
                    return true;
                default:
                    return false;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<EventNameAlias> listAllAlias() {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.existsQuery("code"));
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(10000);

        // 搜索超时时间
        searchSourceBuilder.timeout(new TimeValue(10 * 1000, TimeUnit.MILLISECONDS));

        try {
            SearchRequest searchRequest = new SearchRequest(eventNameAliasIndex);
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            List<EventNameAlias> list = new LinkedList<>();
            SearchHit[] searchHitArr = searchHits.getHits();
            for (SearchHit searchHit : searchHitArr) {
                list.add(new JSONObject(searchHit.getSourceAsMap()).toJavaObject(EventNameAlias.class));
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
