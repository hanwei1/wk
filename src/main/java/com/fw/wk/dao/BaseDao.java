package com.fw.wk.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class BaseDao {
	
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	
	protected Integer queryIntBySql(String sql){
		return jdbcTemplate.queryForObject(sql, Integer.class);		
	}
	
	protected Double queryDoubleBySql(String sql){
		return jdbcTemplate.queryForObject(sql, Double.class);
	}
}
