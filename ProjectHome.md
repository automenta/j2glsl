# Java2GLSL #
Convert to glsl code.


## Version ##
current version is alpha 0.1


## Issue ##

switch not support.

for\_loop not support (it's rewrite to while\_loop).

in, out type not support


## Will be added feature in future ##

Software simulation.

ShaderUtil (processing on GPU,CPU).


## Toon Shader example of java code ##
```

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

public static void main(String[] args) throws Exception {
J2GLSLConverter glslConverter = new J2GLSLConverter();
StringWriter writer = new StringWriter();
glslConverter.compile( writer, createVertexShader() );
glslConverter.compile( writer, createFragmentShader() );
System.out.println( writer.toString() );
}
```

Standard-Output

<pre>
<pre><code>varying vec3 normal;<br>
varying vec3 lightDir;<br>
<br>
<br>
void main(  )<br>
{<br>
	lightDir = normalize(vec3(gl_LightSource[0].position));<br>
	normal = normalize((gl_NormalMatrix*gl_Normal));<br>
	gl_Position = ftransform();<br>
}<br>
<br>
varying vec3 normal;<br>
varying vec3 lightDir;<br>
<br>
<br>
void main(  )<br>
{<br>
	vec3 n = normalize(normal);<br>
	float intensity = max(dot(lightDir, n), 0.0);<br>
	vec4 color;<br>
	if( intensity &gt; 0.98 )<br>
	{<br>
		color = vec4(0.8, 0.8, 0.8, 1.0);<br>
	}<br>
	else if( intensity &gt; 0.5 )<br>
	{<br>
		color = vec4(0.4, 0.4, 0.8, 1.0);<br>
	}<br>
	else if( intensity &gt; 0.25 )<br>
	{<br>
		color = vec4(0.2, 0.2, 0.4, 1.0);<br>
	}<br>
	else<br>
	{<br>
		color = vec4(0.1, 0.1, 0.1, 1.0);<br>
	}<br>
	gl_FragColor = color;<br>
}<br>
</code></pre>
</pre>