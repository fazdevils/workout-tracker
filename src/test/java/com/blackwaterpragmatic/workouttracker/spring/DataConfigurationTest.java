package com.blackwaterpragmatic.workouttracker.spring;

import static org.junit.Assert.assertEquals;

import com.blackwaterpragmatic.workouttracker.spring.DataConfiguration;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = {DataConfiguration.class})
@Component
public class DataConfigurationTest {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private DataSourceTransactionManager dataSourceTransactionManager;

	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	@Test
	public void should_get_datasource() {
		assertEquals("HikariDataSource", dataSource.getClass().getSimpleName());
	}

	@Test
	public void should_get_datasource_transaction_manager() {
		assertEquals(dataSource, dataSourceTransactionManager.getDataSource());
	}

	@Test
	public void should_get_sql_session_factory() {
		assertEquals(dataSource, sqlSessionFactory.getConfiguration().getEnvironment().getDataSource());
	}

}
