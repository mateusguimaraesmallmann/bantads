package com.bantads.ms_conta.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.*;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(
  basePackages = "com.bantads.ms_conta.repository.query",
  entityManagerFactoryRef = "queryEntityManager",
  transactionManagerRef = "queryTransactionManager")
public class QueryDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.query")
    public DataSourceProperties queryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource queryDataSource() {
        return queryDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "queryEntityManager")
    public LocalContainerEntityManagerFactoryBean queryEntityManager(
            EntityManagerFactoryBuilder builder) {

        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.default_schema", "contaleitura");

        return builder
            .dataSource(queryDataSource())
            .packages("com.bantads.ms_conta.model.entity.query")
            .properties(props)
            .persistenceUnit("query")
            .build();
    }

    @Bean
    public PlatformTransactionManager queryTransactionManager(final @Qualifier("queryEntityManager") LocalContainerEntityManagerFactoryBean emf) {
        return new JpaTransactionManager(emf.getObject());
    }
    
}