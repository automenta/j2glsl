package kr.co.iniline.j2glsl;

import java.io.IOException;
import java.util.ArrayList;

import kr.co.iniline.j2glsl.JumpAnalyzer.JumpAnalyzedResult;


import org.objectweb.asm.Label;

public class LabelCode extends Code {
	boolean isBefRef = false;
	Label label = null;
	String caseVal = null;
	
	ArrayList<JumpCode> befRefJumper = new ArrayList<JumpCode>();
	ArrayList<JumpCode> aftRefJumper = new ArrayList<JumpCode>();
	
	LabelCode stackStartLabel = null;
	LabelCode linkedJumpLabel = null;
	
	JumpAnalyzedResult loopJumpAnalyzedResult;
	
	public LabelCode(Label label) {
		this.label = label;
	}
	
	public void switchBefRef() {
		isBefRef = true;
	}
	
	public JumpCode getFirstBefJumper() {
		if( befRefJumper.size() > 0 ) {
			return (JumpCode)befRefJumper.get( 0 );
		} else {
			return null;
		}
	}
	
	public JumpCode getLastBefJumper() {
		if( befRefJumper.size() > 0 ) {
			return (JumpCode)befRefJumper.get( befRefJumper.size()-1 );
		} else {
			return null;
		}
	}
	
	public JumpCode getLastAftJumper() {
		if( aftRefJumper.size() > 0 ) {
			return (JumpCode)aftRefJumper.get( aftRefJumper.size()-1 );
		} else {
			return null;
		}
	}
	
	public void refJump(JumpCode jumper) {
		if( isBefRef ) {
			befRefJumper.add( jumper );
		} else {
			aftRefJumper.add( jumper );
		}
	}
	
	public boolean isLoopType() {
		return getLastAftJumper() != null;
	}
	
	public JumpAnalyzedResult getLoopAnalyzedResult() {
		return loopJumpAnalyzedResult;
	}
	
	public LabelCode getLinkedLoopLabel() {
		return linkedJumpLabel;
	}
	
	public LabelCode getLoopStackStartLabel() {
		return stackStartLabel;
	}
	
	void checkAndBindLastJumper(Block block, JumpAnalyzer analyzer) {
		JumpCode jumper = getLastAftJumper();
		if( jumper != null ) {
			loopJumpAnalyzedResult = analyzer.analyzeLoop(block, jumper);
			Code endStack = loopJumpAnalyzedResult.getEndStack();
			if( endStack.getPrevious() instanceof LabelCode ) {
//				System.out.println("Setting stack start Label : loopLabel:"+(LabelCode)endStack.getPrevious()+", stackLabel:"+this);
				((LabelCode)endStack.getPrevious()).stackStartLabel = this;
			}
		}
		
//		if( jumper != null ) {
//			LabelCode jumperLabel = null;
//			if( jumper.getPrevious() instanceof LabelCode ) {
//				jumperLabel = (LabelCode)jumper.getPrevious();
//			} else if( jumper.getPrevious().getPrevious() instanceof LabelCode ) {
//				jumperLabel = (LabelCode)jumper.getPrevious().getPrevious();
//			}
//			jumperLabel.linkedJumpLabel = this;
//		}
	}
	
	public String toParamString() {
		return "labelcode."+label.toString();
	}
	
	public String toString() {
		return label.toString()+"("+hashCode()+")";
	}

	@Override
	public void write(TabbedWriter writer) throws IOException {
		// TODO Auto-generated method stub
		
	}
}
