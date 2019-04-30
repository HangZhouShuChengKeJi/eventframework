package com.orange.eventframework.console.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.orange.commons.support.elasticsearch.ESHelper;
import com.orange.eventframework.console.entity.EventRelation;
import com.orange.eventframework.console.service.EventRelationService;
import com.orange.eventframework.eventinfo.ConsumeEventInfo;
import com.orange.eventframework.eventinfo.ProduceEventInfo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 小天
 * @date 2019/4/10 11:25
 */
@Service
public class EventRelationServiceImpl implements EventRelationService {

    @Resource
    private ESHelper esHelper;

    @Value("${eventframework.elastic.event.index}")
    public String eventIndex;

    @Value("${eventframework.elastic.event.type}")
    public String eventType;

    @Value("${eventframework.elastic.event_relation.index}")
    private String eventRelationIndex;

    @Value("${eventframework.elastic.event_relation.type}")
    private String eventRelationType;

    @Override
    public void save(EventRelation eventRelation) {
        String eventRelationJson = JSON.toJSONString(eventRelation);

        esHelper.save(eventRelationIndex, eventRelationType, DigestUtils.md5Hex(eventRelationJson), eventRelationJson);

    }

    @Override
    public void save(ProduceEventInfo eventInfo) {
        esHelper.save(eventIndex, eventType, null, JSON.toJSONString(eventInfo));
    }

    @Override
    public void save(ConsumeEventInfo eventInfo) {
        esHelper.save(eventIndex, eventType, null, JSON.toJSONString(eventInfo));
    }

    @Override
    public List<EventRelation> listAll() {
        List<Map<String, Object>> searchResponse = esHelper.searchSourceMapList(eventRelationIndex, QueryBuilders.existsQuery("eventCode"), 0, 1000, 10 * 1000);

        if (CollectionUtils.isEmpty(searchResponse)) {
            return Collections.emptyList();
        }
        return searchResponse.stream().map(i -> new JSONObject(i).toJavaObject(EventRelation.class)).collect(Collectors.toList());
    }
}
