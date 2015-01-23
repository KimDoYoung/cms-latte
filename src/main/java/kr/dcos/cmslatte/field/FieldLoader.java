package kr.dcos.cmslatte.field;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.token.TokenType;
import kr.dcos.cmslatte.utils.LatteUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * fieldchanggo에서 사용하는 클래스로 
 * xml에 기술된 변수들을 읽어들인다.
 * @author Administrator
 *
 */
public class FieldLoader {
	
	private static Logger logger = LoggerFactory
			.getLogger(FieldLoader.class);
	

	private List<Field> list;
	
	/**
	 * 생성자
	 */
	public FieldLoader(){
		list = new ArrayList<Field>();
	}
	
	private static String getCharacterDataFromElement(Element e) {
        String queryText = null;
        NodeList nl = e.getChildNodes();
        for(int i=0; i<nl.getLength();i++){
            if(nl.item(i).getNodeType() == Node.CDATA_SECTION_NODE){
                queryText = nl.item(i).getNodeValue().trim();
                break;
            }
        }
        return queryText;
    }
	public Iterator<Field> getIterator(){
		return list.iterator();
	}
	public int size(){
		return list.size();
	}

	public void loadFromXml(File file) throws CmsLatteException{
		DocumentBuilderFactory docBuildFact = DocumentBuilderFactory.newInstance();

		DocumentBuilder docBuild;
		try {
			docBuild = docBuildFact.newDocumentBuilder();
			Document doc = docBuild.parse(file);
			doc.getDocumentElement().normalize();
			
			xmlConstantParsing(doc);
			xmlArrayParsing(doc);
			xmlMatrixParsing(doc);
			
		} catch (ParserConfigurationException e) {
			throw new CmsLatteException(e.getMessage());
		} catch (SAXException e) {
			throw new CmsLatteException(e.getMessage());
		} catch (IOException e) {
			throw new CmsLatteException(e.getMessage());
		}
		
	}
	/**
	 * xmlPath로 부터 변수들을 읽어들여서 List에 넣는다.
	 * @param path
	 * @throws CmsLatteException 
	 */
	public void loadFromXml(String xmlPath) throws CmsLatteException {
		File file = new File(xmlPath);
		loadFromXml(file);
	}

	private void xmlMatrixParsing(Document doc) throws CmsLatteException {
		NodeList constantList = doc.getElementsByTagName("Table"); //모든 constant에 대해서
		for (int i = 0; i < constantList.getLength(); i++) {
			Node childNode = constantList.item(i);
			if (childNode.getNodeType() != Node.ELEMENT_NODE) continue;
			NodeList varList = childNode.getChildNodes(); //var chidren
			for(int j=0;j<varList.getLength();j++){  
				Node var = varList.item(j);
				if(var.getNodeType()!=Node.ELEMENT_NODE)continue; //value
				NamedNodeMap map = var.getAttributes();
				Node idNode = map.getNamedItem("id");
				String id = idNode.getNodeValue();
				logger.debug("matrixField id:"+id);
				MatrixField matrixField = createNewMatrixField(id);
				NodeList valueList = var.getChildNodes();
				for(int k=0;k<valueList.getLength();k++){
					Node valueNode = valueList.item(k);
					if(valueNode.getNodeType() != Node.ELEMENT_NODE)continue;
					//attribute row,col,type
					NamedNodeMap mapValue = valueNode.getAttributes();
					Node rowNode = mapValue.getNamedItem("row");
					Node colNode = mapValue.getNamedItem("col");
					Node typeNode = mapValue.getNamedItem("type");
					String rowString = (rowNode==null) ? "0" : rowNode.getNodeValue();
					String colString = (colNode==null) ? "0" : colNode.getNodeValue();
					String type =  (typeNode == null)? "String":typeNode.getNodeValue();
					int row = Integer.parseInt(rowString);
					int col = Integer.parseInt(colString);
					//data
					Element elm = (Element)valueNode;
					String value = getCharacterDataFromElement(elm);
					Object o = getObjectWithType(type,value);
					//create matrix
					logger.debug("matrixField add:"+row+","+col + ":"+value);
					matrixField.appendOrReplace(row,col,o);
					
				}
				list.add(matrixField);
			}
		}
	}


	private void xmlArrayParsing(Document doc) throws CmsLatteException {
		NodeList constantList = doc.getElementsByTagName("Array"); //모든 constant에 대해서
		for (int i = 0; i < constantList.getLength(); i++) {
			Node childNode = constantList.item(i);
			if (childNode.getNodeType() != Node.ELEMENT_NODE) continue;
			NodeList varList = childNode.getChildNodes(); //var chidren
			for(int j=0;j<varList.getLength();j++){  
				Node var = varList.item(j);
				if(var.getNodeType()!=Node.ELEMENT_NODE)continue; //value
				NamedNodeMap map = var.getAttributes();
				Node idNode = map.getNamedItem("id");
				String id = idNode.getNodeValue();
				logger.debug("arrayField id:"+id);
				ArrayField arrayField = createNewArrayField(id);
				NodeList valueList = var.getChildNodes();
				for(int k=0;k<valueList.getLength();k++){
					Node valueNode = valueList.item(k);
					if(valueNode.getNodeType() != Node.ELEMENT_NODE)continue;
					//attribute
					NamedNodeMap mapValue = valueNode.getAttributes();
					Node indexNode = mapValue.getNamedItem("index");
					Node typeNode = mapValue.getNamedItem("type");
					String indexString = (indexNode!=null)?indexNode.getNodeValue() : "0";
					String type =  (typeNode!=null)?typeNode.getNodeValue():"String";
					int index = Integer.parseInt(indexString);
					//data
					Element elm = (Element)valueNode;
					String value = getCharacterDataFromElement(elm);
					Object o = getObjectWithType(type,value);
					//create array field
					logger.debug("arrayField add:"+index+":"+value);
					arrayField.appendOrReplace(index, o);
				}
				list.add(arrayField);
			}
		}

		
	}

	private void xmlConstantParsing(Document doc) {
		NodeList constantList = doc.getElementsByTagName("Constant"); //모든 constant에 대해서
		for (int i = 0; i < constantList.getLength(); i++) {
			Node childNode = constantList.item(i);
			if (childNode.getNodeType() != Node.ELEMENT_NODE) continue;
			NodeList varList = childNode.getChildNodes(); //var chidren
			for(int j=0;j<varList.getLength();j++){  
				Node var = varList.item(j);
				if(var.getNodeType()!=Node.ELEMENT_NODE)continue; //elment
				NamedNodeMap map = var.getAttributes();
				Node idNode = map.getNamedItem("id");
				Node typeNode = map.getNamedItem("type");
				//attribute
				String id = idNode.getNodeValue();
				String type = typeNode.getNodeValue();
				//cdata
				Element elm = (Element)var;
				String value = getCharacterDataFromElement(elm);
				list.add(createNewContantField(id,type,value));
				logger.debug("id:"+id);
				logger.debug("type:"+type);
				logger.debug("value:"+value);
			}
		}
	}

	private MatrixField createNewMatrixField(String name) {
		return new MatrixField(name);
	}
	private ArrayField createNewArrayField(String name) {
		return new ArrayField(name);
	}
	private ConstantField createNewContantField(String id, String type, String value) {
		Object o = getObjectWithType(type, value);
		return new ConstantField(id, o);
	}

	private Object getObjectWithType(String type, String value) {
		Object o = null;
		if(type.equals("String")){
			o = LatteUtil.getObjectWithTokenType(value, TokenType.String);
		}else if(type.equals("Integer")){
			o = LatteUtil.getObjectWithTokenType(value, TokenType.Integer);
		}else if(type.equals("Date")){
			o = LatteUtil.getObjectWithTokenType(value, TokenType.Date);
		}else if(type.equals("Double")){
			o = LatteUtil.getObjectWithTokenType(value, TokenType.Double);
		}else if(type.equals("Boolean")){
			o = LatteUtil.getObjectWithTokenType(value, TokenType.Boolean);
		}else{
			logger.error("unknown field :" + type +",default string type");
			o = LatteUtil.getObjectWithTokenType(value, TokenType.String);
		}
		return o;
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for (Field field : list) {
			sb.append(field.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
