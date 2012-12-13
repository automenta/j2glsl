package kr.co.iniline.j2glsl;

import java.util.ArrayList;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

class MultipleIntercepter implements CodeIntercepter {
	ArrayList<CodeIntercepter> ciList = null;
	
	public MultipleIntercepter() {
		ciList = new ArrayList<CodeIntercepter>();
	}
	
	public void add(CodeIntercepter item) {
		ciList.add( item );
	}
	
	public void remove(CodeIntercepter item) {
		ciList.remove(item);
	}
	
	@Override
	public boolean shouldReplaceCode(CodeRegister register, MethodNode methodNode,
			AbstractInsnNode node) {
		for (CodeIntercepter item : ciList) {
			if( item.shouldReplaceCode(register, methodNode, node) ) {
				return true;
			}
		}
		return false;
	}
}
