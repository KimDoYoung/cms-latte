package kr.dcos.cmslatte.scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import kr.dcos.cmslatte.scanner.ScannerBase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ScannerBaseTest {
	class TmpScanner extends ScannerBase {
		public void setSource(String s){
			srcArray = s.toCharArray();
			index = 0;
		}
		public TmpScanner(){
			super();
			
		}

	}
	private static TmpScanner tmpScanner;
	@Before
	public void setUp() throws Exception {
		tmpScanner = new TmpScanner();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testScannerBase() {
		
	}

	@Test
	public void testIsEmpty() {
		assertTrue(tmpScanner.isEmpty(null));
		assertTrue(tmpScanner.isEmpty(""));
		
	}

	@Test
	public void testIsNotEmpty() {
		assertFalse(tmpScanner.isNotEmpty(null));
		assertFalse(tmpScanner.isNotEmpty(""));

	}

	@Test
	public void testIsBufferEmpty() {
		tmpScanner.bufferClear();
		assertTrue(tmpScanner.isBufferEmpty());
		tmpScanner.buffer.append('a');
		assertFalse(tmpScanner.isBufferEmpty());
	}

	@Test
	public void testBufferNotEmpty() {
		tmpScanner.bufferClear();
		assertFalse(tmpScanner.isBufferNotEmpty());
		tmpScanner.buffer.append('a');
		assertTrue(tmpScanner.isBufferNotEmpty());

	}


	@Test
	public void testNotScanLength() {
		tmpScanner.setSource("ABC");
		assertTrue(tmpScanner.notScanLength()==2);
	}

	@Test
	public void testFetchUntil() {
		tmpScanner.setSource("abc/* abc*/B");
		tmpScanner.fillBufferUntil("*/");
		assertEquals(tmpScanner.srcArray[tmpScanner.index],'B');
		tmpScanner.init();
		tmpScanner.setSource("<@s=\"@>\"@>");
		tmpScanner.index =2;
		String s = tmpScanner.fillBufferUntil("@>",true);
		assertEquals(s, "s=\"@>\"");
	}

	@Test
	public void testMatch() {
		tmpScanner.setSource("012345");
		tmpScanner.index = 1;
		assertTrue(tmpScanner.match("123".toCharArray()));
	}

	@Test
	public void testFillBufferUntilPair() {
		tmpScanner.setSource("a[1,A[2]]=b[1,2].trim()");
		tmpScanner.index = 1;
		tmpScanner.fillBufferUntilPair('[', ']');
		assertEquals(tmpScanner.buffer.toString(),"[1,A[2]]");
		assertEquals(tmpScanner.srcArray[tmpScanner.index],'=');
	}

	@Test
	public void testSkipWhitespace() {
		tmpScanner.setSource("a \t \n \r b");
		tmpScanner.index = 1;
		tmpScanner.skipWhitespace();
		assertEquals(tmpScanner.srcArray[tmpScanner.index],'b');
		
	}

	@Test
	public void testCharInString() {
		assertTrue(tmpScanner.charInString('a', "abc"));
		assertFalse(tmpScanner.charInString('1', "abc \tb"));
		assertTrue(tmpScanner.charInString('\t', "abc \tb"));
	}

}
