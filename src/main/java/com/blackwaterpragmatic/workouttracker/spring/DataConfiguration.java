package com.blackwaterpragmatic.workouttracker.spring;

import com.zaxxer.hikari.HikariDataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
@MapperScan({"com.blackwaterpragmatic.workouttracker.mybatis.mapper"})
public class DataConfiguration {

	@Bean
	public DataSource dataSource() {
		final EmbeddedDatabaseBuilder databaseBuilder =
				new EmbeddedDatabaseBuilder()
						.setType(EmbeddedDatabaseType.HSQL)
						.addScript("com/blackwaterpragmatic/workouttracker/hsql/create-db.sql")
						.addScript("com/blackwaterpragmatic/workouttracker/hsql/insert-data.sql");

		final HikariDataSource dataSource = new HikariDataSource();
		dataSource.setDataSource(databaseBuilder.build());
		return dataSource;
	}

	@Bean
	public DataSourceTransactionManager transactionManager(final DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory(final DataSource dataSource) throws Exception {
		final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
		sessionFactory.setTypeAliasesPackage("com.blackwaterpragmatic.workouttracker.bean");
		return sessionFactory.getObject();
	}
}
