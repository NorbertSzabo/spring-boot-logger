package com.dknv.example.server.main;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.dknv.example.beans.Greeting;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootDknvLoggerApplicationTests {

	@Test
	public void contextLoads() {
		Assert.assertTrue(new Greeting("Hello", "Joe").toString().equalsIgnoreCase("Hello Joe!"));
	}

}
