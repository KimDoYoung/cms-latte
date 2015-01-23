package kr.dcos.cmslatte.functions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.exception.CmsLatteFunctionException;
import kr.dcos.cmslatte.field.ArrayField;
import kr.dcos.cmslatte.field.MatrixField;
import kr.dcos.cmslatte.utils.LatteUtil;

/**
 * 일반적인 함수들
 * @author Administrator
 *
 */
public class LatteCommonFunctions extends LatteFunctionBase {
	
//	private static Logger logger = LoggerFactory
//			.getLogger(LatteCommonFunctions.class);
	
	/**
	 * 
	 * @param args
	 * @return
	 * @throws CmsLatteFunctionException
	 */
	public static Integer length(Object... args) throws CmsLatteFunctionException{
		String functionName = "length";
		String protoType = "integer=length(string|array|table)";
		
		// argument length check
		if(argsLength(args.length,1)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		// argument type check
		if(isString(args[0])){
			String s = args[0].toString();
			return s.length();
		}else if(isArray(args[0])){
			ArrayField a = (ArrayField)args[0];
			return a.size();
		}else if(isMatrix(args[0])){
			MatrixField a = (MatrixField)args[0];
			return a.size();			
		}else{
			return args[0].toString().length();
		}
	}
	public static String typeOf(Object... args) throws CmsLatteFunctionException{
		String functionName = "typeOf";
		String protoType = String.format("string=%s(anyType)",functionName);

		// argument length check
		if(argsLength(args.length,1)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		String t= LatteUtil.typeString(args[0]);
		if(t==null){
			throw new CmsLatteFunctionException(functionName, protoType, args[0].toString() + " is unknown type");
		}
		return t;
	}
	public static String toString(Object... args) throws CmsLatteFunctionException{
		String functionName = "toString";
		String protoType = String.format("string=%s(anyType)",functionName);

		// argument length check
		if(argsLength(args.length,1,2)==false){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		try {
			if (args[0] instanceof ArrayField) {
				ArrayField af = (ArrayField) args[0];
				return af.joinWith("\n");
			} else if (args[0] instanceof MatrixField) {
				MatrixField mf = (MatrixField) args[0];
				return mf.joinWith("\n", ",");
			} else if(isDate(args[0])){
				DateFormat sdFormat;
				if(args.length == 2){
					sdFormat = new SimpleDateFormat(args[1].toString());
				}else{
					sdFormat = LatteUtil.longSimpleDateFormat;
				}
				return sdFormat.format((Date)args[0]);
			} else {
				return (args[0]).toString();
			}
		} catch (CmsLatteException e) {
			throw new CmsLatteFunctionException(functionName, protoType,
					e.getMessage());
		}
	}
	public static Integer toInteger(Object... args) throws CmsLatteFunctionException{
		String functionName = "toInteger";
		String protoType = String.format("integer=%s(numberString|Integer|Double)",
				functionName);
		// argument length check
		if (argsLength(args.length, 1) == false) {
			throw new CmsLatteFunctionException(functionName, protoType,
					ERROR_ARGS_LENGTH);
		}
		if (isString(args[0])) {
			return Integer.parseInt(args[0].toString());
		}else if(isDouble(args[0])){
			Double d = new Double( (Double) args[0]);
			return d.intValue();
		}else if(isInteger(args[0])){
			
			Double d = new Double( (Integer) args[0]);
			return d.intValue();			
		}else{
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
	}
	public static Double toDouble(Object... args) throws CmsLatteFunctionException{
		String functionName = "toDouble";
		String protoType = String.format("double=%s(numberString|Integer|Double|Date)",ERROR_ARGS_LENGTH);
				
		// argument length check
		if (argsLength(args.length, 1) == false) {
			throw new CmsLatteFunctionException(functionName, protoType,
					ERROR_ARGS_LENGTH);
		}
		if(isString(args[0])){
			return Double.parseDouble(args[0].toString());
		}else if(isDouble(args[0])){
			Double d = new Double((Double) args[0]);
			return d.doubleValue();
		}else if(isInteger(args[0])){
			Double d = new Double((Integer) args[0]);
			return d.doubleValue();
		}else if(isDate(args[0])){
			long t =  ((Date)args[0]).getTime();
			Long lo = new Long(t);
			return lo.doubleValue();
		}else{
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}

	}
	public static Boolean toBoolean(Object... args) throws CmsLatteFunctionException{
		String functionName = "toBoolean";
		String protoType = String.format("boolean=%s(string|boolean)",functionName);
		// argument length check
		if (argsLength(args.length, 1) == false) {
			throw new CmsLatteFunctionException(functionName, protoType,
					ERROR_ARGS_LENGTH);
		}
		if(isString(args[0])||isBoolean(args[0])){
			return Boolean.valueOf(args[0].toString());
		}else {
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
		
	}
	public static Date toDate(Object... args) throws CmsLatteFunctionException{
		String functionName = "toDate";
		String protoType = String.format("date=%s(string|Double)",functionName);
		// argument length check
		if (argsLength(args.length, 1,2) == false) {
			throw new CmsLatteFunctionException(functionName, protoType,
					ERROR_ARGS_LENGTH);
		}
		if(isString(args[0])){
			java.util.Date date = null;
			SimpleDateFormat format;
			if(args.length == 2){
				format = new SimpleDateFormat(args[1].toString());
			}else {
				String dateString = (String)args[0];
				format = LatteUtil.dateFormat(dateString);
				if(format == null){
					throw new  CmsLatteFunctionException(functionName,protoType,ERROR_INVALID_DATEFORMAT);
				}
			}
			try {
				date = format.parse(args[0].toString());
			} catch (ParseException e) {
				throw new  CmsLatteFunctionException(functionName,protoType,ERROR_INVALID_DATEFORMAT);
			}
			return date;
		}else if(args.length == 1 && isDouble(args[0])){
			Double d = new Double((Double) args[0]);
			long longDate = d.longValue();
			return new Date(longDate);
		}else{
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
	}
	/**
	 * DISBD이면 그냥 1개짜리 array를 
	 * table이면 테이블의 요소를 모두 array로
	 * @param args
	 * @return
	 * @throws CmsLatteException 
	 */
	public static ArrayField toArray(Object... args) throws CmsLatteFunctionException{
		String functionName = "toArray";
		String protoType = String.format("array=%s(DISBD|table)",functionName);
		// argument length check
		if (argsLength(args.length,1) == false) {
			throw new CmsLatteFunctionException(functionName, protoType,
					ERROR_ARGS_LENGTH);
		}
		
		if(isDISBD(args[0])){
			try {
				ArrayField af = new ArrayField(LatteUtil.getUUID());
				af.append(args[0]);
				return af;
			} catch (CmsLatteException e) {
				throw new CmsLatteFunctionException(functionName, protoType,
						e.getMessage());
			}
		}else if(isArray(args[0])){
			return (ArrayField)args[0];
		}else if(isMatrix(args[0])){
			MatrixField mf = (MatrixField)args[0];
			ArrayField af = new ArrayField(LatteUtil.getUUID());
			try {
				for (int i = 0; i < mf.getRowCount(); i++) {
					for (int j = 0; j < mf.getColumnCount(); j++) {
						af.append(mf.getValue(i, j));
					}
				}
				return af;
			} catch (CmsLatteException e) {
				throw new CmsLatteFunctionException(functionName, protoType,
						e.getMessage());
			}
		}else{
			throw new CmsLatteFunctionException(functionName, protoType,
					"unknown type error");
		}
	}

	public static MatrixField toTable(Object... args) throws CmsLatteFunctionException{
		String functionName = "toTable";
		String protoType = String.format("table=%s(DISBD|array|table)",functionName);
		// argument length check
		if (argsLength(args.length,1,2,3) == false) {
			throw new CmsLatteFunctionException(functionName, protoType,
					ERROR_ARGS_LENGTH);
		}
		if(args.length == 3 && isString(args[0])&&isString(args[1])&&isString(args[2])){
			try {
				MatrixField mf = new MatrixField(LatteUtil.getUUID());
				String org = (String)args[0];
				String colSeperator = (String)args[1];
				String rowSeperator = (String)args[2];
				String[] lines = org.split(rowSeperator);
				for(int row=0;row<lines.length;row++){
					String line = lines[row];
					String[] cols = line.split(colSeperator);
					for(int col=0;col<cols.length;col++){
						mf.appendOrReplace(row, col, cols[col]);		
					}
				}
				return mf;
			} catch (CmsLatteException e) {
				throw new CmsLatteFunctionException(functionName, protoType,
						e.getMessage());
			}			
		}else if(isDISBD(args[0])){
			try {
				MatrixField af = new MatrixField(LatteUtil.getUUID(), 1, 1);
				af.appendOrReplace(0, 0, args[0]);
				return af;
			} catch (CmsLatteException e) {
				throw new CmsLatteFunctionException(functionName, protoType,
						e.getMessage());
			}
		}else if(isMatrix(args[0])){
			return (MatrixField)args[0];
		}else if(isArray(args[0])){
			ArrayField af = (ArrayField)args[0]; 
			MatrixField mf = new MatrixField(LatteUtil.getUUID());
			int columnSize = 1; 
			if(args.length == 2 && isInteger(args[1])){
				columnSize = (Integer)args[1];
			}else{
				throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
			}
			try {
				int r = -1;
				for (int i = 0; i < af.size(); i++) {
					int c = i % columnSize;
					if (c == 0)
						r++;
					mf.appendOrReplace(r, c, af.getValue(i));
				}
				return mf;
			} catch (CmsLatteException e) {
				throw new CmsLatteFunctionException(functionName, protoType,
						e.getMessage());
			}
		}else{
			throw new CmsLatteFunctionException(functionName, protoType,
					"unknown type error");
		}
	}
	/**
	 * function이 존재하는지 여부
	 * @param args
	 * @return
	 * @throws CmsLatteFunctionException
	 */
	public static Boolean functionExist(Object... args) throws CmsLatteFunctionException{
		String functionName = "functionExist";
		String protoType = String.format("boolean=%s(functionName)",functionName);
		
		if (argsLength(args.length,1) == false) {
			throw new CmsLatteFunctionException(functionName, protoType,
					ERROR_ARGS_LENGTH);
		}
		if(isString(args[0])){
			return FunctionManager.getInstance().isCmsFunction(args[0].toString());
		}else{
			throw new CmsLatteFunctionException(functionName, protoType, ERROR_ARGTYPE_MISMATCH);
		}
	}
	/**
	 * DISBD는 null인지 체크하고
	 * Array,Matrix는 size==0 인지 체크한다
	 * @param args
	 * @return
	 * @throws CmsLatteFunctionException
	 */
	public  static Boolean isEmpty(Object... args) throws CmsLatteFunctionException{
		String functionName = "isEmpty";
		String protoType = String.format("boolean=%s(field)",functionName);
		
		if (argsLength(args.length,1) == false) {
			throw new CmsLatteFunctionException(functionName, protoType,
					ERROR_ARGS_LENGTH);
		}
		if(args[0]==null) {  return true;}
		else if(isDISBD(args[0])){
			if( args[0].toString().length() == 0){
				return true;
			}
		}else if(isArray(args[0])){
			ArrayField af = (ArrayField)args[0];
			return af.size()==0;
		}else if(isMatrix(args[0])){
			MatrixField mf = (MatrixField)args[0];
			return mf.size()==0;			
		}
		return false;
	}
	public  static Boolean isContain(Object... args) throws CmsLatteFunctionException{
		String functionName = "isContain";
		String protoType = String.format("boolean=%s(array|table|string)",functionName);
		
		if (argsLength(args.length,2) == false) {
			throw new CmsLatteFunctionException(functionName, protoType,
					ERROR_ARGS_LENGTH);
		}
		if(isString(args[0])){
			if(!isString(args[1])){
				throw new CmsLatteFunctionException(functionName, protoType, ERROR_ARGTYPE_MISMATCH);
			}
			String s = args[0].toString();
			String k = args[1].toString();
			if(s.indexOf(k)<0) {
				return false;
			}
			return true;
		}else if(isArray(args[0])){
			ArrayField af =(ArrayField) args[0];
			try {
				if(af.indexOf(args[1], 0)<0){
					return false;
				}
			} catch (CmsLatteException e) {
				throw new CmsLatteFunctionException(functionName, protoType,e.getMessage());
			}
			return true;
		}else if(isMatrix(args[0])){
			MatrixField mf = (MatrixField)args[0];
			try {
				if(mf.indexOf(args[1])<0){
					return false;
				}
			} catch (CmsLatteException e) {
				throw new CmsLatteFunctionException(functionName, protoType,e.getMessage());
			}
			return true;
		}
		return false;
	}
}
