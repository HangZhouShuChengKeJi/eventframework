package com.orange.eventframework.console.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.orange.eventframework.console.entity.EventRelation;
import com.orange.eventframework.console.service.EventRelationService;
import com.orange.eventframework.eventinfo.ConsumeEventInfo;
import com.orange.eventframework.eventinfo.ProduceEventInfo;
import org.apache.commons.codec.digest.DigestUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
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
 * @author 小天
 * @date 2019/4/10 11:25
 */
@Service
public class EventRelationServiceImpl implements EventRelationService {

    @Resource
    private RestHighLevelClient esClient;

    @Value("${eventframework.elastic.event.index}")
    public String eventIndex;

    @Value("${eventframework.elastic.event.type}")
    public String eventType;

    @Value("${eventframework.elastic.event_relation.index}")
    private String eventRelationIndex;

    @Value("${eventframework.elastic.event_relation.type}")
    private String eventRelationType;

    @Override
    public boolean save(EventRelation eventRelation) {
        String eventRelationJson = JSON.toJSONString(eventRelation);
        try {
            IndexResponse response = esClient.index(new IndexRequest(eventRelationIndex).source(eventRelationJson, XContentType.JSON).id(DigestUtils.md5Hex(eventRelationJson)), RequestOptions.DEFAULT);
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
    public boolean save(ProduceEventInfo eventInfo) {
        try {
            IndexResponse response = esClient.index(new IndexRequest(eventIndex).source(JSON.toJSONStringWithDateFormat(eventInfo, "yyyy-MM-dd'T'HH:mm:ss.SSSZ"), XContentType.JSON), RequestOptions.DEFAULT);
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
    public boolean save(ConsumeEventInfo eventInfo) {
        try {
            IndexResponse response = esClient.index(new IndexRequest(eventIndex).source(JSON.toJSONStringWithDateFormat(eventInfo, "yyyy-MM-dd'T'HH:mm:ss.SSSZ"), XContentType.JSON), RequestOptions.DEFAULT);
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
    public List<EventRelation> listAll() {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.existsQuery("eventCode"));
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(10000);

        // 搜索超时时间
        searchSourceBuilder.timeout(new TimeValue(10 * 1000, TimeUnit.MILLISECONDS));

        try {
            SearchRequest searchRequest = new SearchRequest(eventRelationIndex);
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            List<EventRelation> list = new LinkedList<>();
            SearchHit[] searchHitArr = searchHits.getHits();
            for (SearchHit searchHit : searchHitArr) {
                list.add(new JSONObject(searchHit.getSourceAsMap()).toJavaObject(EventRelation.class));
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
