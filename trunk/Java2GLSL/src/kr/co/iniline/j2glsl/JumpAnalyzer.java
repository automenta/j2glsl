package kr.co.iniline.j2glsl;

import java.util.HashMap;
import java.util.HashSet;

import org.objectweb.asm.Opcodes;

public class JumpAnalyzer {
	int markIdxCount = 0;
	
	class JumpAnalyzedResult {
		String comment = null;
		String condition = null;
		Code blockStartCode = null;
		Code blockEndCode = null;
		Code expectNextCode = null;
		
		String getCondition() {
			return condition;
		}
		
		Code getStartStack() {
			return blockStartCode;
		}
		
		Code getEndStack() {
			return blockEndCode;
		}
		
		Code getNextStack() {
			return expectNextCode;
		}
	}
	
	public JumpAnalyzer() {
	}
	
	public JumpAnalyzedResult analyzeLoop(Block block, JumpCode jmpCode) {
		JumpAnalyzedResult result = new JumpAnalyzedResult();
		if( !jmpCode.isCondition() ) {
			result.blockStartCode = jmpCode.labelCode;
			result.blockEndCode = jmpCode;
			result.expectNextCode = jmpCode.getNext();
			result.condition = "true";
			return result;
		}
		markIdxCount = 0;
		Block parentBlock = block;
		HashMap<LabelCode,MarkedJump> validLabelMap = new HashMap<LabelCode,MarkedJump>();
		LabelCode stackLabel = jmpCode.labelCode;
		
		LabelCode breakLabel = null;
		if( jmpCode.getNext() instanceof LabelCode ) {
			breakLabel = (LabelCode)jmpCode.getNext();
		}
		MarkedJump eJump = null;
		MarkedJump contJump = null;
		Code contCode = jmpCode;
		do {
			if( contCode instanceof JumpCode ) {
				jmpCode = (JumpCode)contCode;
				boolean isJunk = (jmpCode.labelCode == jmpCode.getPrevious());
				
				if( jmpCode.isCondition() ) {
					if( eJump == null ) {
						contJump = eJump = new MarkedJump( jmpCode );
						contJump.trueWhich = MarkedJump.J2STACK;
						contJump.falseWhich = MarkedJump.J2END;
					} else {
						MarkedJump tempJump = new MarkedJump( jmpCode );
						tempJump.falseCase = contJump;
						tempJump.falseWhich = MarkedJump.J2J;
						contJump = tempJump;
						
						if( jmpCode.labelCode == stackLabel ) {
							contJump.trueWhich = MarkedJump.J2STACK;
						} else if( jmpCode.labelCode == breakLabel || jmpCode.labelCode.idx > eJump.idx ) {
							contJump.trueWhich = MarkedJump.J2END;
						} else if( validLabelMap.containsKey(jmpCode.labelCode) ) {
							contJump.trueWhich = MarkedJump.J2J;
							contJump.trueCase = validLabelMap.get( jmpCode.labelCode );
						} else if( jmpCode.labelCode == jmpCode.getNext() ) {
							contJump = contJump.falseCase;
							//?????????????????????
							break;
						} else {
							//????????????????????
							contJump = contJump.falseCase;
							break;
						}
					}
					
					if( contCode.getPrevious() instanceof LabelCode ) {
						contJump.headLabel = (LabelCode)contCode.getPrevious();
						validLabelMap.put(jmpCode.labelCode, contJump);
					}
				} else {
					//just stack
					break;
				}
			} else if( contCode instanceof LabelCode ) {
				//ignore
			} else if( contCode instanceof AssignCode ) {
				break;
			}
		} while( (contCode = contCode.getPrevious() ) != null && contCode.idx > parentBlock.getStartCode().idx );
		
		result.blockStartCode = eJump.jmpCode.labelCode;
		result.blockEndCode = contJump.jmpCode;
		result.expectNextCode = eJump.jmpCode.getNext();
		result.condition = analyzeDependecy( contJump );
		
		return result;
	}
	
	public JumpAnalyzedResult analyzeIf(Block block, JumpCode jmpCode) {
		Block parentBlock = block;
		markIdxCount = 0;
		MarkedJump sJump = null;
		MarkedJump eJump = null;
		MarkedJump contJump = null;
		Code actualEndOfBlock = null;
		Code contCode = jmpCode;
		do {
			if( contCode instanceof JumpCode ) {
				jmpCode = (JumpCode)contCode;
				if( jmpCode.isCondition() ) {
					if( sJump == null ) {
						contJump = sJump = new MarkedJump( jmpCode );
					} else {
						MarkedJump tempJump = new MarkedJump( jmpCode );
						contJump.falseWhich = MarkedJump.J2J;
						contJump.falseCase = tempJump;
						contJump = tempJump;
						
						if( contCode.getPrevious() instanceof LabelCode ) {
							contJump.headLabel = (LabelCode)contCode.getPrevious();
						}
					}
					
					boolean isJunk = (jmpCode.labelCode == jmpCode.getNext());
					/** test */
					Code testedEndOfBlock = null;
					if( !isJunk ) {
						/** 
						 * is focused stack label of none condition loop? 
						 * loop 가 아닌 jump 인데도 회귀점프이면 조건없는 loop 문이므로 endCode를 점프코드로 인식시킴.
						 */
						if( jmpCode.labelCode.idx < jmpCode.idx ) {
							if( jmpCode.labelCode.isLoopType() && !jmpCode.labelCode.getLastAftJumper().isCondition() ) {
								testedEndOfBlock = jmpCode.labelCode.getLastAftJumper();
//								System.out.println("$$$$$$$$$$$$$$$$$$$$$$");
							}
//							System.out.println("$$$$$$$$$$$$$$$$$$$$$$"+jmpCode.labelCode.label+jmpCode.labelCode.isLoopType()+", "+jmpCode.labelCode.getLastAftJumper().isCondition());
						}
						
						/**
						 * 일반 jump  
						 */
						else if( jmpCode.labelCode.idx > jmpCode.idx ) {
							/**
							 * break 일 경우?? 
							 * do {
									System.out.println(1);
									if( (x==1 && y ==2) || x==3 ) break;
									System.out.println(2);
								} while(true);
								와 같은 경우 x==2 가 바로 break 점프가 됨. 때문에 실행스택이 break 인 경우인데 break 가 아닌 경우에 조건문일 경우는 확인안됨.
							 */
							if( parentBlock.getJumpAction(jmpCode) == Block.JUMP_BREAK ) {
								contJump.trueWhich = MarkedJump.J2STACK;
							}
							
							/** 
							 * 일반점프이면 정상 if문이지만 점프가 loop 의 조건문을 가리키는것이면 => if{...} while(..not true){...}
							 * is focused loop condition label?
							 * 단 같은 블럭내의 경우에만. 조건 while 문에 있는 경우 일반 if case 임. 때문에 루프시작스택번호가 더 클 경우에만
							 * TODO : 같은 내의 블럭인 경우 STACK 시작문으로 봐야겠지... 아니면 다음 블럭이 loop 인 경우이고 
							 */
							else if( jmpCode.labelCode.getLoopStackStartLabel() != null 
									&& jmpCode.labelCode.getLoopStackStartLabel().idx > jmpCode.idx ) {
//								testedEndOfBlock = jmpCode.labelCode.stackStartLabel;
//								System.out.println("if jump focused loop condition label"+jmpCode.labelCode.stackStartLabel.label);
								LabelCode loopStackStartBlock = jmpCode.labelCode.getLoopStackStartLabel();
								Code testContCode = jmpCode.labelCode.getLoopStackStartLabel();
								/**
								 * test 해본다. 이전 loop 이전 스택들이 junk 문이 아닌 조건나열이라면
								 * 
								 * x == 444 loop 시작문을 가리키고 x==5 가 루프 종결문을 가르켰으니...
								 * else if( x == 444 || y == 5 ) {
										while( x == y ) {
											y--;
											if( x == -2 ) {
												x=2;
												continue;
											} else if( y == 1 ) {
												x=3;
												continue;
											} else if( y == 4 ) {
												x=5;
											}
											z = x+1+y;
										}
									}
								 */
								boolean isContinuousJump = true;
								while( true ) {
									testContCode = testContCode.getPrevious();
									if( testContCode.idx <= jmpCode.idx ) {
										break;
									}
									
									if( testContCode instanceof JumpCode ) {
										JumpCode testJmpCode = (JumpCode)testContCode;
										if( !testJmpCode.isCondition() && testJmpCode.labelCode.getLoopStackStartLabel() == null) {
											isContinuousJump = false;
											break;
										}
										/** isJunk */
										if (  testJmpCode.labelCode == testContCode.getNext()  ) {
											isContinuousJump = false;
										}
									} else if( testContCode instanceof LabelCode ) {
										continue;
									} else if( testContCode instanceof AssignCode ) {
										isContinuousJump = false;
										break;
									}
								}
								
								/** end of block */
								if( !isContinuousJump ) {
//									System.out.println("11111111111111"+Util.dumpNode(jmpCode.jmpNode));
									testedEndOfBlock = jmpCode.labelCode.stackStartLabel;
								} else {
//									System.out.println("2222222222222"+Util.dumpNode(jmpCode.jmpNode));
									contJump.trueWhich = MarkedJump.J2STACK;
								}
							}
							
							else {
								/** is focused end of stack? */
								Code checkCode = jmpCode.labelCode;
								while( (checkCode = checkCode.getPrevious()) != null ) {
									if( checkCode.idx <= jmpCode.idx ) {
										/** this is not focused to end */
//										System.out.println("111111case1");
										break;
									}
									
									if( checkCode instanceof JumpCode ) {
										JumpCode checkJump = (JumpCode)checkCode;
										if( checkJump.jmpNode.getOpcode() == Opcodes.GOTO ) {
											testedEndOfBlock = jmpCode.labelCode;
//											System.out.println("3333333case1");
											break;
										} else if( checkJump.labelCode == checkJump.getNext() ) {
											testedEndOfBlock = jmpCode.labelCode;
//											System.out.println("44444444case1");
											break;
										}
									}
									
									if( checkCode instanceof AssignCode ) {
										testedEndOfBlock = jmpCode.labelCode;
//										System.out.println("2222222case1");
										break;
									}
								}
							}//end of else
							
						}
						
						/**
						 * 2개이상 찾았는데 찾은것이 마지막으로 찾은 endJump의 label 대상과 다르면...
						 */
						if( actualEndOfBlock != null ) {
							if( testedEndOfBlock != null 
									&& testedEndOfBlock != actualEndOfBlock ) {
//								System.out.println("case4");
								break;
							} else {
								contJump.trueWhich = MarkedJump.J2END;
								eJump = contJump;
							}
						} else {
							if( testedEndOfBlock != null ) {
								actualEndOfBlock = testedEndOfBlock;
								contJump.trueWhich = MarkedJump.J2END;
								eJump = contJump;
							} else {
								//nonoe?
							}
						}
					} else {
						contJump.trueWhich = MarkedJump.J2END;
						eJump = contJump;
//						System.out.println("case3");
						break;
					}
					
				} else {
//					System.out.println("case2");
					break;
				}
			} else if( contCode instanceof LabelCode ) {
				//ignore
			} else if( contCode instanceof AssignCode ) {
//				System.out.println("case1");
				break;
			}
		} while( (contCode = contCode.getNext() ) != null && contCode.idx < parentBlock.getEndCode().idx );
		
		/** cutting */
		eJump.falseWhich = MarkedJump.J2STACK;
		eJump.falseCase = null;
		
		/** set stack start label */
		LabelCode stackStartLabel = null;
		if( eJump.jmpCode.getNext() instanceof LabelCode ) {
			stackStartLabel = (LabelCode)eJump.jmpCode.getNext();
		}
		
		contJump = sJump;
		do {
			if( contJump.trueWhich == MarkedJump.NOTMARKED ) {
				LabelCode j2label = contJump.jmpCode.labelCode;
				if( j2label == stackStartLabel ) {
					contJump.trueWhich = MarkedJump.J2STACK;
				} else {
					
					MarkedJump findCheckJump = contJump;
					while( (findCheckJump=findCheckJump.falseCase) != null ) {
						if( j2label == findCheckJump.headLabel ) {
							contJump.trueWhich = MarkedJump.J2J;
							contJump.trueCase = findCheckJump;
							break;
						}
					}
					
				}
				
			}
		} while( (contJump=contJump.falseCase) != null );
		
		JumpAnalyzedResult result = new JumpAnalyzedResult();
		result.blockStartCode = eJump.jmpCode;
		result.condition = analyzeDependecy( sJump );
		if( actualEndOfBlock == null ) {
			if( eJump.jmpCode.labelCode.idx < eJump.jmpCode.idx ) {
				if( eJump.jmpCode.labelCode.stackStartLabel != null ) {
					result.blockEndCode = eJump.jmpCode.labelCode.stackStartLabel;
					result.expectNextCode = eJump.jmpCode.labelCode.stackStartLabel;
					System.out.println("!!!!!!!!!!!!!!!!!");
				}
				System.out.println("???????????????????????????????????????????"+Util.dumpNode(eJump.jmpCode.jmpNode)+","+eJump.jmpCode.labelCode.label);
			}
			
		} else {
			result.blockEndCode = actualEndOfBlock;
			result.expectNextCode = actualEndOfBlock;
		}
//		System.out.println( "--------------" );
//		System.out.println( result.blockStartCode );
//		System.out.println( ((LabelCode)result.blockEndCode).label );
		return result;
	}
	
	String analyzeDependecy(MarkedJump mJump) {
		StringBuilder sb = new StringBuilder();
		
		do {
			/** check start stack */
			if( mJump.trueWhich == MarkedJump.J2J ) {
				mJump.jmpCode.caseString.setReverse(true);
				sb.append( mJump.jmpCode.caseString.toString() );
				if( mJump.falseCase != null ) {
					sb.append(" && ");
				}
			} 
			else if( mJump.trueWhich == MarkedJump.J2STACK ) {
				mJump.jmpCode.caseString.setReverse(false);
				sb.append( mJump.jmpCode.caseString.toString() );
				if( mJump.falseCase != null ) {
					sb.append(" || ");
				}
			} 
			else if( mJump.trueWhich == MarkedJump.J2END ) {
				mJump.jmpCode.caseString.setReverse(true);
				sb.append( mJump.jmpCode.caseString.toString() );
				if( mJump.falseCase != null ) {
					sb.append(" && ");
				}
			} else {
				//??????
				sb.append(" ??"+mJump.jmpCode.caseString.toString()+"?? ");
			}
		} while( (mJump = mJump.falseCase) != null );
		if( sb.length() == 0 ) {
			sb.append("true");
		}
		return sb.toString();
	}
	
	class JumpGroup {
		int jumpNum = 0;
		JumpGroup next = null;
	}
	
	class MarkedJump {
		static final int NOTMARKED = 0;
		static final int J2STACK = 2;
		static final int J2J = 1;
		static final int J2END = 3;
		
		JumpCode jmpCode = null;
		
		int idx = 0;
		int trueWhich = NOTMARKED;
		int falseWhich = NOTMARKED;
		
		LabelCode headLabel = null;
		MarkedJump falseCase = null;
		MarkedJump trueCase = null;
		
		MarkedJump(JumpCode jmpCode) {
			this.idx = markIdxCount++;
			this.jmpCode = jmpCode;
		}
	}
	
	boolean isJunkJump(JumpCode jmpCode) {
		/**
		 * like
		 * if( ... ) {//no stack} or while( ... ) {//no stack}
		 */
		if( jmpCode.labelCode == jmpCode.getNext() || jmpCode.labelCode == jmpCode.getPrevious() ) {
			return true;
		}
		return false;
	}
	
	class InvalidIfCaseJumpException extends Exception {
		public InvalidIfCaseJumpException(String msg) {
			super( msg );
		}
	}
	
	boolean isStackLoop(JumpCode jmpCode, LabelCode stackLabel) throws InvalidIfCaseJumpException {
		if( jmpCode.idx > jmpCode.labelCode.idx ) {
			throw new InvalidIfCaseJumpException("This jmpcode is loop");
		}
		if( isJunkJump( jmpCode ) ) {
			return true;
		}
		Code checkCode = jmpCode.labelCode;
		while( (checkCode = checkCode.getNext()) != null ) {
			if( checkCode.idx >= jmpCode.idx ) {
				return false;
			}
			
			if( checkCode instanceof JumpCode ) {
				if( ((JumpCode) checkCode).jmpNode.getOpcode() == Opcodes.GOTO ) {
					return true;
				} else if( isJunkJump(jmpCode) ) {
					return true;
				}
			}
			
			if( checkCode instanceof AssignCode ) {
				return true;
			}
		}
		return false;
	}
}
