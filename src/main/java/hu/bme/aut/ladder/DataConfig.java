package hu.bme.aut.ladder;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

/**
 * Configuration for data repositories
 */
@Configuration
@EnableJpaRepositories(basePackages = { "hu.bme.aut.ladder.data.repository" })
public class DataConfig {

    /**
     * In-memory dataSource
     */
    @Bean(destroyMethod = "close", name = "dataSource")
    public DataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.hsqldb.jdbcDriver");
        ds.setUrl("jdbc:hsqldb:mem:ladder-game");
        ds.setUsername("sa");
        ds.setPassword("");
        return ds;
    }

    /**
     * Entity manager factory
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource());
        emf.setPackagesToScan("hu.bme.aut.ladder.data.entity");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        emf.setJpaVendorAdapter(vendorAdapter);

        final Properties properties = new Properties() {
            {
                setProperty("hibernate.hbm2ddl.auto", "create-drop");
                setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
            }
        };
        emf.setJpaProperties(properties);

        return emf;
    }

    /**
     * Transaction manager
     */
    @Bean(name = "transactionManager")
    public JpaTransactionManager getTransactionManager() throws SQLException {
        JpaTransactionManager manager = new JpaTransactionManager();
        manager.setEntityManagerFactory(this.entityManagerFactory().getObject());
        return manager;
    }
}
