package cdg.util;

import cdg.interfaces.*;

public class Matrix4x4 implements IMatrix 
{

	float[] matrix;
	
	public Matrix4x4(float[] matrix)
	{
		this.matrix = matrix;
	}
	
	public Matrix4x4(float x0, float y0, float z0, float w0,
					 float x1, float y1, float z1, float w1,
					 float x2, float y2, float z2, float w2,
					 float x3, float y3, float z3, float w3)
	{
		this(new float[]{x0,y0,z0,w0,x1,y1,z1,w1,x2,y2,z2,w2,x3,y3,z3,w3});
	}
	
	@Override
	public float[] toArray()
	{
		return this.matrix;
	}
	
	@Override
	public IVertex multiply(IVertex vertex) {
		return new Vertex4(vertex.getX()*this.matrix[0]+vertex.getY()*this.matrix[1]+vertex.getZ()*this.matrix[2]+vertex.getW()*this.matrix[3], 
						   vertex.getX()*this.matrix[4]+vertex.getY()*this.matrix[5]+vertex.getZ()*this.matrix[6]+vertex.getW()*this.matrix[7], 
						   vertex.getX()*this.matrix[8]+vertex.getY()*this.matrix[9]+vertex.getZ()*this.matrix[10]+vertex.getW()*this.matrix[11], 
						   vertex.getX()*this.matrix[12]+vertex.getY()*this.matrix[13]+vertex.getZ()*this.matrix[14]+vertex.getW()*this.matrix[15]);
	}

	
	public static Matrix4x4 getIdentity() {
		// TODO Auto-generated method stub
		return new Matrix4x4(1.0f, 0.0f, 0.0f, 0.0f,
							 0.0f, 1.0f, 0.0f, 0.0f,
							 0.0f, 0.0f, 1.0f, 0.0f,
							 0.0f, 0.0f, 0.0f, 1.0f);
	}

}
