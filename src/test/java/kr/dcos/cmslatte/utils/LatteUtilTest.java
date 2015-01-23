package kr.dcos.cmslatte.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.field.ArrayField;
import kr.dcos.cmslatte.field.MatrixField;
import kr.dcos.cmslatte.scanner.MunjangPattern;
import kr.dcos.cmslatte.scanner.MunjangScanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LatteUtilTest {
	
	private static Logger logger = LoggerFactory.getLogger(LatteUtilTest.class);
	

	private static MunjangScanner ms;
	@Before
	public void setUp() throws Exception {
		ms = new MunjangScanner();	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMunjangPattern() throws CmsLatteException {
		assertEquals(LatteUtil.getMunjangPattern(ms.scan("a= 10")),MunjangPattern.Assign);
		assertEquals(LatteUtil.getMunjangPattern(ms.scan("echoln a")),MunjangPattern. EchoLn);
	}
	//value.toString().replaceAll("\\\"", "\"")
	@Test
	public void test1() throws CmsLatteException {
		String s = "abc\"def";
		String t = s.toString().replaceAll("\\\"", "\"");
		assertEquals(t,"abc\"def");
	}	
	public class ABC1{
		private String str1;
		private Integer int1;
		private Date date1;
		public String getStr1() {
			return str1;
		}
		public void setStr1(String str1) {
			this.str1 = str1;
		}
		public Integer getInt1() {
			return int1;
		}
		public void setInt1(Integer int1) {
			this.int1 = int1;
		}
		public Date getDate1() {
			return date1;
		}
		public void setDate1(Date date1) {
			this.date1 = date1;
		}
	}
	@Test
	public void testGetArrayFieldWithObject() throws CmsLatteException {
		ABC1 abc1 = new ABC1();
		abc1.setStr1("ABC");
		abc1.setInt1(11);
		abc1.setDate1(new Date());
		ArrayField af = LatteUtil.getArrayFieldWithObject("a1", abc1);
		assertNotNull(af);
		assertEquals(af.getName(),"a1");
		assertEquals(af.getValue(0),"ABC");
		assertEquals(af.getValue(1),"11");
		
	}
	@Test
	public void testGetMatrixFieldWithList() throws CmsLatteException {
		List<ABC1> list = new ArrayList<ABC1>();
		ABC1 abc1 = new ABC1();
		abc1.setStr1("ABC");
		abc1.setInt1(11);
		abc1.setDate1(new Date());
		
		ABC1 abc2 = new ABC1();
		abc2.setStr1("DEF");
		abc2.setInt1(12);
		abc2.setDate1(new Date());
		
		list.add(abc1);
		list.add(abc2);
		
		MatrixField mf = LatteUtil.getMatrixFieldWithList("m1", list);
		assertNotNull(mf);
		assertEquals(mf.getColumnCount(),3);
		assertEquals(mf.getRowCount(),2);
		assertEquals(mf.getValueString(0, 0),"ABC");
	}

	
}
