package cdg.nut.gui;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import cdg.StaticManager;
import cdg.nut.util.Matrix4x4;
import cdg.nut.util.MatrixTypes;
import cdg.nut.util.VertexData;

public class InnerFrame extends Frame 
{
	private final float[] FRAME_COLOR = new float[]{0.9f, 0.9f, 0.9f, 1.0f};
	private final float FRAME_SIZE = 0.01f;
	private int indiciesVBO;
	private int VBO;
	private int VAO;
	private int indiciesCount;
	private int height;
	private float width;
	
	public InnerFrame(float width, float height, float x, float y, String title, int iconID, Matrix4x4 winMat)
	{
		super(winMat);
		setupGL();
	}

	private void setupGL()
	{
		VertexData[] points = new VertexData[]{//Background
											   new VertexData(new float[]{0.0f,0.0f,0.0f,1.0f}, 
												   new float[]{0.2f, 0.2f, 0.2f, 0.90f}, new float[]{0.0f, 0.0f}),
												   
				   							   new VertexData(new float[]{0.0f,-this.height,0.0f,1.0f}, 
				   									new float[]{0.2f, 0.2f, 0.2f, 0.90f}, new float[]{0.0f, 0.0f}),
				   								   
				   							   new VertexData(new float[]{this.width,-this.height,0.0f,1.0f}, 
				   									new float[]{0.2f, 0.2f, 0.2f, 0.90f}, new float[]{0.0f, 0.0f}),
				   								   
				   							   new VertexData(new float[]{this.width,0.0f,0.0f,1.0f}, 
				   									new float[]{0.2f, 0.2f, 0.2f, 0.90f}, new float[]{0.0f, 0.0f}),
				   									
				   							   //Left Frame
				   							   new VertexData(new float[]{0.0f,0.0f,0.0f,1.0f}, 
													FRAME_COLOR, new float[]{0.0f, 0.0f}),
															   
							   				   new VertexData(new float[]{0.0f,-this.height,0.0f,1.0f}, 
							   						FRAME_COLOR, new float[]{0.0f, 0.0f}),
							   								   
											   new VertexData(new float[]{FRAME_SIZE,-this.height,0.0f,1.0f}, 
													FRAME_COLOR, new float[]{0.0f, 0.0f}),
							   								   
				   							   new VertexData(new float[]{FRAME_SIZE,0.0f,0.0f,1.0f}, 
							   						FRAME_COLOR, new float[]{0.0f, 0.0f}),
							   						
							   					//Top Frame
						   						new VertexData(new float[]{0.0f,0.0f,0.0f,1.0f}, 
													FRAME_COLOR, new float[]{0.0f, 0.0f}),
																	   
									   			new VertexData(new float[]{0.0f,-FRAME_SIZE,0.0f,1.0f}, 
									   				FRAME_COLOR, new float[]{0.0f, 0.0f}),
									   								   
												new VertexData(new float[]{this.width,-FRAME_SIZE,0.0f,1.0f}, 
						   							FRAME_COLOR, new float[]{0.0f, 0.0f}),
									   								   
						   						new VertexData(new float[]{this.width,0.0f,0.0f,1.0f}, 
									   				FRAME_COLOR, new float[]{0.0f, 0.0f}),
									   				
									   			//Right Frame
						   					    new VertexData(new float[]{this.width-FRAME_SIZE,0.0f,0.0f,1.0f}, 
													FRAME_COLOR, new float[]{0.0f, 0.0f}),
																   
							   				    new VertexData(new float[]{this.width-FRAME_SIZE,-this.height,0.0f,1.0f}, 
							   						FRAME_COLOR, new float[]{0.0f, 0.0f}),
									   								   
											    new VertexData(new float[]{this.width,-this.height,0.0f,1.0f}, 
													FRAME_COLOR, new float[]{0.0f, 0.0f}),
									   								   
				   							    new VertexData(new float[]{this.width,0.0f,0.0f,1.0f}, 
							   						FRAME_COLOR, new float[]{0.0f, 0.0f}),
									   						
							   					//Bot Frame
											    new VertexData(new float[]{0.0f,-this.height-FRAME_SIZE,0.0f,1.0f}, 
													FRAME_COLOR, new float[]{0.0f, 0.0f}),
																			   
									   		    new VertexData(new float[]{0.0f,-this.height,0.0f,1.0f}, 
													FRAME_COLOR, new float[]{0.0f, 0.0f}),
											   								   
											    new VertexData(new float[]{this.width,-this.height,0.0f,1.0f}, 
								   					FRAME_COLOR, new float[]{0.0f, 0.0f}),
											   								   
								   				new VertexData(new float[]{this.width,-this.height-FRAME_SIZE,0.0f,1.0f}, 
									   				FRAME_COLOR, new float[]{0.0f, 0.0f})
				   								};
		
											   
		byte[] indices = {
				0, 1, 2,
				2, 3, 0, //Background
				
				4, 5, 6,
				6, 7, 4, //Left Frame
				
				8, 9, 10,
				10, 11, 8, //Top Frame
				
				12, 13, 14,
				14, 15, 12, //Right Frame
				
				16, 17, 18,
				18, 19, 16 //Bot Frame
				
		};
		
		setupGL(points, indices);
	}
	
	private void setupGL(VertexData[] points, byte[] indices)
	{
		//System.out.println(points.length);
		// Put each 'Vertex' in one FloatBuffer
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(points.length *
				VertexData.ELEMENT_COUNT);
		for (int i = 0; i < points.length; i++) {
			// Add position, color and texture floats to the buffer
			verticesBuffer.put(points[i].getElements());
		}
		verticesBuffer.flip();	
		
		indiciesCount = indices.length;
		ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indiciesCount);
		indicesBuffer.put(indices);
		indicesBuffer.flip();
		
		// Create a new Vertex Array Object in memory and select it (bind)
		if(this.VAO == -1)
			VAO = GL30.glGenVertexArrays();
		
		GL30.glBindVertexArray(VAO);
		
		// Create a new Vertex Buffer Object in memory and select it (bind)
		if(this.VBO == -1)
			VBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
		
		// Put the position coordinates in attribute list 0
		GL20.glVertexAttribPointer(0, VertexData.POSITION_ELEMENT_COUNT, GL11.GL_FLOAT, 
				false, VertexData.STRIDE, VertexData.POSITION_BYTE_OFFSET);
		// Put the color components in attribute list 1
		GL20.glVertexAttribPointer(1, VertexData.COLOR_ELEMENT_COUNT, GL11.GL_FLOAT, 
				false, VertexData.STRIDE, VertexData.COLOR_BYTE_OFFSET);
		// Put the texture coordinates in attribute list 2
		GL20.glVertexAttribPointer(2, VertexData.COLOR_ELEMENT_COUNT, GL11.GL_FLOAT, 
				false, VertexData.STRIDE, VertexData.TEXTURE_BYTE_OFFSET);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		// Deselect (bind to 0) the VAO
		GL30.glBindVertexArray(0);
		
		// Create a new VBO for the indices and select it (bind) - INDICES
		if(this.indiciesVBO == -1)
			indiciesVBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indiciesVBO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		
	}
	
	@Override
	public void select()
	{
		this.drawSizeFrame();
		this.drawMoveFrame();
	}
	
	private void drawMoveFrame() {
		// TODO Auto-generated method stub
		
	}

	private void drawSizeFrame() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawComponents()
	{
		this.drawFrame();
		super.drawComponents();
	}

	private void drawFrame() {
		
		Component.DEFAULT_MAIN_SHADER.bind();
		GL30.glBindVertexArray(this.VAO);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
				
		// Bind to the index VBO that has all the information about the order of the vertices
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.indiciesVBO);
		
		/*
		this.shader.passMatrix(this.getScalingMatrix(), MatrixTypes.SCALING);
		this.shader.passMatrix(this.getTranslationMatrix(), MatrixTypes.TRANSLATION);
		this.shader.passMatrix(this.getRotationMatrix(), MatrixTypes.ROTATION);
		*/
		Component.DEFAULT_MAIN_SHADER.passMatrix(StaticManager.WINDOW_MATRIX, MatrixTypes.WINDOW);
		
		int val = true ? 1 : 0;
		Component.DEFAULT_MAIN_SHADER.pass1i("selection", val);
		val = 0;
		
		
		// Draw the vertices
		GL11.glDrawElements(GL11.GL_TRIANGLES, this.indiciesCount, GL11.GL_UNSIGNED_BYTE, 0);
				
		// Put everything back to default (deselect)
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		
		Component.DEFAULT_MAIN_SHADER.unbind();
		
	}
	
}
