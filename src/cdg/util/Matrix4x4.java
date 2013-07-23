package cdg.util;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import cdg.interfaces.*;

public class Matrix4x4 implements IMatrix 
{

	float[] matrix;
	FloatBuffer matrixBuf = BufferUtils.createFloatBuffer(16);
	
	public Matrix4x4(float[] matrix)
	{
		this.matrix = matrix;
		this.updateMatrixBuffer();
	}
	
	public Matrix4x4(float x0, float y0, float z0, float w0,
					 float x1, float y1, float z1, float w1,
					 float x2, float y2, float z2, float w2,
					 float x3, float y3, float z3, float w3)
	{
		this(new float[]{x0,y0,z0,w0,x1,y1,z1,w1,x2,y2,z2,w2,x3,y3,z3,w3});
	}
	
	private void updateMatrixBuffer()
	{
		this.matrixBuf.clear();
		this.matrixBuf.put(this.matrix);
		this.matrixBuf.flip();	
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

	@Override
	public void set(float x0, float y0, float z0, float w0, float x1, float y1,
					float z1, float w1, float x2, float y2, float z2, float w2,
					float x3, float y3, float z3, float w3) {
	
		// TODO Auto-generated method stub
		this.matrix[0] = x0;
		this.matrix[1] = y0;
		this.matrix[2] = z0;
		this.matrix[3] = w0;
		
		this.matrix[4] = x1;
		this.matrix[5] = y1;
		this.matrix[6] = z1;
		this.matrix[7] = w1;
		
		this.matrix[8] = x2;
		this.matrix[9] = y2;
		this.matrix[10] = z2;
		this.matrix[11] = w2;
		
		this.matrix[12] = x3;
		this.matrix[13] = y3;
		this.matrix[14] = z3;
		this.matrix[15] = w3;
				
		this.updateMatrixBuffer();
		
	}

	@Override
	public FloatBuffer getAsBuffer() {
		// TODO Auto-generated method stub
		return this.matrixBuf;
	}

}
