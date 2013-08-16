package cdg;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import cdg.interfaces.IVertex;
import cdg.util.BitmapFont;
import cdg.util.Matrix4x4;
import cdg.util.MatrixTypes;
import cdg.util.ShaderProgram;
import cdg.util.StaticManager;
import cdg.util.Utility;
import cdg.util.Vertex2;
import cdg.util.Vertex4;
import cdg.util.VertexData;

public class FontObject {

	private int VAO = -1;
	private int VBO = -1;
	private int iVBO = -1;
	private int iCount;
	
	public static ShaderProgram SHADER;	
	private BitmapFont font;
	
	private Matrix4x4 translationMatrix = Matrix4x4.getIdentity();
	private Matrix4x4 scalingMatrix = Matrix4x4.getIdentity();
	private Matrix4x4 rotationMatrix = Matrix4x4.getIdentity();
	
	private ShaderProgram shader;
	private Vertex2 textSize;
	private float x;
	private float y;
	private float[] color = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
	private String text;
	private Vertex4 visibleArea = new Vertex4(-1.0f,-1.0f,2.0f,2.0f);
	private Vertex2 rawTextSize;
	
	
	public FontObject(float x, float y, String text, BitmapFont font) 
	{
		this(x,y,text,font, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, FontObject.SHADER);			
	}
	
	public FontObject(float x, float y, String text, BitmapFont font, float[] color, ShaderProgram shader) 
	{
		this.font = font;
		this.text = text;
		this.x = x;
		this.y = y;
		this.shader = shader;
		this.color = color;
		this.rawTextSize = new Vertex2(0,this.font.getHeight('A'));
		this.textSize = this.scalingMatrix.multiply(this.rawTextSize).toVertex2();
		setupGL(text);
		this.translationMatrix.set(1.0f, 0.0f, 0.0f, 0.0f,
				   				   0.0f, 1.0f, 0.0f, 0.0f,
				   				   0.0f, 0.0f, 1.0f, 0.0f,
				   				   x,    y,    0.0f, 1.0f);
			
	}
	
	public FontObject(float x, float y, String text, BitmapFont font, float[] color) 
	{
		this(x,y,text,font, color, FontObject.SHADER);
	}
	
	public FontObject(float x, float y, String text, BitmapFont font, ShaderProgram shader) 
	{
		this(x,y,text,font, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, shader);	
	}
	
	private void setupGL(String text)
	{

		if(text != "")
		{			
			text += (char) 0;
			this.iCount = text.length()*3*2;
			VertexData[] textPoints = new VertexData[text.length()*4];
			float xoff = 0.0f;
			float yoff = 0.0f;
			IntBuffer indicesBuffer = BufferUtils.createIntBuffer(this.iCount);
			
			float xoffmax = 0.0f;
			for(int i = 0; i < text.length(); i++)
			{
				char c = text.charAt(i);
				if(c == '\n')
				{
					xoff = 0.0f;
					yoff += this.font.getHeight('A');
				}
				textPoints[i*4+0] = new VertexData(new float[]{xoff,-yoff,0.0f,1.0f}, 
											 color,
											 new float[]{this.font.getX(c),this.font.getY(c)});
				
				textPoints[i*4+1] = new VertexData(new float[]{xoff,-yoff-this.font.getHeight(c),0.0f,1.0f}, 
											 color,
						 					 new float[]{this.font.getX(c),this.font.getY(c)+this.font.getHeight(c)});
				
				textPoints[i*4+2] = new VertexData(new float[]{xoff+this.font.getWidth(c),-yoff-this.font.getHeight(c),0.0f,1.0f}, 
											 color,
						 					 new float[]{this.font.getX(c)+this.font.getWidth(c),this.font.getY(c)+this.font.getHeight(c)});
				
				textPoints[i*4+3] = new VertexData(new float[]{xoff+this.font.getWidth(c),-yoff,0.0f,1.0f}, 
											 color,
						 					 new float[]{this.font.getX(c)+this.font.getWidth(c),this.font.getY(c)});	
				/*
				textPoints[i*4+0] = new VertexData(new float[]{xoff,-yoff,0.0f,1.0f}, 
						 new float[]{1.0f, 1.0f, 1.0f, 1.0f},
						 new float[]{FontFinals.getX(c)+FontFinals.getWidth(c),FontFinals.getY(c)});

				textPoints[i*4+1] = new VertexData(new float[]{xoff,-yoff-FontFinals.getHeight(c),0.0f,1.0f}, 
						 new float[]{1.0f, 1.0f, 1.0f, 1.0f},
	 					 new float[]{FontFinals.getX(c)+FontFinals.getWidth(c),FontFinals.getY(c)+FontFinals.getHeight(c)});

				textPoints[i*4+2] = new VertexData(new float[]{xoff+FontFinals.getWidth(c),-yoff-FontFinals.getHeight(c),0.0f,1.0f}, 
						 new float[]{1.0f, 1.0f, 1.0f, 1.0f},
	 					 new float[]{FontFinals.getX(c),FontFinals.getY(c)+FontFinals.getHeight(c)});

				textPoints[i*4+3] = new VertexData(new float[]{xoff+FontFinals.getWidth(c),-yoff,0.0f,1.0f}, 
						 new float[]{1.0f, 1.0f, 1.0f, 1.0f},
	 					 new float[]{FontFinals.getX(c),FontFinals.getY(c)});
	 					 */
				xoff += this.font.getWidth(c);
				
				if(xoff > xoffmax )
					xoffmax = xoff;
				
				indicesBuffer.put(new int[]{(int) (0+i*4),
						 (int) (1+i*4), 
						 (int) (2+i*4), 
						 (int) (2+i*4), 
						 (int) (3+i*4), 
						 (int) (0+i*4)}); //put in indicies
				
			}
			indicesBuffer.flip();
			
			FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(textPoints.length *
					VertexData.ELEMENT_COUNT);
			for (int i = 0; i < textPoints.length; i++) {
				// Add position, color and texture floats to the buffer
				verticesBuffer.put(textPoints[i].getElements());
			}
			verticesBuffer.flip();	
			
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
			if(this.iVBO == -1)
				iVBO = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, iVBO);
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
			
			Vertex4 textEdge = (Vertex4) StaticManager.WINDOW_MATRIX.multiply(this.scalingMatrix.multiply((IVertex) new Vertex2(xoffmax, yoff+this.font.getHeight('A'))));
			this.textSize = new Vertex2(textEdge.getX(), textEdge.getY());
		}
	}
	
	public void draw()
	{
		this.shader.bind();
		
		if(this.font.getFontTextureID() != -1)
		{
			// Bind the texture
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.font.getFontTextureID() );
		}
				
		// Bind to the VAO that has all the information about the vertices
		GL30.glBindVertexArray(this.VAO);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
				
		// Bind to the index VBO that has all the information about the order of the vertices
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.iVBO);
		
		this.shader.passMatrix(scalingMatrix, MatrixTypes.SCALING);
		this.shader.passMatrix(translationMatrix, MatrixTypes.TRANSLATION);
		this.shader.passMatrix(rotationMatrix, MatrixTypes.ROTATION);
		this.shader.passMatrix(StaticManager.WINDOW_MATRIX, MatrixTypes.WINDOW);
		this.shader.pass1i("selection", 0);
		this.shader.pass4f("color",this.color[0],this.color[1],this.color[2],this.color[3]);
		this.shader.pass4f("visible_Area",this.visibleArea.getX(),this.visibleArea.getY(),this.visibleArea.getZ(),this.visibleArea.getW());
		// Draw the vertices
		GL11.glDrawElements(GL11.GL_TRIANGLES, this.iCount, GL11.GL_UNSIGNED_INT, 0);
				
		// Put everything back to default (deselect)
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		
		this.shader.unbind();
	}
	
	public ShaderProgram getShader() {
		return shader;
	}

	public void setShader(ShaderProgram shader) {
		this.shader = shader;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setText(String text)
	{
		this.text = text;
		setupGL(text);
	}
	
	public String getText()
	{
		return this.text;
	}
	
	public Vertex2 getPosition()
	{
		return new Vertex2(this.x,this.y);
	}
	
	public void setPosition(Vertex2 pos)
	{
		this.x = pos.getX();
		this.y = pos.getY();
		this.translationMatrix.set(1.0f, 0.0f, 0.0f, 0.0f,
				   				   0.0f, 1.0f, 0.0f, 0.0f,
				   				   0.0f, 0.0f, 1.0f, 0.0f,
				   				   x,    y,    0.0f, 1.0f);
	}
	
	public void setPosition(float x, float y)
	{
		this.x = x;
		this.y = y;
		this.translationMatrix.set(1.0f, 0.0f, 0.0f, 0.0f,
				   				   0.0f, 1.0f, 0.0f, 0.0f,
				   				   0.0f, 0.0f, 1.0f, 0.0f,
				   				   x,    y,    0.0f, 1.0f);
	}
	
	public void scale(float f)
	{
		this.scalingMatrix.set(   f, 0.0f, 0.0f, 0.0f,
				   				   0.0f,    f, 0.0f, 0.0f,
				   				   0.0f, 0.0f,    f, 0.0f,
				   				   0.0f, 0.0f, 0.0f, 1.0f);

		this.textSize = this.scalingMatrix.multiply(this.rawTextSize).toVertex2();
	}

	/**
	 * @return the color
	 */
	public float[] getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(float[] color) {
		this.color = color;
		//this.setupGL(this.text);
	}

	/**
	 * @return the textSize
	 */
	public Vertex2 getTextSize() {
		return textSize;
	}

	/**
	 * @return the visibleArea
	 */
	public Vertex4 getVisibleArea() {
		return visibleArea;
	}

	/**
	 * @param visibleArea the visibleArea to set
	 */
	public void setVisibleArea(Vertex4 visibleArea) {
		this.visibleArea = visibleArea;
	}

	public void setFont(BitmapFont font) {
		this.font = font;
		setupGL(this.text);
	}
	
	public void move(float x, float y)
	{
		this.x += x;
		this.y += y;
	}
}
