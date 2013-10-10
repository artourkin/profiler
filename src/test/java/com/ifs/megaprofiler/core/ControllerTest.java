package com.ifs.megaprofiler.core;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ControllerTest {
	Controller controller;
	@Before
	public void setUp() throws Exception {
		controller=new Controller();
	}

	@Test
	public void testExecute() {
		controller.Execute("src/test/resources/", "src/test/resources/");
		assertEquals(1, controller.count);
	}

}
