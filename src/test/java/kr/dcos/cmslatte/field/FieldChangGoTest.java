package kr.dcos.cmslatte.field;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import kr.dcos.cmslatte.core.TestBase;
import kr.dcos.cmslatte.exception.CmsLatteException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldChangGoTest extends TestBase {
	
	private static Logger logger = LoggerFactory
			.getLogger(FieldChangGoTest.class);


	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		FieldChangGo cg = new FieldChangGo();
		cg.putField(new ConstantField("a", "123"));
		assertEquals(cg.size(),1);
		
		FieldName fieldName = new FieldName("a");
		logger.debug(fieldName.toString());
		Field f = cg.getField(fieldName);
		assertNotNull(f);
		assertEquals(f.getType(),FieldType.Constant);
		ConstantField cf = (ConstantField)cg.getField(fieldName);
		assertNotNull(cf);
		assertEquals(cf.getValue(), "123");
	}
	
	@Test
	public void testLoadXmml() throws CmsLatteException {
		File file = getFileFromResource(TemplatePath + "var.xml");
		FieldChangGo cg = new FieldChangGo();
		cg.loadVariableFromXml(file);
		assertEquals(cg.size(),6);
		
		//logger.debug(cg.toString());
	}
	@Test
	public void testSaveXmml() throws CmsLatteException, IOException {
		File file = getFileFromResource(TemplatePath + "var.xml");
		FieldChangGo cg = new FieldChangGo();
		cg.loadVariableFromXml(file);
		assertEquals(cg.size(),6);
		String outXml = cg.toString();
		
		File outxmlFile = File.createTempFile("cmsLatte", ".xml");
		cg.saveVariableToXml(outxmlFile,"utf-8",true);
		assertEquals(cg.size(),6);
		//logger.debug(cg.toString());
	}
	public class ABC{
		private String s;
		private Integer i;
		private Date d;
		public String getS() {
			return s;
		}
		public void setS(String s) {
			this.s = s;
		}
		public Integer getI() {
			return i;
		}
		public void setI(Integer i) {
			this.i = i;
		}
		public Date getD() {
			return d;
		}
		public void setD(Date d) {
			this.d = d;
		}
	}
	/**
	 * class로부터 vairable을 만드는 것 테스트
	 * @throws CmsLatteException
	 */
	@Test
	public void testputFieldWithObject() throws CmsLatteException {
		ABC abc = new ABC();
		abc.setS("ABC");
		abc.setI(10);
		abc.setD(new Date());
		FieldChangGo fieldChangGo = new FieldChangGo();
		fieldChangGo.putFieldsWithObject(abc);
		Field f = fieldChangGo.getField("s");
		assertEquals(f.value,"ABC");
		f = fieldChangGo.getField("i");
		assertEquals(f.value,10);
		f = fieldChangGo.getField("d");
		assertEquals(f.type,FieldType.Constant);
		logger.debug(f.value.toString());
	}
}
