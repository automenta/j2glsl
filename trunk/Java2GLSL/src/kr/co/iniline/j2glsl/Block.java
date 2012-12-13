package kr.co.iniline.j2glsl;

import java.io.IOException;
import java.util.ArrayList;

import kr.co.iniline.j2glsl.ChildBlock.ElseIfCBlock;
import kr.co.iniline.j2glsl.ChildBlock.IfCBlock;
import kr.co.iniline.j2glsl.JumpAnalyzer.JumpAnalyzedResult;

public abstract class Block implements CodeWriter {
	public static final int TYPE_MAIN = 0;
	public static final int TYPE_IF = 1;
	public static final int TYPE_ELSEIF = 2;
	public static final int TYPE_ELSE = 3;
	public static final int TYPE_WHILE = 4;
	public static final int TYPE_DOWHILE = 5;
	public static final int TYPE_FOR = 6;
	
	public static final int JUMP_CONTINUE = 1;
	public static final int JUMP_BREAK = 2;
	public static final int JUMP_OUTOFBLOCK = 3;
	public static final int JUMP_LOOP = 4;
	public static final int JUMP_AFTERINBLOCK = 5;
	
	Block parent = null;
	Code focusedCode = null;
	
	ArrayList<CodeWriter> codeList = null;
	
	protected abstract Block createControlBlock(int type, String condition, Code startCode, Code endCode);
	
	public abstract Code getStartCode();
	
	public abstract Code getEndCode();
	
	public abstract Block getParent();
	
	public abstract void writeBlockStart(TabbedWriter writer) throws IOException;
	
	public abstract void writeBlockEnd(TabbedWriter writer) throws IOException;
	
//	static boolean TEMP_COMMENT_WRITE_FLAG = false; 
	final static boolean TEMP_COMMENT_WRITE_FLAG = true;
	
	public JumpAnalyzer getJumpAnalyzer() {
		if( getParent() != null ) {
			return getParent().getJumpAnalyzer();
		}
		return null;
	}
	
	public void writeContent(TabbedWriter writer) throws IOException {
		for(int i=0; (codeList != null && i<codeList.size() ); i++) {
			CodeWriter code = codeList.get(i);
			if( code instanceof CommentCode ) {
				if( TEMP_COMMENT_WRITE_FLAG ) code.write(writer);
			} else {
				code.write(writer);
			}
		}
	}
	
	public void write(TabbedWriter writer) throws IOException {
		writeBlockStart( writer );
		writeContent( writer );
		writeBlockEnd( writer );
	}
	
	public abstract int getType();
	
	public boolean isLoopBlock() {
		if( getType() == TYPE_WHILE || getType() == TYPE_DOWHILE || getType() == TYPE_FOR ) {
			return true;
		}
		return false;
	}
	
	public void addCodeWriter(CodeWriter codeWriter) {
		if( codeList == null ) {
			codeList = new ArrayList<CodeWriter>();
		}
		codeList.add( codeWriter );
	}
	
	public int size() {
		if( codeList == null ) {
			return 0;
		}
		return codeList.size();
	}
	
	public CodeWriter codeAt(int index) {
		if( codeList == null || (index+1) > codeList.size() ) {
			return null;
		}
		int countCode = 0;
		for (int i = 0; i < codeList.size(); i++) {
			CodeWriter codeWriter = codeList.get( i );
			if( codeWriter instanceof CommentCode ) {
				continue;
			} else {
				if( countCode == index ) {
					return codeWriter;
				}
				countCode++;
			}
		}
		return null;
	}
	
	public CodeWriter lastCodeAt(int index) {
		if( codeList == null || index < 0 ) {
			return null;
		}
		int countCode = 0;
		for (int i = 0; i < codeList.size(); i++) {
			CodeWriter codeWriter = codeList.get( (codeList.size()-1-i) );
			if( codeWriter instanceof CommentCode ) {
				continue;
			} else {
				if( countCode == index ) {
					return codeWriter;
				}
				countCode++;
			}
		}
		return null;
	}
	
	public void setFocusCode(Code code) {
		focusedCode = code.getPrevious();
	}
	
	public Code getNext() {
		Code rCode = null;
		if( focusedCode == null ) {
			focusedCode = getStartCode();
		}
		Code nextCode = focusedCode.getNext();
		if( isInBlock(nextCode) ) {
			rCode = focusedCode = nextCode;
		}
		return rCode;
	}
	
	public boolean isInBlock(Code code) {
		if( code.idx > getStartCode().idx && code.idx < getEndCode().idx ) {
			return true;
		}
		return false;
	}
	
	public int getJumpAction(JumpCode jmpCode) {
		/** check continue */
		Block block = this;
		do {
			if( block.isLoopBlock() ) {
				/** while(true) */
				if( !jmpCode.isCondition() && jmpCode.labelCode == block.getStartCode() ) {
					return JUMP_CONTINUE;
				} else if( block.getEndCode().getPrevious() == jmpCode.labelCode 
						|| block.getEndCode().getPrevious().getPrevious() == jmpCode.labelCode ) {
					return JUMP_CONTINUE;
				} else if( jmpCode.labelCode.idx > block.getEndCode().idx ) {
					return JUMP_BREAK;
				}
				break;
			}
		} while( (block=block.getParent()) != null );
		
		/** check afterside */
		if( jmpCode.labelCode.idx > jmpCode.idx && jmpCode.labelCode.idx < getEndCode().idx ) {
			return JUMP_AFTERINBLOCK;
		}
		
		/** check loop request */
		if( jmpCode.labelCode.idx < jmpCode.idx && jmpCode.labelCode.idx > getStartCode().idx ) {
			return JUMP_LOOP;
		}
		
		/** check unknown inJump */
		if( jmpCode.labelCode.idx < getEndCode().idx && jmpCode.labelCode.idx > getStartCode().idx ) {
			//Unknown jump : goto label
		}
		
		return JUMP_OUTOFBLOCK;
	}
	
	public static final CodeWriter CONTINUE_CODE = new CodeWriter() {
		@Override
		public void write(TabbedWriter writer) throws IOException {
			writer.appendNewTabbedLine();
			writer.append("continue;");
		}
	};
	
	public static final CodeWriter BREAK_CODE = new CodeWriter() {
		@Override
		public void write(TabbedWriter writer) throws IOException {
			writer.appendNewTabbedLine();
			writer.append("break;");
		}
	};
	
	static final class CommentCode implements CodeWriter {
		String content = null;
		public CommentCode(String str) {
			this.content = str;
		} 
		
		@Override
		public void write(TabbedWriter writer) throws IOException {
//			writer.appendNewTabbedLine();
//			writer.append("// ");
//			writer.append(content);
		}
	}
	
	CodeWriter cmt(String string) {
		return new CommentCode(string);
	}
	
	public void buildCodeStack() {
		Code procCode = null;
		LabelCode expectedElseLabel = null;
		while( (procCode=getNext()) != null ) {
			
			/** if or continue */
			if( procCode instanceof JumpCode ) {
				JumpCode jmpCode = (JumpCode)procCode;
				if( jmpCode.isCondition() ) {
					JumpAnalyzedResult result = getJumpAnalyzer().analyzeIf( this, jmpCode );
					jmpCode = (JumpCode)result.getStartStack();
					jmpCode.altCond = result.condition;
					Code endOfBlock = result.getEndStack();
					/** check else if */
					
					int ifType = TYPE_IF;
					CodeWriter lastCodeWriter = lastCodeAt(0);
					if( lastCodeWriter != null 
							&& (lastCodeWriter instanceof IfCBlock || lastCodeWriter instanceof ElseIfCBlock) ) {
						Code lastCodeInPreviousIfBlock = ((Block)lastCodeWriter).getEndCode().getPrevious();
						JumpCode lastJumpCodePreviousIfBlock = null;
						if( lastCodeInPreviousIfBlock instanceof JumpCode ) {
							lastJumpCodePreviousIfBlock = (JumpCode)lastCodeInPreviousIfBlock;
							if( ! lastJumpCodePreviousIfBlock.isCondition() ) {
								ifType = TYPE_ELSEIF;
							}
						}
					}
					
					Block cBlock = createControlBlock(ifType, result.condition, jmpCode, endOfBlock);
					cBlock.buildCodeStack();
					addCodeWriter( cBlock );
					setFocusCode( result.getNextStack() );
				} else {
					int jumpType = getJumpAction( jmpCode );
					
					if( jumpType == JUMP_CONTINUE ) {
						addCodeWriter( CONTINUE_CODE );
					} else if( jumpType == JUMP_BREAK ) {
						addCodeWriter( BREAK_CODE );
					} else if( jumpType == JUMP_AFTERINBLOCK ) {
						addCodeWriter( cmt("error-endCode is normal label but jumper is goto") );
					} else if( jumpType == JUMP_LOOP ) {
						addCodeWriter( cmt("jump??? - unexpected case") );
					} else if( jumpType == JUMP_OUTOFBLOCK ) {
						if( getParent() != null ) {
							int jumpActionUpSide = getParent().getJumpAction( jmpCode );
							if( jumpActionUpSide == JUMP_CONTINUE || jumpActionUpSide == JUMP_AFTERINBLOCK ) {
								addCodeWriter( cmt("we should else mark") );
//								jmpCode.labelCode.markElseEnd();
							}
						}
						addCodeWriter( cmt("goto jumper is out of block. Is need set else mark?") );
					} else {
						//??
						addCodeWriter( cmt("error case-2") );
					}
				}
			} else if( procCode instanceof LabelCode ) {
				LabelCode labelCode = (LabelCode)procCode;
				
				if( labelCode.getLoopAnalyzedResult() != null ) {
					JumpAnalyzedResult loopJumpAnalyzed = labelCode.getLoopAnalyzedResult();
//					System.out.println("!!!!!!!Create loop"+loopJumpAnalyzed.getStartStack()+","+loopJumpAnalyzed.getEndStack()+","+loopJumpAnalyzed.getNextStack());
					int loopType = TYPE_DOWHILE;
					
					/** check while */
					/**
					 * -while 문인데도 Label 이전에 GOTO가 아닐수도 있다. 이 전의 컨트롤 블럭이 if 문일 경우
					 * 바로 루프 조건 레이블점프로 이동하기 때문.
					 * -while(true) == do{}while(true);
					 * -루프 조건부시작에서 스택시작을 찾고 조건부시작을 참조한 제일 이전 점프가 루프스택시작보다 이전 스택일 경우
					 * (이전 if 블럭이 직접 조건부시작으로 점프하거나 루프스택시작 이전이 GOTO로 조건부시작을 가르킨경우다)
					 */
//					if( labelCode.getPrevious() instanceof JumpCode 
//							&& !((JumpCode)labelCode.getPrevious()).isCondition() ) {
//						if( ((JumpCode)labelCode.getPrevious()).labelCode == labelCode.getLoopAnalyzedResult().getEndStack()
//								|| ((JumpCode)labelCode.getPrevious()).labelCode == labelCode.getLoopAnalyzedResult().getEndStack().getPrevious() ) {
//							loopType = TYPE_DOWHILE;
//						}
//					}
					Code endCode = loopJumpAnalyzed.getEndStack();
					LabelCode loopStack = null;
					if( endCode instanceof JumpCode 
							&& endCode.getPrevious() instanceof LabelCode ) {
						loopStack = (LabelCode)endCode.getPrevious();
					} else if( endCode instanceof LabelCode ) {
						loopStack = (LabelCode)endCode;
					}
					/**
					 *  
					 */
					if( loopStack != null 
							&& loopStack.getFirstBefJumper() != null 
							&& loopStack.getFirstBefJumper().idx < loopStack.getLoopStackStartLabel().idx ) {
						loopType = TYPE_WHILE;
					}
					
					
					Block cBlock = createControlBlock(loopType,
							labelCode.getLoopAnalyzedResult().getCondition(),
							labelCode.getLoopAnalyzedResult().getStartStack(), 
							labelCode.getLoopAnalyzedResult().getEndStack());
					cBlock.buildCodeStack();
					addCodeWriter( cBlock );
					setFocusCode( labelCode.getLoopAnalyzedResult().getNextStack() );
				}
				
				else {
					CodeWriter lastCodeWriter = lastCodeAt(0);
					
					/**
					 * else {
					 * 	 if {
					 *   }
					 * }
					 * 
					 * 인지 아니면
					 * 
					 * else {
					 *   if {
					 *   }
					 *   some stack...
					 * }
					 * 를 확인하는 방법은 Analyze를 통해서 endCode가 같은지 확인한다. 물론 GOTO에 대한 analyze를 구현해야겠지??
					 */
					
					/**
					 * 이전 코드가 if 또는 else if 블럭이며 그 조건이 현재레이블을 가리키고 있다면
					 * else if 또는 else 일 경우
					 */
					if( (lastCodeWriter instanceof IfCBlock || lastCodeWriter instanceof ElseIfCBlock)
							&& (  (JumpCode)((Block)lastCodeWriter).getStartCode()  ).labelCode == labelCode ) {
						addCodeWriter( cmt("expect else (if)") );
						/** find end of else */
						CodeWriter code = null;
						for(int i=0; ;i++) {
							if( code != null && code instanceof IfCBlock ) {
								break;
							}
							code = lastCodeAt(i);
							if( code != null 
									&& (code instanceof IfCBlock || code instanceof ElseIfCBlock) ) {
								Block caseBlock = (Block)code;
								Code lastCode = caseBlock.getEndCode().getPrevious();
								if( lastCode instanceof JumpCode ) {
									/** else, else if 일 경우이면 이전의 블럭의 마지막 코드가 GOTO 이다. */
									JumpCode expectEndOfElseJumper = (JumpCode)lastCode;
									int jumpAction = getJumpAction( expectEndOfElseJumper );
									addCodeWriter( cmt("find end of else - not yet. step1") );
									
									Code endOfElseBlock = null;
									
									if( expectEndOfElseJumper.idx > expectEndOfElseJumper.labelCode.idx
										&& expectEndOfElseJumper.labelCode.isLoopType()
										&& !expectEndOfElseJumper.labelCode.getLastAftJumper().isCondition() ) {
										endOfElseBlock = expectEndOfElseJumper.labelCode.getLastAftJumper();
//										System.out.println("1111111"+Util.dumpNode(expectEndOfElseJumper.jmpNode));
									}
									/** 
									 * inblock 이 아니면 else 안둘러도 상관없다.
									 * 이전의 if( else if ) 블럭들이 continue나 break 란 이거나
									 * 하위 처리 스택이 없는것이기 때문(JUMP_CONTINUE)
									 */
									else if( jumpAction == JUMP_AFTERINBLOCK 
//												&& labelCode.idx < lastJumpCode.labelCode.idx ) {
											&& labelCode.idx != expectEndOfElseJumper.labelCode.idx ) {
										
										addCodeWriter( cmt("find end of else - not yet. step2") );
										/**
										 * 1. else 블럭으로 기대되는 블럭의 시작코드가 조건문아 아니면
										 * 2. else 블럭으로 기대되는 블럭의 시작코드가 조건문이면
										 * 
										 */
//										System.out.println("#############################check else : "+expectEndOfElseJumper.labelCode+", "+expectEndOfElseJumper.labelCode.getLoopStackStartLabel());
										if( expectEndOfElseJumper.labelCode.getLoopStackStartLabel() != null ) {
//											System.out.println("#############################");
											endOfElseBlock = expectEndOfElseJumper.labelCode.getLoopStackStartLabel();
//											System.out.println("222222");
										}
										
										else if( !(labelCode.getNext() instanceof JumpCode) ) { 
											endOfElseBlock = expectEndOfElseJumper.labelCode;
											addCodeWriter( cmt("find end of else - ok. next(of start) code is not jumper") );
//											System.out.println("3333333");
										} else {
											/**
											 * 조건문의 점프코드가 기대된 else 블럭의 마지막 코드가 
											 * 아니면 else 확정(그렇지 않을 경우는 else if 일것이다)
											 */
//											JumpAnalyzedResult result = getJumpAnalyzer().analyzeIf(this, (JumpCode)labelCode.getNext());
//											LabelCode expectEndOfLabel = null;
//											if( result.getEndStack() instanceof LabelCode ) {
//												expectEndOfLabel = (LabelCode)result.getEndStack();
//												System.out.println("44444");
//											} else if( result.getEndStack().getPrevious() instanceof LabelCode ) {
//												expectEndOfLabel = (LabelCode)result.getEndStack().getNext();
//												System.out.println("5555555555");
//											}
//											if( result.getEndStack() == endOfElseBlock || result.getEndStack() == expectEndOfElseJumper.labelCode ) {
//												endOfElseBlock = null;
//												System.out.println("66666666");
//											}
											
//											JumpAnalyzedResult result = getJumpAnalyzer().analyzeIf(this, (JumpCode)labelCode.getNext());
											
											
											
											Code inCaseBlock = ((JumpCode)labelCode.getNext()).labelCode.getPrevious();
											if( (inCaseBlock instanceof JumpCode) 
													&& !((JumpCode)inCaseBlock).isCondition() ) {
												endOfElseBlock = null;
											} else {
												endOfElseBlock = expectEndOfElseJumper.labelCode;
											}
										}
										
										if( endOfElseBlock != null ) {
											Block cBlock = createControlBlock(TYPE_ELSE, null, labelCode, endOfElseBlock);
											cBlock.buildCodeStack();
											addCodeWriter( cBlock );
											setFocusCode( endOfElseBlock );
											break;
										}
									}
								} else {
									addCodeWriter( cmt("why last code is not jumper. unknown expect. "+lastCode) );
									continue;
								}
							} else {
								break;
							}
						}
					}
				}
			} else {
				addCodeWriter( procCode );
			}
		}
	}
}
