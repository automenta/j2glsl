package kr.co.iniline.j2glsl;

public class ConditionString {
	public static final int AND = 1;
	public static final int OR = 2;
	int contType = AND;
	
	int opCode = 0;
	String param1 = null;
	String param2 = null;
	boolean reverse = true;
	
	ConditionString contCondition = null;
	
	public ConditionString(int opCode, String param1, String param2) {
		this.opCode = opCode;
		this.param1 = param1;
		this.param2 = param2;
	}
	
	public ConditionString(int opCode, int contType, String param1, String param2) {
		this.opCode = opCode;
		this.contType = contType;
		this.param1 = param1;
		this.param2 = param2;
	}
	
	public boolean isReverse() {
		return reverse;
	}
	
	public void setReverse(boolean flag) {
		this.reverse = flag;
	}
	
	public void reverse() {
		this.reverse = !reverse;
	}
	
	public void setContCondition(ConditionString cond) {
		this.contCondition = cond;
	}
	
	public void setContCondition(int contType, ConditionString cond) {
		this.contType = contType;
		this.contCondition = cond;
	}
	
	public String toString() {
		return toString(reverse);
	}
	
	public String toString(boolean reverse) {
		StringBuilder sb = new StringBuilder();
		sb.append( param1 );
		sb.append(" ");
		sb.append( Util.getCmpSign(opCode, reverse) );
		sb.append(" ");
		sb.append( param2 );
		if( contCondition != null ) {
			if( contType == AND ) {
				sb.append(" && ");
			} else {
				sb.append(" || ");
			}
			sb.append( contCondition.toString() );
		}
		return sb.toString();
	}
}
