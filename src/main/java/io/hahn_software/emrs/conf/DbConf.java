package io.hahn_software.emrs.conf;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.hahn_software.emrs.utils.PropertiesLoader;
import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
public class DbConf {


    @Autowired
    private Environment env;
    

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("propertiesLoader1") PropertiesLoader propertiesLoader) {

        Properties properties = propertiesLoader.loadProperties("src/main/resources/application.properties");

        properties.put("hibernate.connection.url", resolveJdbcUrl());
        properties.put("hibernate.connection.password", env.getProperty("DB_PASSWORD" , "incorrect_password")); 
        properties.put("hibernate.connection.username", env.getProperty("DB_USER" , "keycloak_user"));
        properties.put("hibernate.connection.driver_class", "org.postgresql.Driver");

        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();

        /***
         * if we do not set packToScan the hibernate will use dynamic config loading ,
         * and it will , try to load configs from classpath*:META-INF/persistence.xml
         * 
         * so we will get this error 🛑 No persistence units parsed from 
         *                              {classpath*:META-INF/persistence.xml} 🛑
         */

        emf.setPackagesToScan("io.hahn_software.emrs.entities");
        
        emf.setJpaProperties(properties);

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        emf.setJpaVendorAdapter(vendorAdapter);

        return emf;
    }



    @Bean
    public PlatformTransactionManager transcationManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager() ;
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);

        return jpaTransactionManager ;
    }



    private String resolveJdbcUrl() {
        String urlTemplate = env.getProperty("jdbc.url.template");
        return urlTemplate
                .replace("${DB_HOST}", env.getProperty("DB_HOST"))
                .replace("${DB_NAME}", env.getProperty("DB_NAME"))
                .replace("${DB_SCHEMA}", env.getProperty("DB_SCHEMA"));
    }
}
