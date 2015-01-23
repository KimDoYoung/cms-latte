package kr.dcos.cmslatte.functions;

import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.exception.CmsLatteFunctionException;
import kr.dcos.cmslatte.field.ArrayField;
import kr.dcos.cmslatte.field.MatrixField;
import kr.dcos.cmslatte.utils.LatteUtil;

public class LatteMatrixFunctions extends LatteFunctionBase{
	/**
	 * 2개의 ArrayField를 더한다
	 * @param args
	 * @return
	 * @throws CmsLatteException 
	 */
	public static MatrixField matrixPlus(Object... args) throws CmsLatteException{
		String functionName = "matrixPlus";
		String protoType = String.format("array=%s(array1,array2)",functionName);
		
		//
		// argument length check
		//
		if(argsLength(args.length,2)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		String key = args[0].toString()+args[1].toString();
		MatrixField newMatrixField = new MatrixField(LatteUtil.getUniqueKey(key));
		newMatrixField.append(args[0]);
		newMatrixField.append(args[1]);
		
		return newMatrixField;
	}
	/**
	 * lastRowIndex
	 * @param args
	 * @return
	 * @throws CmsLatteException
	 */
	public static Integer lastRowIndex(Object... args) throws CmsLatteFunctionException{
		String functionName = "lastRowIndex";
		String protoType = String.format("integer=%s(matrix)",functionName);
		if(argsLength(args.length,1)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(isMatrix(args[0])==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
		MatrixField mf = (MatrixField) args[0];
		return mf.getLastRowIndex();
	}
	public static Integer lastColIndex(Object... args) throws CmsLatteFunctionException{
		String functionName = "lastRowIndex";
		String protoType = String.format("integer=%s(matrix)",functionName);
		if(argsLength(args.length,1)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(isMatrix(args[0])==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
		MatrixField mf = (MatrixField) args[0];
		return mf.getLastColumnIndex();
	}
	public static Integer rowCount(Object... args) throws CmsLatteFunctionException{
		String functionName = "rowCount";
		String protoType = String.format("integer=%s(matrix)",functionName);
		if(argsLength(args.length,1)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(isMatrix(args[0])==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
		MatrixField mf = (MatrixField) args[0];
		return mf.getRowCount();
	}
	public static Integer colCount(Object... args) throws CmsLatteFunctionException{
		String functionName = "colCount";
		String protoType = String.format("integer=%s(matrix)",functionName);
		if(argsLength(args.length,1)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(isMatrix(args[0])==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
		MatrixField mf = (MatrixField) args[0];
		return mf.getColumnCount();
	}
	/**
	 * row(index)->index에 해당하는 row를 array로 리턴한다
	 * index가 범위를 벗어나면 비어있는 배열을 리턴한다 
	 * @param args
	 * @return
	 * @throws CmsLatteFunctionException
	 */
	public static ArrayField row(Object... args) throws CmsLatteFunctionException{
		String functionName = "row";
		String protoType = String.format("array=%s(table,index)",functionName);
		if(argsLength(args.length,2)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(isMatrix(args[0])==false||isInteger(args[1])==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
		
		MatrixField mf = (MatrixField) args[0];
		Integer index = (Integer)args[1];
		try {
			return mf.getRow((int)index);
		} catch (CmsLatteException e) {
			throw new CmsLatteFunctionException(functionName, protoType, e.getMessage());
		}
	}
	/**
	 * col(table,index)
	 * index에 해당한느 col을 배열로 리턴한다
	 * @param args
	 * @return
	 * @throws CmsLatteFunctionException
	 */
	public static ArrayField col(Object... args) throws CmsLatteFunctionException{
		String functionName = "col";
		String protoType = String.format("array=%s(table,index)",functionName);
		if(argsLength(args.length,2)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(isMatrix(args[0])==false||isInteger(args[1])==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
		
		MatrixField mf = (MatrixField) args[0];
		Integer index = (Integer)args[1];
		try {
			return mf.getColumn((int)index);
		} catch (CmsLatteException e) {
			throw new CmsLatteFunctionException(functionName, protoType, e.getMessage());
		}
	}	
	/**
	 * array를 테이블에 row로 끼워 넣는다. 이때 row와 column의 크기가 격자에 맞게 변화한다
	 * @param args
	 * @return
	 * @throws CmsLatteFunctionException
	 */
	public static MatrixField insertRow(Object... args) throws CmsLatteFunctionException{
		String functionName = "insertRow";
		String protoType = String.format("table=%s(table,index,array)",functionName);
		if(argsLength(args.length,3)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(!isMatrix(args[0])||!isInteger(args[1]) ||!isArray(args[2])){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
		
		MatrixField mf = (MatrixField) args[0];
		Integer index = (Integer)args[1];
		ArrayField af = (ArrayField)args[2];
		try {
			mf.insertRow((int)index,af);
			return mf;
		} catch (CmsLatteException e) {
			throw new CmsLatteFunctionException(functionName, protoType, e.getMessage());
		}
	}
	public static MatrixField insertCol(Object... args) throws CmsLatteFunctionException{
		String functionName = "insertCol";
		String protoType = String.format("table=%s(table,index,array)",functionName);
		if(argsLength(args.length,3)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(!isMatrix(args[0])||!isInteger(args[1]) ||!isArray(args[2])){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
		
		MatrixField mf = (MatrixField) args[0];
		Integer index = (Integer)args[1];
		ArrayField af = (ArrayField)args[2];
		try {
			mf.insertColumn((int)index,af);
			return mf;
		} catch (CmsLatteException e) {
			throw new CmsLatteFunctionException(functionName, protoType, e.getMessage());
		}
	}
	/**
	 * 
	 * @param args
	 * @return
	 * @throws CmsLatteFunctionException
	 */
	public static MatrixField removeRow(Object... args) throws CmsLatteFunctionException{
		String functionName = "removeRow";
		String protoType = String.format("table=%s(table,index)",functionName);
		if(argsLength(args.length,2)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(!isMatrix(args[0])||!isInteger(args[1])){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
		
		MatrixField mf = (MatrixField) args[0];
		Integer index = (Integer)args[1];
		mf.removeRow(index);
		return mf;
	}
	public static MatrixField removeCol(Object... args) throws CmsLatteFunctionException{
		String functionName = "removeCol";
		String protoType = String.format("table=%s(table,index)",functionName);
		if(argsLength(args.length,2)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(!isMatrix(args[0])||!isInteger(args[1])){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
		
		MatrixField mf = (MatrixField) args[0];
		Integer index = (Integer)args[1];
		try {
			mf.removeCol(index);
		} catch (CmsLatteException e) {
			throw new CmsLatteFunctionException(functionName, protoType, e.getMessage());
		}
		return mf;
	}
	public static MatrixField sortMatrix(Object... args) throws CmsLatteFunctionException{
		String functionName = "sortMatrix";
		String protoType = String.format("table=%s(table,index,dir)",functionName);
		if(argsLength(args.length,3)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(args.length == 2 && !isMatrix(args[0])||!isInteger(args[1])){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
		
		MatrixField mf = (MatrixField) args[0];
		Integer index = (Integer)args[1];
		String dir = "DESC";
		if(args.length ==3 ){
			dir = (String)args[2];
		}
		try {
			mf.sort(index, dir);
		} catch (CmsLatteException e) {
			throw new CmsLatteFunctionException(functionName, protoType, e.getMessage());
		}
		return mf;
		
	}
}
