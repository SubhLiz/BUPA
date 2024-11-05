package com.incture.bupa;

import javax.sql.DataSource;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cloud.config.java.ServiceScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
@ServiceScan
public class BupaApplication {
	public static void main(String[] args) {
		SpringApplication.run(BupaApplication.class, args);
	}
	
	@Bean
	public ModelMapper modelMapper() {
	ModelMapper modelMapper = new ModelMapper();
	modelMapper.getConfiguration().setSkipNullEnabled(true);
	return modelMapper;
	}

	@Bean
	public WebClient webClient() {
		return WebClient.create();
	}
	@Bean
	@Primary
	public DataSource dataSource(
	@Value("${sap.url}") final String url,
	@Value("${sap.username}") final String user,
	@Value("${sap.password}") final String password) {
	DataSource dataSource = DataSourceBuilder.create().type(DriverManagerDataSource.class)
	.driverClassName(com.sap.db.jdbc.Driver.class.getName()).url(url).username(user).password(password)
	.build();
	return dataSource;
	}
	
}