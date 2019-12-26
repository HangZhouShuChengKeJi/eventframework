package com.orange.eventframework.console.web.controller;

import com.alibaba.fastjson.JSON;
import com.orange.eventframework.console.common.constant.EventRoleConstant;
import com.orange.eventframework.console.dal.entity.EventNameAlias;
import com.orange.eventframework.console.entity.EventRelation;
import com.orange.eventframework.console.service.EventAliasService;
import com.orange.eventframework.console.service.EventRelationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 小天
 * @date 2019/1/18 15:13
 */
@Controller
public class HomeController {

    @Resource
    private EventRelationService eventRelationService;
    @Resource
    private EventAliasService    eventAliasService;

    @RequestMapping(path = {"/index.htm"})
    public String index(Model model) {
        List<EventRelation> list = eventRelationService.listAllRelations();

        List<EventNameAlias> nameAlias = eventAliasService.listAllAlias();
        Map<String, String> eventAlias = nameAlias.stream().filter((k) -> k.getRole().equals(EventRoleConstant.EVENT_ROLE.getValue())).collect(Collectors.toMap(EventNameAlias::getCode, EventNameAlias::getDisplayName, (k, v) -> k));
        Map<String, String> consumerAlias = nameAlias.stream().filter((k) -> k.getRole().equals(EventRoleConstant.CONSUMER_ROLE.getValue())).collect(Collectors.toMap(EventNameAlias::getCode, EventNameAlias::getDisplayName, (k, v) -> k));

        for (EventRelation eventRelation : list) {
            eventRelation.setConsumerCode(consumerAlias.get(eventRelation.getConsumerCode()));
            eventRelation.setEventCode(eventAlias.get(eventRelation.getEventCode()));
            eventRelation.setSourceEventCode(eventAlias.get(eventRelation.getSourceEventCode()));
        }

        model.addAttribute("eventRelationListJson", JSON.toJSONString(list));
        return "index";
    }
}
