package cdg.nut.util;

import cdg.interfaces.*;

public class Vertex4 implements IVertex {
	
	float x, y, z, w;
	
	public Vertex4(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Vertex4(Vertex2 vertex2) {
		this(vertex2.getX(), vertex2.getY(), 0.0f, 1.0f);
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
	
	public Vertex2 toVertex2()
	{
		return new Vertex2(this.x,this.y);
	}

	@Override
	public boolean isGreater(IVertex v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEqual(IVertex v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLess(IVertex v) {
		// TODO Auto-generated method stub
		return false;
	}

}
