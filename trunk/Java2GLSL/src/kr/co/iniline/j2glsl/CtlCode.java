package kr.co.iniline.j2glsl;

import java.io.IOException;
import java.util.ArrayList;

public class CtlCode extends Code {
	public static final int NONE = 0;
	public static final int IF = 1;
	public static final int ELSEIF = 2;
	public static final int ELSE = 3;
	public static final int WHILE_START = 4;
	public static final int WHILE_END = 5;
	public static final int DOWHILE_START = 6;
	public static final int DOWHILE_END = 7;
	public static final int FOR = 8;
	public static final int BREAK = 9;
	public static final int CONTINUE = 10;
	
	public int INCONTROL_TYPE = NONE;
	public int CONTROL_TYPE = NONE;
	
	boolean ENDFRAME_DEBUG = true;
	
	ArrayList<String> endFrames = new ArrayList<String>();
	
	Condition caseString = null;
	
	public void attachEndFrame(String comment) {
		endFrames.add( comment );
	}
	
	public int getType() {
		return CONTROL_TYPE;
	}
	
	public int getInnerType() {
		return INCONTROL_TYPE;
	}
	
	public void addBreak() {
		INCONTROL_TYPE = BREAK;
	}
	
	public void addContinue() {
		INCONTROL_TYPE = CONTINUE;
	}
	
//	public boolean isLoopType() {
//		if(getType() == WHILE_END || getType() == DOWHILE_END) {
//			return true;
//		}
//		return false;
//	}
	
	public void fixIf() {
		CONTROL_TYPE = IF;
	}
	
	public void fixElseif() {
		CONTROL_TYPE = ELSEIF;
	}
	
	public void fixElse() {
		CONTROL_TYPE = ELSE;
	}
	
	public void fixDoWhileStart() {
		CONTROL_TYPE = DOWHILE_START;
	}
	
	public void fixDoWhileEnd() {
		CONTROL_TYPE = DOWHILE_END;
	}
	
	public void fixWhileStart() {
		CONTROL_TYPE = WHILE_START;
	}
	
	public void fixWhileEnd() {
		CONTROL_TYPE = WHILE_END;
	}
	
	@Override
	public void write(TabbedWriter writer) throws IOException {
//		if( this instanceof JumpCode ) {
//			if( CONTROL_TYPE == NONE && INCONTROL_TYPE == NONE && caseString == null ) {
//				System.out.println( "--------"+((JumpCode)this).labelCode );
//			}
//		}
		
		for (int i = endFrames.size(); i > 0; i--) {
			writer.endFrame();
			if( ENDFRAME_DEBUG ) {
				writer.append("//");
				writer.append(endFrames.get( (i-1) ));
			}
		}
		
		if(CONTROL_TYPE == IF) {
			writer.appendNewTabbedLine();
			writer.append("if( ");
			writer.append(caseString.toString());
			writer.append(" )");
			writer.appendNewTabbedLine();
			writer.startFrame();
		} else if(CONTROL_TYPE == ELSEIF) {
			writer.appendNewTabbedLine();
			writer.append("else if( ");
			writer.append(caseString.toString());
			writer.append(" )");
			writer.appendNewTabbedLine();
			writer.startFrame();
		} else if(CONTROL_TYPE == ELSE) {
			writer.appendNewTabbedLine();
			writer.append("else");
			writer.startFrame();
		} else if(CONTROL_TYPE == DOWHILE_START) {
			writer.appendNewTabbedLine();
			writer.append("do");
			writer.startFrame();
		} else if(CONTROL_TYPE == DOWHILE_END) {
			writer.appendNewTabbedLine();
			writer.endFrame();
			writer.append(" while( ");
			if( caseString == null ) {
				writer.append("true");
			} else {
				writer.append(caseString.toString());
			}
			writer.append(" );");
		} else if(CONTROL_TYPE == WHILE_START) {
			writer.appendNewTabbedLine();
			writer.append("while( ");
			if( caseString == null ) {
				writer.append("true");
			} else {
				writer.append(caseString.toString());
			}
			writer.append(" )");
			writer.startFrame();
		}
		
		if(INCONTROL_TYPE == CONTINUE) {
			writer.appendNewTabbedLine();
			writer.append("continue;");
		} else if(INCONTROL_TYPE == BREAK) {
			writer.appendNewTabbedLine();
			writer.append("break;");
		}
		if(INCONTROL_TYPE != NONE && CONTROL_TYPE >= IF && CONTROL_TYPE <= ELSE ) {
			writer.endFrame();
		}
	}

	@Override
	public String toParamString() {
		return "error";
	}
}
