package kr.co.iniline.j2glsl;

public class vec3 extends ShaderVar {
	public float x;
	public float y;
	public float z;

	vec3() {
	}

	vec3(vec3 arg) {
		set(arg);
	}
	
	vec3(vec4 arg) {
	}

	vec3(float x, float y, float z) {
		set(x, y, z);
	}

	public vec3 copy() {
		return new vec3(this);
	}

	public void set(vec3 arg) {
		set(arg.x, arg.y, arg.z);
	}

	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
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
		case 2:
			z = val;
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
		case 2:
			return z;
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

	public float z() {
		return z;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public float dot(vec3 arg) {
		return x * arg.x + y * arg.y + z * arg.z;
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
	public vec3 times(float val) {
		vec3 tmp = new vec3(this);
		tmp.scale(val);
		return tmp;
	}

	/** this = this * val */
	public void scale(float val) {
		x *= val;
		y *= val;
		z *= val;
	}

	/** Returns this + arg; creates new vector */
	public vec3 plus(vec3 arg) {
		vec3 tmp = new vec3();
		tmp.add(this, arg);
		return tmp;
	}
	
	/** Returns this + arg; creates new vector */
	public vec3 plus(float arg) {
		vec3 tmp = new vec3();
		tmp.add(this, arg);
		return tmp;
	}

	/** this = this + b */
	public void add(vec3 b) {
		add(this, b);
	}
	
	/** this = a + b */
	public void add(vec3 a, float b) {
		x = a.x + b;
		y = a.y + b;
		z = a.z + b;
	}

	/** this = a + b */
	public void add(vec3 a, vec3 b) {
		x = a.x + b.x;
		y = a.y + b.y;
		z = a.z + b.z;
	}

	/** Returns this + s * arg; creates new vector */
	public vec3 addScaled(float s, vec3 arg) {
		vec3 tmp = new vec3();
		tmp.addScaled(this, s, arg);
		return tmp;
	}

	/** this = a + s * b */
	public void addScaled(vec3 a, float s, vec3 b) {
		x = a.x + s * b.x;
		y = a.y + s * b.y;
		z = a.z + s * b.z;
	}

	/** Returns this - arg; creates new vector */
	public vec3 minus(vec3 arg) {
		vec3 tmp = new vec3();
		tmp.sub(this, arg);
		return tmp;
	}
	
	/** Returns this - arg; creates new vector */
	public vec3 minus(float arg) {
		vec3 tmp = new vec3();
		tmp.sub(this, arg);
		return tmp;
	}
	
	/** this = this - b */
	public void sub(vec3 a, float b) {
		x = a.x - b;
		y = a.y - b;
		z = a.z - b;
	}

	/** this = this - b */
	public void sub(vec3 b) {
		sub(this, b);
	}

	/** this = a - b */
	public void sub(vec3 a, vec3 b) {
		x = a.x - b.x;
		y = a.y - b.y;
		z = a.z - b.z;
	}

	/** Returns this cross arg; creates new vector */
	public vec3 cross(vec3 arg) {
		vec3 tmp = new vec3();
		tmp.cross(this, arg);
		return tmp;
	}

	/**
	 * this = a cross b. NOTE: "this" must be a different vector than both a and
	 * b.
	 */
	public void cross(vec3 a, vec3 b) {
		x = a.y * b.z - a.z * b.y;
		y = a.z * b.x - a.x * b.z;
		z = a.x * b.y - a.y * b.x;
	}

	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
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
	public vec3 plus(ShaderVar var) {
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
	public vec3 minus(ShaderVar var) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public vec3 minus() {
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
	public vec3 mult(ShaderVar var) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public vec3 mult(float var) {
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
	public vec3 div(ShaderVar var) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public vec3 div(float var) {
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
