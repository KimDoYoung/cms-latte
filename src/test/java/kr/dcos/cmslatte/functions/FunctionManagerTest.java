package kr.dcos.cmslatte.functions;

import kr.dcos.cmslatte.exception.CmsLatteFunctionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionManagerTest {
	
	private static Logger logger = LoggerFactory
			.getLogger(FunctionManagerTest.class);
	

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws CmsLatteFunctionException {
		logger.debug(FunctionManager.getInstance().toString());
//		String jarPath = "c:/workspace/CmsLatteExternalLib/target/CmsLatteExternalLib-0.0.1-SNAPSHOT.jar";;
//		String className ="kr.kalpa.co.cmslib.Reservation";
//		
//		
//		FunctionManager.getInstance().loadExternalLib(jarPath, className);
//		
//		FunctionArguments funcArgs = new FunctionArguments("sayNames");
//		//funcArgs.addArgument("1");
//		Object o = FunctionManager.getInstance().execute(funcArgs );
//		logger.debug(o.toString());
		
		FunctionManager.getInstance();
	}

}
