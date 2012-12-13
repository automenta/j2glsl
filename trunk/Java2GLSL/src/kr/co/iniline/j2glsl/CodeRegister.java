package kr.co.iniline.j2glsl;

public interface CodeRegister {
	public Code popCode();
	
	public void pushCode(Code code);
}
