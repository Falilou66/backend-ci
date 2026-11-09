package com.uadb.backend.identity.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class InstitutionDatabaseConfig {

    @Bean
    public JdbcTemplate institutionJdbcTemplate(
            @Value("${institution.datasource.url}") String url,
            @Value("${institution.datasource.username}") String username,
            @Value("${institution.datasource.password}") String password) {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return new JdbcTemplate(dataSource);
    }
}
