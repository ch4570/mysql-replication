package replication.test.config;


import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import replication.test.config.properties.DataSourceProperties;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableTransactionManagement
public class DataSourceConfiguration {

    private final DataSourceProperties properties;

    @Bean
    public DataSource routingDataSource() {
        DataSource masterSource = createDataSource(
          properties.getUsername(),
          properties.getPassword(),
          properties.getJdbcUrl(),
          properties.getDriverClassName()
        );

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("master", masterSource);
        properties.getReplicationSources().forEach((key, value) ->
                    dataSourceMap.put(value.getName(), createDataSource(
                       value.getJdbcUrl(), value.getUsername(), value.getPassword(), value.getDriverClassName()
                    ))
                );

        RoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(masterSource);

        return routingDataSource;
    }


    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean =
                new LocalContainerEntityManagerFactoryBean();

        entityManagerFactoryBean.setDataSource(dataSource());
        entityManagerFactoryBean.setPackagesToScan("replication.test");
        entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter());
        entityManagerFactoryBean.setPersistenceUnitName("entityManager");
        return entityManagerFactoryBean;
    }

    private JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        // DDL 생성 기능을 비활성화
        hibernateJpaVendorAdapter.setGenerateDdl(false);
        // SQL 쿼리를 로깅하지 않도록 설정
        hibernateJpaVendorAdapter.setShowSql(false);
        // SQL 방언을 MySQL 5 Inno DB 방언으로 설정
        hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5InnoDBDialect");
        return hibernateJpaVendorAdapter;
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        return new LazyConnectionDataSourceProxy(routingDataSource());
    }


    private DataSource createDataSource(String username, String password, String jdbcUrl, String driverClassName) {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url(jdbcUrl)
                .driverClassName(driverClassName)
                .username(username)
                .password(password)
                .build();
    }

    @Bean
    public PlatformTransactionManager transactionManager (
            // 이름이 entityManager인 Bean을 주입받는다.
            @Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        // 주입받은 entityManagerFactory의 객체를 설정한다 -> 트랜잭션 매니저가 올바른 엔티티 매니저 팩토리를 사용하여 트랜잭션을 관리할 수 있다.
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return jpaTransactionManager;
    }
}
