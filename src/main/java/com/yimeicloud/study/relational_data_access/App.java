package com.yimeicloud.study.relational_data_access;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

@SpringBootApplication
public class App implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(App.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		logger.info("create table...");

		jdbcTemplate.execute("DROP TABLE customer IF EXISTS");
		jdbcTemplate.execute("CREATE TABLE customer(id SERIAL, first_name VARCHAR(20), last_name VARCHAR(20))");

		logger.info("insert into customer...");
		String insertSql = "insert into customer(first_name, last_name) values(?, ?)";
		jdbcTemplate.update(insertSql, "Jack", "S");
		
		logger.info("query from customer...");
		queryCustomer();
		
		logger.info("update customer...");
		String updateSql = "update customer set last_name=? where id=?";
		jdbcTemplate.update(updateSql, "Jazz", 1);
		
		logger.info("query from customer after update...");
		queryCustomer();
		
		logger.info("delete from customer...");
		String deleteSql = "delete from customer";
		jdbcTemplate.execute(deleteSql);
		
		logger.info("query from customer after delete...");
		queryCustomer();
		Object[] par1 = "John Woo".split(" ");
		Object[] par2 = "Jeff Dean".split(" ");
		Object[] par3 = "Josh Bloch".split(" ");
		logger.info("batch insert...");
		List<Object[]> parameters = Arrays.asList(par1, par2, par3);
		jdbcTemplate.batchUpdate(insertSql, parameters);
		
		logger.info("query from customer after batch insert...");
		queryCustomer();
	}
	
	public void queryCustomer() {

		ResultSetExtractor<List<Customer>> customerResultSetExtractor = new ResultSetExtractor<List<Customer>>() {
			@Override
			public List<Customer> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<Customer> result = new ArrayList<Customer>();
				Customer customer = null;
				while (rs.next()) {
					customer = new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"));
					result.add(customer);
				}
				return result;
			}
		};
		String querySql = "select id, first_name, last_name from customer";
		List<Customer> result = jdbcTemplate.query(querySql, customerResultSetExtractor);
		if(0 == result.size()) {
			logger.info("result is empty");
		}
		for(Customer customer : result) {
			System.out.println(customer);
		}
	}
}
