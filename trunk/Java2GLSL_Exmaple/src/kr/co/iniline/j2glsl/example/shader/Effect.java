package kr.co.iniline.j2glsl.example.shader;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.io.StringWriter;

import kr.co.iniline.j2glsl.FragmentShader;
import kr.co.iniline.j2glsl.J2GLSLConverter;
import kr.co.iniline.j2glsl.Shader;
import kr.co.iniline.j2glsl.ShaderCompileException;
import kr.co.iniline.j2glsl.VertexShader;

public abstract class Effect {
	final static J2GLSLConverter j2glsl = new J2GLSLConverter();
	
	int shaderPid = -1;
	int vertexId = -1;
	int fragmentId = -1;
	
	public abstract Shader[] getShaders();
	
	public void initShaders() throws ShaderCompileException {
		int v = glCreateShader(GL_VERTEX_SHADER);
		int f = glCreateShader(GL_FRAGMENT_SHADER);
		
		Shader shaders[] = getShaders();
		for (int i = 0; i < shaders.length; i++) {
			StringWriter writer = new StringWriter();
			j2glsl.compile(writer, shaders[i]);
			if( shaders[i] instanceof VertexShader ) {
				glShaderSource(v, writer.toString());
			} else if( shaders[i] instanceof FragmentShader ) {
				glShaderSource(f, writer.toString());
			}
		}
		
		glCompileShader(v);
		glCompileShader(f);
		shaderPid = glCreateProgram();
		glAttachShader(shaderPid,f);
		glAttachShader(shaderPid,v);
	}
	
	public int getProgramId() {
		return shaderPid;
	}
	
	public int getVertexId() {
		return vertexId;
	}
	
	public int getFragmentId() {
		return fragmentId;
	}
	
	public void apply() {
		glLinkProgram(shaderPid);
		glUseProgram(shaderPid);
	}
	
	public abstract void renderReady();
	
	public abstract void renderEnd();
}
