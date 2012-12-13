package kr.co.iniline.j2glsl;


import java.io.IOException;


public class AssignCode extends Code {
	String type = null;
	int varIdx = -1;
	String named = null;
	String param = null;
	boolean isCreateCode = false;
	boolean isVariable = false;
	boolean isWritable = true;
	
	public AssignCode(boolean isVariable, String code) {
		this.isVariable = isVariable;
		this.param = code;
	}
	
	public AssignCode(String type, int varIdx, String varName, boolean isVariable, String code) {
		this.type = type;
		this.varIdx = varIdx;
		this.named = varName;
		this.isVariable = isVariable;
		this.param = code;
		if( type != null ) {
			isCreateCode = true;
		}
	}
	
	public boolean isVarCreateCode() {
		return isCreateCode;
	}
	
	public void setVarCreateFlag(boolean flag) {
		isCreateCode = flag;
	}
	
	public int getCreateVarIdx() {
		return varIdx;
	}
	
	public boolean getWritable() {
		return isWritable;
	}
	
	public void setWritable(boolean flag) {
		this.isWritable = flag;
	}
	
	public boolean isVariable() {
		return isVariable;
	}
	
	public void write(TabbedWriter writer) throws IOException {
		if( isWritable ) {
			writer.appendNewTabbedLine();
			if( isVarCreateCode() ) {
				writer.append( type );
				writer.append( " " );
			}
			writer.append( param );
			writer.append(";");
		}
	}
	
	public String toParamString() {
		return param;
	}
}
