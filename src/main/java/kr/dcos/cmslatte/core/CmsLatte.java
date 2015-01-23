package kr.dcos.cmslatte.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import kr.dcos.cmslatte.controls.FlowController;
import kr.dcos.cmslatte.controls.ForController;
import kr.dcos.cmslatte.controls.ForeachController;
import kr.dcos.cmslatte.controls.IfController;
import kr.dcos.cmslatte.controls.SwitchController;
import kr.dcos.cmslatte.controls.WhileController;
import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.field.FieldChangGo;
import kr.dcos.cmslatte.functions.FunctionManager;
import kr.dcos.cmslatte.scanner.LatteItem;
import kr.dcos.cmslatte.scanner.LatteItem.LatteItemType;
import kr.dcos.cmslatte.scanner.LatteSplitter;
import kr.dcos.cmslatte.scanner.MunjangPattern;
import kr.dcos.cmslatte.scanner.MunjangScanner;
import kr.dcos.cmslatte.token.Token;
import kr.dcos.cmslatte.token.TokenStack;
import kr.dcos.cmslatte.token.TokenType;
import kr.dcos.cmslatte.utils.LatteUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 외부에서 이 클래스로 Page를 생성한다
 * @author Administrator
 *
 */
public class CmsLatte {
	
	private static Logger logger = LoggerFactory.getLogger(CmsLatte.class);
	

	private FieldChangGo fieldChangGo; //variable collections

	private String source; //page template

	private List<LatteItem> srcList;
	private LatteSplitter latteSplitter; // Separate text and latte code
	private StringBuilder output;
	private CommandSuHangGi commandSuHangGi;
	private PasaengCommand pasaengCommand;
	private GotoManager gotoManager;
	//
	// constructor
	//
	public CmsLatte() throws CmsLatteException{
		this(null);
	}
	public CmsLatte(FieldChangGo fieldChangGo) throws CmsLatteException{		
			this.fieldChangGo = fieldChangGo;
	}
	private void init() throws CmsLatteException{

		if(fieldChangGo == null){
			this.fieldChangGo = new FieldChangGo();
		}
		
		if(output!=null)output=null;
		if(latteSplitter!=null)latteSplitter=null;
		if(commandSuHangGi!=null)commandSuHangGi = null;
		if(pasaengCommand!=null)pasaengCommand=null;
		if(gotoManager !=null)gotoManager=null;

		output = new StringBuilder(1024*5);
		latteSplitter = new LatteSplitter(source);
		//logger.debug(latteSplitter.toString());
		commandSuHangGi = new CommandSuHangGi(this.fieldChangGo);
		pasaengCommand = new PasaengCommand();
		gotoManager = new GotoManager();
		
	}
	/**
	 * 실제로 해석해서 결과 page를 만든다
	 * template가 null이면 기존의 source로 생성한다
	 * @return
	 * @throws CmsLatteException 
	 */
	public String createPage(String template ) throws CmsLatteException{
		if(!LatteUtil.isNullOrEmpty(template)){
			this.source = template;
		}
		//template가 null이면 기존의 source로 생성한다
		if(LatteUtil.isNullOrEmpty(source)){
			logger.warn("CmsLatte can not create page with null or empty string");
			return "";
		}
		init();
		latte();
		return output.toString();
	}
	public String createPage( ) throws CmsLatteException{
		return createPage(null);
	}
	public FieldChangGo getFieldChangGo() {
		return fieldChangGo;
	}
	public void setFieldChangGo(FieldChangGo fieldChangGo) {
		this.fieldChangGo = fieldChangGo;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * questionIf가 허용하는 문장패턴들
	 * @param tmpMp
	 * @return
	 */
	private boolean isQuestionIfAllowMunjangPatterh(MunjangPattern tmpMp) {
		if( 
			tmpMp == MunjangPattern.Echo ||
			tmpMp == MunjangPattern.Assign ||
			tmpMp == MunjangPattern.Continue ||
			tmpMp == MunjangPattern.Break ||
			tmpMp == MunjangPattern.Goto 
			){
			return true;
		}
		return false;
	}
	/**
	 * ++, --를 포함한 스택을 여러개의 LatteItem으로 만든다
	 * a = (i++)*3 +(--j)+1;
	 * @param stack
	 * @return
	 * @throws CmsLatteException 
	 */
	private List<LatteItem> getLatteItemListWithPlusPlusOrMinuMinus(final TokenStack stack) throws CmsLatteException {
		MunjangScanner ms = new MunjangScanner();
		
		List<LatteItem> list = new ArrayList<LatteItem>();
		TokenStack orgStack = new TokenStack();
		
		LatteItem orgLatteItem = new LatteItem(LatteItemType.LatteCode, "", orgStack);
		list.add(orgLatteItem);
		
		TokenType prevTT = TokenType.Unknown;
		TokenType nextTT = TokenType.Unknown;
		TokenType nowTT =  TokenType.Unknown;
		for(int i=0;i<stack.size();i++){
			nowTT = stack.get(i).getTokenType();
			if(i>0) prevTT = stack.get(i-1).getTokenType();
			if(i<stack.size()-1) nextTT = stack.get(i+1).getTokenType();
			
			if(nowTT == TokenType.PlusPlus){
				if(prevTT == TokenType.Field ){ //i++;
					String variableName = stack.get(i-1).getString();
					String s = variableName +"=" +variableName+"+1";  //a=a+1
					list.add(new LatteItem(LatteItemType.LatteCode,"",ms.scan(s))); //본래문장 뒤에
				}else if(nextTT == TokenType.Field){//++i
					String variableName = stack.get(i+1).getString();
					String s = variableName+"=" +variableName+"+1";  //a=a+1
					list.add(0,new LatteItem(LatteItemType.LatteCode,"",ms.scan(s))); //앞에					
				}
			}else if(nowTT==TokenType.MinusMinus){
				if(prevTT == TokenType.Field ){ //i--;
					String variableName = stack.get(i-1).getString();
					String s = variableName+"=" +variableName+"-1";  //a=a+1
					list.add(new LatteItem(LatteItemType.LatteCode,"",ms.scan(s))); //뒤에
				}else if(nextTT == TokenType.Field){//--i
					String variableName = stack.get(i+1).getString();
					String s = variableName+"=" +variableName+"-1";  //a=a+1
					list.add(0,new LatteItem(LatteItemType.LatteCode,"",ms.scan(s))); //앞에					
				}				
			}else{
				orgStack.push(stack.get(i));
			}
		}
		return list;
	}
	/**
	 * srcList를 scan해서 label을 찾아서 gotoManager에 넣는다
	 */
	private void preScan() {
		for ( int i=0;i<srcList.size();i++){
			LatteItem item = srcList.get(i);
			if(item.getType() == LatteItemType.JustString){
				continue;
			}
			if(LatteUtil.getMunjangPattern(item.getTokenStack())==MunjangPattern.Label){
				String labelName = item.getTokenStack().get(0).getString().substring(1);
				gotoManager.add(labelName, i);
			}
		}
	}
	/**
	 * 다음 case문이나 default문을 찾는다
	 * @param codeIndex
	 * @return
	 * @throws CmsLatteException 
	 */
	private int nextCaseOrDefault(int codeIndex) throws CmsLatteException {
		int tmpIndex = codeIndex;
		while (tmpIndex < srcList.size()) {
			if (srcList.get(tmpIndex).getType() != LatteItemType.LatteCode) {
				tmpIndex++;
				continue;
			}
			TokenType tt = srcList.get(tmpIndex).getTokenStack().get(0)
					.getTokenType();
			if (tt == TokenType.Case || tt == TokenType.Default	) {
				return tmpIndex;
			}
			tmpIndex++;
		}
		throw new CmsLatteException("case or default not found");
	}
	private void fillSwitchController(SwitchController switchController,int codeIndex) {
		int tmpIndex = codeIndex;
		int beginCount = 0;
		boolean firstBegin = false;
		TokenStack stack = srcList.get(tmpIndex).getTokenStack();
		switchController.setSwitchSusikStack(stack.subStack(1));
		switchController.setConditionIndex(tmpIndex);
		// begin end index를 구한다.
		while (tmpIndex < srcList.size()) {
			if (srcList.get(tmpIndex).getType() != LatteItemType.LatteCode) {
				tmpIndex++;
				continue;
			}
			TokenType tt = srcList.get(tmpIndex).getTokenStack().get(0)
					.getTokenType();
			if (tt == TokenType.Begin) {
				beginCount++;
				if (beginCount == 1 && firstBegin == false) {
					switchController.setBeginIndex(tmpIndex);
					firstBegin = true;
				}
			} else if (tt == TokenType.End) {
				if (beginCount == 1) {
					if (switchController.getEndIndex() < 0)
						switchController.setEndIndex(tmpIndex);
					break;
				}
				beginCount--;
			}
			tmpIndex++;
		}
		
	}
	/**
	 * foreach controller를 채운다
	 * @param foreachController
	 * @param codeIndex
	 */
	private void fillForeachController(ForeachController foreachController,
			int codeIndex) {
		int tmpIndex = codeIndex;
		int beginCount = 0;
		boolean firstBegin = false;
		TokenStack foreachStack = srcList.get(tmpIndex).getTokenStack();
		//for FieldName or Assign to susik step susik
		//if 그 자체의 index
		foreachController.setConditionIndex(tmpIndex);
		foreachController.setLeftField(foreachStack.get(1).getString());
		foreachController.setRightStack(foreachStack.subStack(3));
		
		
		// begin end index를 구한다.
		while (tmpIndex < srcList.size()) {
			if (srcList.get(tmpIndex).getType() != LatteItemType.LatteCode) {
				tmpIndex++;
				continue;
			}
			TokenType tt = srcList.get(tmpIndex).getTokenStack().get(0)
					.getTokenType();
			if (tt == TokenType.Begin) {
				beginCount++;
				if (beginCount == 1 && firstBegin == false) {
					foreachController.setBeginIndex(tmpIndex);
					firstBegin = true;
				}
			} else if (tt == TokenType.End) {
				if (beginCount == 1) {
					if (foreachController.getEndIndex() < 0)
						foreachController.setEndIndex(tmpIndex);
					break;
				}
				beginCount--;
			}
			tmpIndex++;
		}
	}
	private void fillWhileController(WhileController whileController,
			int index) {
		int tmpIndex = index;
		int beginCount = 0;
		boolean firstBegin = false;
		TokenStack whileStack = srcList.get(tmpIndex).getTokenStack();
		//for FieldName or Assign to susik step susik
		//if 그 자체의 index
		whileController.setConditionStack(whileStack.subStack(1));
		// begin end index를 구한다.
		while (tmpIndex < srcList.size()) {
			if (srcList.get(tmpIndex).getType() != LatteItemType.LatteCode) {
				tmpIndex++;
				continue;
			}
			TokenType tt = srcList.get(tmpIndex).getTokenStack().get(0)
					.getTokenType();
			if (tt == TokenType.Begin) {
				beginCount++;
				if (beginCount == 1 && firstBegin == false) {
					whileController.setBeginIndex(tmpIndex);
					firstBegin = true;
				}
			} else if (tt == TokenType.End) {
				if (beginCount == 1) {
					if (whileController.getEndIndex() < 0)
						whileController.setEndIndex(tmpIndex);
					break;
				}
				beginCount--;
			}
			tmpIndex++;
		}
		
	}
	/**
	 * for문장을 수행하기 위한 begin end를 구한다.
	 * to 인지 down인지 체크한다. 
	 * condition stack을 채운다
	 * to susik과 down susik을
	 * @param forController
	 * @param codeIndex
	 */
	private void fillForController(ForController forController, int index) {
		int tmpIndex = index;
		int beginCount = 0;
		boolean firstBegin = false;
		TokenStack forStack = srcList.get(tmpIndex).getTokenStack();
		//for FieldName or Assign to susik step susik
		//if 그 자체의 index
		forController.setConditionIndex(tmpIndex);
	
		//to 인지 down인지 
		int toOrDownPos = forStack.indexOf(TokenType.To);
		if(toOrDownPos >= 0){
			forController.setToOrDownTokenType(TokenType.To);
		}else {
			forController.setToOrDownTokenType(TokenType.Down);
			toOrDownPos = forStack.indexOf(TokenType.Down);
		}
		//indexField or Assign문를 구한다.
		if(toOrDownPos == 2){ //indexfield
			forController.setIndexField(forStack.get(1).getValue().toString());
		}else{ //대입문
			TokenStack assignStack = forStack.subStack(1,toOrDownPos-1);
			forController.setIndexField(assignStack.get(0).getValue().toString());
			forController.setIndexStack(assignStack);
		}
		//to susik , step susik 을 구한다
		int stepPos = forStack.indexOf(TokenType.Step);
		if(stepPos>=0){
			TokenStack toOrTokenStack = forStack.subStack(toOrDownPos+1,stepPos-1);
			forController.setToOrDownStack(toOrTokenStack);
			forController.setStepStack(forStack.subStack(stepPos+1));
		}else{
			TokenStack toOrTokenStack = forStack.subStack(toOrDownPos+1);
			forController.setToOrDownStack(toOrTokenStack);
		}
		// begin end index를 구한다.
		while(tmpIndex < srcList.size()){
			if(srcList.get(tmpIndex).getType()!=LatteItemType.LatteCode){
				tmpIndex++; continue;
			}
			TokenType tt = srcList.get(tmpIndex).getTokenStack().get(0).getTokenType();
			if(tt==TokenType.Begin){
				beginCount++;
				if(beginCount == 1 && firstBegin==false ){
					forController.setBeginIndex(tmpIndex);
					firstBegin = true;
				}
			}else if( tt == TokenType.End){
				if(beginCount == 1 ){
					if(forController.getEndIndex()<0) forController.setEndIndex(tmpIndex);
					break;
				}
				beginCount--;
			}					
			tmpIndex++;
		}
	}
	/**
	 * if문장을 수행하기 위한 begin elseif else end 를 찾는다
	 * @param ifController
	 */
	private void fillIfController(IfController ifController,int index) {
		int tmpIndex = index;
		int beginCount = 0;
		boolean firstBegin = false;
		//if 그 자체의 index
		ifController.setConditionIndex(tmpIndex);
		//condistion stack
		ifController.setConditionStack(srcList.get(tmpIndex).getTokenStack().subStack(1));
		
		while(tmpIndex < srcList.size()){
			if(srcList.get(tmpIndex).getType()!=LatteItemType.LatteCode){
				tmpIndex++; continue;
			}
			TokenType tt = srcList.get(tmpIndex).getTokenStack().get(0).getTokenType();
			if(tt==TokenType.Begin){
				beginCount++;
				if(beginCount == 1 && firstBegin==false ){
					ifController.setBeginIndex(tmpIndex);
					firstBegin = true;
				}
			}else if( tt == TokenType.End){
				if(beginCount == 1 ){
					if(ifController.getEndIndex()<0) ifController.setEndIndex(tmpIndex);
					TokenType nextToken = getNextTokenType(tmpIndex);
					if(nextToken == TokenType.Else || nextToken == TokenType.Elseif){
						;
					}else{
						ifController.setFinalEndIndex(tmpIndex);
						break;
					}
				}
				beginCount--;
			}					
			tmpIndex++;
		}
		
	}
	/**
	 * nowIndex 다음의 토큰을 구해본다
	 * @param tmpIndex
	 * @return
	 */
	private TokenType getNextTokenType(int nowIndex) {
		int nextIndex = nowIndex+1;
		while(nextIndex < srcList.size()){
			if(srcList.get(nextIndex).getType()!=LatteItemType.LatteCode){
				nextIndex++; continue;
			}
			TokenStack stack = srcList.get(nextIndex).getTokenStack();
			if(stack != null && stack.size()>0){
				return stack.get(0).getTokenType();
			}
		}
		return TokenType.Unknown;
	}
	private void outputClear() {
		output.setLength(0);		
	}
	private void writeOutput(String string) {
		output.append(string);
		
	}
	//
	// public
	//
	//
	// private
	//
	/**
	 * 1.이미 latteSplitter에는 모두 들어있다.
	 * @throws CmsLatteException 
	 */
	private void latte() throws CmsLatteException{
		
		outputClear();
		pasaengCommand.clear();
		
		Stack<FlowController> controllerStack = new Stack<FlowController>();
		srcList = latteSplitter.getList();
		
		//label을 찾아서 gotoManager에 넣는다.
		preScan();
				
		int codeIndex = 0; 	
		while(codeIndex < srcList.size()){
			LatteItem item=null;
			//파생command(동작 중에 만들어진 명령문장들)가 있다면 그걸 수행한다
			if (!pasaengCommand.isEmpty()){
				item = pasaengCommand.deQueue();
				codeIndex = pasaengCommand.getStoredCodeIndex();
			}else{
				item = srcList.get(codeIndex);
			}
			
			//단지 텍스트는 그냥 추가한다
			if(item.getType()==LatteItemType.JustString){
				output.append(item.getContent());
				codeIndex++;
				continue;
			}
			TokenStack stack = item.getTokenStack();
			if(stack.indexOf(TokenType.PlusPlus)>=0 || stack.indexOf(TokenType.MinusMinus)>=0){
				pasaengCommand.addList(codeIndex,getLatteItemListWithPlusPlusOrMinuMinus(stack));
				continue;
			}
			MunjangPattern pattern = LatteUtil.getMunjangPattern(stack);
			switch (pattern) {
			case Default:
			case Begin:
			case Label:
				break;
			case Assign	:
					commandSuHangGi.doAssign(stack);
				break;
			case Echo:
					writeOutput(commandSuHangGi.echo(stack));
				break;
			case EchoLn:
					writeOutput(commandSuHangGi.echo(stack)+"\n");
					break;
			case EchoNo:
					//수행하고 output으로 보내지는 않는다
					commandSuHangGi.echo(stack); 
					break;
			case Else:
					stack.push(new Token(true, TokenType.Boolean));
			case If:
			case ElseIf:
				{ 
					IfController ifController = new IfController();
					fillIfController(ifController,codeIndex);
					//logger.debug(ifController.toString());
					if(ifController.isValid()==false){
						throw new CmsLatteException("if statement fail");
					}
					controllerStack.push(ifController);
					if(commandSuHangGi.isLogicalTrue(ifController.getConditionStack())){
						ifController.setDone(true);
						codeIndex = ifController.getBeginIndex();
					}else{
						codeIndex = ifController.getEndIndex();
					}
					codeIndex--; //주의 밑에서 ++ 하기 때문에
				}
				break;
				
			case For:{
					ForController forController  = new ForController();
					fillForController(forController,codeIndex);
					//logger.debug(forController.toString());
					if(forController.isValid()==false){
						throw new CmsLatteException("for statement fail");
					}
					//assign이라면 수행한다.
					if(forController.getIndexStack()!=null){
						commandSuHangGi.doAssign(forController.getIndexStack());
					}
					controllerStack.push(forController);
					//바로 조건에 안 맞는다.
					if(commandSuHangGi.forConditionFinishedPre(forController)){
						codeIndex = forController.getEndIndex();
						codeIndex--;
					}
				}
				break;
			case While:{
					WhileController whileController = new WhileController();
					fillWhileController(whileController,codeIndex);
					//logger.debug(whileController.toString());
					if(whileController.isValid()==false){
						throw new CmsLatteException("while statement fail");
					}
					controllerStack.push(whileController);
					//바로 조건에 안 맞는다.
					if(commandSuHangGi.isLogicalTrue(whileController.getConditionStack())){
						codeIndex = whileController.getEndIndex();
						codeIndex--;
					}
				}
				break;
			case Foreach:{
					ForeachController foreachController = new ForeachController();
					fillForeachController(foreachController,codeIndex);
					//logger.debug(foreachController.toString());
					
					if(foreachController.isValid()==false){
						throw new CmsLatteException("foreach statement fail");
					}
					controllerStack.push(foreachController);
					commandSuHangGi.foreachAction(foreachController); //left에 right의 값 index에 해당하는것을 assign
					if(foreachController.isDone()){
						codeIndex = foreachController.getEndIndex();
					}
					
				}
				break;
			case Switch:{
					SwitchController switchController = new SwitchController();
					fillSwitchController(switchController,codeIndex);
					if(switchController.isValid()==false){
						throw new CmsLatteException("switch statement fail");
					}
					controllerStack.push(switchController);
				}
				break;
			case Case:{
					if(stack.size() != 2 && stack.get(1).getTokenType() != TokenType.Field){
						throw new CmsLatteException("case only use Field");
					}
					if(!controllerStack.isEmpty()){
						if(controllerStack.peek() instanceof SwitchController == false){
							throw new CmsLatteException("case statement failed, no switch statement");
						}
						SwitchController switchController = (SwitchController) controllerStack.peek();
						commandSuHangGi.switchCaseAction(switchController,stack);
						if(switchController.isDone()){
							;//codeIndex = switchController.getEndIndex();
						}else{
							codeIndex = nextCaseOrDefault(codeIndex+1); //다음case 또는 break를 찾는다.
							codeIndex--;
						}
						
					}
				}
				break;
			case Break:{
					if(controllerStack.peek() instanceof SwitchController){
						codeIndex = controllerStack.peek().getEndIndex();
					}else{
						while(controllerStack.isEmpty()==false){
							FlowController cb = controllerStack.peek();
							if(cb instanceof ForController   || 
							   cb instanceof WhileController ||
							   cb instanceof ForeachController   ) break;
							else {
								controllerStack.pop();
							}
						}
						if(controllerStack.isEmpty()){
							throw new CmsLatteException("break statement fail");
						}
						FlowController cb = controllerStack.peek();
						cb.setDone(true);
						codeIndex = cb.getEndIndex();
						codeIndex--;
					}
				}
				break;
			case Continue:{
				while(controllerStack.isEmpty()==false){
					FlowController cb = controllerStack.peek();
					if(cb instanceof ForController   || 
					   cb instanceof WhileController ||
					   cb instanceof ForeachController ) break;
					else {
						controllerStack.pop();
					}
				}
				if(controllerStack.isEmpty()){
					throw new CmsLatteException("continue statement fail");
				}
				FlowController cb = controllerStack.peek();
				codeIndex = cb.getEndIndex();
				codeIndex--;
			}
				break;		
			case End:{
					if(controllerStack.size()>0){
						FlowController c = controllerStack.peek();
						if(c instanceof IfController){
							IfController ifController = (IfController)controllerStack.pop();
							if(ifController.isDone()){
								codeIndex = ifController.getFinalEndIndex();
							}
						}else if(c instanceof ForController){
							ForController forController = (ForController)controllerStack.pop();
							if(forController.isDone()){ //break 문에 의해서;
								break;
							}
							if(commandSuHangGi.forConditionFinished(forController)){
								break;
							}else{
								//값을 증가시킨다.
								commandSuHangGi.forSetNewIndexValue(forController);
								codeIndex = forController.getBeginIndex();
								codeIndex--;
								controllerStack.push(forController);
							}
						}else if(c instanceof WhileController){
							WhileController whileController = (WhileController)controllerStack.pop();
							if(whileController.isDone()){ //break;
								break;
							}
							if(commandSuHangGi.isLogicalTrue(whileController.getConditionStack())){
								codeIndex = whileController.getBeginIndex();
								codeIndex--;
								controllerStack.push(whileController);
							}else{
								break;
							}
							
						}else if(c instanceof ForeachController){
							ForeachController foreachController = (ForeachController)controllerStack.pop();
							if(foreachController.isDone()){ //처음부터 조건이 안 맞는다면
								break;
							}else{
								commandSuHangGi.foreachAction(foreachController); //left에 right의 값 index에 해당하는것을 assign
								if(foreachController.isDone()){
									codeIndex = foreachController.getEndIndex();
								}else{
									codeIndex = foreachController.getBeginIndex();
								}
								controllerStack.push(foreachController);
							}
						}else if(c instanceof SwitchController){
							controllerStack.pop();
						}
					}
				}
				break;
			case QuestionIf: {
				int posQuestion = stack.indexOf(TokenType.Question);
				int posColon = stack.indexOf(TokenType.Colon);
				if(posQuestion>=0 && posColon>=0 && posColon>posQuestion){
					TokenStack condition = stack.subStack(0,posQuestion-1);
					TokenStack pasaengStack = null;
					if(commandSuHangGi.isLogicalTrue(condition)){
						pasaengStack = stack.subStack(posQuestion+1,posColon-1);
					}else {
						pasaengStack = stack.subStack(posColon+1);
					}
					if(pasaengStack.isEmpty()==false){
						MunjangPattern tmpMp = LatteUtil.getMunjangPattern(pasaengStack);
						if(!isQuestionIfAllowMunjangPatterh(tmpMp)){
							throw new CmsLatteException("questionif  allow echo,assign,continue,break,goto"); 
						}
						LatteItem newLatteItem = new LatteItem(LatteItemType.LatteCode, "", pasaengStack);
						pasaengCommand.add(codeIndex,newLatteItem);
					}
				}else{
					throw new CmsLatteException("questionif synatax error");
				}
			}
				break;
			case Goto:{
					String labelName = stack.get(1).getString();
					int gotoIndex = gotoManager.get(labelName);
					if(gotoIndex<0){
						throw new CmsLatteException("label " +labelName+" is not exist");
					}
					codeIndex = gotoIndex; codeIndex--;
				}
				break;
			case Exit:
					codeIndex = srcList.size();
					break;
			case Clear:{
					commandSuHangGi.clearAction(stack,output);
					}
					break;
			case Save:{
					commandSuHangGi.saveToAction(stack,output);
				}
				break;
			case JustField:
			case Susik:{
					TokenStack  tmpTokenStack = new TokenStack();
					tmpTokenStack.push(new Token(TokenType.Echo));
					Iterator<Token> it = stack.getIterator();
					while(it.hasNext()){
						tmpTokenStack.push(it.next());
					}
					pasaengCommand.add(codeIndex,new LatteItem(LatteItemType.LatteCode,null,tmpTokenStack));
					codeIndex--;
				}
					break;
			case Load:
					//load variables from "1.txt"
					commandSuHangGi.loadVariables(stack);
					break;
			default:
					logger.error("munjang patterh:" + pattern.toString()+" not defined");
					logger.error("tokenStack:" + stack.toString());
				break;
			}
			codeIndex++;
		}
	}

	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("source:" + source);
		if(fieldChangGo!=null){
			sb.append(fieldChangGo.toString());
		}else{
			sb.append("fieldChangGo:null");
		}
		sb.append("\nfunctionManager\n");
		sb.append(FunctionManager.getInstance().toString());
		return sb.toString();
	}
}
