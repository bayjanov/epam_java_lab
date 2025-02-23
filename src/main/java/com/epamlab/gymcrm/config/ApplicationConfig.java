package com.epamlab.gymcrm.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = "com.epamlab.gymcrm")
@PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
public class ApplicationConfig {
}