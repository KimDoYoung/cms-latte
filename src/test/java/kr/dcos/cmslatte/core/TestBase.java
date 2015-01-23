package kr.dcos.cmslatte.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TestBase {
	public static final String TemplatePath = "test/";

	/**
	 * resourceName에 해당하는 파일을 읽어서 문자열로 만들어서 리턴한다
	 * resource명을 못 찾았을 경우 null을 리턴한다
	 * @param resourceName
	 * @return
	 * @throws IOException 
	 */
	protected  String readStringFromResource(String resourceName) throws IOException{
		 
		File file = this.getFileFromResource(resourceName);

		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(file));

		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
//			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	 
	}
	/**
	 * resourceName으로 resource에서 파일을 찾아서 File을 리턴한다
	 * 찾지 못했을 경우 null을 리턴한다
	 * @param resourceName
	 * @return
	 */
	protected File getFileFromResource(String resourceName) {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(resourceName).getFile());
		return file;
	}
}
