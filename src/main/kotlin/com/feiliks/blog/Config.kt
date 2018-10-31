package com.feiliks.blog

import org.apache.commons.dbcp.BasicDataSource
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import javax.sql.DataSource

@Configuration
@ComponentScan(basePackages = [
    "com.feiliks.blog"
])
@EntityScan(basePackages = [
    "com.feiliks.blog.entities"
])
@EnableJpaRepositories(basePackages = [
    "com.feiliks.blog.repositories"
])
class Config {

    @Bean
    fun dataSource() : DataSource {
        val ds = BasicDataSource()
        ds.driverClassName = "com.mysql.cj.jdbc.Driver"
        ds.url = "jdbc:mysql://localhost/boot_blog"
        ds.username = "dev"
        ds.password = "dev123"
        return ds
    }

}
