package kr.dcos.cmslatte.core;

import java.util.HashMap;
import java.util.Map;

/**
 * goto label과 codeIndex를 가지고있는다.
 * @author Administrator
 *
 */
public class GotoManager {
	private Map<String,Integer> map;
	public GotoManager(){
		map = new HashMap<String,Integer>();
	}
	public void clear(){
		map.clear();
	}
	public int size(){
		return map.size();
	}
	public void add(String label,int codeIndex){
		map.put(label, codeIndex);
	}
	public int get(String label){
		if(map.containsKey(label)){
			return map.get(label);
		}
		return -1;
	}
}
