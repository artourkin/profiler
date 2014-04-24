package com.ifs.megaprofiler.core;

import static org.junit.Assert.*;
import com.google.caliper.memory.ObjectGraphMeasurer;

import com.ifs.megaprofiler.elements.Filter;
import com.ifs.megaprofiler.elements.FilterCondition;
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
	//	controller.Execute("/home/artur/rnd/data/govdocs_subset", "src/test/resources/");
        //controller.latticeManager.clear();
        // /home/artur/rnd/data/fits/produced
        // /src/test/resources
        // /home/artur/rnd/data/govdocs_subset

       // System.out.print(controller.latticeManager.getLattice());
       // ObjectGraphMeasurer.Footprint measure = ObjectGraphMeasurer.measure(controller.latticeManager.getLattice());
       // System.out.print(measure);
        //assertEquals(1, controller.count);
	}

    @Test
    public void testApplyFilter() {
     //   Filter f=new Filter();
     //   f.addFilterCondition(new FilterCondition("format", "Portable Document Format"));
     //   controller.applyFilter(f);

    }

}



