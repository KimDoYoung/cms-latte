package kr.dcos.cmslatte.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import kr.dcos.cmslatte.exception.CmsLatteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * code를 테스트하기 위해서
 * xml에 코드와 결과를 넣어둔다.
 * loading해서 테스트할 수 있게 한다.
 * @author Administrator
 *
 */
public class TestWithXml {
	
	private static Logger logger = LoggerFactory.getLogger(TestWithXml.class);
	
	private List<TestItem> list;
	
	public List<TestItem> getList() {
		return list;
	}

	public TestWithXml(){
		list = new ArrayList<TestItem>();
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
	/**
	 * latte테스트를 위한 xml파일을 loading한다
	 * @param file
	 * @throws CmsLatteException 
	 */

	public void load(File file) throws CmsLatteException{
		DocumentBuilderFactory docBuildFact = DocumentBuilderFactory.newInstance();

		DocumentBuilder docBuild;
		try {
			docBuild = docBuildFact.newDocumentBuilder();
			Document doc = docBuild.parse(file);
			doc.getDocumentElement().normalize();
			
			NodeList itemtList = doc.getElementsByTagName("item"); //모든 constant에 대해서
			for (int i = 0; i < itemtList.getLength(); i++) {
				Node childNode = itemtList.item(i);
				if (childNode.getNodeType() != Node.ELEMENT_NODE) continue;
				NamedNodeMap map = childNode.getAttributes();
				Node idNode = map.getNamedItem("title");
				String title = idNode.getNodeValue();
				String code=null;
				String result = null;
				NodeList varList = childNode.getChildNodes(); 
				for(int j=0;j<varList.getLength();j++){  
					Node var = varList.item(j);
					if(var.getNodeType()!=Node.ELEMENT_NODE)continue; 
					if(var.getNodeName().equals("code")){
						//cdata
						Element elm = (Element)var;
						code = getCharacterDataFromElement(elm);
						
					}else if(var.getNodeName().equals("result")){
						//cdata
						Element elm = (Element)var;
						result = getCharacterDataFromElement(elm);
					}
					if(title !=null && code!=null&& result !=null){
						list.add(new TestItem(title,code,result));
					}

				}
			}
			
		} catch (ParserConfigurationException e) {
			throw new CmsLatteException(e.getMessage());
		} catch (SAXException e) {
			throw new CmsLatteException(e.getMessage());
		} catch (IOException e) {
			throw new CmsLatteException(e.getMessage());
		}
	}
	/**
	 * latte테스트를 위한 xml파일을 loading한다
	 * @param path
	 * @throws CmsLatteException 
	 */
	public void load(String xmlPath) throws CmsLatteException{
		load(new File(xmlPath));
	}
}
