package kr.co.iniline.j2glsl;

public abstract class FragmentShader extends Shader {
	public vec4 gl_FragColor;
	public vec4 gl_FragData[] = null; //gl_MaxDrawBuffers
	public float gl_FragDepth;
	
	/** varying inputs */
	public vec4 gl_Color;
	public vec4 gl_SecondaryColor;
	public vec4 gl_TexCoord[]; //MAX=gl_MaxTextureCoords
	public float gl_FogFragCoord;
	
	/** special input variables */
	public vec4 gl_FragCoord; //pixel coordinates
	public boolean gl_FrontFacing;
	
	public void define() {
	}
	
	/** Texture Lookup funcs */
	public vec4 texture1D(sampler1D var1, float var2) {
		return null;
	}
	
	public vec4 texture1DProj(sampler1D var1, vec2 var2) {
		return null;
	}
	
	public vec4 texture1DProj(sampler1D var1, vec4 var2) {
		return null;
	}
	
	public vec4 texture2D(sampler2D var1, vec2 var2) {
		return null;
	}
	
	public vec4 texture2DProj(sampler2D var1, vec3 var2) {
		return null;
	}
	
	public vec4 texture2DProj(sampler2D var1, vec4 var2) {
		return null;
	}
	
	public vec4 texture3D(sampler3D var1, vec3 var2) {
		return null;
	}
	
	public vec4 texture3DProj(sampler3D var1, vec4 var2) {
		return null;
	}
	
	public vec4 textureCube(samplerCube var1, vec3 var2) {
		return null;
	}
	
	public vec4 shadow1D(sampler1DShadow var1, vec3 var2) {
		return null;
	}
	
	public vec4 shadow2D(sampler2DShadow var1, vec3 var2) {
		return null;
	}
	
	public vec4 shadow1DProj(sampler1DShadow var1, vec4 var2) {
		return null;
	}
	
	public vec4 shadow2DProj(sampler2DShadow var1, vec4 var2) {
		return null;
	}
}