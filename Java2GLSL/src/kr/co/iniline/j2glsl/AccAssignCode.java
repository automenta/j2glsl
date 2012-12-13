package kr.co.iniline.j2glsl;


import java.io.IOException;


public class AccAssignCode extends AssignCode {
	public AccAssignCode(boolean isVariable, String code) {
		super(isVariable, code);
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
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(param);
		sb.append(")");
		return sb.toString();
	}
}
