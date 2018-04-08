package com.myApplication.myFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.ComponentScan;

/**
 * Hello world!
 *
 */
@ComponentScan
@SpringBootApplication
public class App implements EmbeddedServletContainerCustomizer {
	private static final Logger _logger = LoggerFactory.getLogger(App.class);

	public void customize(ConfigurableEmbeddedServletContainer container) {

		container.setPort(8099);
	}

	public static void main(String[] args) {
		_logger.debug("Hello Formatter!");
		SpringApplication.run(App.class, args);
	}

}
