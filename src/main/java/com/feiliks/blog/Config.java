package com.feiliks.blog;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate4.support.OpenSessionInViewFilter;

import javax.servlet.Filter;
import javax.sql.DataSource;


@Configuration
@EntityScan(basePackages = {
        "com.feiliks.blog",
        "com.feiliks.common.entities"})
@ComponentScan(basePackages = {
        "com.feiliks.blog",
        "com.feiliks.controllers"})
@EnableJpaRepositories(basePackages = {
        "com.feiliks.blog",
        "com.feiliks.common.repositories"})
public class Config {

    @Bean
    public DataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost/boot_blog");
        ds.setUsername("dev");
        ds.setPassword("dev123");
        return ds;
    }

    /*
    @Bean
    public Filter openSessionInViewFilter() {
        return new OpenSessionInViewFilter();
    }
    */

}
