package com.sooner.framework.jdbc.core.test;

import com.sooner.framework.jdbc.annotation.TableField;
import com.sooner.framework.jdbc.annotation.TableName;

@TableName("sys_user")
public class User {

    private Integer id;

    @TableField("username")
    private String name;

    private Integer roleId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}
