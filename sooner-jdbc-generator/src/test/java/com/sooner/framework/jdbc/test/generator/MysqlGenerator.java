/**
 * Copyright (c) 2011-2016, hubin (jobob@qq.com).
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
package com.sooner.framework.jdbc.test.generator;

import com.sooner.framework.jdbc.annotation.DbType;
import com.sooner.framework.jdbc.annotation.FieldFill;
import com.sooner.framework.jdbc.generator.AutoGenerator;
import com.sooner.framework.jdbc.generator.InjectionConfig;
import com.sooner.framework.jdbc.generator.config.*;
import com.sooner.framework.jdbc.generator.config.converts.MySqlTypeConvert;
import com.sooner.framework.jdbc.generator.config.po.TableFill;
import com.sooner.framework.jdbc.generator.config.po.TableInfo;
import com.sooner.framework.jdbc.generator.config.rules.NamingStrategy;
import com.sooner.framework.jdbc.generator.config.rules.PropertyInfo;
import com.sooner.framework.jdbc.generator.engine.FreemarkerTemplateEngine;

import java.util.*;

/**
 * <p>
 * 代码生成器演示
 * </p>
 *
 * @author hubin
 * @since 2016-12-01
 */
public class MysqlGenerator extends GeneratorTest {

    /**
     * <p>
     * MySQL 生成演示
     * </p>
     */
    public static void main(String[] args) {
        int result = scanner();
        // 自定义需要填充的字段
        List<TableFill> tableFillList = new ArrayList<>();
        tableFillList.add(new TableFill("ASDD_SS", FieldFill.INSERT_UPDATE));

        // 代码生成器
        GlobalConfig globalConfig = new GlobalConfig(){{
            setOutputDir("/develop/code/");//输出目录
            setFileOverride(true);// 是否覆盖文件
            setActiveRecord(true);// 开启 activeRecord 模式
            setEnableCache(false);// XML 二级缓存
            setBaseResultMap(true);// XML ResultMap
            setBaseColumnList(true);// XML columList
            //.setKotlin(true) 是否生成 kotlin 代码
            setAuthor("Yanghu");}};

        DataSourceConfig dataSourceConfig = new DataSourceConfig(){{
            setDbType(DbType.MYSQL);// 数据库类型
            setTypeConvert(new MySqlTypeConvert() {
                    // 自定义数据库表字段类型转换【可选】
                @Override
                public PropertyInfo processTypeConvert(GlobalConfig globalConfig, String fieldType) {
                    System.out.println("转换类型：" + fieldType);
                    return super.processTypeConvert(globalConfig, fieldType); }
            });
            setDriverName("com.mysql.jdbc.Driver");
            setUsername("root");
            setPassword("123456");
            setUrl("jdbc:mysql://127.0.0.1:3306/sooner?characterEncoding=utf8");
        }};

        StrategyConfig strategyConfig = new StrategyConfig(){{
            // .setCapitalMode(true)// 全局大写命名
            // .setDbColumnUnderline(true)//全局下划线命名
            setTablePrefix(new String[]{"bmd_", "mp_"});// 此处可以修改为您的表前缀
            setNaming(NamingStrategy.underline_to_camel);// 表名生成策略

            // 自定义实体，公共字段
            setSuperEntityColumns(new String[]{"test_id"});
            setTableFillList(tableFillList);

        }};

        PackageConfig packageConfig = new PackageConfig(){{
            setModuleName("test") ;
            setParent("com.sooner");// 自定义包路径
            ;setController("controller");// 这里是控制器包名，默认 web
        }};

        InjectionConfig injectionConfig = new InjectionConfig() {
            @Override
            public void initMap() {
                new InjectionConfig() {
                    @Override
                    public void initMap() {
                        Map<String, Object> map = new HashMap<>();
                        map.put("abc", this.getConfig().getGlobalConfig().getAuthor() + "-mp");
                        this.setMap(map);
                    }
                }.setFileOutConfigList(Collections.<FileOutConfig>singletonList(new FileOutConfig(
                    "/templates/mapper.xml" + ((1 == result) ? ".ftl" : ".vm")) {
                    // 自定义输出文件目录
                    @Override
                    public String outputFile(TableInfo tableInfo) {
                        return "/develop/code/xml/" + tableInfo.getEntityName() + ".xml";
                    }
                }));
            }
        };

        AutoGenerator autoGenerator = new AutoGenerator(){{
            setGlobalConfig(globalConfig);
            setDataSource(dataSourceConfig);
            setStrategy(strategyConfig);
            setPackageInfo(packageConfig);
            setCfg(injectionConfig);
            setTemplate(new TemplateConfig(){{setXml(null);}});
        }};

        // 执行生成
        if (1 == result) {
            autoGenerator.setTemplateEngine(new FreemarkerTemplateEngine());
        }
        autoGenerator.execute();

        // 打印注入设置，这里演示模板里面怎么获取注入内容【可无】
        System.err.println(autoGenerator.getCfg().getMap().get("abc"));
    }

}
