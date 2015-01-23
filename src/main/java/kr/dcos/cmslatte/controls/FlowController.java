package kr.dcos.cmslatte.controls;

/**
 * if,while,for,switch,foreach 등의 흐름제어를 위한 
 * 정보를 담고 있는 클래스들의 부모클래스
 * 추상클래스임
 * @author Administrator
 *
 */
public abstract class FlowController  {
	
	protected boolean done=false; //control block안의 작업을 모두 수행했을 때 true
	protected int conditionIndex=-1; // condition의 시작되는 index=자기자신의 index

	protected int beginIndex=-1; //시작위치
	protected int endIndex=-1;   //종료위치
	
	public int getConditionIndex() {
		return conditionIndex;
	}
	public void setConditionIndex(int conditionIndex) {
		this.conditionIndex = conditionIndex;
	}
	public int getBeginIndex() {
		return beginIndex;
	}
	public void setBeginIndex(int beginIndex) {
		this.beginIndex = beginIndex;
	}
	public int getEndIndex() {
		return endIndex;
	}
	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}
	public boolean isDone() {
		return done;
	}
	public void setDone(boolean done) {
		this.done = done;
	}
	public boolean isValid(){
		if(beginIndex<0 || endIndex<0){
			return false;
		}
		if(beginIndex>=endIndex){
			return false;
		}
		return true;
	}
	@Override
	public String toString(){
		return String.format("conditionIndex:%d, beginIndex:%d, endIndex:%d, done:%s",
				conditionIndex,beginIndex,endIndex,Boolean.valueOf(done).toString());
	}
	
}
