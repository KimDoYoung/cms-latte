package kr.dcos.cmslatte.field;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.utils.LatteUtil;

import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * field창고의 모든 내용을 xml파일에 저장한다
 * @author Administrator
 *
 */
class FieldSaver {

	private FieldChangGo fieldChangGo;
	
	/**
	 * 생성자
	 * @return
	 */
	public FieldSaver(FieldChangGo fieldChangGo) {
		this.fieldChangGo=fieldChangGo;
	}
	public FieldSaver() {
		this(null);
	}
	//getter,setter
	public FieldChangGo getFieldChangGo() {
		return fieldChangGo;
	}

	public void setFieldChangGo(FieldChangGo fieldChangGo) {
		this.fieldChangGo = fieldChangGo;
	}


	public String getXmlString(String encoding) throws CmsLatteException{
		if (fieldChangGo == null){
			return "";
		}
		Document doc = createDom();
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			String output = writer.getBuffer().toString();
			return output;
		} catch (TransformerException e) {
			throw new CmsLatteException(e.getMessage());
		}

	}
	public void saveToXml(String path, String encoding) throws CmsLatteException {
		if(fieldChangGo==null)return ;
		
		Document doc = createDom();
        //실제파일에 write
        writeToXml(doc,path,encoding);
        		
	}
	private Document createDom() throws CmsLatteException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new CmsLatteException(e.getMessage());
		}
		// 루트 엘리먼트
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("Variables");
        doc.appendChild(rootElement);

        addConstantToDom(doc, rootElement);
        addArrayToDom(doc, rootElement);
        addTableToDom(doc,rootElement);
		return doc;
	}
	private void addConstantToDom(Document doc, Element rootElement) {
		//constant field
        Element constantElement = doc.createElement("Constant");
        rootElement.appendChild(constantElement);

        List<Field> constantFieldList = fieldChangGo.getFieldList(FieldType.Constant);
        for (Field field : constantFieldList) {
			ConstantField constantField = (ConstantField)field;

			Element var = doc.createElement("var");
			var.setAttribute("id", constantField.getName());
			String typeString = LatteUtil.typeString(constantField.getValue());
			var.setAttribute("type",typeString);
			CDATASection cdata = doc.createCDATASection(constantField.getValueString());
			var.appendChild(cdata);
			constantElement.appendChild(var);
		}
	}
	private void addArrayToDom(Document doc, Element rootElement)
			throws CmsLatteException {
		//array field
        Element arrayElement = doc.createElement("Array");
        rootElement.appendChild(arrayElement);
        List<Field> arrayFieldList = fieldChangGo.getFieldList(FieldType.Array);
        for (Field field : arrayFieldList) {
			ArrayField arrayField = (ArrayField)field;

			Element var = doc.createElement("var");
			arrayElement.appendChild(var);
			
			var.setAttribute("id", arrayField.getName());
			for(int i=0;i<arrayField.size();i++){
				Object o = arrayField.getValue(i);
				if(o==null)continue; //null은 write하지 않는다
				Element valueElement = doc.createElement("value");
				var.appendChild(valueElement);

				String typeString = LatteUtil.typeString(o);
				valueElement.setAttribute("index",Integer.toString(i));
				valueElement.setAttribute("type",typeString);
				CDATASection cdata = doc.createCDATASection(arrayField.getValueString(i));
				valueElement.appendChild(cdata);				
			}
		}
	}
	private void addTableToDom(Document doc,Element rootElement) throws DOMException, CmsLatteException {
		 //array field
        Element tableElement = doc.createElement("Table");
        rootElement.appendChild(tableElement);
        List<Field> matrixFieldList = fieldChangGo.getFieldList(FieldType.Matrix);
        for (Field field : matrixFieldList) {
			MatrixField matrixField = (MatrixField)field;

			Element var = doc.createElement("var");
			tableElement.appendChild(var);
			
			var.setAttribute("id", matrixField.getName());
			for(int i=0;i<matrixField.getRowCount();i++){
				for(int j=0;j<matrixField.getColumnCount();j++){
					Object o = matrixField.getValue(i,j);
					if(o==null)continue; //null은 write하지 않는다
					Element valueElement = doc.createElement("value");
					var.appendChild(valueElement);
	
					String typeString = LatteUtil.typeString(o);
					valueElement.setAttribute("type",typeString);
					valueElement.setAttribute("row",Integer.toString(i));
					valueElement.setAttribute("col",Integer.toString(j));
					CDATASection cdata = doc.createCDATASection(matrixField.getValueString(i,j));
					valueElement.appendChild(cdata);				
				}
			}
		}
		
	}
	private void writeToXml(Document doc ,String path, String encoding) throws CmsLatteException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        String encode="utf-8";
        if(LatteUtil.isNullOrEmpty(encoding)==false){
        	encode=encoding;
        }
        BufferedWriter bw=null;
		try {
			transformer = transformerFactory.newTransformer();

			transformer.setOutputProperty(OutputKeys.ENCODING, encode);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			DOMSource source = new DOMSource(doc);
			
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);

			transformer.transform(source, result);
			String xmlString = sw.toString();
	

			File file = new File(path);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
                file.createNewFile();
            }
			bw = new BufferedWriter(new OutputStreamWriter(	new FileOutputStream(file)));
			bw.write(xmlString);
//			bw.flush();
//			bw.close();			
			
		} catch (TransformerException e) {
			throw new CmsLatteException(e.getMessage());
		} catch (IOException e) {
			throw new CmsLatteException(e.getMessage());
		}finally{
			if(bw!=null){
				try {
					bw.flush();
					bw.close();
				} catch (IOException e) {
					throw new CmsLatteException(e.getMessage());
				}
				
			}
		}
	}

}
