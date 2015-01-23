package kr.dcos.cmslatte.core;

import static org.junit.Assert.assertEquals;

import java.io.File;

import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.utils.TestItem;
import kr.dcos.cmslatte.utils.TestWithXml;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchTestWithXml  extends TestBase {
	
	private static Logger logger = LoggerFactory.getLogger(BatchTestWithXml.class);

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	@Test
	public void testWithXml() throws CmsLatteException {
		String[] xmls = { 
				"testcodeCommonFunctions1.xml",
				"testcodeStringFunctions1.xml",
				"testcodeArrayFunctions1.xml",
				"testcodeMatrixFunctions1.xml",
				"testcodeDateFunctions1.xml",
				"testcodeControls1.xml",
				"testcodeMisc.xml"
		};
		for (String xml : xmls) {
			TestWithXml testXml = new TestWithXml();
			File file =  getFileFromResource(TemplatePath + xml);
			testXml.load(file);
			for (TestItem item : testXml.getList()) {
				CmsLatte cmsLatte = new CmsLatte();
				String output = cmsLatte.createPage(item.getCode().trim());
				logger.debug("\n"+item.toString());
				assertEquals(item.getTitle()+":",output,item.getResult().trim());
			}
			
		}
	}
}
