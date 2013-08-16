package cdg.util;

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

public abstract class Component 
{
	public static final float[] DEFAULT_FRAME_COLOR = new float[]{0.9f, 0.9f, 0.9f, 1.0f};
	public static final float[] DEFAULT_TEXT_COLOR = new float[]{0.9f, 0.9f, 0.9f, 1.0f};
	public static final float[] DEFAULT_BACKGROUND_COLOR = new float[]{0.2f, 0.2f, 0.2f, 0.9f};
	public static final float DEFAULT_FRAME_SIZE = 0.01f;
	public static ShaderProgram DEFAULT_TEXT_SHADER;
	public static ShaderProgram DEFAULT_MAIN_SHADER;
	
	private int id;
	private int iconTexID;
	private BitmapFont textFont;
	private boolean hasText;
	private String text;
	private float textOffsetX;
	private float textOffsetY;
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
	
	private float x;
	private float y;
	private float width;
	private float height;
	
	private float iconX;
	private float iconY;
	private float iconWidth;
	private float iconHeight;
	
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
	
	public Component(int id, float x, float y, float width, float height)
	{
		this(id, x, y, width, height, true, true);
	}
	
	public Component(int id, float x, float y, float width, float height, boolean frame, boolean background)
	{
		this.id = id;
		this.setX(x);
		this.setY(y);
		this.width = width;
		this.height = height;
		this.hasFrame = frame;
		this.hasBackground = background;
		this.hasText = false;
		this.setupMainGL();
		this.setupSelectionGL();
	}
	
	public Component(int id, float x, float y, float width, float height, boolean frame, boolean background, BitmapFont font, String text)
	{
		this.id = id;
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
		this.visibleArea = this.translationMatrix.multiply(new Vertex4(this.textOffsetX+this.textEdgeDistance, this.textOffsetY+this.textEdgeDistance, 
				this.width-this.frameSize-this.textEdgeDistance, -this.height+this.frameSize+this.textEdgeDistance));
		this.setupMainGL();
		this.setupSelectionGL();
		this.setupTextGL();
	}
	
	private final void setupSelectionGL()
	{
		VertexData[] points = new VertexData[]{
			   new VertexData(new float[]{0.0f,0.0f,0.0f,1.0f}, 
				   Utility.idToGlColor(this.id, false), new float[]{1.0f, 0.0f}),
				   
			   new VertexData(new float[]{0.0f,this.height,0.0f,1.0f}, 
				   Utility.idToGlColor(this.id, false), new float[]{1.0f, 1.0f}),
				   
			   new VertexData(new float[]{this.width,this.height,0.0f,1.0f}, 
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
											 textColor,
											 new float[]{this.textFont.getX(c),this.textFont.getY(c)});
				
				textPoints[i*4+1] = new VertexData(new float[]{xoff,-yoff-this.textFont.getHeight(c),0.0f,1.0f}, 
											 textColor,
						 					 new float[]{this.textFont.getX(c),this.textFont.getY(c)+this.textFont.getHeight(c)});
				
				textPoints[i*4+2] = new VertexData(new float[]{xoff+this.textFont.getWidth(c),-yoff-this.textFont.getHeight(c),0.0f,1.0f}, 
											 textColor,
						 					 new float[]{this.textFont.getX(c)+this.textFont.getWidth(c),this.textFont.getY(c)+this.textFont.getHeight(c)});
				
				textPoints[i*4+3] = new VertexData(new float[]{xoff+this.textFont.getWidth(c),-yoff,0.0f,1.0f}, 
											 textColor,
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
		
			/*
			Vertex4 textEdge = (Vertex4) StaticManager.WINDOW_MATRIX.multiply(this.scalingMatrix.multiply((IVertex) new Vertex2(xoffmax, yoff+this.textFont.getHeight('A'))));
			this.textSize = new Vertex2(textEdge.getX(), textEdge.getY());*/
		}
	}
	
	private final void setupMainGL()
	{
		VertexData[] points;
		byte[] indices;
		
		System.out.println(this.hasFrame +"/"+this.hasBackground);
		
		if(this.hasFrame && this.hasBackground)
		{
			points = new VertexData[]{
					   new VertexData(new float[]{0.0f,0.0f,0.0f,1.0f}, 
						   this.backgroundColor, new float[]{1.0f, 0.0f}),
						   
					   new VertexData(new float[]{0.0f,-this.height,0.0f,1.0f}, 
						   this.backgroundColor, new float[]{1.0f, 1.0f}),
						   
					   new VertexData(new float[]{this.width,-this.height,0.0f,1.0f}, 
						   this.backgroundColor, new float[]{0.0f, 1.0f}),
						   
					   new VertexData(new float[]{this.width,0.0f,0.0f,1.0f}, 
						   this.backgroundColor, new float[]{0.0f, 0.0f}),
						   
						   //FRAME LEFT
					   new VertexData(new float[]{0.0f,0.0f,0.0f,1.0f}, 
						   this.frameColor, new float[]{1.0f, 0.0f}),
								   
					   new VertexData(new float[]{0.0f,-this.height,0.0f,1.0f}, 
						   this.frameColor, new float[]{1.0f, 1.0f}),
								   
					   new VertexData(new float[]{this.frameSize,-this.height,0.0f,1.0f}, 
						   this.frameColor, new float[]{0.0f, 1.0f}),
								   
					   new VertexData(new float[]{this.frameSize,0.0f,0.0f,1.0f}, 
						   this.frameColor, new float[]{0.0f, 0.0f}),
						   
						   //FRAME TOP
					   new VertexData(new float[]{0.0f,0.0f,0.0f,1.0f}, 
						   this.frameColor, new float[]{1.0f, 0.0f}),
								   
					   new VertexData(new float[]{0.0f,-this.frameSize,0.0f,1.0f}, 
						   this.frameColor, new float[]{1.0f, 1.0f}),
								   
					   new VertexData(new float[]{this.width,-this.frameSize,0.0f,1.0f}, 
						   this.frameColor, new float[]{0.0f, 1.0f}),
								   
					   new VertexData(new float[]{this.width,0.0f,0.0f,1.0f}, 
						   this.frameColor, new float[]{0.0f, 0.0f}),
								   
						   //FRAME RIGHT
					   new VertexData(new float[]{this.width-this.frameSize,0.0f,0.0f,1.0f}, 
						   this.frameColor, new float[]{1.0f, 0.0f}),
										   
					   new VertexData(new float[]{this.width-this.frameSize,-this.height,0.0f,1.0f}, 
						   this.frameColor, new float[]{1.0f, 1.0f}),
										   
					   new VertexData(new float[]{this.width,-this.height,0.0f,1.0f}, 
						   this.frameColor, new float[]{0.0f, 1.0f}),
										   
					   new VertexData(new float[]{this.width,0.0f,0.0f,1.0f}, 
						   this.frameColor, new float[]{0.0f, 0.0f}),
						   
						 //FRAME BOT
					   new VertexData(new float[]{0.0f,-this.height+this.frameSize,0.0f,1.0f}, 
						   this.frameColor, new float[]{1.0f, 0.0f}),
											   
					   new VertexData(new float[]{0.0f,-this.height,0.0f,1.0f}, 
						   this.frameColor, new float[]{1.0f, 1.0f}),
											   
					   new VertexData(new float[]{this.width,-this.height,0.0f,1.0f}, 
						   this.frameColor, new float[]{0.0f, 1.0f}),
											   
					   new VertexData(new float[]{this.width,-this.height+this.frameSize,0.0f,1.0f}, 
						   this.frameColor, new float[]{0.0f, 0.0f})};
			
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
			points = new VertexData[]{
					   	   //FRAME LEFT
					   new VertexData(new float[]{0.0f,0.0f,0.0f,1.0f}, 
						   this.frameColor, new float[]{1.0f, 0.0f}),
								   
					   new VertexData(new float[]{0.0f,-this.height,0.0f,1.0f}, 
						   this.frameColor, new float[]{1.0f, 1.0f}),
								   
					   new VertexData(new float[]{this.frameSize,-this.height,0.0f,1.0f}, 
						   this.frameColor, new float[]{0.0f, 1.0f}),
								   
					   new VertexData(new float[]{this.frameSize,0.0f,0.0f,1.0f}, 
						   this.frameColor, new float[]{0.0f, 0.0f}),
						   
						   //FRAME TOP
					   new VertexData(new float[]{0.0f,0.0f,0.0f,1.0f}, 
						   this.frameColor, new float[]{1.0f, 0.0f}),
								   
					   new VertexData(new float[]{0.0f,-this.frameSize,0.0f,1.0f}, 
						   this.frameColor, new float[]{1.0f, 1.0f}),
								   
					   new VertexData(new float[]{this.width,-this.frameSize,0.0f,1.0f}, 
						   this.frameColor, new float[]{0.0f, 1.0f}),
								   
					   new VertexData(new float[]{this.width,0.0f,0.0f,1.0f}, 
						   this.frameColor, new float[]{0.0f, 0.0f}),
								   
						   //FRAME RIGHT
					   new VertexData(new float[]{this.width-this.frameSize,0.0f,0.0f,1.0f}, 
						   this.frameColor, new float[]{1.0f, 0.0f}),
										   
					   new VertexData(new float[]{this.width-this.frameSize,-this.height,0.0f,1.0f}, 
						   this.frameColor, new float[]{1.0f, 1.0f}),
										   
					   new VertexData(new float[]{this.width,-this.height,0.0f,1.0f}, 
						   this.frameColor, new float[]{0.0f, 1.0f}),
										   
					   new VertexData(new float[]{this.width,0.0f,0.0f,1.0f}, 
						   this.frameColor, new float[]{0.0f, 0.0f}),
						   
						 //FRAME BOT
					   new VertexData(new float[]{0.0f,-this.height+this.frameSize,0.0f,1.0f}, 
						   this.frameColor, new float[]{1.0f, 0.0f}),
											   
					   new VertexData(new float[]{0.0f,-this.height,0.0f,1.0f}, 
						   this.frameColor, new float[]{1.0f, 1.0f}),
											   
					   new VertexData(new float[]{this.width,-this.height,0.0f,1.0f}, 
						   this.frameColor, new float[]{0.0f, 1.0f}),
											   
					   new VertexData(new float[]{this.width,-this.height+this.frameSize,0.0f,1.0f}, 
						   this.frameColor, new float[]{0.0f, 0.0f})};
			
			indices = new byte[]{
					0, 1, 2,
					2, 3, 0, //Background
					
					4, 5, 6,
					6, 7, 4, //Left Frame
					
					8, 9, 10,
					10, 11, 8, //Top Frame
					
					12, 13, 14,
					14, 15, 12
			};
			
			System.out.println("hasDatFrame");
		}
		else if(this.hasBackground && !this.hasFrame)
		{
			points = new VertexData[]{
					   new VertexData(new float[]{0.0f,0.0f,0.0f,1.0f}, 
						   this.backgroundColor, new float[]{0.0f, 0.0f}),
						   
					   new VertexData(new float[]{0.0f,-this.height,0.0f,1.0f}, 
						   this.backgroundColor, new float[]{0.0f, 0.0f}),
						   
					   new VertexData(new float[]{this.width,-this.height,0.0f,1.0f}, 
						   this.backgroundColor, new float[]{0.0f, 0.0f}),
						   
					   new VertexData(new float[]{this.width,0.0f,0.0f,1.0f}, 
						   this.backgroundColor, new float[]{0.0f, 0.0f})};
			
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
		
	}
	
	public final void drawSelection()
	{
		
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
		
		this.textShader.passMatrix(Matrix4x4.getIdentity()/*scalingMatrix*/, MatrixTypes.SCALING);
		this.textShader.passMatrix(this.textTranslationMatrix, MatrixTypes.TRANSLATION);
		this.textShader.passMatrix(Matrix4x4.getIdentity()/*rotationMatrix*/, MatrixTypes.ROTATION);
		this.textShader.passMatrix(StaticManager.WINDOW_MATRIX, MatrixTypes.WINDOW);
		this.textShader.pass1i("selection", 0);
		this.textShader.pass4f("visible_Area",this.visibleArea.getX(),this.visibleArea.getY(),this.visibleArea.getZ(),this.visibleArea.getW());
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
		this.mainShader.passMatrix(StaticManager.WINDOW_MATRIX, MatrixTypes.WINDOW);
		this.mainShader.pass1i("selection", 0);
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
	
	public final void draw()
	{
		if(this.hasBackground || this.hasFrame)
			this.drawMainGL();
		
		if(this.hasIcon)
			this.drawIcon();
		
		if(this.hasText && this.text.trim().length() != 0)
			this.drawText();
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
		return text;
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
	
}
