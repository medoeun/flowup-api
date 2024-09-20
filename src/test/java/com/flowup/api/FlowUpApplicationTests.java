package com.flowup.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.flowup.api.security.config.TestSecurityConfig;

@SpringBootTest
@Import(TestSecurityConfig.class)
class FlowUpApplicationTests {

	@Test
	void contextLoads() {
	}

}
