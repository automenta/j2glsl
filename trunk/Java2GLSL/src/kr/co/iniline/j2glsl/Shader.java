package kr.co.iniline.j2glsl;

import java.util.HashSet;

public abstract class Shader {
	HashSet<String> defSet = null;
	
	/** built-in constants */
	public int gl_MaxVertexUniformComponents;
	public int gl_MaxFragmentUniformComponents;
	public int gl_MaxVertexAttribs;
	public int gl_MaxVaryingFloats;
	public int gl_MaxDrawBuffers;
	public int gl_MaxTextureCoords;
	public int gl_MaxTextureUnits;
	public int gl_MaxTextureImageUnits;
	public int gl_MaxVertexTextureImageUnits;
	public int gl_MaxCombinedTextureImageUnits;
	public int gl_MaxLights;
	public int gl_MaxClipPlanes;
	
	/** built-in uniforms */
	public mat4 gl_ModelViewMatrix;
	public mat4 gl_ModelViewProjectionMatrix;
	public mat4 gl_ProjectionMatrix;
	public mat4 gl_TextureMatrix[]; //gl_MaxTextureCoords
	
	public mat4 gl_ModelViewMatrixInverse;
	public mat4 gl_ModelViewProjectionMatrixInverse;
	public mat4 gl_ProjectionMatrixInverse;
	public mat4 gl_TextureMatrixInverse[]; //gl_MaxTextureCoords
	
	public mat4 gl_ModelViewMatrixTranspose;
	public mat4 gl_ModelViewProjectionMatrixTranspose;
	public mat4 gl_ProjectionMatrixTranspose;
	public mat4 gl_TextureMatrixTranspose[]; //gl_MaxTextureCoords
	
	public mat4 gl_ModelViewMatrixInverseTranspose;
	public mat4 gl_ModelViewProjectionMatrixInverseTranspose;
	public mat4 gl_ProjectionMatrixInverseTranspose;
	public mat4 gl_TextureMatrixInverseTranspose[]; //gl_MaxTextureCoords
	
	public mat3 gl_NormalMatrix;
	public float gl_NormalScale;
	
	public static class varDefine {
		public float _float;
		public vec2 vec2 = new vec2();
		public vec3 vec3 = new vec3();
		public vec4 vec4 = new vec4();
		public mat3 mat3 = new mat3();
		public mat4 mat4 = new mat4();
		public sampler1D sampler1D = new sampler1D();
		public sampler2D sampler2D = new sampler2D();
		public sampler2DShadow sampler2DShadow = new sampler2DShadow();
		public sampler3D sampler3D = new sampler3D();
		public samplerCube samplerCube = new samplerCube();
	}
	
	public static final class varying extends varDefine {
	}
	
	public static final class attribute extends varDefine {
	}
	
	public static final class uniform extends varDefine {
	}
	
	public static final class in {
		public class vec2 extends kr.co.iniline.j2glsl.vec2 {
		}
		public class vec3 extends kr.co.iniline.j2glsl.vec3 {
		}
		public class vec4 extends kr.co.iniline.j2glsl.vec4 {
		}
	}
	
	public static final class out {
		public class vec2 extends kr.co.iniline.j2glsl.vec2 {
		}
		public class vec3 extends kr.co.iniline.j2glsl.vec3 {
		}
		public class vec4 extends kr.co.iniline.j2glsl.vec4 {
		}
	}
	
	public static final class inout {
		public class vec2 extends kr.co.iniline.j2glsl.vec2 {
		}
		public class vec3 extends kr.co.iniline.j2glsl.vec3 {
		}
		public class vec4 extends kr.co.iniline.j2glsl.vec4 {
		}
	}
	
	public static final varying varying = new varying();
	public static final attribute attribute = new attribute();
	public static final uniform uniform = new uniform();
	
	public abstract void main();
	
	public class gl_DepthRangeParameters {
		public float near;
		public float far;
		public float diff;
	}
	public gl_DepthRangeParameters gl_DepthRange;
	
	public class gl_FogParameters {
		public vec4 color;
		public float density;
		public float start;
		public float end;
		public float scale;
	}
	public gl_FogParameters gl_Fog;
	
	public class gl_LightSourceParameters {
		public vec4 ambient;
		public vec4 diffuese;
		public vec4 specular;
		public vec4 position;
		public vec4 halfVector;
		public vec3 spotDirection;
		public float spotExponent;
		public float spotCutoff;
		public float spotCosCutoff;
		public float constantAttenuation;
		public float linearAttenuation;
		public float quadraticAttenuation;
	}
	public gl_LightSourceParameters gl_LightSource[]; //gl_MaxLights
	
	public class gl_LightModeParameters {
		public vec4 ambient;
	}
	public gl_LightModeParameters gl_LightModel;
	
	public class gl_LightModelProducts {
		public vec4 sceneColor;
	}
	public gl_LightModelProducts gl_FrontLightModelProduct;
	public gl_LightModelProducts gl_BackLightModelProduct;
	
	public class gl_LightProducts {
		public vec4 ambient;
		public vec4 diffuse;
		public vec4 specular;
	}
	public gl_LightProducts gl_FrontLightProducts[]; //gl_MaxLights
	public gl_LightProducts gl_BackLightProducts[]; //gl_MaxLights
	
	public class gl_MaterialParameters {
		public vec4 emission;
		public vec4 ambient;
		public vec4 diffuse;
		public vec4 specular;
		public float shininess;
	}
	public gl_MaterialParameters gl_FrontMaterial;
	public gl_MaterialParameters gl_BackMaterial;
	
	public class gl_PointParameters {
		public float size;
		public float sizeMin;
		public float sizeMax;
		public float fadeTresholdSize;
		public float distanceConstantAttenuation;
		public float distanceLinearAttenuation;
		public float distanceQuadraticAttenuation;
	}
	public gl_PointParameters gl_Point;
	
	public vec4 gl_TextureEnvColor[]; //gl_MaxTextureUnits
	
	public vec4 gl_ClipPlane[]; //gl_MaxClipPlanes
	
	public vec4 gl_EyePlaneS[]; //gl_MaxTextureCoords
	public vec4 gl_EyePlaneT[]; //gl_MaxTextureCoords
	public vec4 gl_EyePlaneR[]; //gl_MaxTextureCoords
	public vec4 gl_EyePlaneQ[]; //gl_MaxTextureCoords
	
	public vec4 gl_ObjectPlaneS[]; //gl_MaxTextureCoords
	public vec4 gl_ObjectPlaneT[]; //gl_MaxTextureCoords
	public vec4 gl_ObjectPlaneR[]; //gl_MaxTextureCoords
	public vec4 gl_ObjectPlaneQ[]; //gl_MaxTextureCoords
	
	/** PREPROCESSOR */
	public void preProc_ifdef( String defVal ) {
	}
	
	public void preProc_define( String defVal ) {
	}
	
	public void preProc_undef( String defVal ) {
		
	}
	
	
	/*
	#define
	#undef
	#if
	#ifdef
	#ifndef
	#else
	#elif
	#endif
	#error
	#pragma
	#line
	*/
	
	/** geometric funcs */
	public vec3 cross(vec3 var1, vec3 var2) {
		return null;
	}
	
	public float disatance(vec2 var1, vec2 var2) {
		return .0f;
	}
	
	public float disatance(vec3 var1, vec3 var2) {
		return .0f;
	}
	
	public float disatance(vec4 var1, vec4 var2) {
		return .0f;
	}
	
	public float dot(vec2 var1, vec2 var2) {
		return 0f;
	}
	
	public float dot(vec3 var1, vec3 var2) {
		return 0f;
	}
	
	public float dot(vec4 var1, vec4 var2) {
		return 0f;
	}
	
	public vec2 faceforward(vec2 V, vec2 I, vec2 N) {
		return null;
	}
	
	public vec3 faceforward(vec3 V, vec3 I, vec3 N) {
		return null;
	}
	
	public vec4 faceforward(vec4 V, vec4 I, vec4 N) {
		return null;
	}
	
	public float length(vec2 var1) {
		return 0f;
	}
	
	public float length(vec3 var1) {
		return 0f;
	}
	
	public float length(vec4 var1) {
		return 0f;
	}
	
	public vec2 normalize(vec2 var1) {
		return null;
	}
	
	public vec3 normalize(vec3 var1) {
		return null;
	}
	
	public vec4 normalize(vec4 var1) {
		return null;
	}
	
	public vec2 reflect(vec2 I, vec2 N) {
		return null;
	}
	
	public vec3 reflect(vec3 I, vec3 N) {
		return null;
	}
	
	public vec4 reflect(vec4 I, vec4 N) {
		return null;
	}
	
	public vec2 refract(vec2 I, vec2 N, float eta) {
		return null;
	}
	
	public vec3 refract(vec3 I, vec3 N, float eta) {
		return null;
	}
	
	public vec4 refract(vec4 I, vec4 N, float eta) {
		return null;
	}
	
	/** Angle&Trigonometry Funcs */
	public vec2 sin( vec2 var ) {
		return null;
	}
	
	public vec3 sin( vec3 var ) {
		return null;
	}
	
	public vec4 sin( vec4 var ) {
		return null;
	}
	
	public vec2 cos( vec2 var ) {
		return null;
	}
	
	public vec3 cos( vec3 var ) {
		return null;
	}
	
	public vec4 cos( vec4 var ) {
		return null;
	}
	
	public vec2 tan( vec2 var ) {
		return null;
	}
	
	public vec3 tan( vec3 var ) {
		return null;
	}
	
	public vec4 tan( vec4 var ) {
		return null;
	}
	
	public vec2 asin( vec2 var ) {
		return null;
	}
	
	public vec3 asin( vec3 var ) {
		return null;
	}
	
	public vec4 asin( vec4 var ) {
		return null;
	}
	
	public vec2 acos( vec2 var ) {
		return null;
	}
	
	public vec3 acos( vec3 var ) {
		return null;
	}
	
	public vec4 acos( vec4 var ) {
		return null;
	}
	
	public vec2 atan( vec2 var1, vec2 var2 ) {
		return null;
	}
	
	public vec3 atan( vec3 var1, vec3 var2 ) {
		return null;
	}
	
	public vec4 atan( vec4 var1, vec4 var2 ) {
		return null;
	}
	
	public vec2 atan( vec2 var ) {
		return null;
	}
	
	public vec3 atan( vec3 var ) {
		return null;
	}
	
	public vec4 atan( vec4 var ) {
		return null;
	}
	
	public vec2 radians( vec2 var ) {
		return null;
	}
	
	public vec3 radians( vec3 var ) {
		return null;
	}
	
	public vec4 radians( vec4 var ) {
		return null;
	}
	
	public vec2 degrees( vec2 var ) {
		return null;
	}
	
	public vec3 degrees( vec3 var ) {
		return null;
	}
	
	public vec4 degrees( vec4 var ) {
		return null;
	}
	
	
	/** Exponential Functions */
	public vec2 pow( vec2 var1, vec2 var2 ) {
		return null;
	}
	
	public vec3 pow( vec3 var1, vec3 var2 ) {
		return null;
	}
	
	public vec4 pow( vec4 var1, vec4 var2 ) {
		return null;
	}
	
	public float pow( float var1, float var2 ) {
		return 0f;
	}
	
	public vec2 exp( vec2 var ) {
		return null;
	}
	
	public vec3 exp( vec3 var ) {
		return null;
	}
	
	public vec4 exp( vec4 var ) {
		return null;
	}
	
	public vec2 log( vec2 var ) {
		return null;
	}
	
	public vec3 log( vec3 var ) {
		return null;
	}
	
	public vec4 log( vec4 var ) {
		return null;
	}
	
	public vec2 exp2( vec2 var ) {
		return null;
	}
	
	public vec3 exp2( vec3 var ) {
		return null;
	}
	
	public vec4 exp2( vec4 var ) {
		return null;
	}
	
	public vec2 log2( vec2 var ) {
		return null;
	}
	
	public vec3 log2( vec3 var ) {
		return null;
	}
	
	public vec4 log2( vec4 var ) {
		return null;
	}
	
	public vec2 sqrt( vec2 var ) {
		return null;
	}
	
	public vec3 sqrt( vec3 var ) {
		return null;
	}
	
	public vec4 sqrt( vec4 var ) {
		return null;
	}
	
	public vec2 inversesqrt( vec2 var ) {
		return null;
	}
	
	public vec3 inversesqrt( vec3 var ) {
		return null;
	}
	
	public vec4 inversesqrt( vec4 var ) {
		return null;
	}
	
	
	/** common function */
	public vec4 abs(vec4 var1) {
		return null;
	}
	
	public vec2 ceil( vec2 var ) {
		return null;
	}
	
	public vec3 ceil( vec3 var ) {
		return null;
	}
	
	public vec4 ceil( vec4 var ) {
		return null;
	}
	
	public vec2 clamp( vec2 var1, vec2 var2, vec2 var ) {
		return null;
	}
	
	public vec3 clamp( vec3 var1, vec3 var2, vec3 var ) {
		return null;
	}
	
	public vec4 clamp( vec4 var1, vec4 var2, vec4 var ) {
		return null;
	}
	
	public vec2 clamp( vec2 var1, float var2, float var3) {
		return null;
	}
	
	public vec3 clamp( vec3 var1, float var2, float var3) {
		return null;
	}
	
	public vec4 clamp( vec4 var1, float var2, float var3) {
		return null;
	}
	
	public vec2 floor( vec2 var ) {
		return null;
	}
	
	public vec3 floor( vec3 var ) {
		return null;
	}
	
	public vec4 floor( vec4 var ) {
		return null;
	}
	
	public vec2 fract(vec2 vec4) {
		return null;
	}
	
	public vec3 fract(vec3 vec4) {
		return null;
	}
	
	public vec4 fract(vec4 vec4) {
		return null;
	}
	
	public vec2 max( vec2 var1, vec2 var2 ) {
		return null;
	}
	
	public vec3 max( vec3 var1, vec3 var2 ) {
		return null;
	}
	
	public vec4 max( vec4 var1, vec4 var2 ) {
		return null;
	}
	
	public vec2 max( vec2 var1, float var2 ) {
		return null;
	}
	
	public vec3 max( vec3 var1, float var2 ) {
		return null;
	}
	
	public vec4 max( vec4 var1, float var2 ) {
		return null;
	}
	
	public float max( float var1, float var2 ) {
		return 0f;
	}
	
	public vec2 min( vec2 var1, vec2 var2 ) {
		return null;
	}
	
	public vec3 min( vec3 var1, vec3 var2 ) {
		return null;
	}
	
	public vec4 min( vec4 var1, vec4 var2 ) {
		return null;
	}
	
	public vec2 min( vec2 var1, float var2) {
		return null;
	}
	
	public vec3 min( vec3 var1, float var2) {
		return null;
	}
	
	public vec4 min( vec4 var1, float var2) {
		return null;
	}
	
	public vec2 mix(vec2 var1, vec2 var2, vec2 var3) {
		return null;
	}
	
	public vec3 mix(vec3 var1, vec3 var2, vec3 var3) {
		return null;
	}
	
	public vec4 mix(vec4 var1, vec4 var2, vec4 var3) {
		return null;
	}
	
	public vec2 mix( vec2 var1, vec2 var2, float var3 ) {
		return null;
	}
	
	public vec3 mix( vec3 var1, vec3 var2, float var3 ) {
		return null;
	}
	
	public vec4 mix( vec4 var1, vec4 var2, float var3 ) {
		return null;
	}
	
	public float mix( float var1, float var2, float var3 ) {
		return 0f;
	}
	
	public vec2 mod(vec2 var1, vec2 var2) {
		return null;
	}
	
	public vec3 mod(vec3 var1, vec3 var2) {
		return null;
	}
	
	public vec4 mod(vec4 var1, vec4 var2) {
		return null;
	}
	
	public vec2 mod( vec2 var1, float var2 ) {
		return null;
	}
	
	public vec3 mod( vec3 var1, float var2 ) {
		return null;
	}
	
	public vec4 mod( vec4 var1, float var2 ) {
		return null;
	}
	
	public vec2 sign( vec2 var ) {
		return null;
	}
	
	public vec3 sign( vec3 var ) {
		return null;
	}
	
	public vec4 sign( vec4 var ) {
		return null;
	}
	
	public vec2 smoothstep( vec2 var1, vec2 var2, vec2 var3 ) {
		return null;
	}
	
	public vec3 smoothstep( vec3 var1, vec3 var2, vec3 var3 ) {
		return null;
	}
	
	public vec4 smoothstep( vec4 var1, vec4 var2, vec4 var3 ) {
		return null;
	}
	
	public vec2 smoothstep( float var1, float var2, vec2 var3 ) {
		return null;
	}
	
	public vec3 smoothstep( float var1, float var2, vec3 var3 ) {
		return null;
	}
	
	public vec4 smoothstep( float var1, float var2, vec4 var3 ) {
		return null;
	}
	
	public vec2 step( vec2 var1, vec2 var2 ) {
		return null;
	}
	
	public vec3 step( vec3 var1, vec3 var2 ) {
		return null;
	}
	
	public vec4 step( vec4 var1, vec4 var2 ) {
		return null;
	}
	
	public vec2 step(float var1, vec2 var2) {
		return null;
	}
	
	public vec3 step(float var1, vec3 var2) {
		return null;
	}
	
	public vec4 step(float var1, vec4 var2) {
		return null;
	}
	
	
	/** for easy convert */
	public vec4 vec4(ShaderVar ...vars) {
		return null;
	}
	
	public vec4 vec4(ShaderVar vars1, float ...vars2) {
		return null;
	}
	
	public vec4 vec4(float ...vars) {
		return null;
	}
	
	public vec3 vec3(ShaderVar ...vars) {
		return null;
	}
	
	public vec3 vec3(ShaderVar sVar, float vars) {
		return null;
	}
	
	public vec3 vec3(float ...vars) {
		return null;
	}
	
	public vec2 vec2(ShaderVar var) {
		return null;
	}
	
	public vec2 vec2(float ...vars) {
		return null;
	}
}