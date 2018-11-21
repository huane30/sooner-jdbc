package com.sooner.framework.jdbc.test.generator;

import com.sooner.framework.jdbc.annotation.DbType;
import com.sooner.framework.jdbc.generator.AutoGenerator;
import com.sooner.framework.jdbc.generator.config.DataSourceConfig;
import com.sooner.framework.jdbc.generator.config.GlobalConfig;
import com.sooner.framework.jdbc.generator.config.PackageConfig;
import com.sooner.framework.jdbc.generator.config.StrategyConfig;
import com.sooner.framework.jdbc.generator.config.rules.NamingStrategy;

public class DB2Generator {

    public static void main(String[] args) {
        String packageName = "com.crea.gwms";
        boolean serviceNameStartWithI = true;//user -> UserService, 设置成true: user -> IUserService
        generateByTables(serviceNameStartWithI, packageName, "USER");
    }

    public static void generateByTables(boolean serviceNameStartWithI, String packageName, String... tableNames) {
        GlobalConfig config = new GlobalConfig();
        String dbUrl = "jdbc:db2://192.168.0.227:50000/TRADECTR";
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDbType(DbType.DB2);
        dataSourceConfig.setUrl(dbUrl);
        dataSourceConfig.setUsername("tc");
        dataSourceConfig.setPassword("business");
        dataSourceConfig.setDriverName("com.ibm.db2.jcc.DB2Driver");
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig
            .setCapitalMode(true)
            .setEntityLombokModel(false)
            // .setDbColumnUnderline(true) 改为如下 2 个配置
            .setNaming(NamingStrategy.underline_to_camel)
            .setColumnNaming(NamingStrategy.underline_to_camel)
            .entityTableFieldAnnotationEnable(true)//实体属性上添加表字段映射
            .setInclude(tableNames);//修改替换成你需要的表名，多个表名传数组
        config.setActiveRecord(true);
        config.setEnableCache(false);// XML 二级缓存
        config.setAuthor("zhanyao");
        config.setBaseResultMap(true);// XML ResultMap
        config.setBaseColumnList(false);// XML columList
        config.setOutputDir("/Users/zhanyao/Downloads/code");
        config.setFileOverride(true);
        if (!serviceNameStartWithI) {
            config.setServiceName("%sService");
        }
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent(packageName);
        packageConfig.setController("controller");
        packageConfig.setMapper("dao");
        packageConfig.setEntity("bean");
        AutoGenerator autoGenerator =new AutoGenerator();
        autoGenerator.setGlobalConfig(config);
        autoGenerator.setDataSource(dataSourceConfig);
        autoGenerator.setStrategy(strategyConfig);
        autoGenerator.setPackageInfo(packageConfig);
        autoGenerator.execute();
    }

    private void generateByTables(String packageName, String... tableNames) {
        generateByTables(true, packageName, tableNames);
    }

}
