package cdg;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import cdg.util.BitmapFont;
import cdg.util.FrameLabel;
import cdg.util.MatrixTypes;
import cdg.util.ShaderProgram;
import cdg.util.StaticManager;
import cdg.util.Utility;
import cdg.util.Vertex4;
import cdg.util.VertexData;

public class GLConsole 
{
	
	//public static final int HISTORY_SIZE = 600;
	
	
	private BitmapFont font;
	private FontObject console;
	private FontObject textBox;
	private FontObject title;
	private int closeTextureID = -1;
	private float x;
	private float y;
	private float width;
	private float height;
	private float scaleFactor = 0.5f;
	private boolean visible;
	private ShaderProgram consoleTextShader;
	private int indiciesCount;
	private int VAO = -1;
	private int VBO = -1;
	private int indiciesVBO = -1;
	
	private final int WINDOW_EDGE_ID = (2^24)-1;
	private final int WINDOW_CLOSE_BUTTON_ID = (2^24)-2;
	private final int WINDOW_CONSOLE_PART_ID = (2^24)-3;
	private final int WINDOW_TEXT_BOX_ID = (2^24)-4;
	private final int WINDOW_UNUSED_ID = (2^24)-5;
	
	private final float INNER_FRAME_SIZE = 0.01f;
	private final float FRAME_SIZE = 0.01f;
	
	private final float SELECTION_FRAME_SIZE = 0.05f;
	
	private final float[] TEST_COLOR = new float[]{1.0f, 0.0f, 0.0f, 1.0f};
	private final float[] FRAME_COLOR = new float[]{0.9f, 0.9f, 0.9f, 1.0f};
	
	private ShaderProgram shader;
	private float textX;
	private float textY;
	private float textVX;
	private float textVY;
	

	private int oldMouseX;
	private int oldMouseY;
	
	private FrameLabel con;
	
	public GLConsole(BitmapFont font)
	{
		this.font = font;
		this.consoleTextShader = new ShaderProgram("res/shader/console_text.vert", "res/shader/console_text.frag");
		this.shader = new ShaderProgram("res/shader/console.vert","res/shader/console.frag");
		this.width = 0.8f;
		this.height = 0.8f;
		this.x = 0;
		this.y = 0;
		this.textX = SELECTION_FRAME_SIZE+INNER_FRAME_SIZE+0.005f;
		this.textY = -SELECTION_FRAME_SIZE-INNER_FRAME_SIZE-0.005f;
		this.textVX = this.x + this.width-SELECTION_FRAME_SIZE-INNER_FRAME_SIZE - 0.005f;
		this.textVY = this.y - this.height+(2*SELECTION_FRAME_SIZE)+(scaleFactor*font.getHeight('A')+(2*INNER_FRAME_SIZE)) + 0.005f;
		this.console = new FontObject(this.textX, this.textY, "", font, this.consoleTextShader);
		this.console.setVisibleArea(new Vertex4(this.textX,this.textY,this.textVX,this.textVY));
		this.console.scale(scaleFactor);
		this.title = new FontObject(INNER_FRAME_SIZE+0.005f, -INNER_FRAME_SIZE, "GLConsole", font);
		this.title.scale(0.5f);
		this.closeTextureID = Utility.loadPNGTextureSmooth("res/icons/close.png", GL13.GL_TEXTURE0);
		
		this.con = new FrameLabel(7777, SELECTION_FRAME_SIZE, -SELECTION_FRAME_SIZE, 0.4f, 0.4f, font, "aiwdhaiwdh\nawdadw\nwdawdawdawd\n\nawdawdawd");
		
		this.textBox = new FontObject(this.textX, this.textVY-SELECTION_FRAME_SIZE-(2*INNER_FRAME_SIZE), "blaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", font, this.consoleTextShader);
		this.textBox.scale(this.scaleFactor);
		this.textBox.setVisibleArea(new Vertex4(this.textX, this.textVY-SELECTION_FRAME_SIZE-(2*INNER_FRAME_SIZE), this.textVX, this.textVY-SELECTION_FRAME_SIZE-(2*INNER_FRAME_SIZE)-(scaleFactor*font.getHeight('A'))));
		this.setupGL();
		
	}
	
	private void setupGL()
	{
	
		float consoleFrameHeight = -this.height+(2*SELECTION_FRAME_SIZE)+(scaleFactor*font.getHeight('A')+(2*INNER_FRAME_SIZE));
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
									   				FRAME_COLOR, new float[]{0.0f, 0.0f}),
									   				
						   							   //Console Left Frame
				   							   new VertexData(new float[]{SELECTION_FRAME_SIZE,-SELECTION_FRAME_SIZE,0.0f,1.0f}, 
													FRAME_COLOR, new float[]{0.0f, 0.0f}),
															   
							   				   new VertexData(new float[]{SELECTION_FRAME_SIZE,consoleFrameHeight,0.0f,1.0f}, 
							   						FRAME_COLOR, new float[]{0.0f, 0.0f}),
									   								   
											   new VertexData(new float[]{SELECTION_FRAME_SIZE+INNER_FRAME_SIZE,consoleFrameHeight,0.0f,1.0f}, 
													FRAME_COLOR, new float[]{0.0f, 0.0f}),
									   								   
				   							   new VertexData(new float[]{SELECTION_FRAME_SIZE+INNER_FRAME_SIZE,-SELECTION_FRAME_SIZE,0.0f,1.0f}, 
							   						FRAME_COLOR, new float[]{0.0f, 0.0f}),
									   						
							   					//Console Top Frame
						   						new VertexData(new float[]{SELECTION_FRAME_SIZE,-SELECTION_FRAME_SIZE,0.0f,1.0f}, 
													FRAME_COLOR, new float[]{0.0f, 0.0f}),
																			   
									   			new VertexData(new float[]{SELECTION_FRAME_SIZE,-SELECTION_FRAME_SIZE-INNER_FRAME_SIZE,0.0f,1.0f}, 
									   				FRAME_COLOR, new float[]{0.0f, 0.0f}),
											   								   
												new VertexData(new float[]{this.width-SELECTION_FRAME_SIZE,-SELECTION_FRAME_SIZE-INNER_FRAME_SIZE,0.0f,1.0f}, 
						   							FRAME_COLOR, new float[]{0.0f, 0.0f}),
											   								   
						   						new VertexData(new float[]{this.width-SELECTION_FRAME_SIZE,-SELECTION_FRAME_SIZE,0.0f,1.0f}, 
									   				FRAME_COLOR, new float[]{0.0f, 0.0f}),
											   				
									   			//Console Right Frame
						   					    new VertexData(new float[]{this.width-SELECTION_FRAME_SIZE-FRAME_SIZE,-SELECTION_FRAME_SIZE,0.0f,1.0f}, 
													FRAME_COLOR, new float[]{0.0f, 0.0f}),
																		   
							   				    new VertexData(new float[]{this.width-SELECTION_FRAME_SIZE-FRAME_SIZE,consoleFrameHeight,0.0f,1.0f}, 
							   						FRAME_COLOR, new float[]{0.0f, 0.0f}),
										   								   
											    new VertexData(new float[]{this.width-SELECTION_FRAME_SIZE,consoleFrameHeight,0.0f,1.0f}, 
													FRAME_COLOR, new float[]{0.0f, 0.0f}),
									   								   
				   							    new VertexData(new float[]{this.width-SELECTION_FRAME_SIZE,-SELECTION_FRAME_SIZE,0.0f,1.0f}, 
							   						FRAME_COLOR, new float[]{0.0f, 0.0f}),
									   						
							   					//Console Bot Frame
											    new VertexData(new float[]{SELECTION_FRAME_SIZE,consoleFrameHeight-INNER_FRAME_SIZE,0.0f,1.0f}, 
													FRAME_COLOR, new float[]{0.0f, 0.0f}),
																			   
									   		    new VertexData(new float[]{SELECTION_FRAME_SIZE,consoleFrameHeight,0.0f,1.0f}, 
													FRAME_COLOR, new float[]{0.0f, 0.0f}),
											   								   
											    new VertexData(new float[]{this.width-SELECTION_FRAME_SIZE,consoleFrameHeight,0.0f,1.0f}, 
								   					FRAME_COLOR, new float[]{0.0f, 0.0f}),
												   								   
								   				new VertexData(new float[]{this.width-SELECTION_FRAME_SIZE,consoleFrameHeight-INNER_FRAME_SIZE,0.0f,1.0f}, 
									   				FRAME_COLOR, new float[]{0.0f, 0.0f}),
										   				
									   			//Close Button
											    new VertexData(new float[]{this.width-SELECTION_FRAME_SIZE,0.0f-INNER_FRAME_SIZE,0.0f,1.0f}, 
													FRAME_COLOR, new float[]{1.0f, 0.0f}),
																						   
									   		    new VertexData(new float[]{this.width-SELECTION_FRAME_SIZE,-SELECTION_FRAME_SIZE,0.0f,1.0f}, 
									   		    	FRAME_COLOR, new float[]{1.0f, 1.0f}),
													   								   
											    new VertexData(new float[]{this.width-INNER_FRAME_SIZE,-SELECTION_FRAME_SIZE,0.0f,1.0f}, 
											    	FRAME_COLOR, new float[]{0.0f, 1.0f}),
														   								   
								   				new VertexData(new float[]{this.width-INNER_FRAME_SIZE,0.0f-INNER_FRAME_SIZE,0.0f,1.0f}, 
								   					FRAME_COLOR, new float[]{0.0f, 0.0f}),
								   					
								   				//TextBox Left Frame
				   							   new VertexData(new float[]{SELECTION_FRAME_SIZE,-this.height+SELECTION_FRAME_SIZE+INNER_FRAME_SIZE+scaleFactor*font.getHeight('A'),0.0f,1.0f}, 
													FRAME_COLOR, new float[]{0.0f, 0.0f}),
																	   
							   				   new VertexData(new float[]{SELECTION_FRAME_SIZE,-this.height+SELECTION_FRAME_SIZE,0.0f,1.0f}, 
							   						FRAME_COLOR, new float[]{0.0f, 0.0f}),
									   								   
											   new VertexData(new float[]{SELECTION_FRAME_SIZE+INNER_FRAME_SIZE,-this.height+SELECTION_FRAME_SIZE,0.0f,1.0f}, 
													FRAME_COLOR, new float[]{0.0f, 0.0f}),
									   								   
				   							   new VertexData(new float[]{SELECTION_FRAME_SIZE+INNER_FRAME_SIZE,-this.height+SELECTION_FRAME_SIZE+INNER_FRAME_SIZE+scaleFactor*font.getHeight('A'),0.0f,1.0f}, 
							   						FRAME_COLOR, new float[]{0.0f, 0.0f}),
									   						
							   					//TextBox Top Frame
						   						new VertexData(new float[]{SELECTION_FRAME_SIZE,-this.height+SELECTION_FRAME_SIZE+(2*INNER_FRAME_SIZE)+scaleFactor*font.getHeight('A'),0.0f,1.0f}, 
													FRAME_COLOR, new float[]{0.0f, 0.0f}),
																			   
									   			new VertexData(new float[]{SELECTION_FRAME_SIZE,-this.height+SELECTION_FRAME_SIZE+INNER_FRAME_SIZE+scaleFactor*font.getHeight('A'),0.0f,1.0f}, 
									   				FRAME_COLOR, new float[]{0.0f, 0.0f}),
											   								   
												new VertexData(new float[]{this.width-SELECTION_FRAME_SIZE,-this.height+SELECTION_FRAME_SIZE+INNER_FRAME_SIZE+scaleFactor*font.getHeight('A'),0.0f,1.0f}, 
						   							FRAME_COLOR, new float[]{0.0f, 0.0f}),
											   								   
						   						new VertexData(new float[]{this.width-SELECTION_FRAME_SIZE,-this.height+SELECTION_FRAME_SIZE+(2*INNER_FRAME_SIZE)+scaleFactor*font.getHeight('A'),0.0f,1.0f}, 
									   				FRAME_COLOR, new float[]{0.0f, 0.0f}),
											   				
									   			//TextBox Right Frame
						   					    new VertexData(new float[]{this.width-SELECTION_FRAME_SIZE-INNER_FRAME_SIZE,-this.height+SELECTION_FRAME_SIZE+INNER_FRAME_SIZE+scaleFactor*font.getHeight('A'),0.0f,1.0f}, 
													FRAME_COLOR, new float[]{0.0f, 0.0f}),
																		   
							   				    new VertexData(new float[]{this.width-SELECTION_FRAME_SIZE-INNER_FRAME_SIZE,-this.height+SELECTION_FRAME_SIZE,0.0f,1.0f}, 
							   						FRAME_COLOR, new float[]{0.0f, 0.0f}),
											   								   
											    new VertexData(new float[]{this.width-SELECTION_FRAME_SIZE,-this.height+SELECTION_FRAME_SIZE,0.0f,1.0f}, 
													FRAME_COLOR, new float[]{0.0f, 0.0f}),
											   								   
				   							    new VertexData(new float[]{this.width-SELECTION_FRAME_SIZE,-this.height+SELECTION_FRAME_SIZE+INNER_FRAME_SIZE+scaleFactor*font.getHeight('A'),0.0f,1.0f}, 
							   						FRAME_COLOR, new float[]{0.0f, 0.0f}),
											   						
							   					//TextBox Bot Frame
											    new VertexData(new float[]{SELECTION_FRAME_SIZE,-this.height+INNER_FRAME_SIZE+SELECTION_FRAME_SIZE,0.0f,1.0f}, 
													FRAME_COLOR, new float[]{0.0f, 0.0f}),
																					   
									   		    new VertexData(new float[]{SELECTION_FRAME_SIZE,-this.height+SELECTION_FRAME_SIZE,0.0f,1.0f}, 
													FRAME_COLOR, new float[]{0.0f, 0.0f}),
												   								   
											    new VertexData(new float[]{this.width-SELECTION_FRAME_SIZE,-this.height+SELECTION_FRAME_SIZE,0.0f,1.0f}, 
								   					FRAME_COLOR, new float[]{0.0f, 0.0f}),
													   								   
								   				new VertexData(new float[]{this.width-SELECTION_FRAME_SIZE,-this.height+INNER_FRAME_SIZE+SELECTION_FRAME_SIZE,0.0f,1.0f}, 
									   				FRAME_COLOR, new float[]{0.0f, 0.0f}),
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
				18, 19, 16, //Bot Frame
				
				//*
				20, 21, 22,
				22, 23, 20, //Console Left Frame
				
				24, 25, 26,
				26, 27, 24, //Console Top Frame
				
				28, 29, 30,
				30, 31, 28, //Console Right Frame
				
				32, 33, 34,
				34, 35, 32, //Console Bot Frame
				//*/
				36, 37, 38,
				38, 39, 36, //Close Button
				
				40, 41, 42,
				42, 43, 40, //TextBox Left Frame
				
				44, 45, 46,
				46, 47, 44, //TextBox Top Frame
				
				48, 49, 50,
				50, 51, 48, //TextBox Right Frame
				
				52, 53, 54,
				54, 55, 52, //TextBox Bot Frame
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
	
	public void draw()
	{
		if(this.visible)
		{
			this.shader.bind();
			if(this.closeTextureID != -1)
			{
				// Bind the texture
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.closeTextureID);
			}
				
			// Bind to the VAO that has all the information about the vertices
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
			this.shader.passMatrix(StaticManager.WINDOW_MATRIX, MatrixTypes.WINDOW);
			
			int val = true ? 1 : 0;
			this.shader.pass1i("selection", val);
			val = 0;
			
			
			// Draw the vertices
			GL11.glDrawElements(GL11.GL_TRIANGLES, this.indiciesCount, GL11.GL_UNSIGNED_BYTE, 0);
					
			// Put everything back to default (deselect)
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL20.glDisableVertexAttribArray(2);
			GL30.glBindVertexArray(0);
			
			this.shader.unbind();
			
			if(this.console.getText().trim().length() != 0)
				this.console.draw();
			
			if(this.textBox.getText().trim().length() != 0)
				this.textBox.draw();
			
			this.title.draw();
			//this.con.draw();
		}
	}
	
	public void writeLine(String text)
	{
		this.write(text+"\n", new float[]{1.0f, 1.0f, 1.0f, 1.0f});
	}
	
	public void writeLine(String text, Color color)
	{
		this.write(text+"\n", new float[]{(1.0f/255.0f)*color.getRed(), (1.0f/255.0f)*color.getGreen(), (1.0f/255.0f)*color.getBlue(), (1.0f/255.0f)*color.getAlpha()});
	}	

	public void writeLine(String text, float[] color)
	{
		this.write(text+"\n", color);
	}
	
	public void write(String text)
	{
		this.write(text, new float[]{1.0f, 1.0f, 1.0f, 1.0f});
	}
	
	public void write(String text, Color color)
	{
		this.write(text, new float[]{(1.0f/255.0f)*color.getRed(), (1.0f/255.0f)*color.getGreen(), (1.0f/255.0f)*color.getBlue(), (1.0f/255.0f)*color.getAlpha()});
	}
	
	private void write(String text, float[] color) 
	{
		this.console.setText(this.console.getText()+text);
	}

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * @return the font
	 */
	public BitmapFont getFont() {
		return font;
	}

	/**
	 * @param font the font to set
	 */
	public void setFont(BitmapFont font) {
		this.font = font;
		this.console.setFont(font);
		this.textBox.setFont(font);
	}
	
	
}
