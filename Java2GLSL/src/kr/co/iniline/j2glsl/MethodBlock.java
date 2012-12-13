package kr.co.iniline.j2glsl;

import java.io.IOException;

import org.objectweb.asm.tree.MethodNode;

public class MethodBlock extends Block {
	String paramNames[] = null;
	MethodNode methodNode = null;
	JumpAnalyzer jumpAnalyzer = null;
	Code startCode, endCode;
	
	public MethodBlock(String paramNames[], MethodNode methodNode, JumpAnalyzer jumpAnal, Code startCode, Code endCode) {
		this.paramNames = paramNames;
		this.methodNode = methodNode;
		this.jumpAnalyzer = jumpAnal;
		this.startCode = startCode;
		this.endCode = endCode;
	}
	
	public MethodNode getMethodNode() {
		return methodNode;
	}
	
	public void writeStartBlock(TabbedWriter writer) throws IOException {
		writer.startFrame();
	}
	
	public void writeEndBlock(TabbedWriter writer) throws IOException {
		writer.endFrame();
	}
	
	public JumpAnalyzer getJumpAnalyzer() {
		return jumpAnalyzer;
	}

	@Override
	protected Block createControlBlock(int type, String condition,
			Code startCode, Code endCode) {
		return ChildBlock.createBlock(this, type, condition, startCode, endCode);
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
		return null;
	}

	@Override
	public void writeBlockStart(TabbedWriter writer) throws IOException {
		writer.write( Util.getMethodDeclaration(methodNode, paramNames) );
		writer.appendNewTabbedLine();
		writer.startFrame();
	}

	@Override
	public void writeBlockEnd(TabbedWriter writer) throws IOException {
		writer.endFrame();
	}

	@Override
	public int getType() {
		return TYPE_MAIN;
	}
}
