package kr.dcos.cmslatte.functions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import kr.dcos.cmslatte.annotation.CmsLatteFunction;
import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.exception.CmsLatteFunctionException;
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
 * functionTable을 가지고 있다.
 * execute 함수의 호출에 의해서 지정된 함수를 수행하고 결과값을 Token의 형태로 넘겨준다.
 * 싱글레톤
 * @author Administrator
 *
 */
public class FunctionManager {
	private static Logger logger = LoggerFactory.getLogger(FunctionManager.class);
	private volatile static FunctionManager instance;
	
	private class ExternalItem {
		String jar;
		String className;
		public ExternalItem(String jar,String className){
			this.jar = jar;
			this.className =className;
		}
		public ExternalItem(String className){
			this.jar = null;
			this.className = className;
		}
	}
	private List<ExternalItem> externalList = null;
	
	public static FunctionManager getInstance() {
		if (instance == null) {
			synchronized (FunctionManager.class) {
				if (instance == null) {
					instance = new FunctionManager();
				}
			}
		}
		return instance;
	}
	/**
	 * function을 가지고 있는 테이블
	 */
	private FunctionTable functionTable;
	
	/**
	 * constructor of FunctionExecutor
	 * 1. CmsLatteExternalFunctions.xml을 classpath에서찾는다.
	 * 2. xml을 parsing해서 externList를 채운다.
	 * 3. externaList를 쭉 훑어가면서 loading한다
	 * 4. 이때 jar속의 class인지
	 * 5. 그냥 존재하는 class인지판별한다
	 */
	private FunctionManager() {
		externalList = new ArrayList<ExternalItem>();
		//functionTable have internal functions, and loading
		functionTable = new FunctionTable();
		//load external file
		loadExternalFunctionWithXml();
	}

	private synchronized void loadExternalFunctionWithXml() {
		try{
			//InputStream is = ClassLoader.class.getResourceAsStream("/CmsLatteExternalFunctions.xml");
			InputStream is = this.getClass().getClassLoader().getResourceAsStream("CmsLatteExternalFunctions.xml");
			if(is !=null){
				loadXml(is);
				for (ExternalItem item : externalList) {
					if(item.jar != null){
						File file = new File(item.jar);
						if(file.exists()){
							loadExternalClassInJar(item.jar, item.className);
							logger.info("loaded :" + item.className + " in jar "+item.jar);
						}else{
							logger.error(item.jar + " is not exist, " + item.className + " can not load");
						}
						
					}else{ //class
						loadExternalClass(item.className);
					}
				}				
			}else{
				logger.info("CmsLatteExternalFunctions.xml is not found, no external function");
			}
		}catch(CmsLatteException e){
			logger.error("",e);
		}
	}
	
	private void loadXml(InputStream inputStream) throws CmsLatteException{
		if(externalList == null){
			externalList = new ArrayList<ExternalItem>();
		}
		DocumentBuilderFactory docBuildFact = DocumentBuilderFactory.newInstance();

		DocumentBuilder docBuild;
		try {
			docBuild = docBuildFact.newDocumentBuilder();
			Document doc = docBuild.parse(inputStream);
			doc.getDocumentElement().normalize();
			loadXmlInJar(doc);
			loadXmlClasses(doc);
		} catch (ParserConfigurationException e) {
			throw new CmsLatteException(e.getMessage());
		} catch (SAXException e) {
			throw new CmsLatteException(e.getMessage());
		} catch (IOException e) {
			throw new CmsLatteException(e.getMessage());
		}		
	}
	private void loadXmlClasses(Document doc) {
		NodeList constantList = doc.getElementsByTagName("classes"); // for all in-jar
		for (int i = 0; i < constantList.getLength(); i++) {
			Node childNode = constantList.item(i);
			if (childNode.getNodeType() != Node.ELEMENT_NODE) continue;
			NodeList classList = childNode.getChildNodes(); // jar chidren
			for (int j = 0; j < classList.getLength(); j++) {
				Node classNode = classList.item(j);
				if (classNode.getNodeType() != Node.ELEMENT_NODE)	continue; // 
					NodeList childs = classNode.getChildNodes();
					if(childs != null && childs.getLength()>0){
						String className = childs.item(0).getNodeValue();
						externalList.add(new ExternalItem(className));
					}
			}
		}
		
	}
	private void loadXmlInJar(Document doc) {
		NodeList constantList = doc.getElementsByTagName("in-jar"); // for all in-jar
		for (int i = 0; i < constantList.getLength(); i++) {
			Node childNode = constantList.item(i);
			if (childNode.getNodeType() != Node.ELEMENT_NODE) continue;
			NodeList jarList = childNode.getChildNodes(); // jar chidren
			for (int j = 0; j < jarList.getLength(); j++) {
				Node jar = jarList.item(j);
				if (jar.getNodeType() != Node.ELEMENT_NODE)	continue; // 
				NamedNodeMap map = jar.getAttributes();
				Node fileNode = map.getNamedItem("file");
				String jarFileName = fileNode.getNodeValue();
				
				//NodeList classdNodeList = jar.getChildNodes();
				Element elm = (Element)jar;
				NodeList classdNodeList = elm.getElementsByTagName("class");
				for(int k=0;k<classdNodeList.getLength();k++){
					Node classNode = classdNodeList.item(k);
					if (classNode.getNodeType() != Node.ELEMENT_NODE)	continue; //
					NodeList childs = classNode.getChildNodes();
					if(childs != null && childs.getLength()>0){
						String className = childs.item(0).getNodeValue();
						externalList.add(new ExternalItem(jarFileName,className));
					}
				}
			}
		}
	}
	/**
	 * functionArguments에 기술된 함수명과 인자들을 가지고 함수를 수행한다 그 결과값을 
	 * Token에 담아서 리턴한다
	 * @param funcArgs
	 * @return
	 * @throws CmsLatteFunctionException 
	 */
	public Object execute(FunctionArguments funcArgs) throws CmsLatteFunctionException{
		ILatteFunction handler = functionTable.get(funcArgs.getFunctionName());
		if(handler == null){
			throw new CmsLatteFunctionException(funcArgs.getFunctionName() + " is not defined funtion");
		}
		return handler.invoke(funcArgs.getArgumentArray());
		//return new Token(object);
	}
	/**
	 * name이 이미 정의된 함수명인지 체크한다
	 * @param name
	 * @return
	 */
	public boolean isCmsFunction(String name){
		if(functionTable.get(name) == null){
			return false;
		}
		return true;
	}
	
	/**
	 * 외부jar에 포함되어 있는 CmsLatteFunction annotation이 붙은 함수들을 로딩한다
	 * 인자가 없는 함수일때 주의해야한다. 자체적으로 null값을 갖는 인자1개를 추가한다.
	 * @param jarPath
	 * @param className
	 * @return
	 */
	public boolean loadExternalClassInJar(String jarPath,String className){
		
		URL url;
		try {
			url = new File(jarPath).toURI().toURL();
			URL[] urls = new URL[] { url };
			ClassLoader cl = new URLClassLoader(urls);
			final Class<?> clazz = cl.loadClass(className);
			extractCmsFunctionsFromClass(clazz);
			return true;
		} catch (MalformedURLException e) {
			logger.error("",e);
			return false;
		} catch (ClassNotFoundException e) {
			logger.error("",e);
			return false;
		}
	}
	/**
	 * className으로 클래스를 찾는다.
	 * @param className
	 */
	private void loadExternalClass(String className) {
		Class<?> clazz = null;
		try {
			//clazz =  ClassLoader.getSystemClassLoader().loadClass(className);
			clazz = Class.forName(className);
			if(clazz == null) {
				logger.error(className + " is not found");
				return;
			}
			extractCmsFunctionsFromClass(clazz);
		} catch (ClassNotFoundException e) {
			logger.error(className + " is not found");
		}
		
	}
	private void extractCmsFunctionsFromClass(final Class<?> clazz) {
		Method[] methods = clazz.getMethods();
		for (final Method method : methods) {
			if(method.isAnnotationPresent(CmsLatteFunction.class)){
				CmsLatteFunction funcAnnotation = method.getAnnotation(CmsLatteFunction.class);
				functionTable.addOrReplace(method.getName(), new ILatteFunction() {
					public Object invoke(Object... args) throws CmsLatteFunctionException {
						try {
							Object[] argu ;
							if(args.length == 0){
								argu = new Object[]{null};
							}else{
								argu = new Object[args.length];
								for(int i=0;i<args.length;i++){
									argu[i] = args[i];
								}
							}
							Method methodToExec = clazz.getDeclaredMethod(method.getName(), new Class[]{Object[].class});
							return methodToExec.invoke(clazz,new Object[]{argu});
						} catch (IllegalAccessException e) {
							throw new CmsLatteFunctionException(clazz.getName()+" " + method.getName()+ " error [" +e.getMessage()+"]");
						} catch (NoSuchMethodException e) 
						{
							throw new CmsLatteFunctionException(clazz.getName()+" " + method.getName()+ " error [" +e.getMessage()+"]");
						} catch (IllegalArgumentException e) {
							throw new CmsLatteFunctionException(clazz.getName()+" " + method.getName()+ " error [" +e.getMessage()+"]");
						} catch (InvocationTargetException e) {
							throw new CmsLatteFunctionException(clazz.getName()+" " + method.getName()+ " error [" +e.getMessage()+"]");
						}
					}
				});
				//Annotation의 기술을 적용한다
				//다른이름
				String anotherName = funcAnnotation.anotherName();
				if(LatteUtil.isNullOrEmpty(anotherName)==false){
					functionTable.addAliasFunctionName(method.getName(), anotherName);
				}
				//subfunction적용대상
				String subApply = funcAnnotation.subApply();
				if(LatteUtil.isNullOrEmpty(subApply)==false){
					functionTable.addSubfunc(method.getName(), subApply);
				}
				//backArgu
				String backArg = funcAnnotation.backArg();
				if(LatteUtil.isNullOrEmpty(backArg)==false){
					if(backArg.toLowerCase().equals("true")){
						functionTable.setBackArgumentFunction(method.getName());
					}
				}
			}
		}
	}
	/**
	 * tokenType이 functionName을 subfunction으로 가질 수 있는지 체크한다
	 * @param tokenType
	 * @param functionName
	 * @return
	 */
	public boolean isAllowedSubfunction(TokenType tokenType, String functionName) {
		return functionTable.isAllowSubfunction(tokenType,functionName);
	}

	@Override
	public String toString(){
		return functionTable.toString();
	}
	/**
	 * 자기 자신을 function인자에서 뒤에 놓아야하는지 format이 그러하다.
	 * 혹시 다른 것도 그럴까봐 일단 함수로 빼둠
	 * @param functionName
	 * @return
	 */
	public  boolean isBackArgumentLocation(String functionName) {
		return functionTable.isBackArgumentFunction(functionName);
	}
}
