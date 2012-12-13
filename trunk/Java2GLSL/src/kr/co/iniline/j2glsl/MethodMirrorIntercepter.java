package kr.co.iniline.j2glsl;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class MethodMirrorIntercepter implements CodeIntercepter {
	HashMap<Class,MirrorIntercepter> mirrorImpled = null;
	
	public interface MirrorIntercepter {
		public void setCodeRegister(CodeRegister codeReg);
	}
	
	public MethodMirrorIntercepter() {
		mirrorImpled = new HashMap<Class, MirrorIntercepter>();
	}
	
	public void addIntercepter(Class cls, MirrorIntercepter impled) {
		do {
			if( java.lang.Object.class.getName().equals(cls.getName()) ) {
				break;
			}
			mirrorImpled.put(cls, impled);
		} while( (cls=cls.getSuperclass()) != null );
	}

	@Override
	public boolean shouldReplaceCode(CodeRegister register, MethodNode methodNode,
			AbstractInsnNode node) {
		if( node instanceof MethodInsnNode ) {
			MethodInsnNode miNode = (MethodInsnNode)node;
			DescParser descParser = DescParser.parse( miNode.desc );
			if( descParser.isMethod && !miNode.name.equals("<init>") ) {
				
				String loadClassName = miNode.owner.replace("/", ".");
				try {
					Class clazz = getClass().getClassLoader().loadClass( loadClassName );
					MirrorIntercepter impled = null;
					do {
						impled = mirrorImpled.get( clazz );
						clazz = clazz.getSuperclass();
						if( clazz == null || java.lang.Object.class == clazz ) {
							break;
						}
					} while( impled == null );
					if( impled != null ) {
						impled.setCodeRegister(register);
						Class paramClasses[] = descParser.getParamClasses();
						Method method = null;
						try {
							method = impled.getClass().getMethod(miNode.name, paramClasses);
						} catch (Exception e) {
							/** retry superClass */
							for (int i = 0; i < paramClasses.length; i++) {
								if( !paramClasses[i].getName().startsWith("java") ) {
									paramClasses[i] = paramClasses[i].getSuperclass();
								}
							}
							method = impled.getClass().getMethod(miNode.name, paramClasses);
						}
						
						method.invoke(impled, makeJunkParams(paramClasses));
						return true;
					}
				} catch (Exception e) {
//					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	Object[] makeJunkParams(Class classes[]) {
		Object paramObjs[] = new Object[classes.length];
		for (int i = 0; i < paramObjs.length; i++) {
			if( classes[i] == int.class ) {
				paramObjs[i] = new Integer(-1);
			} else if( classes[i] == short.class ) {
				paramObjs[i] = new Short((short)-1);
			} else if( classes[i] == byte.class ) {
				paramObjs[i] = new Byte((byte)-1);
			} else if( classes[i] == char.class ) {
				paramObjs[i] = new Character((char)-1);
			} else if( classes[i] == float.class ) {
				paramObjs[i] = new Float(0.0);
			} else if( classes[i] == double.class ) {
				paramObjs[i] = new Double(0.0);
			} else if( classes[i] == boolean.class ) {
				paramObjs[i] = new Boolean(false);
			} else {
				paramObjs[i] = null;
			}
		}
		return paramObjs;
	}
}
