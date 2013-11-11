package cdg.nut.gui;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import cdg.StaticManager;
import cdg.interfaces.IClickListener;
import cdg.interfaces.IKeyListener;
import cdg.interfaces.IMatrix;
import cdg.interfaces.ISelectListener;
import cdg.interfaces.IVertex;
import cdg.nut.util.BitmapFont;
import cdg.nut.util.Matrix4x4;
import cdg.nut.util.MatrixTypes;
import cdg.nut.util.ShaderProgram;
import cdg.nut.util.Utility;
import cdg.nut.util.Vertex2;
import cdg.nut.util.Vertex4;
import cdg.nut.util.VertexData;

public abstract class Component 
{
	public static final float[] DEFAULT_FRAME_COLOR = new float[]{0.9f, 0.9f, 0.9f, 1.0f};
	public static final float[] DEFAULT_TEXT_COLOR = new float[]{0.9f, 0.9f, 0.9f, 1.0f};
	public static final float[] DEFAULT_BACKGROUND_COLOR = new float[]{0.2f, 0.2f, 0.2f, 0.9f};
	
	public static final float[] DEFAULT_FRAME_H_COLOR = new float[]{1.0f, 0.0f, 0.0f, 1.0f};
	public static final float[] DEFAULT_TEXT_H_COLOR = new float[]{1.0f, 0.0f, 0.0f, 1.0f};
	public static final float[] DEFAULT_BACKGROUND_H_COLOR = new float[]{0.2f, 0.2f, 0.2f, 0.9f};
	
	public static final float DEFAULT_FRAME_SIZE = 0.01f;
	public static ShaderProgram DEFAULT_TEXT_SHADER;
	public static ShaderProgram DEFAULT_MAIN_SHADER;
	
	private int id;
	private int iconTexID;
	private BitmapFont textFont;
	private boolean hasText;
	private String text;
	private float textOffsetX = 0;
	private float textOffsetY = 0;
	private float textWidth;
	private float textHeight;
	private ShaderProgram textShader = Component.DEFAULT_TEXT_SHADER;
	private ShaderProgram mainShader = Component.DEFAULT_MAIN_SHADER;
	private float[] backgroundColor = Component.DEFAULT_BACKGROUND_COLOR;
	private float[] frameColor = Component.DEFAULT_FRAME_COLOR;
	private float[] textColor = Component.DEFAULT_TEXT_COLOR;
	private float frameSize = Component.DEFAULT_FRAME_SIZE;
	private boolean hasFrame;
	private boolean hasBackground;
	private boolean selected = false;
	private int tabId;
	private float textScale = 1.0f;
	
	private float x;
	private float y;
	private float width;
	private float height;
	
	private float iconX;
	private float iconY;
	private float iconWidth = 1.0f;
	private float iconHeight = 1.0f;
	
	private int textVAO = -1;
	private int textVBO = -1;
	private int textIVBO = -1;
	private int textICount = -1;
	
	private int mainVAO = -1;
	private int mainVBO = -1;
	private int mainIVBO = -1;
	private int mainICount = -1;
	
	private int selectionVAO = -1;
	private int selectionVBO = -1;
	private int selectionIVBO = -1;
	private int selectionICount = -1;
	
	private int iconVAO = -1;
	private int iconVBO = -1;
	private int iconIVBO = -1;
	private int iconICount = -1;
	
	private boolean autosizeWithText;
	

	private boolean hasIcon = false;
	private Vertex4 visibleArea;
	private float textEdgeDistance = 0.005f;
	
	private Matrix4x4 translationMatrix = Matrix4x4.getIdentity();
	private Matrix4x4 textTranslationMatrix = Matrix4x4.getIdentity();
	private Matrix4x4 textScaleMatrix = Matrix4x4.getIdentity();
	
	private float scrollX;
	private float scrollY;
	
	private Matrix4x4 iconScalingMatrix = Matrix4x4.getIdentity();
	private Matrix4x4 iconTranslationMatrix = Matrix4x4.getIdentity();
	
	private float[] frameHColor =  Component.DEFAULT_FRAME_H_COLOR;
	private float[] backgroundHColor = Component.DEFAULT_BACKGROUND_H_COLOR;
	private float[] textHColor = Component.DEFAULT_TEXT_H_COLOR;
	
	private Frame parent;
	private boolean selectable = false;
	private float[] textNColor = Component.DEFAULT_TEXT_COLOR;
	private float[] frameNColor = Component.DEFAULT_FRAME_COLOR;
	private float[] backgroundNColor = Component.DEFAULT_BACKGROUND_COLOR;
	
	public ArrayList<IClickListener> clickListener = new ArrayList<IClickListener>();
	public ArrayList<ISelectListener> selectListener = new ArrayList<ISelectListener>();
	public ArrayList<IKeyListener> keyListener = new ArrayList<IKeyListener>();
	private boolean centerText = true;
	private int textCursorPos = 0;
	private boolean clickToSelect = false;
	private boolean active = false;
	private boolean scrollable = false;
	private boolean fixedHeigth;
	private boolean fixedWidth;
	
	
	public Component(float x, float y, float width, float height)
	{
		this(x, y, width, height, true, true);
	}
	
	public Component(float x, float y, float width, float height, boolean frame, boolean background)
	{
		this.setX(x);
		this.setY(y);
		this.width = width;
		this.height = height;
		this.hasFrame = frame;
		this.hasBackground = background;
		this.hasText = false;
		this.setupMainGL();
	}
	
	public Component(float x, float y, float width, float height, boolean frame, boolean background, BitmapFont font, String text)
	{
		this.setX(x);
		this.setY(y);
		this.width = width;
		this.height = height;
		this.hasFrame = frame;
		this.hasBackground = background;
		this.hasText = true;
		this.textFont = font;
		this.text = text;
		this.autosizeWithText = false;
		this.textOffsetX = Component.DEFAULT_FRAME_SIZE;
		this.textOffsetY = Component.DEFAULT_FRAME_SIZE;
		this.textTranslationMatrix.set(1.0f, 0.0f, 0.0f, 0.0f, 
									   0.0f, 1.0f, 0.0f, 0.0f, 
									   0.0f, 0.0f, 1.0f, 0.0f, 
									   this.x+this.textOffsetX+this.scrollX+this.textEdgeDistance, (this.y-this.textOffsetY-this.scrollY-this.textEdgeDistance), 0.0f, 1.0f);

		this.translationMatrix.set(1.0f, 0.0f, 0.0f, 0.0f, 
									   0.0f, 1.0f, 0.0f, 0.0f, 
									   0.0f, 0.0f, 1.0f, 0.0f, 
									   this.x, this.y, 0.0f, 1.0f);
		this.visibleArea =  new Vertex4(this.x+this.textOffsetX+this.textEdgeDistance, this.y + this.textOffsetY+this.textEdgeDistance, 
				this.width-this.frameSize/*-this.frameSize-this.textEdgeDistance*/, -this.height+this.frameSize/*+this.frameSize+this.textEdgeDistance*/);
		this.setupMainGL();
		this.setupTextGL();
	}
	
	private final void setupSelectionGL()
	{
		VertexData[] points = new VertexData[]{
			   new VertexData(new float[]{0.0f,0.0f,0.0f,1.0f}, 
				   Utility.idToGlColor(this.id, false), new float[]{1.0f, 0.0f}),
				   
			   new VertexData(new float[]{0.0f,-this.height,0.0f,1.0f}, 
				   Utility.idToGlColor(this.id, false), new float[]{1.0f, 1.0f}),
				   
			   new VertexData(new float[]{this.width,-this.height,0.0f,1.0f}, 
				   Utility.idToGlColor(this.id, false), new float[]{0.0f, 1.0f}),
				   
			   new VertexData(new float[]{this.width,0.0f,0.0f,1.0f}, 
				   Utility.idToGlColor(this.id, false), new float[]{0.0f, 0.0f})};
		byte[] indices = {
			0, 1, 2,
			2, 3, 0
		};
		// Put each 'Vertex' in one FloatBuffer
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(points.length *
				VertexData.ELEMENT_COUNT);
		for (int i = 0; i < points.length; i++) {
			// Add position, color and texture floats to the buffer
			verticesBuffer.put(points[i].getElements());
		}
		verticesBuffer.flip();	
		
		selectionICount = indices.length;
		ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(selectionICount);
		indicesBuffer.put(indices);
		indicesBuffer.flip();
		
		// Create a new Vertex Array Object in memory and select it (bind)
		if(this.selectionVAO == -1)
			selectionVAO = GL30.glGenVertexArrays();
		
		GL30.glBindVertexArray(selectionVAO);
		
		// Create a new Vertex Buffer Object in memory and select it (bind)
		if(this.selectionVBO == -1)
			selectionVBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, selectionVBO);
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
		if(this.selectionIVBO == -1)
			selectionIVBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, selectionIVBO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	private final void setupTextGL()
	{
		if(this.text != "")
		{			
			this.text += (char) 0;
			this.textICount = text.length()*3*2;
			VertexData[] textPoints = new VertexData[text.length()*4];
			float xoff = 0.0f;
			float yoff = 0.0f;
			IntBuffer indicesBuffer = BufferUtils.createIntBuffer(this.textICount);
			
			float xoffmax = 0.0f;
			for(int i = 0; i < text.length(); i++)
			{
				char c = text.charAt(i);
				if(c == '\n')
				{
					xoff = 0.0f;
					yoff += this.textFont.getHeight('A');
				}
				textPoints[i*4+0] = new VertexData(new float[]{xoff,-yoff,0.0f,1.0f}, 
											 new float[]{1.0f, 1.0f, 1.0f, 1.0f},
											 new float[]{this.textFont.getX(c),this.textFont.getY(c)});
				
				textPoints[i*4+1] = new VertexData(new float[]{xoff,-yoff-this.textFont.getHeight(c),0.0f,1.0f}, 
											 new float[]{1.0f, 1.0f, 1.0f, 1.0f},
						 					 new float[]{this.textFont.getX(c),this.textFont.getY(c)+this.textFont.getHeight(c)});
				
				textPoints[i*4+2] = new VertexData(new float[]{xoff+this.textFont.getWidth(c),-yoff-this.textFont.getHeight(c),0.0f,1.0f}, 
											 new float[]{1.0f, 1.0f, 1.0f, 1.0f},
						 					 new float[]{this.textFont.getX(c)+this.textFont.getWidth(c),this.textFont.getY(c)+this.textFont.getHeight(c)});
				
				textPoints[i*4+3] = new VertexData(new float[]{xoff+this.textFont.getWidth(c),-yoff,0.0f,1.0f}, 
											 new float[]{1.0f, 1.0f, 1.0f, 1.0f},
						 					 new float[]{this.textFont.getX(c)+this.textFont.getWidth(c),this.textFont.getY(c)});	
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
				xoff += this.textFont.getWidth(c);
				
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
			if(this.textVAO == -1)
				textVAO = GL30.glGenVertexArrays();
			
			GL30.glBindVertexArray(textVAO);
			
			// Create a new Vertex Buffer Object in memory and select it (bind)
			if(this.textVBO == -1)
				textVBO = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, textVBO);
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
			if(this.textIVBO == -1)
				textIVBO = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, textIVBO);
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		
			
			if(this.autosizeWithText)
			{
				if(!this.fixedHeigth)
					this.height = yoff+this.textFont.getStaticHeight()+2*(this.textOffsetY+this.textEdgeDistance);
				if(!this.fixedWidth)
					this.width = xoffmax+2*(this.textEdgeDistance)+this.textOffsetX;
				
			}
			
			this.textWidth = xoffmax-this.textFont.getWidth((char)0);
			this.textHeight = yoff+this.textFont.getStaticHeight();
			
			
			if(this.autosizeWithText)
			{
				this.setupMainGL();
			}
			//this.textSize = new Vertex2(textEdge.getX(), textEdge.getY());
		}
	}
	
	private final void setupMainGL()
	{
		VertexData[] points;
		byte[] indices;
		
		
		points = new VertexData[]{
				   new VertexData(new float[]{0.0f,0.0f,0.0f,1.0f}, 
					   new float[]{1.0f, 1.0f, 1.0f, 1.0f}, new float[]{1.0f, 0.0f}),
					   
				   new VertexData(new float[]{0.0f,-this.height,0.0f,1.0f}, 
					   new float[]{1.0f, 1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f}),
					   
				   new VertexData(new float[]{this.width,-this.height,0.0f,1.0f}, 
					   new float[]{1.0f, 1.0f, 1.0f, 1.0f}, new float[]{0.0f, 1.0f}),
					   
				   new VertexData(new float[]{this.width,0.0f,0.0f,1.0f}, 
					   new float[]{1.0f, 1.0f, 1.0f, 1.0f}, new float[]{0.0f, 0.0f}),
					   
					   //FRAME LEFT
				   new VertexData(new float[]{0.0f,0.0f,0.0f,1.0f}, 
					   new float[]{0.0f, 0.0f, 0.0f, 0.0f}, new float[]{1.0f, 0.0f}),
							   
				   new VertexData(new float[]{0.0f,-this.height,0.0f,1.0f}, 
					   new float[]{0.0f, 0.0f, 0.0f, 0.0f}, new float[]{1.0f, 1.0f}),
							   
				   new VertexData(new float[]{this.frameSize,-this.height,0.0f,1.0f}, 
					   new float[]{0.0f, 0.0f, 0.0f, 0.0f}, new float[]{0.0f, 1.0f}),
							   
				   new VertexData(new float[]{this.frameSize,0.0f,0.0f,1.0f}, 
					   new float[]{0.0f, 0.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f}),
					   
					   //FRAME TOP
				   new VertexData(new float[]{0.0f,0.0f,0.0f,1.0f}, 
					   new float[]{0.0f, 0.0f, 0.0f, 0.0f}, new float[]{1.0f, 0.0f}),
							   
				   new VertexData(new float[]{0.0f,-this.frameSize,0.0f,1.0f}, 
					   new float[]{0.0f, 0.0f, 0.0f, 0.0f}, new float[]{1.0f, 1.0f}),
							   
				   new VertexData(new float[]{this.width,-this.frameSize,0.0f,1.0f}, 
					   new float[]{0.0f, 0.0f, 0.0f, 0.0f}, new float[]{0.0f, 1.0f}),
							   
				   new VertexData(new float[]{this.width,0.0f,0.0f,1.0f}, 
					   new float[]{0.0f, 0.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f}),
							   
					   //FRAME RIGHT
				   new VertexData(new float[]{this.width-this.frameSize,0.0f,0.0f,1.0f}, 
					   new float[]{0.0f, 0.0f, 0.0f, 0.0f}, new float[]{1.0f, 0.0f}),
									   
				   new VertexData(new float[]{this.width-this.frameSize,-this.height,0.0f,1.0f}, 
					   new float[]{0.0f, 0.0f, 0.0f, 0.0f}, new float[]{1.0f, 1.0f}),
									   
				   new VertexData(new float[]{this.width,-this.height,0.0f,1.0f}, 
					   new float[]{0.0f, 0.0f, 0.0f, 0.0f}, new float[]{0.0f, 1.0f}),
									   
				   new VertexData(new float[]{this.width,0.0f,0.0f,1.0f}, 
					   new float[]{0.0f, 0.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f}),
					   
					 //FRAME BOT
				   new VertexData(new float[]{0.0f,-this.height+this.frameSize,0.0f,1.0f}, 
					   new float[]{0.0f, 0.0f, 0.0f, 0.0f}, new float[]{1.0f, 0.0f}),
										   
				   new VertexData(new float[]{0.0f,-this.height,0.0f,1.0f}, 
					   new float[]{0.0f, 0.0f, 0.0f, 0.0f}, new float[]{1.0f, 1.0f}),
										   
				   new VertexData(new float[]{this.width,-this.height,0.0f,1.0f}, 
					   new float[]{0.0f, 0.0f, 0.0f, 0.0f}, new float[]{0.0f, 1.0f}),
										   
				   new VertexData(new float[]{this.width,-this.height+this.frameSize,0.0f,1.0f}, 
					   new float[]{0.0f, 0.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f})};
		
		if(this.hasFrame && this.hasBackground)
		{
			indices = new byte[]{
					0, 1, 2,
					2, 3, 0, //Background
					
					4, 5, 6,
					6, 7, 4, //Left Frame
					
					8, 9, 10,
					10, 11, 8, //Top Frame
					
					12, 13, 14,
					14, 15, 12, //Right Frame
					
					16, 17, 18,
					18, 19, 16	//Bot Frame
			};
		}
		else if(this.hasFrame && !this.hasBackground)
		{			
			indices = new byte[]{
					4, 5, 6,
					6, 7, 4, //Left Frame
					
					8, 9, 10,
					10, 11, 8, //Top Frame
					
					12, 13, 14,
					14, 15, 12, //Right Frame
					
					16, 17, 18,
					18, 19, 16	//Bot Frame
			};
		}
		else if(this.hasBackground && !this.hasFrame)
		{			
			indices = new byte[]{
					0, 1, 2,
					2, 3, 0
				};
		}
		else
		{
			return;
		}
		
		// Put each 'Vertex' in one FloatBuffer
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(points.length *
				VertexData.ELEMENT_COUNT);
		for (int i = 0; i < points.length; i++) {
			// Add position, color and texture floats to the buffer
			verticesBuffer.put(points[i].getElements());
		}
		verticesBuffer.flip();	
			
		mainICount = indices.length;
		ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(mainICount);
		indicesBuffer.put(indices);
		indicesBuffer.flip();
			
		// Create a new Vertex Array Object in memory and select it (bind)
		if(this.mainVAO == -1)
			mainVAO = GL30.glGenVertexArrays();
		
		GL30.glBindVertexArray(mainVAO);
			
		// Create a new Vertex Buffer Object in memory and select it (bind)
		if(this.mainVBO == -1)
			mainVBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mainVBO);
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
		if(this.mainIVBO == -1)
			mainIVBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mainIVBO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

	}
	
	private final void setupIconGL()
	{
		VertexData[] points = new VertexData[]{
				   new VertexData(new float[]{0.0f,0.0f,0.0f,1.0f}, 
					   new float[]{1.0f, 1.0f, 1.0f, 1.0f}, new float[]{1.0f, 0.0f}),
					   
				   new VertexData(new float[]{0.0f,this.height,0.0f,1.0f}, 
					   new float[]{1.0f, 1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f}),
					   
				   new VertexData(new float[]{this.width,this.height,0.0f,1.0f}, 
					   new float[]{1.0f, 1.0f, 1.0f, 1.0f}, new float[]{0.0f, 1.0f}),
					   
				   new VertexData(new float[]{this.width,0.0f,0.0f,1.0f}, 
					   new float[]{1.0f, 1.0f, 1.0f, 1.0f}, new float[]{0.0f, 0.0f})};
		
			byte[] indices = {
				0, 1, 2,
				2, 3, 0
			};
			// Put each 'Vertex' in one FloatBuffer
			FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(points.length *
					VertexData.ELEMENT_COUNT);
			for (int i = 0; i < points.length; i++) {
				// Add position, color and texture floats to the buffer
				verticesBuffer.put(points[i].getElements());
			}
			verticesBuffer.flip();	
			
			selectionICount = indices.length;
			ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(selectionICount);
			indicesBuffer.put(indices);
			indicesBuffer.flip();
			
			// Create a new Vertex Array Object in memory and select it (bind)
			if(this.selectionVAO == -1)
				selectionVAO = GL30.glGenVertexArrays();
			
			GL30.glBindVertexArray(selectionVAO);
			
			// Create a new Vertex Buffer Object in memory and select it (bind)
			if(this.selectionVBO == -1)
				selectionVBO = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, selectionVBO);
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
			if(this.selectionIVBO == -1)
				selectionIVBO = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, selectionIVBO);
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	public final void drawSelection()
	{
		this.mainShader.bind();
		
		/*
		if(this.textFont.getFontTextureID() != -1)
		{
			// Bind the texture
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textFont.getFontTextureID() );
		}*/
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
				
		// Bind to the VAO that has all the information about the vertices
		GL30.glBindVertexArray(this.selectionVAO);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
				
		// Bind to the index VBO that has all the information about the order of the vertices
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.selectionIVBO);
		
		this.mainShader.passMatrix(Matrix4x4.getIdentity()/*scalingMatrix*/, MatrixTypes.SCALING);
		this.mainShader.passMatrix(this.translationMatrix, MatrixTypes.TRANSLATION);
		this.mainShader.passMatrix(Matrix4x4.getIdentity()/*rotationMatrix*/, MatrixTypes.ROTATION);
		this.mainShader.passMatrix(StaticManager.WINDOW_MATRIX, MatrixTypes.WINDOW);
		this.mainShader.pass1i("selection", 1);
		//this.mainShader.pass4f("visible_Area",this.visibleArea.getX(),this.visibleArea.getY(),this.visibleArea.getZ(),this.visibleArea.getW());
		// Draw the vertices
		GL11.glDrawElements(GL11.GL_TRIANGLES, this.selectionICount, GL11.GL_UNSIGNED_BYTE, 0);
				
		// Put everything back to default (deselect)
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		
		this.mainShader.unbind();
	}
	
	private final void drawText()
	{
		this.textShader.bind();
		
		if(this.textFont.getFontTextureID() != -1)
		{
			// Bind the texture
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textFont.getFontTextureID() );
		}
				
		// Bind to the VAO that has all the information about the vertices
		GL30.glBindVertexArray(this.textVAO);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
				
		// Bind to the index VBO that has all the information about the order of the vertices
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.textIVBO);
		
		this.textShader.passMatrix(this.textScaleMatrix, MatrixTypes.SCALING);
		this.textShader.passMatrix(this.textTranslationMatrix, MatrixTypes.TRANSLATION);
		this.textShader.passMatrix(Matrix4x4.getIdentity()/*rotationMatrix*/, MatrixTypes.ROTATION);
		this.textShader.passMatrix(parent.getWindowMatrix(), MatrixTypes.WINDOW);
		this.textShader.pass1i("selection", 0);
		this.textShader.pass4f("visible_Area",this.visibleArea.getX(),this.visibleArea.getY(),this.visibleArea.getZ(),this.visibleArea.getW());
		this.textShader.pass4f("color", this.textColor[0], this.textColor[1], this.textColor[2], this.textColor[3]);
		// Draw the vertices
		GL11.glDrawElements(GL11.GL_TRIANGLES, this.textICount, GL11.GL_UNSIGNED_INT, 0);
				
		// Put everything back to default (deselect)
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		
		this.textShader.unbind();
	}
	
	private final void drawIcon()
	{
		this.mainShader.bind();
		
		
		if(this.iconTexID != -1)
		{
			// Bind the texture
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.iconTexID);
		}
				
		// Bind to the VAO that has all the information about the vertices
		GL30.glBindVertexArray(this.iconVAO);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
				
		// Bind to the index VBO that has all the information about the order of the vertices
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.iconIVBO);
		
		this.mainShader.passMatrix(this.iconScalingMatrix, MatrixTypes.SCALING);
		this.mainShader.passMatrix(this.iconTranslationMatrix, MatrixTypes.TRANSLATION);
		this.mainShader.passMatrix(Matrix4x4.getIdentity()/*rotationMatrix*/, MatrixTypes.ROTATION);
		this.mainShader.passMatrix(parent.getWindowMatrix(), MatrixTypes.WINDOW);
		this.mainShader.pass1i("selection", 0);
		//this.mainShader.pass4f("visible_Area",this.visibleArea.getX(),this.visibleArea.getY(),this.visibleArea.getZ(),this.visibleArea.getW());
		// Draw the vertices
		GL11.glDrawElements(GL11.GL_TRIANGLES, this.iconICount, GL11.GL_UNSIGNED_BYTE, 0);
				
		// Put everything back to default (deselect)
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		
		this.mainShader.unbind();
	}
	
	private final void drawMainGL()
	{
		this.mainShader.bind();
		
		/*
		if(this.textFont.getFontTextureID() != -1)
		{
			// Bind the texture
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textFont.getFontTextureID() );
		}*/
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
				
		// Bind to the VAO that has all the information about the vertices
		GL30.glBindVertexArray(this.mainVAO);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
				
		// Bind to the index VBO that has all the information about the order of the vertices
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.mainIVBO);
		
		this.mainShader.passMatrix(Matrix4x4.getIdentity()/*scalingMatrix*/, MatrixTypes.SCALING);
		this.mainShader.passMatrix(this.translationMatrix, MatrixTypes.TRANSLATION);
		this.mainShader.passMatrix(Matrix4x4.getIdentity()/*rotationMatrix*/, MatrixTypes.ROTATION);
		this.mainShader.passMatrix(parent.getWindowMatrix(), MatrixTypes.WINDOW);
		this.mainShader.pass1i("selection", 0);
		
		this.mainShader.pass4f("frame_color", this.frameColor[0], this.frameColor[1], 
									this.frameColor[2], this.frameColor[3]);
		this.mainShader.pass4f("background_color", this.backgroundColor[0], 
									this.backgroundColor[1], this.backgroundColor[2], this.backgroundColor[3]);
		
		//this.mainShader.pass4f("visible_Area",this.visibleArea.getX(),this.visibleArea.getY(),this.visibleArea.getZ(),this.visibleArea.getW());
		// Draw the vertices
		GL11.glDrawElements(GL11.GL_TRIANGLES, this.mainICount, GL11.GL_UNSIGNED_BYTE, 0);
				
		// Put everything back to default (deselect)
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		
		this.mainShader.unbind();
	}
	
	public void draw()
	{
		if(this.hasBackground || this.hasFrame)
			this.drawMainGL();
		
		if(this.hasIcon)
			this.drawIcon();
		
		if(this.hasText && this.text.trim().length() != 0)
			this.drawText();
	}
	
	public boolean select(int id)
	{
		if(this.id == id)
		{
			if(!this.selected)
				this.selected(); //selected event
			
			this.selected = true;
			return true;
		}
		else
		{
			if(this.selected)
				this.unselected(); //unselected event
			
			this.selected = false;
			return false;
		}
	}

	public int getIconTexID() {
		return iconTexID;
	}

	public void setIconTexID(int iconTexID) {
		this.iconTexID = iconTexID;
	}
	
	public void setIcon(String s, float relX, float relY, float width, float height)
	{
		this.iconX = relX;
		this.iconY = relY;
		this.width = width;
		this.height = height;
		this.iconTexID = Utility.loadPNGTextureSmooth(s, GL13.GL_TEXTURE0);
	}

	public BitmapFont getTextFont() {
		return textFont;
	}

	public void setTextFont(BitmapFont textFont) {
		this.textFont = textFont;
	}

	public boolean hasText() {
		return hasText;
	}

	public void setHasText(boolean hasText) {
		this.hasText = hasText;
	}

	public float getTextOffsetX() {
		return textOffsetX;
	}

	public void setTextOffsetX(float textOffsetX) {
		this.textOffsetX = textOffsetX;
	}

	public float getTextOffsetY() {
		return textOffsetY;
	}

	public void setTextOffsetY(float textOffsetY) {
		this.textOffsetY = textOffsetY;
	}

	public ShaderProgram getTextShader() {
		return textShader;
	}

	public void setTextShader(ShaderProgram textShader) {
		this.textShader = textShader;
	}

	public ShaderProgram getMainShader() {
		return mainShader;
	}

	public void setMainShader(ShaderProgram mainShader) {
		this.mainShader = mainShader;
	}

	public float[] getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(float[] backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public float[] getFrameColor() {
		return frameColor;
	}

	public void setFrameColor(float[] frameColor) {
		this.frameColor = frameColor;
	}

	public boolean hasFrame() {
		return hasFrame;
	}

	public void setHasFrame(boolean hasFrame) {
		this.hasFrame = hasFrame;
	}

	public boolean hasBackground() {
		return hasBackground;
	}

	public void setHasBackground(boolean hasBackground) {
		this.hasBackground = hasBackground;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		if(this.text.length() > 1)
			return text.substring(0, this.text.length()-1);
		else
			return "";
	}
	
	public void resize(float width, float height)
	{
		this.width = width;
		this.height = height;
		this.setupMainGL();
		this.setupSelectionGL();
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
		this.setupTextGL();
	}

	/**
	 * @return the tabId
	 */
	public int getTabId() {
		return tabId;
	}

	/**
	 * @param tabId the tabId to set
	 */
	public void setTabId(int tabId) {
		this.tabId = tabId;
	}
	
	public void invalidateGL()
	{
		if(this.hasText)
			setupTextGL();
		setupMainGL();
		setupSelectionGL();
	}
	
	public boolean hasIcon()
	{
		return this.hasIcon;
	}
	
	public void setHasIcon(boolean hasIcon)
	{
		this.hasIcon = hasIcon;
		if(this.hasIcon)
			setupIconGL();
	}

	/**
	 * @return the x
	 */
	public float getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public float getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(float y) {
		this.y = y;
	}
	
	public Vertex4 getVisibleArea() {
		return visibleArea;
	}

	public void setVisibleArea(Vertex4 visibleArea) {
		this.visibleArea = visibleArea;
	}

	/**
	 * @return the textScale
	 */
	public float getTextScale() {
		return textScale;
	}

	/**
	 * @param textScale the textScale to set
	 */
	public void setTextScale(float textScale) {
		this.textScale = textScale;
		this.textScaleMatrix.set(textScale,      0.0f, 		0.0f, 0.0f, 
				   				      0.0f, textScale, 		0.0f, 0.0f, 
				   			          0.0f,      0.0f, textScale, 0.0f, 
				   			          0.0f, 	 0.0f, 		0.0f, 1.0f);
		float xdir = ((this.x+this.width-this.frameSize)<this.x+this.textScale*(this.textWidth)-this.frameSize) && !this.autosizeWithText ? this.x+this.width-this.frameSize : this.x+this.textScale*(this.textWidth)-this.frameSize;
		float ydir = ((this.y+this.height-this.frameSize)<this.y+this.textScale*(this.textHeight)-this.frameSize) && !this.autosizeWithText ? this.y+this.height-this.frameSize : this.y+this.textScale*(this.textHeight)-this.frameSize;
		
		this.visibleArea =  new Vertex4(this.x, this.y, xdir, -ydir);
		if(this.autosizeWithText)
		{
			Vertex4 p = this.textScaleMatrix.multiply(new Vertex4(new Vertex2(this.width,this.height)));
			
			this.width = p.getX();
			this.height = p.getY();
			
			this.setupMainGL();
			this.setupSelectionGL();
			this.setupIconGL();
		}
		
		if(this.width > (this.textWidth*this.textScale) && this.centerText)
		{
			this.textOffsetX = (this.width-(this.textWidth*this.textScale)-(2*(this.textEdgeDistance+Component.DEFAULT_FRAME_SIZE)))/2;
			this.textTranslationMatrix.set(1.0f, 0.0f, 0.0f, 0.0f, 
										   0.0f, 1.0f, 0.0f, 0.0f, 
										   0.0f, 0.0f, 1.0f, 0.0f, 
										   this.x+this.textOffsetX+this.scrollX+this.textEdgeDistance, (this.y-this.textOffsetY-this.scrollY-this.textEdgeDistance), 0.0f, 1.0f);
			
		}
	}
	
	public int getId()
	{
		return this.id;
	}
	
	public void setId(int newId)
	{
		this.id = newId;
		this.setupSelectionGL();
	}

	/**
	 * @return the textColor
	 */
	public float[] getTextColor() {
		return textColor;
	}

	/**
	 * @param textColor the textColor to set
	 */
	public void setTextColor(float[] textColor) {
		this.textColor = textColor;
	}

	/**
	 * @return the parent
	 */
	public Frame getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(Frame parent) {
		this.parent = parent;
		
		this.visibleArea =  parent.getWindowMatrix().multiply(new Vertex4(this.x, this.y, this.x+this.width-this.frameSize, this.y-this.height+this.frameSize));
		this.visibleArea = new Vertex4(this.x, this.y, this.visibleArea.getZ(), this.visibleArea.getW());
	}
	
	public boolean getAutosizeWithText() {
		return this.autosizeWithText;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setAutosizeWithText(boolean value) {
		this.autosizeWithText = value;
		
		if(value)
		{
			this.setupTextGL();
			this.setupSelectionGL();
			this.setupMainGL();
			
			if(this.parent != null)
			{
				this.visibleArea =  parent.getWindowMatrix().multiply(new Vertex4(this.x, this.y, this.x+this.width, this.y-this.height));
				this.visibleArea = new Vertex4(this.x, this.y, this.visibleArea.getZ(), this.visibleArea.getW());
			}
		}
	}

	/**
	 * @return the selectable
	 */
	public boolean isSelectable() {
		return selectable;
	}

	/**
	 * @param selectable the selectable to set
	 */
	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}
	
	public void setNormalTextColor(float r, float g, float b, float a)
	{
		this.textNColor = new float[]{r,g,b,a};
	}
	
	public void setNormalBackgroundColor(float r, float g, float b, float a)
	{
		this.backgroundNColor = new float[]{r,g,b,a};
	}
	
	public void setNormalFrameColor(float r, float g, float b, float a)
	{
		this.frameNColor = new float[]{r,g,b,a};
	}
	
	public void setHighlightTextColor(float r, float g, float b, float a)
	{
		this.textHColor = new float[]{r,g,b,a};
	}
	
	public void setHighlightBackgroundColor(float r, float g, float b, float a)
	{
		this.backgroundHColor = new float[]{r,g,b,a};
	}
	
	public void setHighlightFrameColor(float r, float g, float b, float a)
	{
		this.frameHColor = new float[]{r,g,b,a};
	}

	public float getHeight()
	{
		return this.height;
	}
	
	public void setHeight(float height)
	{
		this.height = height;
		if(this.hasText)
		{
			if(this.height > this.textHeight && this.centerText)
			{
				this.textOffsetY = (this.height-this.textHeight-(2*(this.textEdgeDistance+Component.DEFAULT_FRAME_SIZE)))/2;
				this.textTranslationMatrix.set(1.0f, 0.0f, 0.0f, 0.0f, 
											   0.0f, 1.0f, 0.0f, 0.0f, 
											   0.0f, 0.0f, 1.0f, 0.0f, 
											   this.x+this.textOffsetX+this.scrollX+this.textEdgeDistance, (this.y-this.textOffsetY-this.scrollY-this.textEdgeDistance), 0.0f, 1.0f);
				
			}
		}
		
		this.setupIconGL();
		this.setupMainGL();
	}
	
	public float getWidth()
	{
		return this.height;
	}
	
	public void setWidth(float width)
	{
		this.width = width;
		if(this.hasText)
		{
			if(this.width > this.textWidth && this.centerText)
			{
				this.textOffsetX = (this.width-(this.textWidth*this.textScale)-(2*(this.textEdgeDistance+Component.DEFAULT_FRAME_SIZE)))/2;
				this.textTranslationMatrix.set(1.0f, 0.0f, 0.0f, 0.0f, 
											   0.0f, 1.0f, 0.0f, 0.0f, 
											   0.0f, 0.0f, 1.0f, 0.0f, 
											   this.x+this.textOffsetX+this.scrollX+this.textEdgeDistance, (this.y-this.textOffsetY-this.scrollY-this.textEdgeDistance), 0.0f, 1.0f);
				
			}
		}
		
		this.setupIconGL();
		this.setupMainGL();
	}
	
	public void clicked(int x, int y, int button, boolean dMouse) 
	{
		if(this.clickToSelect && this.selectable)
		{
			//System.out.println("WTF");
			if(!this.active)
			{
				this.textColor = this.textHColor;
				this.frameColor = this.frameHColor;
				this.backgroundColor = this.backgroundHColor;
				this.active = true;
			}
		}
		
		if(!dMouse)
		{
			for(int i = 0; i < this.clickListener.size(); i++)
			{
				this.clickListener.get(i).clicked(x, y, button);
			}
		}
		else
		{
			
		}
	}
	
	public void selected() {
		if(this.selectable && !this.clickToSelect)
		{
			this.textColor = this.textHColor;
			this.frameColor = this.frameHColor;
			this.backgroundColor = this.backgroundHColor;
		}
	}
	
	public void unselected() {
		if(!this.clickToSelect)
		{
			this.textColor = this.textNColor;
			this.frameColor = this.frameNColor;
			this.backgroundColor = this.backgroundNColor;
		}
	}
	
	
	public void keyDown(int key, char c)
	{
		for(int i = 0; i < this.keyListener.size(); i++)
		{
			this.keyListener.get(i).keyDown(key, c);
		}
	}
	
	public void addClickListener(IClickListener lis)
	{
		this.clickListener.add(lis);
	}
	
	public void removeClickListener(IClickListener lis)
	{
		this.clickListener.remove(lis);
	}
	
	public void addSelectListener(ISelectListener lis)
	{
		this.selectListener.add(lis);
	}
	
	public void removeSelectListener(ISelectListener lis)
	{
		this.selectListener.remove(lis);
	}
	
	public void addKeyListener(IKeyListener lis)
	{
		this.keyListener.add(lis);
	}
	
	public void removeKeyListener(IKeyListener lis)
	{
		this.keyListener.remove(lis);
	}
	

	public boolean getCenterText() {
		return this.centerText;
		
	}
	
	public void setCenterText(boolean b) {
		this.centerText = b;
		
	}
	
	public int getTextCursorPos()
	{
		return this.textCursorPos;
	}
	
	public void setTextCursorPos(int pos)
	{
		this.textCursorPos = pos;
	}

	/**
	 * @return the clickToSelect
	 */
	public boolean isClickToSelect() {
		return clickToSelect;
	}

	/**
	 * @param clickToSelect the clickToSelect to set
	 */
	public void setClickToSelect(boolean clickToSelect) {
		this.clickToSelect = clickToSelect;
	}
	
	public void setActive(boolean active)
	{
		if(!this.selectable)
			return;
		
		if(!active)
		{
			this.textColor = this.textNColor;
			this.frameColor = this.frameNColor;
			this.backgroundColor = this.backgroundNColor;
		}
		else
		{
			this.textColor = this.textHColor;
			this.frameColor = this.frameHColor;
			this.backgroundColor = this.backgroundHColor;
		}
		this.active = active;
	}

	public boolean isScrollable() {
		// TODO Auto-generated method stub
		return this.scrollable ;
	}
	
	public void setScrollable(boolean scrollable) {
		// TODO Auto-generated method stub
		this.scrollable = scrollable;
	}
}
