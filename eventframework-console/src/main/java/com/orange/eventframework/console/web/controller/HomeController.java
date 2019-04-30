package com.orange.eventframework.console.web.controller;

import com.alibaba.fastjson.JSON;
import com.orange.eventframework.console.entity.EventRelation;
import com.orange.eventframework.console.service.EventRelationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 小天
 * @date 2019/1/18 15:13
 */
@Controller
public class HomeController {

    @Resource
    private EventRelationService eventRelationService;

    @RequestMapping(path = {"/index.htm"})
    public String index(Model model) {
        List<EventRelation> list = eventRelationService.listAll();
        model.addAttribute("eventRelationListJson", JSON.toJSONString(list));
        return "index";
    }
}
