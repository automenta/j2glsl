package kr.co.iniline.j2glsl.example.shader;

import kr.co.iniline.j2glsl.*;

public class ToonEffect extends Effect {
	public ToonEffect() throws ShaderCompileException {
		super();
	}

	@Override
	public Shader[] getShaders() {
		return new Shader[]{
				createVertexShader(),
				createFragmentShader()
		};
	}
	
	VertexShader createVertexShader() {
		return new VertexShader() {
			vec3 normal = varying.vec3;
			vec3 lightDir = varying.vec3;
			
			@Override
			public void main() {
				lightDir = normalize( vec3(gl_LightSource[0].position) );
				normal = normalize(gl_NormalMatrix.mult(gl_Normal));
				gl_Position = ftransform();
			}
		};
	}
	
	FragmentShader createFragmentShader() {
		return new FragmentShader() {
			vec3 normal = varying.vec3;
			vec3 lightDir = varying.vec3;
			
			@Override
			public void main() {
				vec3 n = normalize(normal);
				float intensity = max(dot(lightDir,n), 0.0f);
				vec4 color;

				if (intensity > 0.98)
					color = vec4(0.8f, 0.8f, 0.8f, 1.0f);
				else if (intensity > 0.5)
					color = vec4(0.4f, 0.4f, 0.8f, 1.0f);	
				else if (intensity > 0.25)
					color = vec4(0.2f, 0.2f, 0.4f, 1.0f);
				else
					color = vec4(0.1f, 0.1f, 0.1f, 1.0f);		
					
				gl_FragColor = color;
			}
		};
	}
	
	@Override
	public void renderReady() {
	}

	@Override
	public void renderEnd() {
	}
}