package kr.co.iniline.j2glsl;


import java.io.IOException;

import kr.co.iniline.j2glsl.J2GLSLConverter.MethodBlockWriterIntercepter;
import kr.co.iniline.j2glsl.Shader.varying;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class DefaultGLSLCodeFilter implements CodeIntercepter, MethodBlockWriterIntercepter {
	@Override
	public boolean shouldReplaceCode(CodeRegister register, MethodNode methodNode, AbstractInsnNode node) {
		if( "<init>".equals(methodNode.name) ) {
			if( node.getOpcode() == Opcodes.GETFIELD ) {
				FieldInsnNode fieldInNode = (FieldInsnNode)node;
				String varType = null;
				String ownerName = fieldInNode.owner.replace("/", ".");
				if( Shader.varying.getClass().getName().equals(ownerName) ) {
					varType = "varying";
				} else if( Shader.attribute.getClass().getName().equals(ownerName) ) {
					varType = "attribute";
				} else if( Shader.uniform.getClass().getName().equals(ownerName) ) {
					varType = "uniform";
				}
				
				
				
				if( varType != null ) {
					AbstractInsnNode nextNode = node.getNext();
					if( nextNode instanceof LabelNode ) {
						nextNode = nextNode.getNext();
					}
					if ( nextNode.getOpcode() == Opcodes.PUTFIELD ) {
						FieldInsnNode shaderMbNode = (FieldInsnNode)nextNode;
						StringBuilder sb = new StringBuilder();
						sb.append(varType);
						sb.append(" ");
						if( fieldInNode.name.equals("_float") ) {
							sb.append("float");
						} else {
							sb.append(fieldInNode.name);
						}
						
						sb.append(" ");
						sb.append(shaderMbNode.name);
						register.pushCode( new AssignCode(false, sb.toString()) );
					}
				}
			}
			return true;
		}
		
		if( node instanceof MethodInsnNode ) {
			Code prevCode = register.popCode();
			if( prevCode instanceof ArrayAssignCode ) {
				ArrayAssignCode arrCode = (ArrayAssignCode)prevCode;
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < arrCode.length(); i++) {
					sb.append( arrCode.getElement(i) );
					if( (i+1) < arrCode.length() ) {
						sb.append(", ");
					}
				}
				register.pushCode( new AssignCode(true, sb.toString()) );
				return false;
			}
			register.pushCode( prevCode );
		}
		
		return false;
	}

	@Override
	public void write(MethodBlock block, TabbedWriter writer) throws ShaderCompileException, IOException {
		// TODO Auto-generated method stub
		if( block.getMethodNode().name.equals("<init>") ) {
			block.writeContent(writer);
		} else {
			DescParser descParser = DescParser.parse( block.getMethodNode().desc );
			if( descParser.getReturnParamSpec().dataType == 'V' ) {
				Code contPrevCode = block.getEndCode();
				do {
					if( contPrevCode instanceof EmptyCode || contPrevCode instanceof LabelCode ) {
						continue;
					} else if( contPrevCode instanceof AssignCode && contPrevCode.toParamString().equals("return")) {
						((AssignCode)contPrevCode).setWritable(false);
						break;
					} else {
						break;
					}
				} while( (contPrevCode=contPrevCode.getPrevious()) != null );
			}
			block.write(writer);
		}
	}
	
	public boolean isInclude(MethodNode methodNode) {
		return true;
	}
}
