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
package com.sooner.framework.jdbc.core.injector.methods;

import com.sooner.framework.jdbc.core.enums.SqlMethod;
import com.sooner.framework.jdbc.core.injector.AbstractMethod;
import com.sooner.framework.jdbc.core.metadata.TableInfo;
import com.sooner.framework.jdbc.core.toolkit.Constants;
import com.sooner.framework.jdbc.core.toolkit.StringPool;
import com.sooner.framework.jdbc.core.toolkit.sql.SqlScriptUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * <p>
 * 根据 ID 集合删除
 * </p>
 *
 * @author hubin
 * @since 2018-04-06
 */
public class DeleteBatchByIds extends AbstractMethod {

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        SqlMethod sqlMethod = SqlMethod.DELETE_BATCH_BY_IDS;
        String sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), tableInfo.getKeyColumn(),
            SqlScriptUtils.convertForeach("#{item}", Constants.COLLECTION, null, "item", StringPool.COMMA));
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addDeleteMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource);
    }
}
