package kr.dcos.cmslatte.functions;

import static org.junit.Assert.*;
import kr.dcos.cmslatte.core.CmsLatte;
import kr.dcos.cmslatte.exception.CmsLatteException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PredefinedCommonFunctionsTest {
	
	private CmsLatte cmsLatte ;
	@Before
	public void setUp() throws Exception {
		cmsLatte = new CmsLatte();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLength() throws CmsLatteException {
		String r = cmsLatte.createPage("<@ s=\"123\";echo length(s); @>");
		assertEquals(r,"3");
	}

	@Test
	public void testTypeOf() throws CmsLatteException {
		assertEquals(cmsLatte.createPage("<@ a=1; echo typeOf(a) @>"),"Integer");
		assertEquals(cmsLatte.createPage("<@ a=1.0;echo typeOf(a) @>"),"Double");
		assertEquals(cmsLatte.createPage("<@ a=\"abc\";echo typeOf(a) @>"),"String");
		assertEquals(cmsLatte.createPage("<@ a={};echo typeOf(a) @>"),"Array");
		assertEquals(cmsLatte.createPage("<@ a={{}};echo typeOf(a) @>"),"Table");
	}

	@Test
	public void testToStringObjectArray() throws CmsLatteException {
		assertEquals(cmsLatte.createPage("<@ a=123; echo toString(a); @>"),"123");
		assertEquals(cmsLatte.createPage("<@ a=12.3; echo toString(a); @>"),"12.3");
		assertEquals(cmsLatte.createPage("<@ a=\"123\"; echo toString(a); @>"),"123");
		assertEquals(cmsLatte.createPage("<@ a={1,2,3}; echo toString(a); @>"),"1\n2\n3");
		assertEquals(cmsLatte.createPage("<@ a={{1,2,3},{4,5,6}}; echo toString(a); @>"),"1,2,3\n4,5,6");
	}

	@Test
	public void testToInteger() throws CmsLatteException {
		assertEquals(cmsLatte.createPage("<@ a=123; echo toInteger(a); @>"),"123");
		assertEquals(cmsLatte.createPage("<@ a=12.3; echo toInteger(a); @>"),"12");
		assertEquals(cmsLatte.createPage("<@ a=\"123\"; echo toInteger(a); @>"),"123");
	}

	@Test
	public void testToDouble() throws CmsLatteException {
		assertEquals(cmsLatte.createPage("<@ a=123; echo toDouble(a); @>"),"123.0");
		assertEquals(cmsLatte.createPage("<@ a=12.3; echo toDouble(a); @>"),"12.3");
		//assertEquals(cmsLatte.createPage("<@ a=toDate(\"2012-11-23\"); echo toDouble(a); @>"),"123");
	}

	@Test
	public void testToBoolean() throws CmsLatteException {
		assertEquals(cmsLatte.createPage("<@ a=true; echo toBoolean(a); @>"),"true");
		assertEquals(cmsLatte.createPage("<@ a=\"false\"; echo toBoolean(a); @>"),"false");
	}

	@Test
	public void testToDate() throws CmsLatteException {
		assertEquals(cmsLatte.createPage("<@ d=toDate(\"2012-11-23 00:00:00\"); echo toString(d); @>"),"2012-11-23 00:00:00");
		assertEquals(cmsLatte.createPage("<@ d=toDate(\"2012-11-23 12:30:50\"); dd = toDouble(d); dd2 = toDate(dd); echo dd2; @>"),"2012-11-23 12:30:50");
		assertEquals(cmsLatte.createPage("<@ d=toDate(\"2012-11-23\",\"yyyy-MM-dd\"); dd = toDouble(d); dd2 = toDate(dd); echo toString(dd2,\"yyyyMMdd\"); @>"),"20121123");
	}

	@Test
	public void testToArray() throws CmsLatteException {
		assertEquals(cmsLatte.createPage("<@ a=123; na=toArray(a); echo typeOf(na); echo length(na); @>"),"Array1");
		assertEquals(cmsLatte.createPage("<@ a=12.3; na= toArray(a);echo typeOf(na);echo length(na); @>"),"Array1");
		assertEquals(cmsLatte.createPage("<@ a=\"123\"; na= toArray(a);echo typeOf(na);echo length(na); @>"),"Array1");
		assertEquals(cmsLatte.createPage("<@ a={1,2,3}; na= toArray(a);echo typeOf(na);echo length(na); @>"),"Array3");
		assertEquals(cmsLatte.createPage("<@ t={{1,2,3},{4,5,6}}; na= toArray(t);echo typeOf(na);echo length(na); @>"),"Array6");
	}

	@Test
	public void testToTable() throws CmsLatteException {
		assertEquals(cmsLatte.createPage("<@ a=123; na=toTable(a); echo typeOf(na); echo length(na); @>"),"Table1");
		assertEquals(cmsLatte.createPage("<@ a=12.3; na= toTable(a);echo typeOf(na);echo length(na); @>"),"Table1");
		assertEquals(cmsLatte.createPage("<@ a=\"123\"; na= toTable(a);echo typeOf(na);echo length(na); @>"),"Table1");
		assertEquals(cmsLatte.createPage("<@ a={1,2,3,4,5}; na= toTable(a,2);echo typeOf(na);echo length(na); @>"),"Table6");
		assertEquals(cmsLatte.createPage("<@ t={{1,2,3},{4,5,6}}; na= toTable(t);echo typeOf(na);echo length(na); @>"),"Table6");
		assertEquals(cmsLatte.createPage("<@ t=\"etc02,combo,movieCategoryList\"; na= toTable(t,\",\",\"&\");echo typeOf(na);echo length(na); echo na.lastRowIndex() @>"),"Table30");

	}
	@Test
	public void testFunctionExist() throws CmsLatteException {
		assertEquals(cmsLatte.createPage("<@ name=\"trim\"; echo functionExist(name); @>"),"true");
	}
	@Test
	public void testIfEmpty() throws CmsLatteException {
		assertEquals(cmsLatte.createPage("<@ a=\"\"; echo ifEmpty(a,1); @>"),"1");
	}
}
