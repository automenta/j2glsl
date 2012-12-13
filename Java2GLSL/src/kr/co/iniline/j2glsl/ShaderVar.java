package kr.co.iniline.j2glsl;

public abstract class ShaderVar {
	public abstract float[] get(String ponits);
	public abstract vec2 get2(String ponits);
	public abstract vec3 get3(String ponits);
	public abstract vec4 get4(String ponits);
	
	public abstract ShaderVar plus(ShaderVar var);
	
	public abstract ShaderVar plus(float var);
	
	public abstract void add(ShaderVar var);
	
	public abstract void add(float var);
	
	public abstract ShaderVar minus(ShaderVar var);
	
	public abstract ShaderVar minus(float var);
	
	public abstract ShaderVar minus();
	
	public abstract void sub(ShaderVar var);
	
	public abstract void sub(float var);
	
	public abstract ShaderVar mult(ShaderVar var);
	
	public abstract ShaderVar mult(float var);
	
	public abstract void multLocal(ShaderVar var);
	
	public abstract void multLocal(float var);
	
	public abstract ShaderVar div(ShaderVar var);
	
	public abstract ShaderVar div(float var);
	
	public abstract void divLocal(ShaderVar var);
	
	public abstract void divLocal(float var);
	
	public abstract float[] toFloats();
}
