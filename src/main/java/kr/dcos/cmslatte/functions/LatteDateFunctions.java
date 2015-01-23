package kr.dcos.cmslatte.functions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.exception.CmsLatteFunctionException;
import kr.dcos.cmslatte.field.ArrayField;
import kr.dcos.cmslatte.utils.LatteUtil;

public class LatteDateFunctions extends LatteFunctionBase{
	/**
	 * now() 현재 시간의 날짜시간을 리턴한다
	 * @param args
	 * @return
	 * @throws CmsLatteFunctionException
	 */
	public static Date now(Object... args) throws CmsLatteFunctionException{
		String functionName = "now";
		String protoType = "Date=now()";
		
		// argument length check
		if(args.length > 0){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		return new Date();
	}
	/**
	 * date("2012-12-01 13:30:45"); or
	 * date("2012-12-01");
	 * date("20121201","yyyyMMdd");
	 * @param args
	 * @return
	 * @throws CmsLatteFunctionException
	 */
	public static Date date(Object... args) throws CmsLatteFunctionException{
		String functionName = "date";
		String protoType = "Date=date(dateString)";
		
		// argument length check
		if(!argsLength(args.length, 1,2)){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(!isString(args[0])){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
		String dateString = args[0].toString();
		SimpleDateFormat format = LatteUtil.dateFormat(dateString);
		if (args.length == 2){
			if(!isString(args[1])){
				throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
			}
			format = new SimpleDateFormat(args[1].toString());
		}
		if(format==null){
			throw new CmsLatteFunctionException(functionName,protoType,"date format invalid");
		}
		try {
			return format.parse(dateString);
		} catch (ParseException e) {
			throw new CmsLatteFunctionException(functionName,protoType,"date format invalid");
		}
	}
	/**
	 * 요일에 해당하는 숫자를 리턴한다
	 * @param args
	 * @return
	 * @throws CmsLatteFunctionException
	 */
	public static Integer dayOfWeek(Object... args) throws CmsLatteFunctionException{
		String functionName = "dayOfWeek";
		String protoType = String.format("integer=%s(date)",functionName);
		// argument length check
		if(!argsLength(args.length, 1)){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(!isDate(args[0])){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
		Calendar c = Calendar.getInstance();
		Date d = (Date)args[0];
		c.setTime(d);
		return  c.get(Calendar.DAY_OF_WEEK)-1;
	
	}
	/**
	 * date-> year,month,day,hour,min,sec로 이루어진 array로 리턴한다
	 * @param args
	 * @return
	 * @throws CmsLatteFunctionException
	 */
	public static ArrayField dateToArray(Object... args) throws CmsLatteFunctionException{
		String functionName = "dateToArray";
		String protoType = String.format("array=%s(date)",functionName);
		// argument length check
		if(!argsLength(args.length, 1)){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(!isDate(args[0])){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
		ArrayField af = new ArrayField(LatteUtil.getUUID());
		Calendar c = Calendar.getInstance();
		Date d = (Date)args[0];
		c.setTime(d);
		try {
			af.append(c.get(Calendar.YEAR));
			af.append(c.get(Calendar.MONTH)+1);
			af.append(c.get(Calendar.DAY_OF_MONTH));
			af.append(c.get(Calendar.HOUR_OF_DAY));
			af.append(c.get(Calendar.MINUTE));
			af.append(c.get(Calendar.SECOND));
			return af;
		} catch (CmsLatteException e) {
			throw new CmsLatteFunctionException(functionName, protoType, e.getMessage());
		}
	}
	public static Date dayAdd(Object... args) throws CmsLatteFunctionException{
		String functionName = "dayAdd";
		String protoType = String.format("date=%s(date,integer)",functionName);
		// argument length check
		if(!argsLength(args.length, 2)){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(!isDate(args[0]) || !isInteger(args[1])){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
		Calendar c = Calendar.getInstance();
		Date d = (Date)args[0];
		int amount = (Integer)args[1];
		c.setTime(d);
		c.add(Calendar.DATE, amount);
		return c.getTime();
	}
	public static Date monthAdd(Object... args) throws CmsLatteFunctionException{
		String functionName = "monthAdd";
		String protoType = String.format("date=%s(date,integer)",functionName);
		// argument length check
		if(!argsLength(args.length, 2)){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(!isDate(args[0]) || !isInteger(args[1])){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
		Calendar c = Calendar.getInstance();
		Date d = (Date)args[0];
		int amount = (Integer)args[1];
		c.setTime(d);
		c.add(Calendar.MONTH, amount);
		return c.getTime();
	}
	static final long ONE_HOUR = 60 * 60 * 1000L;
	private static long daysBetween(Date d1, Date d2){
	    return ( (d2.getTime() - d1.getTime() + ONE_HOUR) / 
	                  (ONE_HOUR * 24));
	} 

	public static Integer daysBetween(Object... args) throws CmsLatteFunctionException{
		String functionName = "daysBetween";
		String protoType = String.format("integer=%s(date,date)",functionName);
		// argument length check
		if(!argsLength(args.length, 2)){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGS_LENGTH);
		}
		if(!isDate(args[0]) || !isDate(args[1])){
			throw new CmsLatteFunctionException(functionName,protoType,ERROR_ARGTYPE_MISMATCH);
		}
		Calendar c1 = new GregorianCalendar();
		Calendar c2 = new GregorianCalendar();
		ArrayField af1 = dateToArray((Date)args[0]);
		ArrayField af2 = dateToArray((Date)args[1]);
		 try {
			c1.set( (Integer)af1.getValue(0),
					 (Integer)af1.getValue(1)-1,
					 (Integer)af1.getValue(2),0,0,0);
			c2.set( (Integer)af2.getValue(0),
					 (Integer)af2.getValue(1)-1,
					 (Integer)af2.getValue(2),0,0,0);
			long days=0;
			if(c1.before(c2)){
				days = daysBetween(c1.getTime(),c2.getTime());
			}else if(c1.after(c2)){
				days = daysBetween(c2.getTime(),c1.getTime());
			}
			return LatteUtil.longToInt(days);
		} catch (CmsLatteException e) {
			throw new CmsLatteFunctionException(functionName, protoType, e.getMessage());
		}

		  
	}	
}
	
