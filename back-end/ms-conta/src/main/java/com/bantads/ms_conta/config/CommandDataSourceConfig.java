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
  basePackages = "com.bantads.ms_conta.repository.command", 
  entityManagerFactoryRef = "commandEntityManager", 
  transactionManagerRef = "commandTransactionManager")
public class CommandDataSourceConfig {

  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.command")
  public DataSourceProperties commandDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  public DataSource commandDataSource() {
    return commandDataSourceProperties()
        .initializeDataSourceBuilder()
        .build();
  }

  @Bean(name = "commandEntityManager")
  public LocalContainerEntityManagerFactoryBean commandEntityManager(EntityManagerFactoryBuilder builder) {

      Map<String, Object> props = new HashMap<>();
      props.put("hibernate.default_schema", "contacomando");

      return builder
        .dataSource(commandDataSource())
        .packages("com.bantads.ms_conta.model.entity.command")
        .properties(props)
        .persistenceUnit("commandPU")
        .build();
  }

  @Bean(name = "commandTransactionManager")
  public PlatformTransactionManager commandTransactionManager(
      @Qualifier("commandEntityManager") LocalContainerEntityManagerFactoryBean commandEntityManager) {
    return new JpaTransactionManager(commandEntityManager.getObject());
  }

}