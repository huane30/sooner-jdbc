package com.sooner.framework.jdbc.test.generator;

import com.sooner.framework.jdbc.generator.config.DataSourceConfig;
import com.sooner.framework.jdbc.generator.config.GlobalConfig;
import com.sooner.framework.jdbc.generator.config.PackageConfig;
import com.sooner.framework.jdbc.generator.config.StrategyConfig;
import com.sooner.framework.jdbc.generator.config.rules.NamingStrategy;
import org.junit.Test;

import com.sooner.framework.jdbc.annotation.DbType;
import com.sooner.framework.jdbc.annotation.IdType;
import com.sooner.framework.jdbc.generator.AutoGenerator;

/**
 * <p>
 * 代码生成器 示例
 * </p>
 *
 * @author Hoo
 * @since 2017/12/29
 */
public class CodeGeneratorTest {

    // ============================= 数据库配置 ==================================== //
    private final static String MYSQL_URL = "jdbc:mysql://193.112.71.232:3306/sooner";
    private final static String MYSQL_USERNAME = "root";
    private final static String MYSQL_PASSWORD = "@Hoo888888";
    private final static String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    private final static String MYSQL_TABLE_NAME = "event_store";

    // ============================= 包名称  ================================ //
    private final static String PACKAGE_ENTITY = "entity";
    private final static String PACKAGE_CONTROLLER = "controller";
    private final static String PACKAGE_PATCH = "d:\\codeGen";
    private final static String PACKAGE_NAME = "com.sooner.framework";

    private final static String AUTHOR = "Hoo";

    /**
     * 是否强制带上注解
     */
    boolean enableTableFieldAnnotation = false;
    /**
     * 生成的注解带上IdType类型
     */
    IdType tableIdType = null;
    /**
     * 是否去掉生成实体的属性名前缀
     */
    String[] fieldPrefix = null;
    /**
     * 生成的Service 接口类名是否以I开头
     * 默认是以I开头
     * user表 -> IUserService, UserServiceImpl
     */
    boolean serviceClassNameStartWithI = true;

    @Test
    public void generateCode() {
        enableTableFieldAnnotation = false;
        tableIdType = null;
        generateByTables( PACKAGE_NAME, MYSQL_TABLE_NAME);
    }

    private void generateByTables(String packageName, String... tableNames) {

        // 数据库配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDbType(DbType.MYSQL);
        dataSourceConfig.setUrl(MYSQL_URL);
        dataSourceConfig.setUsername(MYSQL_USERNAME);
        dataSourceConfig.setPassword(MYSQL_PASSWORD);
        dataSourceConfig.setDriverName(MYSQL_DRIVER);

        // 策略配置
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setCapitalMode(true);
        strategyConfig.setEntityLombokModel(false);
        strategyConfig.setNaming(NamingStrategy.underline_to_camel);
        strategyConfig.setColumnNaming(NamingStrategy.underline_to_camel);
        strategyConfig.entityTableFieldAnnotationEnable(enableTableFieldAnnotation);
        strategyConfig.setFieldPrefix(fieldPrefix);//test_id -> id, test_type -> type
        strategyConfig.setInclude(tableNames);//修改替换成你需要的表名，多个表名传数组

        // 全局配置
        GlobalConfig config = new GlobalConfig();
        config.setActiveRecord(false);
        config.setIdType(tableIdType);
        config.setAuthor(AUTHOR);
        config.setOutputDir(PACKAGE_PATCH);
        config.setFileOverride(true);

        // 包配置
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent(packageName);
        packageConfig.setController(PACKAGE_CONTROLLER);
        packageConfig.setEntity(PACKAGE_ENTITY);

        // 代码生成
        AutoGenerator autoGenerator =new AutoGenerator();
        autoGenerator.setGlobalConfig(config);
        autoGenerator.setDataSource(dataSourceConfig);
        autoGenerator.setStrategy(strategyConfig);
        autoGenerator.setPackageInfo(packageConfig);
        autoGenerator.execute();
    }
}
