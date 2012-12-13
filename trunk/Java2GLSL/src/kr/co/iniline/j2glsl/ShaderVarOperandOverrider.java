package kr.co.iniline.j2glsl;

import kr.co.iniline.j2glsl.MethodMirrorIntercepter.MirrorIntercepter;

public class ShaderVarOperandOverrider extends ShaderVar implements MirrorIntercepter {
	CodeRegister codeReg = null;
	
	@Override
	public void setCodeRegister(CodeRegister reg) {
		this.codeReg = reg;
	}

	@Override
	public float[] get(String ponits) {
		String var2 = codeReg.popCode().toParamString();
		var2 = var2.substring(1, var2.length()-1);
		String var1 = codeReg.popCode().toParamString();
		codeReg.pushCode( new AccAssignCode(true, var1+"."+var2) );
		return null;
	}
	
	@Override
	public vec2 get2(String ponits) {
		get(ponits);
		return null;
	}
	
	@Override
	public vec3 get3(String ponits) {
		get(ponits);
		return null;
	}
	
	@Override
	public vec4 get4(String ponits) {
		get(ponits);
		return null;
	}

	@Override
	public ShaderVar plus(ShaderVar var) {
		String var2 = codeReg.popCode().toParamString();
		String var1 = codeReg.popCode().toParamString();
		StringBuilder sb = new StringBuilder();
		sb.append(var1);
		sb.append("+");
		sb.append(var2);
		codeReg.pushCode( new AccAssignCode(true, sb.toString()) );
		return null;
	}

	@Override
	public ShaderVar plus(float var) {
		plus( (ShaderVar)null );
		return null;
	}
	
	@Override
	public void add(ShaderVar var) {
		String var2 = codeReg.popCode().toParamString();
		String var1 = codeReg.popCode().toParamString();
		StringBuilder sb = new StringBuilder();
		sb.append(var1);
		sb.append("+=");
		sb.append(var2);
		codeReg.pushCode( new AccAssignCode(true, sb.toString()) );
	}

	@Override
	public void add(float var) {
		add( (ShaderVar)null );
	}

	@Override
	public ShaderVar minus(ShaderVar var) {
		String var2 = codeReg.popCode().toParamString();
		String var1 = codeReg.popCode().toParamString();
		StringBuilder sb = new StringBuilder();
		sb.append(var1);
		sb.append("-");
		sb.append(var2);
		codeReg.pushCode( new AccAssignCode(true, sb.toString()) );
		return null;
	}

	@Override
	public ShaderVar minus(float var) {
		minus( (ShaderVar)null );
		return null;
	}

	@Override
	public ShaderVar minus() {
		String var1 = codeReg.popCode().toParamString();
		StringBuilder sb = new StringBuilder();
		sb.append("-");
		sb.append(var1);
		codeReg.pushCode( new AccAssignCode(true, sb.toString()) );
		return null;
	}

	@Override
	public void sub(ShaderVar var) {
		String var2 = codeReg.popCode().toParamString();
		String var1 = codeReg.popCode().toParamString();
		StringBuilder sb = new StringBuilder();
		sb.append(var1);
		sb.append("-=");
		sb.append(var2);
		codeReg.pushCode( new AccAssignCode(true, sb.toString()) );
	}

	@Override
	public void sub(float var) {
		sub( (ShaderVar)null );
	}

	@Override
	public ShaderVar mult(ShaderVar var) {
		String var2 = codeReg.popCode().toParamString();
		String var1 = codeReg.popCode().toParamString();
		StringBuilder sb = new StringBuilder();
		sb.append(var1);
		sb.append("*");
		sb.append(var2);
		codeReg.pushCode( new AccAssignCode(true, sb.toString()) );
		return null;
	}

	@Override
	public ShaderVar mult(float var) {
		mult( (ShaderVar)null );
		return null;
	}

	@Override
	public void multLocal(ShaderVar var) {
		String var2 = codeReg.popCode().toParamString();
		String var1 = codeReg.popCode().toParamString();
		StringBuilder sb = new StringBuilder();
		sb.append(var1);
		sb.append("*=");
		sb.append(var2);
		codeReg.pushCode( new AccAssignCode(true, sb.toString()) );
	}

	@Override
	public void multLocal(float var) {
		multLocal( (ShaderVar)null );
	}

	@Override
	public ShaderVar div(ShaderVar var) {
		String var2 = codeReg.popCode().toParamString();
		String var1 = codeReg.popCode().toParamString();
		StringBuilder sb = new StringBuilder();
		sb.append(var1);
		sb.append("/");
		sb.append(var2);
		codeReg.pushCode( new AccAssignCode(true, sb.toString()) );
		return null;
	}

	@Override
	public ShaderVar div(float var) {
		div( (ShaderVar)null );
		return null;
	}

	@Override
	public void divLocal(ShaderVar var) {
		String var2 = codeReg.popCode().toParamString();
		String var1 = codeReg.popCode().toParamString();
		StringBuilder sb = new StringBuilder();
		sb.append(var1);
		sb.append("/=");
		sb.append(var2);
		codeReg.pushCode( new AccAssignCode(true, sb.toString()) );
	}

	@Override
	public void divLocal(float var) {
		divLocal( (ShaderVar)null );
	}

	@Override
	public float[] toFloats() {
		// TODO Auto-generated method stub
		return null;
	}
}
