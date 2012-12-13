package kr.co.iniline.j2glsl;


public class ArrayAssignCode extends AssignCode {
	int length = -1;
	String type = null;
	String elems[] = null;
	
	public ArrayAssignCode(boolean isVariable, int length, String type) {
		super(isVariable, type);
		this.length = length;
		this.type = type;
	}
	
	public int length() {
		return length;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String varType) {
		if( type == null ) {
			type = varType;
		} else {
			if( !type.equals(varType) ) {
				type = "Object";
			}
		}
	}
	
	public String getElement(int pos) {
		return elems[pos];
	}
	
	public void setElement(int pos, String elem) {
		if( elems == null ) {
			elems = new String[length];
		}
		elems[pos] = elem;
	}
	
	public String toParamString() {
		if( elems == null ) {
			StringBuffer sb = new StringBuffer();
			sb.append("new ");
			sb.append(type);
			sb.append("[");
			sb.append(length);
			sb.append("]");
			return sb.toString();
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append("new ");
			sb.append(type);
			sb.append("[]");
			sb.append("{");
			for (int i = 0; i < length; i++) {
				sb.append( elems[i] );
				if( (i+1)<length ) {
					sb.append(", ");
				}
			}
			sb.append("}");
			return sb.toString();
		}
	}
}
