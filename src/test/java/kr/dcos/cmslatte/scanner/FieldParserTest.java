package kr.dcos.cmslatte.scanner;

import static org.junit.Assert.*;
import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.field.FieldName;
import kr.dcos.cmslatte.scanner.FieldParser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FieldParserTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void test() throws CmsLatteException {
		FieldParser fp = new FieldParser();
		FieldName fieldName = fp.scan("a_k1");
		assertEquals(fieldName.getName(),"k1");
		assertEquals(fieldName.getPrefix(),'a');
		
		fieldName = fp.scan("k1");
		assertEquals(fieldName.getName(),"k1");
		assertEquals(fieldName.getPrefix(),0x00);
		
		fieldName = fp.scan("k");
		assertEquals(fieldName.getName(),"k");
		assertEquals(fieldName.getPrefix(),0x00);
	}
	@Test(expected = CmsLatteException.class)  
	public void test1() throws CmsLatteException {  
		FieldParser fp = new FieldParser();
		fp.scan("a_");	  
	}  
	@Test(expected = CmsLatteException.class)  
	public void testError1() throws CmsLatteException {  
		FieldParser fp = new FieldParser();
		fp.scan("a[ ]");	  
	}  
	@Test(expected = CmsLatteException.class)  
	public void testError2() throws CmsLatteException {  
		FieldParser fp = new FieldParser();
		fp.scan("a[1, ]");	  
	}  
	@Test(expected = CmsLatteException.class)  
	public void testError3() throws CmsLatteException {  
		FieldParser fp = new FieldParser();
		fp.scan("a[, 2+3]");	  
	}  
	@Test(expected = CmsLatteException.class)  
	public void testError4() throws CmsLatteException {  
		FieldParser fp = new FieldParser();
		fp.scan("a[, 2+3].");
		
	}  

	@Test
	public void test2() throws CmsLatteException {  
		FieldParser fp = new FieldParser();
		FieldName fieldName = fp.scan("a [1,2].trim()");
		assertEquals(fieldName.getName(),"a");
		assertEquals(fieldName.getRowSusikString(),"1");
		assertEquals(fieldName.getColSusikString(),"2");
		assertEquals(fieldName.getSubFuncDesc(),"trim()");
		

		fieldName = fp.scan("a [1] . trim()");
		assertEquals(fieldName.getName(),"a");
		assertEquals(fieldName.getRowSusikString(),"1");
		assertEquals(fieldName.getSubFuncDesc(),"trim()");

	}
	@Test
	public void test3() throws CmsLatteException {  
		FieldParser fp = new FieldParser();
		FieldName fieldName = fp.scan("a[1 + 3 , indexOf(\"abcdef\",\"b\")].substring(1,3)");
		assertEquals(fieldName.getName(),"a");
		assertEquals(fieldName.getRowSusikString(),"1 + 3");
		assertEquals(fieldName.getColSusikString(),"indexOf(\"abcdef\",\"b\")");
		assertEquals(fieldName.getSubFuncDesc(),"substring(1,3)");
		
	}
	@Test
	public void test4() throws CmsLatteException {  
		FieldParser fp = new FieldParser();
		FieldName fieldName = fp.scan("abc [ i ]");
		assertEquals(fieldName.getName(),"abc");
		assertEquals(fieldName.getRowSusikString(),"i");
		assertNull(fieldName.getColSusikString());
		assertNull(fieldName.getSubFuncDesc());
		
	}
	@Test
	public void test5() throws CmsLatteException {  
		FieldParser fp = new FieldParser();
		FieldName fieldName = fp.scan("u_abc [ i , j+1 ].substring(1,k)");
		assertEquals(fieldName.getName(),"abc");
		assertEquals(fieldName.getPrefix(),'u');
		assertEquals(fieldName.getRowSusikString(),"i");
		assertEquals(fieldName.getColSusikString(),"j+1");
		assertEquals(fieldName.getSubFuncDesc(),"substring(1,k)");
		
	}
}
