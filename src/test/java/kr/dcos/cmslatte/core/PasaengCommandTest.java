package kr.dcos.cmslatte.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import kr.dcos.cmslatte.scanner.LatteItem;
import kr.dcos.cmslatte.scanner.LatteItem.LatteItemType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PasaengCommandTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		PasaengCommand pasaengCommand =new PasaengCommand();
		assertTrue(pasaengCommand.isEmpty());
		LatteItem item1 = new LatteItem(LatteItemType.JustString, "jiral");
		LatteItem item2 = new LatteItem(LatteItemType.JustString, "jiral");
		LatteItem item3 = new LatteItem(LatteItemType.JustString, "jiral");
		LatteItem item4 = new LatteItem(LatteItemType.JustString, "jiral");
		pasaengCommand.add(0,item1);
		pasaengCommand.add(0,item2);
		pasaengCommand.add(0,item3);
		pasaengCommand.add(0,item4);
		assertEquals(pasaengCommand.size(), 4);
		pasaengCommand.peek();
		assertEquals(pasaengCommand.size(), 4);
		LatteItem item = pasaengCommand.deQueue();
		assertEquals(pasaengCommand.size(), 3);
		item = pasaengCommand.deQueue();
		item = pasaengCommand.deQueue();
		item = pasaengCommand.deQueue();
		assertTrue(pasaengCommand.isEmpty());
	}

}
