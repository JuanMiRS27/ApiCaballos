package com.example.apisencilla.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url:}")
    private String datasourceUrl;

    @Value("${spring.datasource.username:root}")
    private String datasourceUsername;

    @Value("${spring.datasource.password:root}")
    private String datasourcePassword;

    @Value("${DB_HOST:localhost}")
    private String dbHost;

    @Value("${DB_PORT:3306}")
    private String dbPort;

    @Value("${DB_NAME:carreras_db}")
    private String dbName;

    @Value("${INSTANCE_CONNECTION_NAME:}")
    private String instanceConnectionName;

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        String jdbcUrl = resolveJdbcUrl();
        dataSource.setDriverClassName(resolveDriverClassName(jdbcUrl));
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(datasourceUsername);
        dataSource.setPassword(datasourcePassword);
        return dataSource;
    }

    private String resolveJdbcUrl() {
        if (StringUtils.hasText(datasourceUrl)) {
            return datasourceUrl;
        }

        if (StringUtils.hasText(instanceConnectionName)) {
            return "jdbc:mysql:///" + dbName
                    + "?cloudSqlInstance=" + instanceConnectionName
                    + "&socketFactory=com.google.cloud.sql.mysql.SocketFactory"
                    + "&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        }

        return "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName
                + "?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    private String resolveDriverClassName(String jdbcUrl) {
        if (jdbcUrl.startsWith("jdbc:h2:")) {
            return "org.h2.Driver";
        }
        return "com.mysql.cj.jdbc.Driver";
    }
}
