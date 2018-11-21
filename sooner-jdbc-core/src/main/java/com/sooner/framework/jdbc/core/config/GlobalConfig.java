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
package com.sooner.framework.jdbc.core.config;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import com.sooner.framework.jdbc.annotation.DbType;
import com.sooner.framework.jdbc.annotation.FieldStrategy;
import com.sooner.framework.jdbc.annotation.IdType;
import com.sooner.framework.jdbc.core.handlers.MetaObjectHandler;
import com.sooner.framework.jdbc.core.incrementer.IKeyGenerator;
import com.sooner.framework.jdbc.core.injector.ISqlInjector;
import org.apache.ibatis.session.SqlSessionFactory;

import com.sooner.framework.jdbc.core.toolkit.GlobalConfigUtils;

/**
 * <p>
 * Mybatis 全局缓存
 * </p>
 *
 * @author Caratacus
 * @since 2016-12-06
 */
@SuppressWarnings("serial")
public class GlobalConfig implements Serializable {

    /**
     * 是否刷新 mapper
     */
    private boolean refresh = false;
    /**
     * 缓存 Sql 解析初始化
     */
    private boolean sqlParserCache = false;
    /**
     * 数据库相关配置
     */
    private DbConfig dbConfig;
    /**
     * SQL注入器
     */
    private ISqlInjector sqlInjector;
    /**
     * 缓存当前Configuration的SqlSessionFactory
     */
    private SqlSessionFactory sqlSessionFactory;
    /**
     * 缓存已注入CRUD的Mapper信息
     */
    private Set<String> mapperRegistryCache = new ConcurrentSkipListSet<>();
    /**
     * 元对象字段填充控制器
     */
    private MetaObjectHandler metaObjectHandler;

    /**
     * <p>
     * 标记全局设置 (统一所有入口)
     * </p>
     */
    public SqlSessionFactory signGlobalConfig(SqlSessionFactory sqlSessionFactory) {
        if (null != sqlSessionFactory) {
            GlobalConfigUtils.setGlobalConfig(sqlSessionFactory.getConfiguration(), this);
        }
        return sqlSessionFactory;
    }

    public static class DbConfig {

        /**
         * 数据库类型
         */
        private DbType dbType = DbType.OTHER;
        /**
         * 主键类型（默认 ID_WORKER）
         */
        private IdType idType = IdType.ID_WORKER;
        /**
         * 表名前缀
         */
        private String tablePrefix;
        /**
         * 表名、是否使用下划线命名（默认 true:默认数据库表下划线命名）
         */
        private boolean tableUnderline = true;
        /**
         * String 类型字段 LIKE
         */
        private boolean columnLike = false;
        /**
         * 大写命名
         */
        private boolean capitalMode = false;
        /**
         * 表关键词 key 生成器
         */
        private IKeyGenerator keyGenerator;
        /**
         * 逻辑删除全局值（默认 1、表示已删除）
         */
        private String logicDeleteValue = "1";
        /**
         * 逻辑未删除全局值（默认 0、表示未删除）
         */
        private String logicNotDeleteValue = "0";
        /**
         * 字段验证策略
         */
        private FieldStrategy fieldStrategy = FieldStrategy.NOT_NULL;

        public DbType getDbType() {
            return dbType;
        }

        public void setDbType(DbType dbType) {
            this.dbType = dbType;
        }

        public IdType getIdType() {
            return idType;
        }

        public void setIdType(IdType idType) {
            this.idType = idType;
        }

        public String getTablePrefix() {
            return tablePrefix;
        }

        public void setTablePrefix(String tablePrefix) {
            this.tablePrefix = tablePrefix;
        }

        public boolean isTableUnderline() {
            return tableUnderline;
        }

        public void setTableUnderline(boolean tableUnderline) {
            this.tableUnderline = tableUnderline;
        }

        public boolean isColumnLike() {
            return columnLike;
        }

        public void setColumnLike(boolean columnLike) {
            this.columnLike = columnLike;
        }

        public boolean isCapitalMode() {
            return capitalMode;
        }

        public void setCapitalMode(boolean capitalMode) {
            this.capitalMode = capitalMode;
        }

        public IKeyGenerator getKeyGenerator() {
            return keyGenerator;
        }

        public void setKeyGenerator(IKeyGenerator keyGenerator) {
            this.keyGenerator = keyGenerator;
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

        public FieldStrategy getFieldStrategy() {
            return fieldStrategy;
        }

        public void setFieldStrategy(FieldStrategy fieldStrategy) {
            this.fieldStrategy = fieldStrategy;
        }
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    public boolean isSqlParserCache() {
        return sqlParserCache;
    }

    public void setSqlParserCache(boolean sqlParserCache) {
        this.sqlParserCache = sqlParserCache;
    }

    public DbConfig getDbConfig() {
        return dbConfig;
    }

    public void setDbConfig(DbConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public ISqlInjector getSqlInjector() {
        return sqlInjector;
    }

    public void setSqlInjector(ISqlInjector sqlInjector) {
        this.sqlInjector = sqlInjector;
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public Set<String> getMapperRegistryCache() {
        return mapperRegistryCache;
    }

    public void setMapperRegistryCache(Set<String> mapperRegistryCache) {
        this.mapperRegistryCache = mapperRegistryCache;
    }

    public MetaObjectHandler getMetaObjectHandler() {
        return metaObjectHandler;
    }

    public void setMetaObjectHandler(MetaObjectHandler metaObjectHandler) {
        this.metaObjectHandler = metaObjectHandler;
    }
}
