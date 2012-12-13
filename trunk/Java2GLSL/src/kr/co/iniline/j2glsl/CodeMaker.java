package kr.co.iniline.j2glsl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class CodeMaker implements CodeRegister {
	HashMap<Integer,String> namedVar = null; 
	String primitiveTypeString[] = new String[]{"int", "long", "float", "double", "Object"};
	
	int ISTORE = 54; // visitVarInsn
    int LSTORE = 55; // -
    int FSTORE = 56; // -
    int DSTORE = 57; // -
    int ASTORE = 58; // -
	
	InsnList instList = null;
	Code firstCode = null;
	Code focusedCode = null;
	
	HashMap<Label, LabelCode> labelStackMap = new HashMap<Label, LabelCode>();
	
	public CodeMaker() {
		namedVar = new HashMap<Integer,String>();
	}
	
	public void pushCode(Code currCode) {
		if( focusedCode == null ) {
			firstCode = focusedCode = currCode;
		} else {
			focusedCode.setNext( currCode );
			currCode.setPrevious( focusedCode );
			currCode.setNext( null );
			focusedCode = currCode;
		}
	}
	
	public Code popCode() {
		Code rCode = focusedCode;
		focusedCode.setNext( null );
		focusedCode = focusedCode.getPrevious();
		if( focusedCode != null ) {
			focusedCode.setNext( null );
		}
		return rCode;
	}
	
	public String popCodeString() {
		return popCode().toParamString();
	}
	
	public String popCodeStringForCompare() {
		Code code = popCode();
		if( !(code instanceof AssignCode) ) {
			pushCode( code );
		} else {
			if( ((AssignCode)code).isVariable() ) {
				return code.toParamString();
			} else {
				pushCode( code );
			}
		}
		return "0";
	}
	
	public Code getFirstCode() {
		return firstCode;
	}
	
	public Code getCode() {
		return focusedCode;
	}
	
	public void reset() {
		firstCode = null;
		focusedCode = null;
		labelStackMap.clear();
		namedVar.clear();
	}
	
	private String buildString(String... vars) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < vars.length; i++) {
			sb.append( vars[i] );
		}
		return sb.toString();
	}
	
	boolean isIncludeAftStack(JumpCode currCode, Code end) {
		LabelCode checkCode = currCode.labelCode;
		Code code = currCode;
		while( (code = currCode.getNext()) != end ) {
			if( code.equals(checkCode) ) {
				return true;
			}
		}
		return false;
	}
	
	public String getObjectClassType(ArrayList<AbstractInsnNode> instList) {
		for (int i = (instList.size()-1); i >= 0; i--) {
			AbstractInsnNode node = instList.get(i);
			if( node instanceof TypeInsnNode && node.getOpcode() == Opcodes.NEW ) {
				TypeInsnNode typeNode = (TypeInsnNode)node;
				return Util.getClassName( typeNode.desc );
			}
		}
		return "Object";
	}
	
	public String popMethodParamCode(int paramNum) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < paramNum; i++) {
			sb.insert(0, popCodeString());
			if( (i+1) < paramNum ) {
				sb.insert(0, ", ");
			}
		}
		return sb.toString();
	}
	
	public Code codeAssign(boolean isVar, String code) {
		return new AssignCode( isVar, code );
	}
	
	public Code codeAssign(String type, int varIdx, String named, boolean isVar, String code) {
		return new AssignCode(type, varIdx, named, isVar, code);
	}
	
	public Code codeJump(JumpInsnNode jmpNode) {
		Label label = jmpNode.label.getLabel();
		JumpCode jmpCode = null;
		
		int opCode = jmpNode.getOpcode();
		
		if( opCode == Opcodes.GOTO ) {
			jmpCode = new JumpCode( jmpNode );
		} else if( opCode >= Opcodes.IFEQ && opCode <= Opcodes.IFLE ) {
			String val2 = popCodeStringForCompare();
			String val1 = popCodeStringForCompare();
			Condition condString = new Condition(opCode, val1, val2);
			jmpCode = new JumpCode(jmpNode, condString );
		} else if( opCode >= Opcodes.IF_ICMPEQ && opCode <= Opcodes.IF_ACMPNE ) {
			String val2 = popCodeStringForCompare();
			String val1 = popCodeStringForCompare();
			Condition condString = new Condition(opCode, val1, val2);
			jmpCode = new JumpCode(jmpNode, condString);
		} else {
			jmpCode = new JumpCode(jmpNode, null);
		}
		
		LabelCode labelCode = labelStackMap.get( label );
		if( labelCode == null ) {
			labelCode = new LabelCode( jmpNode.label.getLabel() );
			labelCode.switchBefRef();
			labelStackMap.put(label, labelCode);
		}
		labelCode.refJump( jmpCode );
		jmpCode.setLabelCode( labelCode );
		
		return jmpCode;
	}
	
	public Code opLabel(Label label) {
		LabelCode labelCode = labelStackMap.get( label );
		if( labelCode == null ) {
			labelCode = new LabelCode( label );
			labelStackMap.put(label, labelCode);
		}
		return labelCode;
	}
	
	String newVarName(int var, String name) {
		namedVar.put(var, name);
		return name;
	}
	
	String getVarName(int var) {
		return namedVar.get( var );
	}
	
	protected boolean interceptProcess(CodeIntercepter codeIntercepter, MethodNode methodNode, AbstractInsnNode node) {
		if( codeIntercepter != null ) {
			return codeIntercepter.shouldReplaceCode(this, methodNode, node);
		}
		return false;
	}
	
	void checkInst(MethodNode methodNode, ArrayList<AbstractInsnNode> instList, CodeIntercepter codeIntercepter) throws IOException {
		AbstractInsnNode node = instList.get( instList.size()-1 );
		if( interceptProcess(codeIntercepter, methodNode, node) ) {
			return;
		}
		if( node instanceof InsnNode ) {
			InsnNode insnNode = (InsnNode)node;
			int opCode = insnNode.getOpcode();
			if( opCode >= Opcodes.ICONST_0 && opCode <= Opcodes.ICONST_5 ) {
				pushCode( codeAssign( true, String.valueOf( opCode-Opcodes.ICONST_0)) );
			} else if( opCode >= Opcodes.LCONST_0 && opCode <= Opcodes.LCONST_1 ) {
				pushCode( codeAssign( true, String.valueOf( (long)(opCode-Opcodes.LCONST_0)) ) );
			} else if( opCode >= Opcodes.FCONST_0 && opCode <= Opcodes.FCONST_2 ) {
				pushCode( codeAssign( true, String.valueOf( (float)(opCode-Opcodes.FCONST_0)) ) );
			} else if( opCode >= Opcodes.DCONST_0 && opCode <= Opcodes.DCONST_1 ) {
				pushCode( codeAssign( true, String.valueOf( (double)(opCode-Opcodes.DCONST_0)) ) );
			} else if( opCode >= Opcodes.IALOAD && opCode <= Opcodes.SALOAD ) {
				String idx = popCodeString();
				String target = popCodeString();
				pushCode( codeAssign( true, buildString( target, "["+idx+"]") )  );
			} else if( opCode >= Opcodes.IASTORE && opCode <= Opcodes.SASTORE ) {
				String elem = popCodeString();
				String elemPos = popCodeString();
				Code code = popCode();
				if( code instanceof ArrayAssignCode ) {
					ArrayAssignCode arrAsCode = (ArrayAssignCode)code;
					arrAsCode.setType( primitiveTypeString[opCode-Opcodes.IASTORE] );
					arrAsCode.setElement(Integer.valueOf(elemPos), elem);
					pushCode( code );
				} else {
					String param = buildString( code.toParamString(),"[",elemPos,"] = ", elem);
					pushCode( codeAssign(true, param) );
				}
			}
			    
			else if( opCode >= Opcodes.IADD && opCode <= Opcodes.DADD ) {
				String val2 = popCodeString();
				String val1 = popCodeString();
				pushCode( codeAssign( true, buildString("(", val1, "+", val2, ")") ) );
			} else if( opCode >= Opcodes.ISUB && opCode <= Opcodes.DSUB ) {
				String val2 = popCodeString();
				String val1 = popCodeString();
				pushCode( codeAssign( true, buildString("(", val1, "-", val2, ")") ) );
			} else if( opCode >= Opcodes.IMUL && opCode <= Opcodes.DMUL ) {
				String val2 = popCodeString();
				String val1 = popCodeString();
				pushCode( codeAssign( true, buildString("(", val1, "*", val2, ")") ) );
			} else if( opCode >= Opcodes.IDIV && opCode <= Opcodes.DDIV ) {
				String val2 = popCodeString();
				String val1 = popCodeString();
				pushCode( codeAssign( true, buildString("(", val1, "/", val2, ")") ) );
			} else if( opCode >= Opcodes.ISHL && opCode <= Opcodes.LSHL ) {
				String val2 = popCodeString();
				String val1 = popCodeString();
				pushCode( codeAssign( true, buildString("(", val1, "<<", val2, ")") ) );
			} else if( opCode >= Opcodes.ISHR && opCode <= Opcodes.LSHR ) {
				String val2 = popCodeString();
				String val1 = popCodeString();
				pushCode( codeAssign( true, buildString("(", val1, ">>", val2, ")") ) );
			} else if( opCode >= Opcodes.IRETURN && opCode <= Opcodes.RETURN ) {
				if( opCode == Opcodes.RETURN ) {
					pushCode( codeAssign( false, "return") );
				} else {
					pushCode( codeAssign( false, buildString("return ", popCodeString() ) ) );
				}
			} else if( opCode == Opcodes.ACONST_NULL ) {
				pushCode( codeAssign( true, "null") );
			} else if( opCode == Opcodes.POP || opCode == Opcodes.POP2 ) {
//				bind( new JunkCode() ); //prevent pop from jumper
			}
		} else if( node instanceof LdcInsnNode ) {
			LdcInsnNode ldcNode = (LdcInsnNode)node;
			if( ldcNode.cst instanceof String ) {
				pushCode( codeAssign( true, buildString("\"", ldcNode.cst.toString(), "\"") ) );
			} else {
				pushCode( codeAssign( true, ldcNode.cst.toString() ) );
			}
		} else if( node instanceof IntInsnNode ) {
			/** BIPUSH, SIPUSH, NEWARRAY */
			IntInsnNode intNode = (IntInsnNode)node;
			int opCode = intNode.getOpcode();
			if( opCode == Opcodes.NEWARRAY ) {
				pushCode( new ArrayAssignCode(true, Integer.parseInt(popCodeString()), null) );
			} else {
				pushCode( codeAssign( true, String.valueOf(intNode.operand) ) );
			}
		} else if( node instanceof FieldInsnNode ) {
			FieldInsnNode fieldInNode = (FieldInsnNode)node;
			int opCode = fieldInNode.getOpcode();
			if( opCode == Opcodes.GETFIELD ) {
//				pushCode( codeAssign( true, fieldInNode.owner+"."+fieldInNode.name ) );
				String owner = popCodeString();
				if( "this".equals(owner) ) {
					owner = "";
				} else {
					owner += ".";
				}
				pushCode( codeAssign( true, owner+fieldInNode.name ) );
			} else if( opCode == Opcodes.PUTFIELD) {
				String var = popCodeString();
				String varOwner = popCodeString();
				if( "this".equals(varOwner) ) {
					pushCode( codeAssign( true, buildString( fieldInNode.name, " = ", var ) ) );
				} else {
					pushCode( codeAssign( true, buildString( varOwner, ".", fieldInNode.name, " = ", var ) ) );
				}
			} else {
				pushCode( codeAssign( true, fieldInNode.owner+"."+fieldInNode.name ) );
			}
		} else if( node instanceof VarInsnNode ) {
			VarInsnNode varNode = (VarInsnNode)node;
			int opCode = varNode.getOpcode();
			if( opCode >= Opcodes.ILOAD && opCode <= Opcodes.ALOAD ) {
				if( opCode == Opcodes.ALOAD && varNode.var == 0 ) {
					pushCode( codeAssign( true, "this") );
				} else {
					/** 
					 * while( (x+2) > 9 )
					 * =>
						LabelNode::........
						IincInsnNode:IINC:1:5
						VarInsnNode:ILOAD:1
						IntInsnNode:BIPUSH:9
						JumpInsnNode:.......
					 * */
					boolean exceptCode = false;
					if( instList.size() >= 2 ) {
						AbstractInsnNode exNode = instList.get( instList.size()-2 );
						if( exNode instanceof IincInsnNode ) {
							if( ((IincInsnNode) exNode).var == varNode.var ) {
								exceptCode = true;
							}
						}
					}
					if( !exceptCode ) {
						String named = getVarName(varNode.var);
						if( named == null ) {
							named = primitiveTypeString[opCode - Opcodes.ILOAD]+"_"+varNode.var;
						}
						pushCode( codeAssign(true, named) );
					}
				}
			} else if( opCode >= Opcodes.ISTORE && opCode <= Opcodes.ASTORE ) {
				int typeIdx = opCode - Opcodes.ISTORE;
				String type = null;
				if( opCode == Opcodes.ASTORE ) {
					if( getCode() instanceof ArrayAssignCode ) {
						type = ((ArrayAssignCode)getCode()).getType()+"[]";
					} else {
						type = getObjectClassType(instList);
					}
				} else {
					type = primitiveTypeString[opCode - Opcodes.ISTORE];
				}
				String named = getVarName( varNode.var );
				String param = null;
				if( named == null ) {
					named = Util.getVariableName(typeIdx, varNode.var, methodNode, varNode);
					newVarName(varNode.var, named);
					String typeFromDebugInfo = Util.getVariableType(methodNode, varNode);
					if( typeFromDebugInfo != null ) {
						type = typeFromDebugInfo;
					}
					param = buildString( named, " = ", popCodeString() );
					pushCode( codeAssign(type, varNode.var, named, false, param) );
				} else {
					param = buildString( named, " = ", popCodeString() );
					pushCode( codeAssign(false, param) );
				}
			}
		} else if( node instanceof MethodInsnNode ) {
			MethodInsnNode miNode = ((MethodInsnNode)node);
			DescParser descParser = DescParser.parse( miNode.desc );
			int opCode = miNode.getOpcode();
			StringBuilder sb = new StringBuilder();
			if( opCode == Opcodes.INVOKESPECIAL && miNode.name.equals("<init>") ) {
				sb.append( "new ");
				sb.append( Util.getClassName(miNode.owner) );
				sb.append("(");
				sb.append( popMethodParamCode(descParser.paramNum) );
				sb.append(")");
			} else if( opCode == Opcodes.INVOKESTATIC ) {
				String param = popMethodParamCode(descParser.paramNum);
				sb.append( Util.getClassName(miNode.owner) );
				sb.append(".");
				sb.append( miNode.name );
				sb.append("(");
				sb.append( param );
				sb.append(")");
			} else {
				String param = popMethodParamCode(descParser.paramNum);
				String owner = popCodeString();
				if( !owner.equals("this") ) {
					sb.append( Util.getClassName(owner) );
					sb.append( "." );
				}
				
				sb.append( miNode.name );
				sb.append("(");
				sb.append( param );
				sb.append(")");
			}
			if( descParser.getReturnParamSpec().getDataType() == 'V' ) {
				pushCode( codeAssign(false, sb.toString()) );
			} else {
				pushCode( codeAssign(true, sb.toString()) );
			}
		} else if( node instanceof TypeInsnNode ) {
			TypeInsnNode typeNode = (TypeInsnNode)node;
//			sb.append( ((TypeInsnNode)node).desc );
			int opCode = typeNode.getOpcode();
			if( opCode == Opcodes.ANEWARRAY  ) {
				pushCode( new ArrayAssignCode(true, Integer.valueOf(popCodeString()), Util.getClassName(typeNode.desc)) );
			}
//			if( typeNode.)
		} else if( node instanceof LabelNode ) {
			LabelNode lbNode = ((LabelNode)node);
			pushCode( opLabel( lbNode.getLabel() ) );
//			sb.append( lbNode.getLabel().info );
//			sb.append( lbNode.getLabel().getOffset() );
		} else if( node instanceof JumpInsnNode ) {
			JumpInsnNode jmpNode = ((JumpInsnNode)node);
			pushCode( codeJump(jmpNode) );
			
		} else if( node instanceof IincInsnNode ) {
			IincInsnNode iincNode = (IincInsnNode)node;
			if( iincNode.incr == 1 ) {
				pushCode( codeAssign(true, buildString( "(", namedVar.get(iincNode.var), "++", ")") ) );
			} else if( iincNode.incr == -1 ) {
				pushCode( codeAssign(true, buildString( "(", namedVar.get(iincNode.var), "--", ")") ) );
			} else {
				pushCode( codeAssign(true, buildString( "(", namedVar.get(iincNode.var), "+=", String.valueOf(iincNode.incr), ")") ) );
			}
		} else if( node instanceof TableSwitchInsnNode ) {
			TableSwitchInsnNode switchNode = (TableSwitchInsnNode)node;
		} else if( node instanceof LookupSwitchInsnNode ) {
			LookupSwitchInsnNode lswitchNode = (LookupSwitchInsnNode)node;
		}
	}
	
    public void writeCode(Code code, TabbedWriter writer) throws IOException {
		do {
			code.write(writer);
		} while( (code = code.getNext()) != null );
	}
	
    void checkLoopBlock(Block block, JumpAnalyzer jumpAnal) {
    	LabelCode labelCodes[] = labelStackMap.values().toArray(new LabelCode[ labelStackMap.size() ]);
    	for (int i = 0; i < labelCodes.length; i++) {
			labelCodes[i].checkAndBindLastJumper(block, jumpAnal);
		}
    }
    
	private EmptyCode createEmptyCode() {
		EmptyCode code = new EmptyCode();
		return code;
	}
	
	private void mapIdx(Code startCode) {
		Code contCode = startCode;
		int idx = 0;
		do {
			contCode.idx = idx;
			idx ++;
		} while( (contCode = contCode.getNext()) != null );
	}
	
	public MethodBlock analyze(MethodNode methodNode, CodeIntercepter codeIntercepter) throws ShaderCompileException, IOException {
		return analyze(methodNode, codeIntercepter, 0);
	}
	
	public MethodBlock analyze(MethodNode methodNode, CodeIntercepter codeIntercepter, int index) throws ShaderCompileException, IOException {
		ArrayList<AbstractInsnNode> instListBuff = new ArrayList<AbstractInsnNode>();
		
		/**
		 * mapping the call parameter
		 */
		DescParser descParser = DescParser.parse( methodNode.desc );
		int skip = 1;
		
		/** 
		 * static 일 경우 VAR_0 이 this 가 아니므로 0부터 시작
		 * static 이 아닌 경우 VAR_0은 this가 됨. 
		 */
		if( methodNode.access == Opcodes.ACC_STATIC ) {
			skip = 0;
		}
		
		String paramNames[] = new String[descParser.getParamNum()];
		for (int i = 0; i < descParser.getParamNum(); i++) {
			String name = null;
			try {
				name = methodNode.localVariables.get(i+skip).name;
			} catch (Exception e) {
				//there class has no debug info
			}
			if( name == null ) {
				DescParser.ParamSpec pSpec = descParser.getParamSpec(i);
				name = pSpec.getTypeClass().getSimpleName()+"_"+i;
			}
			paramNames[i] = name;
			newVarName( (i+skip), name );
		}
		
		EmptyCode startCode = createEmptyCode();
		pushCode( startCode );
		for(int i=index; i<methodNode.instructions.size(); i++) {
			AbstractInsnNode node = methodNode.instructions.get(i);
			instListBuff.add( node );
			checkInst( methodNode, instListBuff, codeIntercepter );	
		}
		
		EmptyCode endCode = createEmptyCode();
		pushCode( endCode );
		mapIdx( startCode );
		JumpAnalyzer jmpAnal = new JumpAnalyzer();
		MethodBlock mainBlock = new MethodBlock( paramNames, methodNode, jmpAnal, startCode, endCode );
		checkLoopBlock( mainBlock, jmpAnal );
		mainBlock.buildCodeStack();
		return mainBlock;
	}
}
