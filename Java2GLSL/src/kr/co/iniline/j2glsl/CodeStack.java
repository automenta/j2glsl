package kr.co.iniline.j2glsl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class CodeStack {
	TabbedWriter writer = null;
	ArrayList<String> inStack = null;
	HashMap<Integer, Object> vars = null;
	
	public CodeStack() {
		inStack = new ArrayList<String>();
		vars = new HashMap<Integer, Object>();
	}
	
	public CodeStack(TabbedWriter writer) {
		this();
		setFrameWriter(writer);
	}
	
	public void setFrameWriter(TabbedWriter writer) {
		this.writer = writer;
	}
	
	protected void addAcVar(String stack) {
//		vars.put(vc, val);
		inStack.add( stack );
	}
	
	protected void addLocalVar(int vc, Object val) {
		vars.put(vc, val);
	}
	
	public void finishUnstableLine() {
	}
	
	public void addStack(AbstractInsnNode stack) throws IOException {
		if( stack instanceof InsnNode ) {
			int opCode = stack.getOpcode();
			if( opCode >= Opcodes.ICONST_0 && opCode <= Opcodes.ICONST_5 ) {
				addAcVar( String.valueOf(opCode - Opcodes.ICONST_0) );
			} else if( opCode >= Opcodes.LCONST_0 && opCode <= Opcodes.LCONST_1 ) {
				addAcVar( String.valueOf( (long)(opCode - Opcodes.LCONST_0) ) );
			} else if( opCode >= Opcodes.FCONST_0 && opCode <= Opcodes.FCONST_2 ) {
				addAcVar( String.valueOf( (float)(opCode - Opcodes.FCONST_0) ) );
			} else if( opCode >= Opcodes.DCONST_0 && opCode <= Opcodes.DCONST_1 ) {
				addAcVar( String.valueOf( (double)(opCode - Opcodes.DCONST_0) ) );
			} 
		} else if( stack instanceof IntInsnNode ) {
			addAcVar( String.valueOf(((IntInsnNode)stack).operand) );
		} else if( stack instanceof LdcInsnNode ) {
			LdcInsnNode ldcNode = ((LdcInsnNode)stack);
			if( ldcNode.cst instanceof String ) {
				addAcVar( "\""+String.valueOf(ldcNode.cst)+"\"" );
			} else {
				addAcVar( String.valueOf(ldcNode.cst) );
			}
		} else if( stack instanceof VarInsnNode ) {
			VarInsnNode varInNode = ((VarInsnNode)stack);
			switch( stack.getOpcode() ) {
			case Opcodes.ISTORE :
				writeISTORE( varInNode );
				break;
			case Opcodes.LSTORE :
				writeLSTORE( varInNode );
				break;
			case Opcodes.FSTORE :
				writeFSTORE( varInNode );
				break;
			case Opcodes.DSTORE :
				writeDSTORE( varInNode );
				break;
			case Opcodes.ASTORE :
				writeASTORE( varInNode );
				break;
			case Opcodes.ILOAD :
				addAcVar("int_"+varInNode.var);
				break;
			case Opcodes.LLOAD :
				addAcVar("long_"+varInNode.var);
				break;
			case Opcodes.FLOAD :
				addAcVar("float_"+varInNode.var);
				break;
			case Opcodes.DLOAD :
				addAcVar("double_"+varInNode.var);
				break;
			case Opcodes.ALOAD :
				addAcVar("obj_"+varInNode.var);
				break;
			}
		}
	}
	
	protected void writeISTORE(VarInsnNode stack) throws IOException {
		writer.appendNewTabbedLine();
		writer.append("int int_"+stack.var+" = "+getAcArgs());
		writer.append(";");
		inStack.clear();
	}
	
	protected void writeLSTORE(VarInsnNode stack) throws IOException {
		writer.appendNewTabbedLine();
		writer.append("long long_"+stack.var+" = "+getAcArgs());
		writer.append(";");
		inStack.clear();
	}
	
	protected void writeFSTORE(VarInsnNode stack) throws IOException {
		writer.appendNewTabbedLine();
		writer.append("float float_"+stack.var+" = "+getAcArgs());
		writer.append(";");
		inStack.clear();
	}
	
	protected void writeDSTORE(VarInsnNode stack) throws IOException {
		writer.appendNewTabbedLine();
		writer.append("double double_"+stack.var+" = "+getAcArgs());
		writer.append(";");
		inStack.clear();
	}
	
	protected void writeASTORE(VarInsnNode stack) throws IOException {
		writer.appendNewTabbedLine();
		writer.append("Object obj_"+stack.var+" = "+getAcArgs());
		writer.append(";");
		inStack.clear();
	}
	
	protected void writeBASTORE(VarInsnNode stack) throws IOException {
		writer.appendNewTabbedLine();
		writer.append("byte[] bytes_"+stack.var+" = "+getAcArgs());
		writer.append(";");
		inStack.clear();
	}
	
	protected String getAcArgs() {
		StringBuilder sb = new StringBuilder();
		
		String args[] = inStack.toArray(new String[inStack.size()]);
		for (int i = 0; i < args.length; i++) {
			if( i>0 ) {
				sb.append(", ");
			}
			sb.append( args[i] );
		}
		return sb.toString();
	}
	
	protected void clearAcArgs() {
		inStack.clear();
	}
	
	public static void main(String[] args) {
		
	}
}
