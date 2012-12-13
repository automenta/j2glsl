package kr.co.iniline.j2glsl;

import java.lang.reflect.Array;

public final class DescParser {
	private static DescParser SINGLE_OBJ = new DescParser();
	
	public boolean isMethod = false;
	
	int paramNum = 0;
	ParamSpec params[] = null;
	ParamSpec returnParam = null;
	
	private DescParser() {
		params = new ParamSpec[0];
		paramSpecCache(20);
		returnParam = new ParamSpec();
	}
	
	protected void paramSpecCache(int size) {
		if( size <= params.length ) {
			return;
		}
		params = new ParamSpec[20];
		for (int i = 0; i < params.length; i++) {
			params[i] = new ParamSpec();
		}
	}
	
	public static DescParser parse(String desc) {
		SINGLE_OBJ.parseString( desc );
		return SINGLE_OBJ;
	}
	
	public int getParamNum() {
		return paramNum;
	}
	
	protected void parseString(String desc) {
		int pos = 0;
		int ePos = desc.length();
		String rParamString = null;
		if( desc.charAt(0) == '(' ) {
			isMethod = true;
			pos = 1;
			ePos = desc.indexOf(')');
			rParamString = desc.substring(ePos+1);
		} else {
			isMethod = false;
		}
		
		String paramsString = desc.substring(pos, ePos);
		
		paramNum = 0;
		int paramPos = 0;
		while( paramPos < paramsString.length() ) {
			paramPos = parseParam( params[paramNum], paramsString, paramPos );
			paramNum++;
			if( paramPos >= paramsString.length() ) break;
		}
		if( rParamString != null ) {
			parseParam(returnParam, rParamString, 0);
		}
	}
	
	public Class getReturnParamClass() {
		return returnParam.clazz;
	}
	
	public Class[] getParamClasses() {
		Class classes[] = new Class[ paramNum ];
		for (int i = 0; i < paramNum; i++) {
			classes[i] = getParamSpec(i).clazz;
		}
		return classes;
	}
	
	public ParamSpec getParamSpec(int index) {
		return params[index];
	}
	
	public ParamSpec getReturnParamSpec() {
		return returnParam;
	}
	
	public static void main(String[] args) throws Exception {
		DescParser descParser = new DescParser();
//		descParser.parse( "([Lmtk/engine/shader/Uniform;[[Ljava/lang/String;)V" );
		descParser.parse("([[B[I)[F");
		System.out.println("param num : "+descParser.getParamNum());
		for (int i = 0; i < descParser.getParamNum(); i++) {
			System.out.println( "param "+i+" : "+descParser.getParamSpec(i) );
		}
		System.out.println("TEST : "+Class.forName("[B"));
//		new DescParser().parse( "[Lmtk/engine/shader/Uniform;[[Ljava/lang/String;" );
	}
	
	protected int parseParam(ParamSpec pSpec, String dataType, int pos) {
		pSpec.arrayDepth = 0;
		pSpec.dataType = 0;
		pSpec.className = null;
		
		do {
			if( dataType.charAt( (pos) ) == '[' ) {
				pSpec.arrayDepth++;
				pos++;
			} else {
				break;
			}
		} while(true);
		
		
		
		/** 'L'(Class), 'I'(Integer), 'S'(short), 'B'(byte), 'C'(char), 'F'(float), 'D'(double), 'Z'(boolean) */ 
		pSpec.dataType = dataType.charAt(pos);
		if( pSpec.dataType == 'L' ) {
			int endPos = dataType.indexOf(';', pos);
			pSpec.className = dataType.substring(pos+1, endPos );
			/** parse class */
			try {
				pSpec.clazz = Class.forName(pSpec.className.replace("/", "."));
			} catch (Exception e) {
				e.printStackTrace();
			}
			pos = endPos;
		} else {
			if( pSpec.arrayDepth > 0 ) {
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < pSpec.arrayDepth; i++) {
					sb.append( "[" );
				}
				sb.append( pSpec.dataType );
				try {
					pSpec.clazz = Class.forName(sb.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				if( 'I' == pSpec.dataType ) {
					pSpec.clazz = int.class;
				} else if( 'S'  == pSpec.dataType ) {
					pSpec.clazz = short.class;
				} else if( 'B' == pSpec.dataType ) {
					pSpec.clazz = byte.class;
				} else if( 'C' == pSpec.dataType ) {
					pSpec.clazz = char.class;
				} else if( 'F' == pSpec.dataType ) {
					pSpec.clazz = float.class;
				} else if( 'D' == pSpec.dataType ) {
					pSpec.clazz = double.class;
				} else if( 'Z' == pSpec.dataType ) {
					pSpec.clazz = boolean.class;
				} else if( 'V' == pSpec.dataType ) {
					pSpec.clazz = void.class;
				} else {
					pSpec.clazz = Object.class;
				}
			}
		}
		
		return pos+1;
	}
	
	public static class ParamSpec {
		int arrayDepth = 0;
		char dataType = 0;
		String className = null;
		Class clazz = null;
		
		public int getArrayDepth() {
			return arrayDepth;
		}
		
		public char getDataType() {
			return dataType;
		}
		
		public String getClassName() {
			return className;
		}
		
		public Class getTypeClass() {
			return clazz;
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			switch(dataType) {
			case 'L' :
				sb.append( className );
				break;
			case 'I' :
				sb.append("int");
				break;
			case 'S' :
				sb.append("short");
				break;
			case 'B' :
				sb.append("byte");
				break;
			case 'C' :
				sb.append("char");
				break;
			case 'F' :
				sb.append("float");
				break;
			case 'D' :
				sb.append("double");
				break;
			case 'Z' :
				sb.append("boolean");
				break;
			case 'V' :
				sb.append("void");
				break;
			default :
				sb.append("???");
			}
			for (int i = 0; i < arrayDepth; i++) {
				sb.append("[]");
			}
			return sb.toString();
		}
	}
}
