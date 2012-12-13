package kr.co.iniline.j2glsl;

import java.io.IOException;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.VarInsnNode;

public abstract class ChildBlock extends Block {
	static boolean commentToEndOfBlock = false;
	String condition = null;
	Block parent = null;
	Code startCode = null, endCode = null;
	
	protected ChildBlock(Block parent, String condition, Code startCode, Code endCode) {
		this.parent = parent;
		this.condition = condition;
		this.startCode = startCode;
		this.endCode = endCode;
	}

	@Override
	protected Block createControlBlock(int type, String condition, Code startCode, Code endCode) {
		return createBlock(this, type, condition, startCode, endCode);
	}

	@Override
	public Code getStartCode() {
		return startCode;
	}

	@Override
	public Code getEndCode() {
		return endCode;
	}

	@Override
	public Block getParent() {
		return parent;
	}
	
	
	@Override
	public Code getNext() {
		/**
		 * 아래와 같은 패턴일 경우
		 * var i;
		 * if( x == 3 ) i = 1;
		 * 
		 * var i; 에 대한 OP명령이 없다. 때문에 하위 스택에서 참조하는지 확인해야함. 그리고 선언문을 넣어준다.
		 */
		Code code = super.getNext();
		if( code != null ) {
			if( code instanceof AssignCode ) {
				AssignCode assignCode = (AssignCode)code;
				if( assignCode.isVarCreateCode() ) {
					Code endCode = getEndCode();
					AbstractInsnNode findRefContCode = null;
					if( endCode instanceof LabelCode ) {
						findRefContCode = (LabelNode) ((LabelCode)endCode).label.info;
					} else if( endCode instanceof JumpCode ) {
						findRefContCode = ((JumpCode)endCode).jmpNode;
					}
					
					if( findRefContCode != null ) {
						while( (findRefContCode=findRefContCode.getNext()) != null ) {
							if( findRefContCode instanceof VarInsnNode ) {
								if( ((VarInsnNode)findRefContCode).var == assignCode.varIdx ) {
//									System.out.println( "Finded define request : "+assignCode );
									/** find */
									assignCode.setVarCreateFlag(false);
									AssignCode addDefineCode = new AssignCode(false, assignCode.type+" "+assignCode.named);
									addDefineCode.setVarCreateFlag(false);
									getParent().addCodeWriter( addDefineCode );
									break;
								}
							}
						}
					}
				}
			}
		}
		return code;
	}
	
	static Block createBlock(Block parent, int type, String condition, Code startCode, Code endCode) {
		Block rBlock = null;
		if( type == TYPE_IF ) {
			rBlock = new IfCBlock(parent, condition, startCode, endCode);
		} else if( type == TYPE_ELSEIF ) {
			rBlock = new ElseIfCBlock(parent, condition, startCode, endCode);
		} else if( type == TYPE_ELSE ) {
			rBlock = new ElseCBlock(parent, startCode, endCode);
		} else if( type == TYPE_WHILE ) {
			rBlock = new WhileCBlock(parent, condition, startCode, endCode);
		} else if( type == TYPE_DOWHILE ) {
			rBlock = new DoWhileCBlock(parent, condition, startCode, endCode);
		} else if( type == TYPE_FOR ) {
//			return new For
		}
//		System.out.println("!!!!Create childBlock : "+rBlock.getClass().getSimpleName()+", "+startCode+", "+endCode);
		return rBlock;
	}
	
	static class IfCBlock extends ChildBlock {
		protected IfCBlock(Block parent, String condition, Code startCode, Code endCode) {
			super( parent, condition, startCode, endCode );
		}
		
		@Override
		public void writeBlockStart(TabbedWriter writer) throws IOException {
			writer.appendNewTabbedLine();
			writer.append("if( ");
			writer.append( condition );
			writer.append(" )");
			writer.appendNewTabbedLine();
			writer.startFrame();
		}

		@Override
		public void writeBlockEnd(TabbedWriter writer) throws IOException {
			writer.endFrame();
			if( commentToEndOfBlock ) {
				writer.append("//end of if ");
				writer.append( condition );
			}
		}

		@Override
		public int getType() {
			return TYPE_IF;
		}
	}
	
	static class ElseIfCBlock extends ChildBlock {
		protected ElseIfCBlock(Block parent, String condition, Code startCode, Code endCode) {
			super( parent, condition, startCode, endCode );
		}
		
		@Override
		public void writeBlockStart(TabbedWriter writer) throws IOException {
			writer.appendNewTabbedLine();
			writer.append("else if( ");
			writer.append( condition );
			writer.append(" )");
			writer.appendNewTabbedLine();
			writer.startFrame();
		}

		@Override
		public void writeBlockEnd(TabbedWriter writer) throws IOException {
			writer.endFrame();
			if( commentToEndOfBlock ) {
				writer.append("//end of else if ");
				writer.append( condition );
			}
		}

		@Override
		public int getType() {
			return TYPE_ELSEIF;
		}
	}
	
	static class ElseCBlock extends ChildBlock {
		protected ElseCBlock(Block parent, Code startCode, Code endCode) {
			super( parent, null, startCode, endCode );
		}
		
		@Override
		public void writeBlockStart(TabbedWriter writer) throws IOException {
			writer.appendNewTabbedLine();
			writer.append("else");
			writer.appendNewTabbedLine();
			writer.startFrame();
		}

		@Override
		public void writeBlockEnd(TabbedWriter writer) throws IOException {
			writer.endFrame();
			if( commentToEndOfBlock ) writer.append("//end of else");
		}

		@Override
		public int getType() {
			return TYPE_ELSE;
		}
	}
	
	static abstract class LoopCBlock extends ChildBlock {
		protected LoopCBlock(Block parent, String condition, Code startCode, Code endCode) {
			super( parent, condition, startCode, endCode );
		}
		
		public LabelCode getStackLabelCode() {
			return (LabelCode)getStartCode();
		}
		
		public JumpCode getLoopJumpCode() {
			return (JumpCode)getEndCode();
		}
		
		public boolean isConditionLoop() {
			return getLoopJumpCode().isCondition();
		}
	}
	
	static class WhileCBlock extends ChildBlock {
		protected WhileCBlock(Block parent, String condition, Code startCode, Code endCode) {
			super( parent, condition, startCode, endCode );
		}
		
		@Override
		public void writeBlockStart(TabbedWriter writer) throws IOException {
			writer.appendNewTabbedLine();
			writer.append("while( ");
			writer.append( ((JumpCode)getEndCode()).getLoopConditionString() );
			writer.append(" )");
			writer.appendNewTabbedLine();
			writer.startFrame();
		}

		@Override
		public void writeBlockEnd(TabbedWriter writer) throws IOException {
			writer.endFrame();
			if( commentToEndOfBlock ) {
				writer.append("//end of while ");
				writer.append( condition );
			}
		}

		@Override
		public int getType() {
			return TYPE_WHILE;
		}
	}
	
	static class DoWhileCBlock extends ChildBlock {
		protected DoWhileCBlock(Block parent, String condition, Code startCode, Code endCode) {
			super( parent, condition, startCode, endCode );
		}
		
		@Override
		public void writeBlockStart(TabbedWriter writer) throws IOException {
			writer.appendNewTabbedLine();
			writer.append("do");
			if( commentToEndOfBlock ) {
				writer.append(" //start of while");
				writer.append( condition );
			}
			writer.appendNewTabbedLine();
			writer.startFrame();
		}

		@Override
		public void writeBlockEnd(TabbedWriter writer) throws IOException {
			writer.endFrame();
			writer.append(" while( ");
			writer.append( condition );
			writer.append(" );");
		}

		@Override
		public int getType() {
			return TYPE_DOWHILE;
		}
	}
}
