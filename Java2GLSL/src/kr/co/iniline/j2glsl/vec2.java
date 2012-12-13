package kr.co.iniline.j2glsl;

public class vec2 extends ShaderVar {
	public float x;
	public float y;

	vec2() {
	}

	vec2(vec2 arg) {
		set(arg);
	}

	vec2(float x, float y) {
		set(x, y);
	}

	public vec2 copy() {
		return new vec2(this);
	}

	public void set(vec2 arg) {
		set(arg.x, arg.y);
	}

	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/** Sets the ith component, 0 <= i < 3 */
	public void set(int i, float val) {
		switch (i) {
		case 0:
			x = val;
			break;
		case 1:
			y = val;
			break;
		default:
			throw new IndexOutOfBoundsException();
		}
	}

	/** Gets the ith component, 0 <= i < 3 */
	public float get(int i) {
		switch (i) {
		case 0:
			return x;
		case 1:
			return y;
		default:
			throw new IndexOutOfBoundsException();
		}
	}

	public float x() {
		return x;
	}

	public float y() {
		return y;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float dot(vec2 arg) {
		return x * arg.x + y * arg.y;
	}

	public float length() {
		return (float)Math.sqrt((double)lengthSquared());
	}

	public float lengthSquared() {
		return this.dot(this);
	}

	public void normalize() {
		float len = length();
		if (len == 0.0)
			return;
		scale(1.0f / len);
	}

	/** Returns this * val; creates new vector */
	public vec2 times(float val) {
		vec2 tmp = new vec2(this);
		tmp.scale(val);
		return tmp;
	}

	/** this = this * val */
	public void scale(float val) {
		x *= val;
		y *= val;
	}

	/** Returns this + arg; creates new vector */
	public vec2 plus(vec2 arg) {
		vec2 tmp = new vec2();
		tmp.add(this, arg);
		return tmp;
	}
	
	public vec2 plus(float arg) {
		return null;
	}

	/** this = this + b */
	public void add(vec2 b) {
		add(this, b);
	}

	/** this = a + b */
	public void add(vec2 a, vec2 b) {
		x = a.x + b.x;
		y = a.y + b.y;
	}

	/** Returns this + s * arg; creates new vector */
	public vec2 addScaled(float s, vec2 arg) {
		vec2 tmp = new vec2();
		tmp.addScaled(this, s, arg);
		return tmp;
	}

	/** this = a + s * b */
	public void addScaled(vec2 a, float s, vec2 b) {
		x = a.x + s * b.x;
		y = a.y + s * b.y;
	}

	/** Returns this - arg; creates new vector */
	public vec2 minus(vec2 arg) {
		vec2 tmp = new vec2();
		tmp.sub(this, arg);
		return tmp;
	}
	
	/** Returns this - arg; creates new vector */
	public vec2 minus(float arg) {
		vec2 tmp = new vec2();
		tmp.sub(this, arg);
		return tmp;
	}
	
	/** this = this - b */
	public void sub(vec2 a, float b) {
		x = a.x - b;
		y = a.y - b;
	}

	/** this = this - b */
	public void sub(vec2 b) {
		sub(this, b);
	}

	/** this = a - b */
	public void sub(vec2 a, vec2 b) {
		x = a.x - b.x;
		y = a.y - b.y;
	}

	/** Returns this cross arg; creates new vector */
	public vec2 cross(vec2 arg) {
		vec2 tmp = new vec2();
		tmp.cross(this, arg);
		return tmp;
	}

	/**
	 * this = a cross b. NOTE: "this" must be a different vector than both a and
	 * b.
	 */
	public void cross(vec2 a, vec2 b) {
		x = a.y - b.y;
		y = b.x - a.x;
	}

	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	@Override
	public float[] get(String ponits) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public vec2 get2(String ponits) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public vec3 get3(String ponits) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public vec4 get4(String ponits) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public vec2 plus(ShaderVar var) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(ShaderVar var) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void add(float var) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public vec2 minus(ShaderVar var) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public vec2 minus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sub(ShaderVar var) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sub(float var) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public vec2 mult(ShaderVar var) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public vec2 mult(float var) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void multLocal(ShaderVar var) {
		// TODO Auto-generated method stub
	}

	@Override
	public void multLocal(float var) {
		// TODO Auto-generated method stub
	}

	@Override
	public vec2 div(ShaderVar var) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public vec2 div(float var) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void divLocal(ShaderVar var) {
		// TODO Auto-generated method stub
	}

	@Override
	public void divLocal(float var) {
		// TODO Auto-generated method stub
	}

	@Override
	public float[] toFloats() {
		// TODO Auto-generated method stub
		return null;
	}
}
