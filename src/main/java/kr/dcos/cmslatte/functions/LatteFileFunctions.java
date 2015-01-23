package kr.dcos.cmslatte.functions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.dcos.cmslatte.exception.CmsLatteFunctionException;

public class LatteFileFunctions extends LatteFunctionBase{

	private static Logger logger = LoggerFactory.getLogger(LatteFileFunctions.class);


	private static String readFileAsString(String filePath)
			throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}
	/**
	 * content를 fileName에 encoding으로 write한다.override 가 true이면 덮어쓰기를
	 * 아니면 그냥 리턴한다 
	 * @param string
	 * @param filePath
	 * @param encoding
	 * @param override
	 * @throws IOException 

	 */
	private static void saveToFile(String content, String filePath,
			String encoding) throws IOException  {
		
		BufferedWriter out;
		out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
				filePath), encoding));
		out.write(content);
		out.close();
	}



	public static void writeToFile(String content, String filePath,
			String encoding, boolean override) throws CmsLatteFunctionException {

		File targetFile = new File(filePath);
		if(override && targetFile.exists()){
			String msg = "save to file "+filePath+" fail, file is already exist";
			logger.error(msg);
			throw new CmsLatteFunctionException("writeToFile",msg);
		}
		
		try {
			saveToFile(content,filePath,encoding);
		} catch (IOException e) {
			logger.error("",e);
			throw new CmsLatteFunctionException("writeToFile", e.getMessage());
		}
	}	
}
