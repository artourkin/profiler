package com.ifs.megaprofiler.core;

import static org.junit.Assert.*;
import com.google.caliper.memory.ObjectGraphMeasurer;

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
		controller.Execute("/home/artur/rnd/data/govdocs1", "src/test/resources/");
        ObjectGraphMeasurer.Footprint measure = ObjectGraphMeasurer.measure(controller.lattice);
        System.out.print(measure);
        assertEquals(1, controller.count);
	}

}



