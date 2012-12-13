package kr.co.iniline.j2glsl.example.shader;

import org.lwjgl.opengl.GL20;

import kr.co.iniline.j2glsl.*;

/**
 * Reference by http://www.kamend.com/tag/glsl/
 */
public class PerlinNoiseEffect extends Effect {
	float count = 0;
	
	public PerlinNoiseEffect() throws ShaderCompileException {
		super();
	}

	@Override
	public Shader[] getShaders() {
		return new Shader[]{
			VertexShader.defaultVertexShader,
			createFragmentShader()
		};
	}
	
	FragmentShader createFragmentShader() {
		return new FragmentShader() {
			float time = uniform._float;
			
			vec4 mod289(vec4 x) {
			    return x.minus( floor( x.mult(1.0f / 289.0f) ).mult(289.0f) );
			}
			
			vec4 permute(vec4 x) {
				return mod289( x.mult( x.mult(34.0f).plus(1.0f) )  );
			}
			
			vec4 taylorInvSqrt(vec4 r) {
			    return r.mult( 0.85373472095314f ).minus( 1.79284291400159f );
			}
			
			vec2 fade(vec2 t) {
				return t.mult( t ).mult( t ).mult( t.mult( t.mult(6.0f).minus(15f) ).plus( 10.0f ) );
			}
			
			// Classic Perlin noise
			float cnoise(vec2 P) {
			    vec4 Pi = floor( P.get4("xyxy")).plus( vec4(0.0f, 0.0f, 1.0f, 1.0f) );
			    vec4 Pf = fract( P.get4("xyxy")).minus( vec4(0.0f, 0.0f, 1.0f, 1.0f) );
			    Pi = mod289(Pi); // To avoid truncation effects in permutation
			    vec4 ix = Pi.get4("xzxz");
			    vec4 iy = Pi.get4("yyww");
			    vec4 fx = Pf.get4("xzxz");
			    vec4 fy = Pf.get4("yyww");
			     
			    vec4 i = permute( permute(ix).plus(iy) );
			     
			    vec4 gx = fract( i.mult( 1.0f/41.0f ).mult( 2.0f ).minus( 1.0f ) );
			    vec4 gy = abs(gx).minus(0.5f);
			    vec4 tx = floor(gx.plus(0.5f));
			    gx = gx.minus(tx);
			     
			    vec2 g00 = vec2(gx.x,gy.x);
			    vec2 g10 = vec2(gx.y,gy.y);
			    vec2 g01 = vec2(gx.z,gy.z);
			    vec2 g11 = vec2(gx.w,gy.w);
			     
			    vec4 norm = taylorInvSqrt(vec4(dot(g00, g00), dot(g01, g01), dot(g10, g10), dot(g11, g11)));
			    g00 = g00.mult(norm.x);
			    g01 = g01.mult(norm.y);
			    g10 = g10.mult(norm.z);
			    g11 = g11.mult(norm.w);
			    
			    float n00 = dot(g00, vec2(fx.x, fy.x));
			    float n10 = dot(g10, vec2(fx.y, fy.y));
			    float n01 = dot(g01, vec2(fx.z, fy.z));
			    float n11 = dot(g11, vec2(fx.w, fy.w));
			     
			    vec2 fade_xy = fade(Pf.get2("xy"));
			    vec2 n_x = mix(vec2(n00, n01), vec2(n10, n11), fade_xy.x);
			    float n_xy = mix(n_x.x, n_x.y, fade_xy.y);
			    return 2.3f * n_xy;
			}
			 
			// Classic Perlin noise, periodic variant
			float pnoise(vec2 P, vec2 rep)
			{
			    vec4 Pi = floor(P.get4("xyxy")).plus( vec4(0.0f, 0.0f, 1.0f, 1.0f) );
			    vec4 Pf = fract(P.get4("xyxy")).minus( vec4(0.0f, 0.0f, 1.0f, 1.0f) );
			    Pi = mod(Pi, rep.get4("xyxy")); // To create noise with explicit period
			    Pi = mod289(Pi);        // To avoid truncation effects in permutation
			    vec4 ix = Pi.get4("xzxz");
			    vec4 iy = Pi.get4("yyww");
			    vec4 fx = Pf.get4("xzxz");
			    vec4 fy = Pf.get4("yyww");
			    
			    vec4 i = permute( permute(ix).plus(iy) );
			    
			    vec4 gx = fract( i.mult(1.0f / 41.0f) ).mult(2.0f).minus( 1.0f );
			    vec4 gy = abs(gx).minus( 0.5f ) ;
			    vec4 tx = floor( gx.plus(0.5f) );
			    gx = gx.minus(tx);
			    
			    vec2 g00 = vec2(gx.x,gy.x);
			    vec2 g10 = vec2(gx.y,gy.y);
			    vec2 g01 = vec2(gx.z,gy.z);
			    vec2 g11 = vec2(gx.w,gy.w);
			    
			    vec4 norm = taylorInvSqrt(vec4(dot(g00, g00), dot(g01, g01), dot(g10, g10), dot(g11, g11)));
			    g00 = g00.mult( norm.x );
			    g01 = g01.mult( norm.y );
			    g10 = g10.mult( norm.z );
			    g11 = g11.mult( norm.w );
			     
			    float n00 = dot(g00, vec2(fx.x, fy.x));
			    float n10 = dot(g10, vec2(fx.y, fy.y));
			    float n01 = dot(g01, vec2(fx.z, fy.z));
			    float n11 = dot(g11, vec2(fx.w, fy.w));
			     
			    vec2 fade_xy = fade(Pf.get2("xy"));
			    vec2 n_x = mix(vec2(n00, n01), vec2(n10, n11), fade_xy.x);
			    float n_xy = mix(n_x.x, n_x.y, fade_xy.y);
			    return 2.3f * n_xy;
			}
			 
			float fbm(vec2 P, int octaves, float lacunarity, float gain) 	{
			    float sum = 0.0f;
			    float amp = 1.0f;
			    vec2 pp = P;
			     
			    int i;
			     
			    for(i = 0; i < octaves; i+=1) {
			        amp *= gain; 
			        sum += amp * cnoise(pp);
			        pp = pp.mult( lacunarity );
			    }
			    return sum;
			}
			
			float pattern(vec2 p) {
			    float l = 2.5f;
			    float g = 0.4f;
			    int oc = 10;
			     
			    vec2 q = vec2( fbm( p.plus(vec2(0f,0f)),oc,l,g),fbm( p.plus(vec2(5.2f,1.3f)),oc,l,g));
			    vec2 r = vec2( fbm( p.plus(q.mult(4.0f)).plus(vec2(1.7f,9.2f)),oc,l,g ), fbm( p.plus( q.mult(4.0f) ).plus( vec2(8.3f,2.8f) ) ,oc,l,g));
			    return fbm( p.plus( r.mult(4.0f) ) ,oc,l,g);    
			}
			
			float pattern2( vec2 p, vec2 q, vec2 r , float time) {
			    float l = 2.3f;
			    float g = 0.4f;
			    int oc = 10; 
			    
			    q.x = fbm( p.plus( vec2(time,time) ), oc, l, g);
			    q.y = fbm( p.plus( vec2(5.2f*time, 1.3f*time) ), oc, l, g);
			    
			    r.x = fbm( p.plus( q.mult(4.0f) ).plus( vec2(1.7f, 9.2f) ), oc, l, g );
			    r.y = fbm( p.plus( q.mult(4.0f) ).plus( vec2(8.3f,2.8f) ), oc, l, g);
			    
			    return fbm( p.plus(r.mult(4.0f)), oc, l, g);
			}
			
			@Override
			public void main() {
				vec2 q = gl_FragCoord.get2("xy").div( vec2(640.0f, 480.0f) );
			    vec2 p = q.mult(2.0f).minus(1f);
			    vec2 qq = vec2(0,0);
			    vec2 r = vec2(0,0);
			    float color = pattern2(p,qq,r,time);
			     
			    vec4 c = vec4(color,color,color,color);
			    c.multLocal(3.5f);
			    
			    gl_FragColor = c;
			}
		};
	}
	
	@Override
	public void initShaders() throws ShaderCompileException {
		super.initShaders();
		count = 0.0001f;
	}
	
	@Override
	public void renderReady() {
		count += 0.0002f;
		int loc = GL20.glGetUniformLocation(getProgramId(), "time");
		GL20.glUniform1f(loc, count);
	}

	@Override
	public void renderEnd() {
	}
}
