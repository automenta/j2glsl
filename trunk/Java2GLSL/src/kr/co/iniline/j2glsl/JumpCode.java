package kr.co.iniline.j2glsl;

import java.io.IOException;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.JumpInsnNode;

public class JumpCode extends Code {
	JumpInsnNode jmpNode = null;
	LabelCode labelCode = null;
	Condition caseString = null;
	String altCond = null;
	
	
	public JumpCode(JumpInsnNode node) {
		this.idx = idx;
		this.jmpNode = node;
	}
	
	public JumpCode(JumpInsnNode node, Condition condString) {
		this.idx = idx;
		this.jmpNode = node;
		this.caseString = condString;
	}
	
	public void setLabelCode(LabelCode labelCode) {
		this.labelCode = labelCode;
	}
	
	public boolean isCondition() {
		return jmpNode.getOpcode() != Opcodes.GOTO;
	}
	
	public String getLoopConditionString() {
		if( caseString == null ) {
			return "true";
		} else {
			return caseString.toString();
		}
	}
	
	public boolean isRecurrStack() {
		if( idx > labelCode.idx ) {
			return true;
		}
		return false;
	}
	
//	@Override
//	public void writeStack(TabbedWriter writer) throws IOException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public String toParamString() {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
	public String toString() {
		return Util.dumpNode(this.jmpNode);
	}

	@Override
	public void write(TabbedWriter writer) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toParamString() {
		// TODO Auto-generated method stub
		return null;
	}
}
