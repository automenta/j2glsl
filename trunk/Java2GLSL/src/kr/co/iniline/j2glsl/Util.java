package kr.co.iniline.j2glsl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import kr.co.iniline.j2glsl.DescParser.ParamSpec;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.util.Printer;

public class Util {
	private static String primitiveTypeString[] = new String[]{"int", "long", "float", "double", "Object"};
	
	public static boolean isCondition(Code code) {
		if( code instanceof JumpCode ) {
			return ((JumpCode) code).isCondition();
		}
		return false;
	}
	
	public static String getCmpSign(int opCode, boolean reverse) {
		String cmpSign = "??";
		if( opCode == Opcodes.GOTO ) {
			cmpSign = "GOTO";
		}
		if( !reverse ) {
			if( opCode == Opcodes.IFEQ ) {
				cmpSign = "==";
			} else if( opCode == Opcodes.IFNE ) {
				cmpSign = "!=";
			} else if( opCode == Opcodes.IFLT ) {
				cmpSign = "<";
			} else if( opCode == Opcodes.IFGE ) {
				cmpSign = ">=";
			} else if( opCode == Opcodes.IFGT ) {
				cmpSign = ">";
			} else if( opCode == Opcodes.IFLE ) {
				cmpSign = "<=";
			} else if( opCode == Opcodes.IF_ICMPEQ ) {
				cmpSign = "==";
			} else if( opCode == Opcodes.IF_ICMPNE ) {
				cmpSign = "!=";
			} else if( opCode == Opcodes.IF_ICMPLT ) {
				cmpSign = "<";
			} else if( opCode == Opcodes.IF_ICMPGE ) {
				cmpSign = ">=";
			} else if( opCode == Opcodes.IF_ICMPGT ) {
				cmpSign = ">";
			} else if( opCode == Opcodes.IF_ICMPLE ) {
				cmpSign = "<=";
			} else if( opCode == Opcodes.IF_ACMPEQ ) {
				cmpSign = "==";
			} else if( opCode == Opcodes.IF_ACMPNE ) {
				cmpSign = "!=";
			}
		} else {
			if( opCode == Opcodes.IFEQ ) {
				cmpSign = "!=";
			} else if( opCode == Opcodes.IFNE ) {
				cmpSign = "==";
			} else if( opCode == Opcodes.IFLT ) {
				cmpSign = ">=";
			} else if( opCode == Opcodes.IFGE ) {
				cmpSign = "<";
			} else if( opCode == Opcodes.IFGT ) {
				cmpSign = "<=";
			} else if( opCode == Opcodes.IFLE ) {
				cmpSign = ">";
			} else if( opCode == Opcodes.IF_ICMPEQ ) {
				cmpSign = "!=";
			} else if( opCode == Opcodes.IF_ICMPNE ) {
				cmpSign = "==";
			} else if( opCode == Opcodes.IF_ICMPLT ) {
				cmpSign = ">=";
			} else if( opCode == Opcodes.IF_ICMPGE ) {
				cmpSign = "<";
			} else if( opCode == Opcodes.IF_ICMPGT ) {
				cmpSign = "<=";
			} else if( opCode == Opcodes.IF_ICMPLE ) {
				cmpSign = ">";
			} else if( opCode == Opcodes.IF_ACMPEQ ) {
				cmpSign = "!=";
			} else if( opCode == Opcodes.IF_ACMPNE ) {
				cmpSign = "==";
			}
		}
		
		return cmpSign;
	}
	
	static String dumpNode(AbstractInsnNode node) {
		return dumpNode( null, node );
	}
	
	static String dumpNode(MethodNode methodNode, AbstractInsnNode node) {
		StringBuffer sb = new StringBuffer();
		sb.append( node.getClass().getSimpleName() );
		sb.append(":");
		if( node.getOpcode() != -1 ) {
			sb.append( Printer.OPCODES[node.getOpcode()]);
		}
		
		sb.append(":");
		if( node instanceof InsnNode ) {
			InsnNode insnNode = (InsnNode)node;
		} else if( node instanceof LdcInsnNode ) {
			sb.append( String.valueOf( ((LdcInsnNode) node).cst ) );
		} else if( node instanceof IntInsnNode ) {
			sb.append( ((IntInsnNode)node).operand );
		} else if( node instanceof FieldInsnNode ) {
			FieldInsnNode fieldInNode = (FieldInsnNode)node;
			sb.append( fieldInNode.owner+"."+fieldInNode.name );
		} else if( node instanceof VarInsnNode ) {
			VarInsnNode varNode = (VarInsnNode)node;
			sb.append( varNode.var );
			sb.append(":");
			int opCode = varNode.getOpcode();
			
			if( opCode >= Opcodes.ILOAD && opCode <= Opcodes.ALOAD ) {
				int typeIdx = varNode.getOpcode() - Opcodes.ILOAD;
				if( methodNode != null ) {
					sb.append( getVariableName(typeIdx, varNode.var, methodNode, varNode));
				} else {
					sb.append( varNode.var );
				}
			} else if( opCode >= Opcodes.ISTORE && opCode <= Opcodes.ASTORE ) {
				int typeIdx = varNode.getOpcode() - Opcodes.ISTORE;
				if( methodNode != null ) {
					sb.append( getVariableName(typeIdx, varNode.var, methodNode, varNode));
				} else {
					sb.append( varNode.var );
				}
			}
		} else if( node instanceof MethodInsnNode ) {
			MethodInsnNode miNode = ((MethodInsnNode)node);
			
			sb.append( miNode.owner+"."+miNode.name+"("+miNode.desc+")" );
		} else if( node instanceof TypeInsnNode ) {
			TypeInsnNode typeNode = (TypeInsnNode)node;
			sb.append( ((TypeInsnNode)node).desc );
		} else if( node instanceof InsnNode ) {
//			sb.append( ((InsnNode)node). )
		} else if( node instanceof LabelNode ) {
			LabelNode lbNode = ((LabelNode)node);
			sb = new StringBuffer( "=> "+lbNode.getLabel().toString() );
		} else if( node instanceof JumpInsnNode ) {
			JumpInsnNode jmpNode = ((JumpInsnNode)node);
			sb.append( getCmpSign(node.getOpcode(), false) );
			sb.append( "(");
			sb.append( getCmpSign(node.getOpcode(),true));
			sb.append( ")");
			sb.append( " ->");
			sb.append( jmpNode.label.getLabel() );
		} else if( node instanceof IincInsnNode ) {
			IincInsnNode iincNode = (IincInsnNode)node;
			sb.append( iincNode.var+":"+iincNode.incr ); 
		} else if( node instanceof FrameNode ) {
			FrameNode frameNode = (FrameNode)node;
			sb.append( frameNode.stack );
		}
		return sb.toString();
	}
	
	public static String getSimpleNaming(int typeIdx, int varCount) {
		return primitiveTypeString[typeIdx]+"_"+varCount;
	}
	
	public static String getVariableType(MethodNode methodNode, VarInsnNode varNode) {
		LocalVariableNode locVarNode = getLocalVariableNode(varNode, methodNode);
		if( locVarNode != null ) {
			try {
				DescParser descParser = DescParser.parse( locVarNode.desc );
				DescParser.ParamSpec pSpec = descParser.getParamSpec(0);
				return Util.getClassName( pSpec.toString() );
			} catch (Exception e) {
				e.printStackTrace();
				return locVarNode.desc;
			}
		} else {
			return null;
		}
	}
	
	public static String getVariableName(int typeIdx, int var, MethodNode methodNode, VarInsnNode varNode) {
		LocalVariableNode locVarNode = getLocalVariableNode(varNode, methodNode);
		if( locVarNode != null ) {
			return locVarNode.name;
		} else {
			try {
				return getSimpleNaming(typeIdx, var);
			} catch (Exception e) {
				return "unknown";
			}
		}
	}
	
    private static LocalVariableNode getLocalVariableNode(VarInsnNode varInsnNode, MethodNode methodNode) {
        int varIdx = varInsnNode.var;
        int instrIdx = getInstrIndex(varInsnNode);
        if( instrIdx == -1 ) {
        	return null;
        }
        List<?> localVariables = methodNode.localVariables;
        
        for (int idx = 0; idx < localVariables.size(); idx++) {
            LocalVariableNode localVariableNode = (LocalVariableNode) localVariables.get(idx);
            if (localVariableNode.index == varIdx) {
                int scopeEndInstrIndex = getInstrIndex(localVariableNode.end);
                if (scopeEndInstrIndex >= instrIdx) {
                    // still valid for current line
                    return localVariableNode;
                }
            }
        }
        return null;
//        throw new RuntimeException("Variable with index " + varIdx + " and scope end >= " + instrIdx
//                + " not found for method " + methodNode.name + "!");
    }
    
    private static int getInstrIndex(AbstractInsnNode insnNode) {
        try {
            Field indexField = AbstractInsnNode.class.getDeclaredField("index");
            indexField.setAccessible(true);
            Object indexValue = indexField.get(insnNode);
            return ((Integer) indexValue).intValue();
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }
    
    public static String getClassName(String fullName) {
    	int idx = fullName.lastIndexOf("/");
    	if( idx != -1 ) {
    		return fullName.substring(idx+1);
    	} else {
    		return fullName;
    	}
    }
    
//    public static String getMethodDeclaration(MethodNode node) throws IOException {
//		StringBuilder sb = new StringBuilder();
//		DescParser descParser = DescParser.parse( node.desc );
//		ParamSpec rParam = descParser.getReturnParamSpec();
//		switch( rParam.getDataType() ) {
//		case 'L' :
//			sb.append( rParam.getClassName() );
//			break;
//		case 'I' :
//			sb.append("int");
//			break;
//		case 'S' :
//			sb.append("short");
//			break;
//		case 'B' :
//			sb.append("byte");
//			break;
//		case 'C' :
//			sb.append("char");
//			break;
//		case 'F' :
//			sb.append("float");
//			break;
//		case 'D' :
//			sb.append("double");
//			break;
//		case 'Z' :
//			sb.append("boolean");
//			break;
//		case 'V' :
//			sb.append("void");
//			break;
//		default :
//			throw new RuntimeException("Unknown method return type : "+rParam.getDataType());
//		}
//		sb.append(" ");
//		sb.append(node.name);
//		sb.append("( ");
//		for (int i = 0; i < descParser.getParamNum(); i++) {
//			sb.append( descParser.getParamSpec(i).toString() );
//			sb.append( " " );
////			sb.append( paramNames[i] );
//			sb.append( "var_"+i );
//			if( (i+1)<descParser.getParamNum() ) sb.append(", ");
//		}
//		sb.append(" )");
//		return sb.toString();
//	}
    
    public static String getMethodDeclaration(MethodNode node, String paramNames[]) throws IOException {
		StringBuilder sb = new StringBuilder();
		DescParser descParser = DescParser.parse( node.desc );
		sb.append( descParser.getReturnParamSpec().getTypeClass().getSimpleName() );
		sb.append(" ");
		sb.append(node.name);
		sb.append("( ");
		for (int i = 0; i < descParser.getParamNum(); i++) {
			
			sb.append( descParser.getParamSpec(i).getTypeClass().getSimpleName() );
			sb.append( " " );
			if( paramNames != null && paramNames.length > i) {
				sb.append( paramNames[i] );
			} else {
				sb.append( "var_"+i );
			}
			if( (i+1)<descParser.getParamNum() ) sb.append(", ");
		}
		sb.append(" )");
		return sb.toString();
	}
}
