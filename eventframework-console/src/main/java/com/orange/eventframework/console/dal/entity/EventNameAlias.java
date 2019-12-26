package com.orange.eventframework.console.dal.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 * @table event_name_alias
 * @author MyBatis Generator
 * @version 1.0.0, 2019-12-25 07:55:34
 */
public class EventNameAlias implements Serializable {
    /**
     * 
     * 
     * @column event_name_alias.id
     */
    private Integer id;

    /**
     * Code
     * 
     * @column event_name_alias.code
     */
    private String code;

    /**
     * 显示名称
     * 
     * @column event_name_alias.display_name
     */
    private String displayName;

    /**
     * 角色
     * 
     * @column event_name_alias.role
     */
    private String role;

    /**
     * 
     * 
     * @column event_name_alias.create_time
     */
    private Date createTime;

    /**
     * 
     * 
     * @column event_name_alias.update_time
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    /**
     * @column event_name_alias.id
     * 
     * @return 
     */
    public Integer getId() {
        return id;
    }

    /**
     * @column event_name_alias.id
     * 
     * @param id 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @column event_name_alias.code
     * 
     * @return Code
     */
    public String getCode() {
        return code;
    }

    /**
     * @column event_name_alias.code
     * 
     * @param code Code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @column event_name_alias.display_name
     * 
     * @return 显示名称
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @column event_name_alias.display_name
     * 
     * @param displayName 显示名称
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @column event_name_alias.role
     * 
     * @return 角色
     */
    public String getRole() {
        return role;
    }

    /**
     * @column event_name_alias.role
     * 
     * @param role 角色
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * @column event_name_alias.create_time
     * 
     * @return 
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @column event_name_alias.create_time
     * 
     * @param createTime 
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @column event_name_alias.update_time
     * 
     * @return 
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @column event_name_alias.update_time
     * 
     * @param updateTime 
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}