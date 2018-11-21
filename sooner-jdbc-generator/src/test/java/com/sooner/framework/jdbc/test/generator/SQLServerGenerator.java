package com.sooner.framework.jdbc.test.generator;

import com.sooner.framework.jdbc.annotation.DbType;
import com.sooner.framework.jdbc.core.toolkit.StringPool;
import com.sooner.framework.jdbc.generator.AutoGenerator;
import com.sooner.framework.jdbc.generator.InjectionConfig;
import com.sooner.framework.jdbc.generator.config.*;
import com.sooner.framework.jdbc.generator.config.converts.SqlServerTypeConvert;
import com.sooner.framework.jdbc.generator.config.po.TableInfo;
import com.sooner.framework.jdbc.generator.config.rules.NamingStrategy;
import com.sooner.framework.jdbc.generator.config.rules.PropertyInfo;
import com.sooner.framework.jdbc.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * SQLServerGenerator
 * </p>
 *
 * @author nieqiurong
 * @since 2016/12/25
 */
public class SQLServerGenerator extends GeneratorTest {

    public static void main(String[] args) {
        int result = scanner();
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        gc.setOutputDir("D://");
        gc.setFileOverride(true);
        gc.setActiveRecord(true);// 开启 activeRecord 模式
        gc.setEnableCache(false);// XML 二级缓存
        gc.setBaseResultMap(true);// XML ResultMap
        gc.setBaseColumnList(false);// XML columList
        //gc.setKotlin(true) 是否生成 kotlin 代码
        gc.setAuthor("nieqiurong");

        // 自定义文件命名，注意 %s 会自动填充表实体属性！
        // gc.setEntityName("%sEntity");
        // gc.setMapperName("%sDao");
        // gc.setXmlName("%sDao");
        // gc.setServiceName("MP%sService");
        // gc.setServiceImplName("%sServiceDiy");
        // gc.setControllerName("%sAction");
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.SQL_SERVER);
        dsc.setTypeConvert(new SqlServerTypeConvert() {
            // 自定义数据库表字段类型转换【可选】
            @Override
            public PropertyInfo processTypeConvert(GlobalConfig globalConfig, String fieldType) {
                System.out.println("转换类型：" + fieldType);
                return super.processTypeConvert(globalConfig, fieldType);
            }
        });
        dsc.setDriverName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dsc.setUsername("sa");
        dsc.setPassword("nieqiuqiu");
        dsc.setUrl("jdbc:sqlserver://192.168.1.105:1433;databaseName=sooner");
        mpg.setDataSource(dsc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        // strategy.setCapitalMode(true);// 全局大写命名
        // strategy.setDbColumnUnderline(true);//全局下划线命名
        strategy.setTablePrefix(new String[]{"bmd_", "mp_"});// 此处可以修改为您的表前缀
        strategy.setNaming(NamingStrategy.underline_to_camel);// 表名生成策略

        mpg.setStrategy(strategy);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName("test");
        pc.setParent("com.sooner");// 自定义包路径
        pc.setController("controller");// 这里是控制器包名，默认 web
        mpg.setPackageInfo(pc);

        // 注入自定义配置，可以在 VM 中使用 cfg.abc 设置的值
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<>();
                map.put("abc", this.getConfig().getGlobalConfig().getAuthor() + "-mp");
                this.setMap(map);
            }
        };
        List<FileOutConfig> focList = new ArrayList<>();
        focList.add(new FileOutConfig("/templates/entity.java" + ((1 == result) ? ".ftl" : ".vm")) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输入文件名称
                return "D://my_" + tableInfo.getEntityName() + StringPool.DOT_JAVA;
            }
        });
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);


        // 执行生成
        if (1 == result) {
            mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        }
        mpg.execute();
        // 打印注入设置
        System.err.println(mpg.getCfg().getMap().get("abc"));
    }

}
