package kr.co.iniline.j2glsl;

import java.util.ArrayList;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

public class InstStack {
	int instStartFocus = 0;
	int currentFocus = 0;
	ArrayList<AbstractInsnNode> inStack = null;
	
	public InstStack(InsnList insnList) {
		inStack = new ArrayList<AbstractInsnNode>();
	}
	
	public void addStack(AbstractInsnNode stack) {
		inStack.add( stack );
	}
	
	public void addStack(int index, AbstractInsnNode stack) {
		inStack.add(index, stack);
	}
	
	public AbstractInsnNode nextStack() {
		if( currentFocus >= inStack.size() ) {
			return null;
		}
		AbstractInsnNode rStack = inStack.get(currentFocus);
		currentFocus++;
		return rStack;
	}
	
	public void removeOldStack() {
		for (int i = 0; i < currentFocus; i++) {
			inStack.remove(0);
		}
		currentFocus = 0;
	}
	
	public static void main(String args[]) {
		ArrayList list = new ArrayList();
		list.add("1");
		list.add("2");
		list.remove(0);
		System.out.println( list );
	}
}
