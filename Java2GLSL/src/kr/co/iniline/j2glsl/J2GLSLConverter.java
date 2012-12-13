package kr.co.iniline.j2glsl;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import kr.co.iniline.j2glsl.DescParser.ParamSpec;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class J2GLSLConverter {
	CodeMaker instAnal = null;
	DefaultGLSLCodeFilter generalFilter = null;
	MultipleIntercepter codeIntercepter = null;
	
	public J2GLSLConverter() {
		init();
	}
	
	protected void init() {
		generalFilter = new DefaultGLSLCodeFilter();
		codeIntercepter = new MultipleIntercepter();
		instAnal = new CodeMaker();
		MethodMirrorIntercepter miIntercepter = new MethodMirrorIntercepter();
		miIntercepter.addIntercepter(ShaderVar.class, new ShaderVarOperandOverrider());
		addCodeIntercepter( miIntercepter );
		addCodeIntercepter( generalFilter );
	}

	public void addCodeIntercepter(CodeIntercepter item) {
		codeIntercepter.add( item );
	}
	
	public void removeCodeIntercepter(CodeIntercepter item) {
		codeIntercepter.remove( item );
	}
	
	public boolean debugOpCodeStack = false;
	
	protected void writeMethodBlock(TabbedWriter writer, MethodNode node, MethodBlockWriterIntercepter filter) throws IOException, ShaderCompileException {
		if( filter == null ) {
			if( !generalFilter.isInclude(node) ) {
				return;
			}
		} else if( !filter.isInclude(node) ) {
			return;
		}
		
		writer.appendNewTabbedLine();
		
		if( debugOpCodeStack ) {
			for (int i = 0; i < node.instructions.size(); i++) {
				AbstractInsnNode inNode = node.instructions.get(i);
				writer.appendNewTabbedLine();
				writer.append( Util.dumpNode(node, inNode) );
			}
		}
		
		try {
			instAnal.reset();
			MethodBlock block = instAnal.analyze(node, codeIntercepter);
			if( filter == null ) {
				generalFilter.write(block, writer);
			} else {
				filter.write( block, writer);
			}
		} catch (Exception e) {
			e.printStackTrace();
			for (int i = 0; i < node.instructions.size(); i++) {
				AbstractInsnNode inNode = node.instructions.get(i);
				writer.appendNewTabbedLine();
				writer.append( Util.dumpNode(node, inNode) );
			}
		}
	}
	
	public void compile(Writer writer, Shader shader) throws ShaderCompileException {
		compile(writer, shader, null);
	}
	
	public void compile(Writer writer, Shader shader, MethodBlockWriterIntercepter filter) throws ShaderCompileException {
		try {
			TabbedWriter frameWriter = new TabbedWriter(writer);
			ClassReader cl = new ClassReader( shader.getClass().getName() );
			ClassNode cln = new ClassNode();
			
			cl.accept(cln, 0);
//			cl.accept(cln, ClassReader.SKIP_DEBUG|ClassReader.SKIP_FRAMES);
			
			/** write define vars */
			for(FieldNode field : cln.fields) {
//				System.out.println( field.name +", "+field.desc+", "+field.value);
			}
			
			/** write methods */
			for (MethodNode mt : cln.methods) {
				writeMethodBlock( frameWriter, mt, filter );
				frameWriter.appendNewTabbedLine();
				frameWriter.appendNewTabbedLine();
			}
		} catch (IOException e) {
			throw new ShaderCompileException("IOException : "+e.getMessage());
		}
	}
	
	public interface MethodBlockWriterIntercepter {
		public boolean isInclude(MethodNode methodNode);
		public void write(MethodBlock block, TabbedWriter writer) throws ShaderCompileException, IOException;
	}
}
