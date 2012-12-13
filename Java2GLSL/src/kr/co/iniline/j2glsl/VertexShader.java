package kr.co.iniline.j2glsl;

public abstract class VertexShader extends Shader {
	/** special output */
	public vec4 gl_Position = null;
	public float gl_PointSize = 0;
	public vec4 gl_ClipVertex = null;
	
	/** attribute intputs */
	public vec4 gl_Vertex;
	public vec3 gl_Normal;
	public vec4 gl_Color;
	public vec4 gl_SecondaryColor;
	
	public vec4 gl_MultiTexCoord0;
	public vec4 gl_MultiTexCoord1;
	public vec4 gl_MultiTexCoord2;
	public vec4 gl_MultiTexCoord3;
	public vec4 gl_MultiTexCoord4;
	public vec4 gl_MultiTexCoord5;
	public vec4 gl_MultiTexCoord6;
	public vec4 gl_MultiTexCoord7;
	public float gl_FogCoord;
	
	/** varying */
	public vec4 gl_FrontColor;
	public vec4 gl_BackColor; //enable GL_VERTEX_PROGRAM_TWO_SIDE
	public vec4 gl_FrontSecondaryColor;
	public vec4 gl_BackSecondaryColor;
	public vec4 gl_TexCoord[]; //MAX=gl_MaxTextureCoords
	public float gl_FogFragCoord;
	
	public void define() {
	}
	
	public vec4 ftransform() {
		return null;
	}
	
	/** Texture Lookup Funcs with LOD */
	public vec4 texture1DLod(sampler1D var1, float var2, float lod) {
		return null;
	}
	
	public vec4 texture1DProjLod(sampler1D var1, vec2 var2, float lod) {
		return null;
	}
	
	public vec4 texture1DProjLod(sampler1D var1, vec4 var2, float lod) {
		return null;
	}
	
	public vec4 texture2DLod(sampler2D var1, float var2, float lod) {
		return null;
	}
	
	public vec4 texture2DProjLod(sampler2D var1, vec2 var2, float lod) {
		return null;
	}
	
	public vec4 texture2DProjLod(sampler2D var1, vec3 var2, float lod) {
		return null;
	}
	
	public vec4 texture2DProjLod(sampler2D var1, vec4 var2, float lod) {
		return null;
	}
	
	public vec4 texture3DProjLod(sampler3D var1, vec2 var2, float lod) {
		return null;
	}
	
	public vec4 textureCubeLod(sampler3D var1, vec4 var2, float lod) {
		return null;
	}
	
	public vec4 shadow1DLod(sampler1DShadow var1, vec3 var2, float lod) {
		return null;
	}
	
	public vec4 shadow2DLod(sampler2DShadow var1, vec3 var2, float lod) {
		return null;
	}

	public vec4 shadow1DLodProj(sampler1DShadow var1, vec4 var2, float lod) {
		return null;
	}
	
	public vec4 shadow2DLodProj(sampler2DShadow var1, vec4 var2, float lod) {
		return null;
	}
	
	public static final VertexShader defaultVertexShader = new VertexShader() {
		@Override
		public void main() {
			gl_Position = gl_ModelViewProjectionMatrix.mult( gl_Vertex );
		}
	};
}
