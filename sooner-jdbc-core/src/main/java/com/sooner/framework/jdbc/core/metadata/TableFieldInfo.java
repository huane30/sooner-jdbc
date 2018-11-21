/*
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.sooner.framework.jdbc.core.metadata;

import com.sooner.framework.jdbc.annotation.*;
import com.sooner.framework.jdbc.core.config.GlobalConfig;
import com.sooner.framework.jdbc.core.toolkit.StringPool;
import com.sooner.framework.jdbc.core.toolkit.StringUtils;
import com.sooner.framework.jdbc.core.toolkit.TableInfoHelper;
import com.sooner.framework.jdbc.core.toolkit.sql.SqlScriptUtils;
import com.sooner.framework.jdbc.core.toolkit.sql.SqlUtils;

import java.lang.reflect.Field;

/**
 * <p>
 * 数据库表字段反射信息
 * </p>
 *
 * @author hubin sjy willenfoo tantan
 * @since 2016-09-09
 */
public class TableFieldInfo {

    /**
     * 是否有存在字段名与属性名关联
     * true: 表示要进行 as
     */
    private boolean related;
    /**
     * 是否进行 select 查询
     * 大字段可设置为 false 不加入 select 查询范围
     */
    private boolean select = true;
    /**
     * 字段名
     */
    private String column;
    /**
     * 属性名
     */
    private String property;
    /**
     * 属性表达式#{property}, 可以指定jdbcType, typeHandler等
     */
    private String el;
    /**
     * 属性类型
     */
    private Class<?> propertyType;
    /**
     * 属性是否是 CharSequence 类型
     */
    private boolean isCharSequence;
    /**
     * 字段策略【 默认，自判断 null 】
     */
    private FieldStrategy fieldStrategy;
    /**
     * 逻辑删除值
     */
    private String logicDeleteValue;
    /**
     * 逻辑未删除值
     */
    private String logicNotDeleteValue;
    /**
     * 字段 update set 部分注入
     */
    private String update;
    /**
     * where 字段比较条件
     */
    private String condition = SqlCondition.EQUAL;
    /**
     * 字段填充策略
     */
    private FieldFill fieldFill = FieldFill.DEFAULT;
    /**
     * 标记该字段属于哪个类
     */
    private Class<?> clazz;
    /**
     * 缓存 sql select
     */
    private String sqlSelect;

    /**
     * <p>
     * 存在 TableField 注解时, 使用的构造函数
     * </p>
     */
    public TableFieldInfo(GlobalConfig.DbConfig dbConfig, TableInfo tableInfo, Field field,
                          String column, String el, TableField tableField) {
        this.property = field.getName();
        this.propertyType = field.getType();
        this.isCharSequence = StringUtils.isCharSequence(this.propertyType);
        this.fieldFill = tableField.fill();
        this.clazz = field.getDeclaringClass();
        this.update = tableField.update();
        this.el = el;
        tableInfo.setLogicDelete(this.initLogicDelete(dbConfig, field));

        if (StringUtils.isEmpty(tableField.value())) {
            if (tableInfo.isUnderCamel()) {
                column = StringUtils.camelToUnderline(column);
            }
        }
        this.column = column;
        this.related = TableInfoHelper.checkRelated(tableInfo.isUnderCamel(), this.property, this.column);

        /*
         * 优先使用单个字段注解，否则使用全局配置
         */
        if (dbConfig.getFieldStrategy() != tableField.strategy()) {
            this.fieldStrategy = tableField.strategy();
        } else {
            this.fieldStrategy = dbConfig.getFieldStrategy();
        }

        if (StringUtils.isNotEmpty(tableField.condition())) {
            // 细粒度条件控制
            this.condition = tableField.condition();
        } else {
            // 全局配置
            this.setCondition(dbConfig);
        }

        // 字段是否注入查询
        this.select = tableField.select();
    }

    /**
     * <p>
     * 不存在 TableField 注解时, 使用的构造函数
     * </p>
     */
    public TableFieldInfo(GlobalConfig.DbConfig dbConfig, TableInfo tableInfo, Field field) {
        this.property = field.getName();
        this.propertyType = field.getType();
        this.isCharSequence = StringUtils.isCharSequence(this.propertyType);
        this.el = field.getName();
        this.fieldStrategy = dbConfig.getFieldStrategy();
        this.setCondition(dbConfig);
        this.clazz = field.getDeclaringClass();
        tableInfo.setLogicDelete(this.initLogicDelete(dbConfig, field));

        String column = field.getName();
        if (tableInfo.isUnderCamel()) {
            /* 开启字段下划线申明 */
            column = StringUtils.camelToUnderline(column);
        }
        if (dbConfig.isCapitalMode()) {
            /* 开启字段全大写申明 */
            column = column.toUpperCase();
        }
        this.column = column;
        this.related = TableInfoHelper.checkRelated(tableInfo.isUnderCamel(), this.property, this.column);
    }

    /**
     * <p>
     * 逻辑删除初始化
     * </p>
     *
     * @param dbConfig 数据库全局配置
     * @param field    字段属性对象
     */
    private boolean initLogicDelete(GlobalConfig.DbConfig dbConfig, Field field) {
        /* 获取注解属性，逻辑处理字段 */
        TableLogic tableLogic = field.getAnnotation(TableLogic.class);
        if (null != tableLogic) {
            if (StringUtils.isNotEmpty(tableLogic.value())) {
                this.logicNotDeleteValue = tableLogic.value();
            } else {
                this.logicNotDeleteValue = dbConfig.getLogicNotDeleteValue();
            }
            if (StringUtils.isNotEmpty(tableLogic.delval())) {
                this.logicDeleteValue = tableLogic.delval();
            } else {
                this.logicDeleteValue = dbConfig.getLogicDeleteValue();
            }
            return true;
        }
        return false;
    }

    /**
     * 是否开启逻辑删除
     */
    public boolean isLogicDelete() {
        return StringUtils.isNotEmpty(logicDeleteValue);
    }

    /**
     * 全局配置开启字段 LIKE 并且为字符串类型字段
     * 注入 LIKE 查询！！！
     */
    private void setCondition(GlobalConfig.DbConfig dbConfig) {
        if (null == condition || SqlCondition.EQUAL.equals(condition)) {
            if (dbConfig.isColumnLike() && isCharSequence) {
                condition = dbConfig.getDbType().getLike();
            }
        }
    }

    /**
     * 获取 select sql 片段
     *
     * @param dbType 数据库类型
     * @return sql 片段
     */
    public String getSqlSelect(DbType dbType) {
        if (sqlSelect != null) {
            return sqlSelect;
        }
        sqlSelect = SqlUtils.sqlWordConvert(dbType, getColumn(), true);
        if (related) {
            sqlSelect += (" AS " + SqlUtils.sqlWordConvert(dbType, getProperty(), false));
        }
        return sqlSelect;
    }

    /**
     * 获取 inset 时候插入值 sql 脚本片段
     * insert into table (字段) values (值)
     * 位于 "值" 部位
     *
     * @return sql 脚本片段
     */
    public String getInsertSqlProperty() {
        String sqlScript = SqlScriptUtils.safeParam(el) + StringPool.COMMA;
        if (fieldFill == FieldFill.INSERT || fieldFill == FieldFill.INSERT_UPDATE) {
            return sqlScript;
        }
        return convertIf(sqlScript, property);
    }

    /**
     * 获取 inset 时候字段 sql 脚本片段
     * insert into table (字段) values (值)
     * 位于 "字段" 部位
     *
     * @return sql 脚本片段
     */
    public String getInsertSqlColumn() {
        String sqlScript = column + StringPool.COMMA;
        if (fieldFill == FieldFill.INSERT || fieldFill == FieldFill.INSERT_UPDATE) {
            return sqlScript;
        }
        return convertIf(sqlScript, property);
    }

    /**
     * 获取 set sql 片段
     *
     * @param prefix 前缀
     * @return sql 脚本片段
     */
    public String getSqlSet(final String prefix) {
        String newPrefix = prefix == null ? StringPool.EMPTY : prefix;
        // 默认: column=
        String sqlSet = column + StringPool.EQUALS;
        if (StringUtils.isNotEmpty(update)) {
            sqlSet += String.format(update, column);
        } else {
            sqlSet += SqlScriptUtils.safeParam(newPrefix + el);
        }
        sqlSet += StringPool.COMMA;
        if (fieldFill == FieldFill.UPDATE || fieldFill == FieldFill.INSERT_UPDATE) {
            // 不进行 if 包裹
            return sqlSet;
        }
        return convertIf(sqlSet, newPrefix + property);
    }

    /**
     * 获取 查询的 sql 片段
     *
     * @param prefix 前缀
     * @return sql 脚本片段
     */
    public String getSqlWhere(final String prefix) {
        String newPrefix = prefix == null ? StringPool.EMPTY : prefix;
        // 默认:  AND column=#{prefix + el}
        String sqlScript = " AND " + String.format(condition, column, newPrefix + el);
        // 查询的时候只判非空
        return convertIf(sqlScript, newPrefix + property);
    }

    /**
     * 转换成 if 标签的脚本片段
     *
     * @param sqlScript sql 脚本片段
     * @param property  字段名
     * @return if 脚本片段
     */
    private String convertIf(final String sqlScript, final String property) {
        if (fieldStrategy == FieldStrategy.IGNORED) {
            return sqlScript;
        }
        if (fieldStrategy == FieldStrategy.NOT_EMPTY && isCharSequence) {
            return SqlScriptUtils.convertIf(sqlScript, String.format("%s != null and %s != ''", property, property),
                false);
        }
        return SqlScriptUtils.convertIf(sqlScript, String.format("%s != null", property), false);
    }

    public boolean isRelated() {
        return related;
    }

    public void setRelated(boolean related) {
        this.related = related;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getEl() {
        return el;
    }

    public void setEl(String el) {
        this.el = el;
    }

    public Class<?> getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(Class<?> propertyType) {
        this.propertyType = propertyType;
    }

    public boolean isCharSequence() {
        return isCharSequence;
    }

    public void setCharSequence(boolean charSequence) {
        isCharSequence = charSequence;
    }

    public FieldStrategy getFieldStrategy() {
        return fieldStrategy;
    }

    public void setFieldStrategy(FieldStrategy fieldStrategy) {
        this.fieldStrategy = fieldStrategy;
    }

    public String getLogicDeleteValue() {
        return logicDeleteValue;
    }

    public void setLogicDeleteValue(String logicDeleteValue) {
        this.logicDeleteValue = logicDeleteValue;
    }

    public String getLogicNotDeleteValue() {
        return logicNotDeleteValue;
    }

    public void setLogicNotDeleteValue(String logicNotDeleteValue) {
        this.logicNotDeleteValue = logicNotDeleteValue;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public FieldFill getFieldFill() {
        return fieldFill;
    }

    public void setFieldFill(FieldFill fieldFill) {
        this.fieldFill = fieldFill;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getSqlSelect() {
        return sqlSelect;
    }

    public void setSqlSelect(String sqlSelect) {
        this.sqlSelect = sqlSelect;
    }
}
