package com.feiliks.rms;

import com.feiliks.common.KeyPairProvider;
import com.feiliks.common.TokenAuthFilter;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;

@Configuration
@EntityScan(basePackages = {
        "com.feiliks.rms.entities"
})
@ComponentScan(basePackages = {
        "com.feiliks.rms.controllers",
        "com.feiliks.rms.commandline"
})
@EnableJpaRepositories(basePackages = {
        "com.feiliks.rms.repositories"
})
public class Config {

    @Bean
    public DataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost/boot_rms");
        ds.setUsername("dev");
        ds.setPassword("dev123");
        return ds;
    }

    @Bean
    public KeyPairProvider keyPairProvider() {
        KeyPairProvider p = new KeyPairProvider();
        p.setSshPrivateKey("/home/guzhiji/Projects/keys/id_rsa");
        p.setSshPublicKey("/home/guzhiji/Projects/keys/id_rsa.pub");
        return p;
    }

    @Bean
    public TokenAuthFilter tokenAuthFilter() {
        TokenAuthFilter taf = new TokenAuthFilter();
        taf.setProtectedPaths("/**");
        taf.setOpenPaths("/account/login");
        return taf;
    }

}
