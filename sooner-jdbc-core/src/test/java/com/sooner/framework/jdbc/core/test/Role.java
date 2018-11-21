package com.sooner.framework.jdbc.core.test;

import com.sooner.framework.jdbc.annotation.TableField;


public class Role {

    private Integer id;

    @TableField("roleName")
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
