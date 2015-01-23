package kr.dcos.cmslatte.scanner;

import static org.junit.Assert.*;

import java.util.Date;

import kr.dcos.cmslatte.exception.CmsLatteException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArrayParserTest {
	
	private static Logger logger = LoggerFactory
			.getLogger(ArrayParserTest.class);
	

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws CmsLatteException {
		//{$:1,2,3} {1,"$2","2:3","3,4"}, { 1,2,3}, {$4:}, { $: 1,2,3}
		String s="{$$1,2,3}";
		ArrayParser ap = new ArrayParser();
		ap.scan(s);
		assertEquals(ap.size(),3);
		logger.debug(ap.toString());
		assertTrue(ap.get(0) instanceof String);
	}
	@Test
	public void test1() throws CmsLatteException {
		//{$:1,2,3} {1,"$2","2:3","3,4"}, { 1,2,3}, {$4:}, { $: 1,2,3}
		String s="{ $ $ 1,2,3}";
		ArrayParser ap = new ArrayParser();
		ap.scan(s);
		assertEquals(ap.size(),3);
		logger.debug(ap.toString());
		assertTrue(ap.get(0) instanceof String);
	}
	@Test
	public void test2() throws CmsLatteException {
		//{$:1,2,3} {1,"$2","2:3","3,4"}, { 1,2,3}, {$4:}, { $: 1,2,3}
		String s="{1,2.3,\"  3,4$,:  \" , 2012-11-19 }";
		ArrayParser ap = new ArrayParser();
		ap.scan(s);
		assertEquals(ap.size(),4);
		logger.debug(ap.toString());
		assertTrue(ap.get(0) instanceof Integer);
		assertTrue(ap.get(1) instanceof Double);
		assertTrue(ap.get(2) instanceof String);
		assertTrue(ap.get(3) instanceof Date);
		
		s="{$s$1,2.3,\"  3,4$,:  \" , 2012-11-19 }";
		ap.scan(s);
		assertEquals(ap.size(),4);
		logger.debug(ap.toString());
		assertTrue(ap.get(0) instanceof String);
		assertTrue(ap.get(1) instanceof String);
		assertTrue(ap.get(2) instanceof String);
		assertTrue(ap.get(3) instanceof String);
		
	}
	@Test
	public void test3() throws CmsLatteException {
		String s="{$10$1,2.3,\"  3,4$,: } \" , 2012-11-19 }";
		ArrayParser ap = new ArrayParser();
		ap.scan(s);
		logger.debug(ap.toString());
		assertEquals(ap.size(),10);
		
	}
	@Test
	public void test4() throws CmsLatteException {
		String s="{}";
		ArrayParser ap = new ArrayParser();
		ap.scan(s);
		assertEquals(ap.size(),0);
		
	}
	@Test
	public void test5() throws CmsLatteException {
		String[] ss= new String[]{
				"{1,2,3}",
				"{1, 2, 3}",
				"{1 , 2 , 3}",
				"{ 1 , 2 , 3 }",
				"{\"1\",\"2\",\"3\"}",
				"{\"1\" ,\"2\" ,\"3\"}",
				"{ \"1\", \"2\", \"3\"}",
				"{ \"1\" , \"2\" , \"3\"}",
		};
		ArrayParser ap = new ArrayParser();
		for (String s : ss) {
			ap.scan(s);
			assertEquals(ap.size(),3);
			assertEquals(ap.get(0).toString(), "1");
			assertEquals(ap.get(1).toString(), "2");
			assertEquals(ap.get(2).toString(), "3");
		}
	}
}
