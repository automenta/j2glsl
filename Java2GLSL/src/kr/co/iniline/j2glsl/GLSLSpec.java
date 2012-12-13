package kr.co.iniline.j2glsl;

public interface GLSLSpec {
	public int glCreateShader(int method);
	public void glShaderSource(int method, CharSequence source);
	public void glCompileShader(int method);
}
