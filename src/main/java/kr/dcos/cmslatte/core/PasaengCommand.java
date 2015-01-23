package kr.dcos.cmslatte.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import kr.dcos.cmslatte.scanner.LatteItem;

/**
 * 문장에서 파생된 또는 수식에서 파생된(새로 생긴) 문장을 저장한다
 * 기본적으로 Queue에 LatteItem을 만들어서 넣도록한다.
 * latte가 수행시 PasaengCommand를 살펴보고 있으면 수행한 후에 동작하도록한다.
 * 
 * 추후의 기능향상에 도움이 될 수있지 않을가?
 * @author Administrator
 *
 */
public class PasaengCommand {
	private int storedCodeIndex=-1;
	private Queue<LatteItem> itemQueue=null;
	//
	//constructor
	//
	public PasaengCommand(){
		storedCodeIndex=-1;
		itemQueue = new LinkedList<LatteItem>();
		
	}
	
	public boolean isEmpty(){
		return itemQueue.size()==0;
	}
	public int size(){
		return itemQueue.size();
	}
	private void queue(LatteItem latteItem){
		itemQueue.add(latteItem);
	}
	public void add(int nowCodeIndex,LatteItem latteItem){
		setStoredCodeIndex(nowCodeIndex);
		queue(latteItem);
	}
	public LatteItem deQueue(){
		if(itemQueue.isEmpty()){
			return null;
		}
		return itemQueue.poll();
	}
	public LatteItem peek(){
		if(itemQueue.isEmpty()){
			return null;
		}
		return itemQueue.peek();		
	}
	public int getStoredCodeIndex() {
		return storedCodeIndex;
	}
	public void setStoredCodeIndex(int storedCodeIndex) {
		this.storedCodeIndex = storedCodeIndex;
	}

	public void clear() {
		itemQueue.clear();
		storedCodeIndex = -1;
	}

	/**
	 * 
	 * @param divideStackWithPlusPlusOrMinuMinus
	 */
	public void addList(int codeIndex,List<LatteItem> list) {
		for (LatteItem latteItem : list) {
			add(codeIndex,latteItem);
		}
		
	}

}
