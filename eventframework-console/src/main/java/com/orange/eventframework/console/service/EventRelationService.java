package com.orange.eventframework.console.service;

import com.alibaba.fastjson.JSONObject;
import com.orange.eventframework.console.entity.EventRelation;
import com.orange.eventframework.eventinfo.ConsumeEventInfo;
import com.orange.eventframework.eventinfo.ProduceEventInfo;

import java.util.List;

/**
 * @author 小天
 * @date 2019/4/10 11:24
 */
public interface EventRelationService {

    void save(EventRelation eventRelation);

    void save(ProduceEventInfo eventInfo);

    void save(ConsumeEventInfo eventInfo);

    List<EventRelation> listAll();
}
