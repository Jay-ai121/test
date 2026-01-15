package com.quzhou.tourism.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * MyBatis 配置类
 * 配置mapper扫描、SqlSessionFactory、事务管理等核心功能
 */
@Configuration
@EnableTransactionManagement // 开启事务管理
@MapperScan(basePackages = "com.quzhou.tourism.mapper") // 扫描mapper接口所在包
public class MybatisConfig {

    /**
     * 配置 SqlSessionFactory
     * @param dataSource 数据源（SpringBoot 自动配置，无需手动创建）
     * @return SqlSessionFactory
     * @throws Exception 异常
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        // 设置数据源
        sqlSessionFactoryBean.setDataSource(dataSource);
        // 配置mapper XML 文件路径
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(
                resolver.getResources("classpath:mapper/*.xml") // 对应 resources/mapper 目录下的XML文件
        );
        // 配置实体类别名扫描包（可选，简化XML中的实体类引用）
        sqlSessionFactoryBean.setTypeAliasesPackage("com.quzhou.tourism.model.entity");

        // 获取 MyBatis 配置对象，设置额外参数
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        // 开启驼峰命名自动转换（如数据库字段 trip_name → 实体类属性 tripName）
        configuration.setMapUnderscoreToCamelCase(true);
        // 开启日志打印（可选，便于调试）
        configuration.setLogImpl(org.apache.ibatis.logging.stdout.StdOutImpl.class);
        // 关闭二级缓存（如需使用可开启，默认关闭）
        configuration.setCacheEnabled(false);
        sqlSessionFactoryBean.setConfiguration(configuration);

        return sqlSessionFactoryBean.getObject();
    }

    /**
     * 配置事务管理器
     * @param dataSource 数据源
     * @return PlatformTransactionManager
     */
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        // 基于数据源的事务管理器
        return new DataSourceTransactionManager(dataSource);
    }
}