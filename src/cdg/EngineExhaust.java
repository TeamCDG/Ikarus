package cdg;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import cdg.interfaces.IEntity;
import cdg.interfaces.IMatrix;
import cdg.util.Matrix4x4;
import cdg.util.MatrixTypes;
import cdg.util.ShaderProgram;
import cdg.util.StaticManager;
import cdg.util.Utility;
import cdg.util.VertexData;

public class EngineExhaust extends Entity2D {

	private Random r = new Random();
	
	public static int ENGINE_EXHAUST_TEXTURE_ID;
	public static ShaderProgram ENGINE_EXHAUST_SHADER;
	
	public EngineExhaust(long id, float x, float y) {
		super(id, 0.0f, 0.0f, 0.02f, 0.08f, EngineExhaust.ENGINE_EXHAUST_TEXTURE_ID, EngineExhaust.ENGINE_EXHAUST_SHADER, new VertexData[]{
				   new VertexData(new float[]{x-(0.02f/2.0f),y+(0.08f/2.0f),0.0f,1.0f}, new float[]{1.0f,1.0f,1.0f,1.0f}, new float[]{0.0f, 0.0f}),
				   new VertexData(new float[]{x-(0.02f/2.0f),y-(0.08f/2.0f),0.0f,1.0f}, new float[]{1.0f,1.0f,1.0f,1.0f}, new float[]{0.0f, 1.0f}),
				   new VertexData(new float[]{x+(0.02f/2.0f),y-(0.08f/2.0f),0.0f,1.0f}, new float[]{1.0f,1.0f,1.0f,1.0f}, new float[]{1.0f, 1.0f}),
				   new VertexData(new float[]{x+(0.02f/2.0f),y+(0.08f/2.0f),0.0f,1.0f}, new float[]{1.0f,1.0f,1.0f,1.0f}, new float[]{1.0f, 0.0f})},
				   new byte[]{0, 1, 2, 2, 3, 0});
	}
	public void doTick(float deg, float x, float y)
	{
		this.setRotation(deg);
		this.setPosition(x, y);
	}

	@Override
	public void damage(float f) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void passShaderVariables() {
		this.getShader().passMatrix(StaticManager.WINDOW_MATRIX, MatrixTypes.WINDOW);
		this.getShader().pass2f("seed", r.nextInt(1338), r.nextInt(1338));
	}

	@Override
	public void drawChilds() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doTick() {
		// TODO Auto-generated method stub
		
	}

}
