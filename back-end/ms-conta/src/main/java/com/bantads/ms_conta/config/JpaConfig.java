package com.bantads.ms_conta.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;

@Configuration
public class JpaConfig {

    @Bean
    public JpaVendorAdapter jpaVendorAdapter(JpaProperties jpaProperties) {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        
        if (jpaProperties.getDatabasePlatform() != null) {
            adapter.setDatabasePlatform(jpaProperties.getDatabasePlatform());
        }

        adapter.setShowSql(Boolean.TRUE.equals(jpaProperties.isShowSql()));
        adapter.setGenerateDdl(Boolean.TRUE.equals(jpaProperties.isGenerateDdl()));
        
        return adapter;
    }

    @Bean
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder(JpaVendorAdapter jpaVendorAdapter, JpaProperties jpaProperties) {
        return new EntityManagerFactoryBuilder(
            jpaVendorAdapter,
            jpaProperties.getProperties(),
            null
        );
    }

}