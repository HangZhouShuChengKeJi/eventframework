package com.orange.eventframework.console.service;

import com.orange.eventframework.console.entity.EventRelation;
import com.orange.eventframework.eventinfo.ConsumeEventInfo;
import com.orange.eventframework.eventinfo.ProduceEventInfo;

import java.util.List;

/**
 * @author 小天
 * @date 2019/4/10 11:24
 */
public interface EventRelationService {

    boolean save(EventRelation eventRelation);

    boolean save(ProduceEventInfo eventInfo);

    boolean save(ConsumeEventInfo eventInfo);

    List<EventRelation> listAll();
}
