package kr.co.iniline.j2glsl.example.shader;

import kr.co.iniline.j2glsl.*;

public class GoochShadeEffect extends Effect {
	public GoochShadeEffect() throws ShaderCompileException {
		super();
	}
	
	public Shader[] getShaders() {
		return new Shader[] {
			createVertexShader(),
			createFragmentShader()
		};
	}
	
	
	VertexShader createVertexShader() {
		return new VertexShader() {
			float NdotL = varying._float;
			vec3  ReflectVec = varying.vec3;
			vec3  ViewVec = varying.vec3;

			public void main() {
				vec3 LightPosition = vec3(0.0f, 10.0f, 4.0f);
			    vec3 ecPos      = vec3 ( gl_ModelViewMatrix.mult(gl_Vertex) );
			    vec3 tnorm      = normalize( gl_NormalMatrix.mult(gl_Normal) );
			    vec3 lightVec   = normalize( LightPosition.minus(ecPos) );
			    ReflectVec      = normalize( reflect( lightVec.minus(), tnorm) );
			    ViewVec         = normalize( ecPos.minus() );
			    NdotL           = (dot(lightVec, tnorm) + 1.0f) * 0.5f;
			    gl_Position     = ftransform();
			}
		};
	}
	
	FragmentShader createFragmentShader() {
		return new FragmentShader() {
			float NdotL = varying._float;
			vec3  ReflectVec = varying.vec3;
			vec3  ViewVec = varying.vec3;

			public void main () {
				vec3  SurfaceColor = vec3(0.75f, 0.75f, 0.75f); // (0.75, 0.75, 0.75)
				vec3  WarmColor = vec3(0.6f, 0.6f, 0.0f);    // (0.6, 0.6, 0.0)
				vec3  CoolColor = vec3(0.0f, 0.0f, 0.6f);    // (0.0, 0.0, 0.6)
				float DiffuseWarm = 0.45f;  // 0.45
				float DiffuseCool = 0.45f;  // 0.45

				vec3 kcool    = vec3( min(CoolColor.plus( SurfaceColor.mult(DiffuseCool) ), 1.0f) );
			    vec3 kwarm    = vec3( min(WarmColor.plus( SurfaceColor.mult(DiffuseWarm) ), 1.0f) ); 
			    vec3 kfinal   = vec3( mix(kcool, kwarm, NdotL) );

			    vec3 nreflect = normalize(ReflectVec);
			    vec3 nview    = normalize(ViewVec);
			    float spec    = max(dot(nreflect, nview), 0.0f);
			    spec          = pow(spec, 32.0f);

			    gl_FragColor = vec4 (min(kfinal.plus(spec), 1.0f), 1.0f);
			}
		};
	}

	@Override
	public void renderReady() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void renderEnd() {
		// TODO Auto-generated method stub
		
	}
}
