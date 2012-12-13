package kr.co.iniline.j2glsl.example;


import java.io.IOException;
import java.io.StringWriter;


import kr.co.iniline.j2glsl.*;
import kr.co.iniline.j2glsl.example.shader.*;


public class ConvertTest {
	public static void main(String[] args) throws Exception {
		
		J2GLSLConverter glslConverter = new J2GLSLConverter();
//		glslConverter.debugOpCodeStack = true;
		Effect effects[] = new Effect[]{new ToonEffect()};
		
		for (int i = 0; i < effects.length; i++) {
			StringWriter writer = new StringWriter();
			Shader shaders[] = effects[i].getShaders();
			for (int k = 0; k < shaders.length; k++) {
				glslConverter.compile( writer, shaders[k]);
				writer.append("//------------------- "+shaders[k].getClass().getSuperclass().getSimpleName());
			}
			System.out.println(">>>>>>>>>>> Start "+effects[i].getClass().getSimpleName());
			System.out.println( writer.toString() );
			System.out.println("<<<<<<<<<<< End of "+effects[i].getClass().getSimpleName());
		}
	}
}
