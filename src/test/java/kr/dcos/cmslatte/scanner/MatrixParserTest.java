package kr.dcos.cmslatte.scanner;

import static org.junit.Assert.*;
import kr.dcos.cmslatte.exception.CmsLatteException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatrixParserTest {
	
	private static Logger logger = LoggerFactory
			.getLogger(MatrixParserTest.class);
	

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws CmsLatteException {
		MatrixParser mp = new MatrixParser();
		String[] ss = new String[] {
				"{{}}",
				"{{ }}",
				"{{ } }",
				"{ { }}",
				"{ { } }"
				
		};
		for (String s : ss) {
			mp.scan(s);
			assertEquals(mp.rowSize(),0);
			assertEquals(mp.colSize(),0);			
			assertEquals(mp.lastColIndex(),-1);			
			assertEquals(mp.lastRowIndex(),-1);			
		}
	}
	@Test
	public void test1() throws CmsLatteException {
		MatrixParser mp = new MatrixParser();
		String[] ss = new String[] {
				//"{$2,3:{}}",
				"{$ 2 ,3 ${ }}",
				"{$ 2, 3 ${ } }",
				"{$ 2, 3$ { }}",
				"{ $ 2, 3${ } }"
				
		};
		for (String s : ss) {
			mp.scan(s);
			logger.debug(s);
			assertEquals(mp.rowSize(),2);
			assertEquals(mp.colSize(),3);			
						
			assertEquals(mp.lastRowIndex(),1);
			assertEquals(mp.lastColIndex(),2);
		}
	}
	@Test
	public void test2() throws CmsLatteException {
		MatrixParser mp = new MatrixParser();
		String[] ss = new String[] {
				"{{1,2,3},{4,5,6}}",
				"{{1,2,3},{4,\"abcd}e{()f\",6}}",
				"{ {1,2,3}, {4,\"abcd}e{()f\",6}}",
				"{ { 1, 2, 3 } , {4,\"ab,cd}e{()f\",6.5 } } ",
				
		};
		for (String s : ss) {
			mp.scan(s);
			logger.debug(s);
			assertEquals(mp.rowSize(),2);
			assertEquals(mp.colSize(),3);			
						
			assertEquals(mp.lastRowIndex(),1);
			assertEquals(mp.lastColIndex(),2);
		}

	}

}
