package kr.co.iniline.j2glsl;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

public interface CodeIntercepter {
	public boolean shouldReplaceCode(CodeRegister register, MethodNode methodNode, AbstractInsnNode node);
}
