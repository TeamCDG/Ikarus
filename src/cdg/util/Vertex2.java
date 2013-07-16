package cdg.util;

import cdg.interfaces.*;

public class Vertex2 implements IVertex {
	
	float x, y, z, w;

	public Vertex2(float x, float y) {
		this.x = x;
		this.y = y;
		this.z = 0.0f;
		this.w = 1.0f;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public float getZ() {
		return this.z;
	}

	public float getW() {
		return this.w;
	}

}
