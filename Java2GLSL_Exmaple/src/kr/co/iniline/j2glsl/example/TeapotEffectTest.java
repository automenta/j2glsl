package kr.co.iniline.j2glsl.example;

import java.nio.FloatBuffer;

import kr.co.iniline.j2glsl.*;
import kr.co.iniline.j2glsl.example.shader.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import static org.lwjgl.opengl.GL11.*;

/**
 * Reference by : https://bitbucket.org/chuck/lwjgl-sandbox
 */
public class TeapotEffectTest {
	Effect effects[] = null;
    private float xRot = 0.0f;
    private float yRot = 0.0f;
    private float zRot = 0.0f;
    
    float position[] = {0.0f, 3.0f, 3.0f, 0.0f};
    float local_view[] = {0.0f};
    float ambient[] = {0.1745f, 0.01175f, 0.01175f, 1.0f};
    float diffuse[] = {0.61424f, 0.04136f, 0.04136f, 1.0f};
    float specular[] = {0.727811f, 0.626959f, 0.626959f, 1.0f};

    public TeapotEffectTest(String title) throws Exception {
    	Display.setTitle("J2GLSL Test");
		Display.setDisplayMode( new DisplayMode(1024,768) );
		Display.setFullscreen(false);
		Display.setVSyncEnabled(true);
		Display.create();
		glClearColor(0f, 0f, 0f, 0f);
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
		initEffects();
    }
    
    static FloatBuffer toBuffer(float[] src) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(src.length);
        buffer.put(src);
        buffer.rewind();
        return buffer;
    }

    public static void main(String[] args) throws Exception {
        TeapotEffectTest s = new TeapotEffectTest("Teapot");
        s.setup();
        while(true) {
        	s.processInput();
        	s.render();
        	Display.update();
        }
    }

    protected void setup() throws ShaderCompileException {
        glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        glColor3f(1.0f, 0.0f, 0.0f);          // red
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glEnable(GL_AUTO_NORMAL);
        glEnable(GL_NORMALIZE);
        onResize(1024, 768);
        Keyboard.enableRepeatEvents(true);
		initEffects();
    }
    
    
    void initEffects() throws ShaderCompileException {
		effects = new Effect[]{
				new ToonEffect(), 
				new GoochShadeEffect(), 
				new PerlinNoiseEffect()
				};
		for (int i = 0; i < effects.length; i++) {
			effects[i].initShaders();
		}
		effects[nextEffectIdx].apply();
    }

    int nextEffectIdx = 0;
    long effectKeepTime = 2500;
    long lastTime = System.currentTimeMillis();
    protected void render() {
    	Effect effect = effects[nextEffectIdx];
    	
    	effect.renderReady();
    	
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glPushMatrix();
        glRotatef(zRot, 0.0f, 0.0f, 1.0f);
        glRotatef(xRot, 1.0f, 0.0f, 0.0f);
        glRotatef(yRot, 0.0f, 1.0f, 0.0f);

        // Lighting is still all wrong, it's "stuck" to the model

        glLight(GL_LIGHT0, GL_POSITION, toBuffer(position));
        glLightModelf(GL_LIGHT_MODEL_LOCAL_VIEWER, 0.0f);

        glMaterial(GL_FRONT, GL_AMBIENT, toBuffer(ambient));
        glMaterial(GL_FRONT, GL_DIFFUSE, toBuffer(diffuse));
        glMaterial(GL_FRONT, GL_SPECULAR, toBuffer(specular));
        glMaterialf(GL_FRONT, GL_SHININESS, 75);

        Teapot.glutSolidTeapot(50, false);
        glPopMatrix();
        effect.renderEnd();
        
        long currTime = System.currentTimeMillis();
    	if( (currTime - lastTime) > effectKeepTime ) {
    		lastTime = currTime;
    		nextEffectIdx++;
    		if( nextEffectIdx >= effects.length ) {
    			nextEffectIdx = 0;
    		}
    		effect = effects[ nextEffectIdx ];
    		effect.apply();
    	}
    }

    protected void processInput() {

    	Keyboard.poll();
        Keyboard.next();
        
        if( !Keyboard.getEventKeyState() ) return;

	    if( Keyboard.KEY_ESCAPE == Keyboard.getEventKey() ) {
			Display.destroy();
			System.exit(1);
		}
	    
	    int key = Keyboard.getEventKey();
	    switch (key) {
	        case Keyboard.KEY_UP:
	            xRot -= 5.0f;
	            break;
	        case Keyboard.KEY_DOWN:
	            xRot += 5.0f;
	            break;
	        case Keyboard.KEY_LEFT:
	            yRot -= 5.0f;
	            break;
	        case Keyboard.KEY_RIGHT:
	            yRot += 5.0f;
	            break;
	        case Keyboard.KEY_COMMA:
	            zRot -= 5.0f;
	            break;
	        case Keyboard.KEY_PERIOD:
	            zRot += 5.0f;
	            break;
	
	    }
    }

    protected void onResize(int w, int h) {
        glViewport(0, 0, w, h);
        float aspect = (float) w / (float) h;
        if (w <= h) {
            glOrtho(-100.0, 100.0, -100 / aspect, 100.0 / aspect, 100.0, -100.0);
        } else {
            glOrtho(-100.0 * aspect, 100.0 * aspect, -100.0, 100.0, 100.0, -100.0);
        }
    }
}