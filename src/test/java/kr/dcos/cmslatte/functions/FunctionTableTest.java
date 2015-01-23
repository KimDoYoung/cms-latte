package kr.dcos.cmslatte.functions;

import static org.junit.Assert.*;
import kr.dcos.cmslatte.core.CmsLatte;
import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.exception.CmsLatteFunctionException;
import kr.dcos.cmslatte.token.Token;
import kr.dcos.cmslatte.token.TokenType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FunctionTableTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws CmsLatteFunctionException {
//		IPredefinedFunction h = FunctionTable.getInstance().get("trim");
//		String s = (String)h.invoke(" ABC ");
//		assertEquals(s,"ABC");
	}
	/**
	 * 토큰으로 argument추가
	 * @throws CmsLatteFunctionException
	 */
	@Test
	public void test1() throws CmsLatteFunctionException {
		FunctionArguments functionArguments = new FunctionArguments("trim");
		functionArguments.addArgument(new Token(" ABC "));

		Object o = FunctionManager.getInstance().execute(functionArguments);
		assertEquals((String)o.toString(),"ABC");
		
		FunctionArguments functionArguments2 = new FunctionArguments("leftTrim");
		functionArguments2.addArgument(new Token(" ABC "));
		
		o = FunctionManager.getInstance().execute(functionArguments2);
		assertEquals(o.toString(),"ABC ");
		
		FunctionArguments functionArguments3 = new FunctionArguments("rtrim");
		functionArguments3.addArgument(new Token(" ABC "));	
		o = FunctionManager.getInstance().execute(functionArguments3);
		assertEquals((String)o.toString()," ABC");
		
	}
	
	@Test
	public void test2() throws CmsLatteFunctionException {
		FunctionArguments functionArguments = new FunctionArguments("substring");
		
		functionArguments.addArgument("012345");
		functionArguments.addArgument(1);
		functionArguments.addArgument(3);
		
		Object o = FunctionManager.getInstance().execute(functionArguments);
		assertEquals(o.toString(),"12");

		
	}
	@Test
	public void testSubstr() throws CmsLatteFunctionException {
		FunctionArguments functionArguments = new FunctionArguments("subStr");
		
		functionArguments.addArgument("012345");
		functionArguments.addArgument(1);
		functionArguments.addArgument(3);
		Object t = FunctionManager.getInstance().execute(functionArguments);
		assertEquals(t.toString(),"123");
	}
	@Test
	public void testUpperLowerCase() throws CmsLatteFunctionException {
		FunctionArguments functionArguments = new FunctionArguments("toUpperCase");		
		functionArguments.addArgument("abc");
		Object o = FunctionManager.getInstance().execute(functionArguments);
		assertEquals(o.toString(),"ABC");
		
		FunctionArguments functionArguments2 = new FunctionArguments("toLowerCase");		
		functionArguments2.addArgument("ABC");
		o = FunctionManager.getInstance().execute(functionArguments2);
		assertEquals(o.toString(),"abc");
		
	}
	@Test
	public void testLength() throws CmsLatteFunctionException {
		FunctionArguments functionArguments = new FunctionArguments("length");		
		functionArguments.addArgument("abc");
		Object o = FunctionManager.getInstance().execute(functionArguments);
		assertTrue((Integer)o==3);
	}
	@Test
	public void testPrefix() throws CmsLatteException {
		String[]  source = new String[]{
				"<@ s=\"abc\"; echo u_s;@>",
				"<@ s=\"abc Def\"; echo l_s;@>",
				"<@ s=\"abc\"; echo a_s;@>",
				"<@ s=\"ABcdEfG\"; echo z_s;@>",
				"<@ s=\"ABcdEfG\"; echo c_s;@>",
				"<@ s=\"abc def ghi\"; echo c_s;@>",
				"<@ s=\"abc def ghi\"; echo p_s;@>",
				"<@ s=\"WCM_USER\"; echo p_s;@>",
				"<@ s=\"WCM_USER\"; echo s_s;@>",
				"<@ s=\"WCM_USER\"; echo d_s;@>",
		};
		String[] result = new String[]{
			"ABC",
			"abc def",
			"Abc",
			"aBcdEfG",
			"Abcdefg",
			"Abc Def Ghi",
			"abcDefGhi",
			"wcmUser",
			"'WCM_USER'",
			"\"WCM_USER\"",
		};
		for(int i=0;i<source.length;i++){
			CmsLatte cmsLatte = new CmsLatte();
			String r = cmsLatte.createPage(source[i]);
			assertEquals(r,result[i]);
		}

	}
}
