package com.orange.eventframework.console.service;

import com.orange.eventframework.console.dal.entity.EventNameAlias;

import java.util.List;

/**
 * 事件框架的别名service
 *
 * @author yajun.wu
 */
public interface EventAliasService {


    EventNameAlias getAlias(String code, String role);

    boolean save(EventNameAlias eventNameAlias);

    List<EventNameAlias> listAllAlias();
}
