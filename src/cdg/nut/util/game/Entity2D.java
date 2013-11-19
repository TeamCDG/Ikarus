package cdg.nut.util.game;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import cdg.World;
import cdg.nut.util.Globals;
import cdg.nut.util.Matrix4x4;
import cdg.nut.util.MatrixTypes;
import cdg.nut.util.ShaderProgram;
import cdg.nut.util.Utility;
import cdg.nut.util.Vertex2;
import cdg.nut.util.VertexData;

public abstract class Entity2D extends Entity {
	
	public static final int SELECTION_WITH_SPRITE = 0;
	public static final int SELECTION_WITH_QUAD = 1;
	
	public static final int CDT_CIRCLE = 0;
	public static final int CDT_BOUNDING_LINES = 1;
	
	public static final int BOUNDS_TOP_LEFT_EDGE = 0;
	public static final int BOUNDS_BOT_LEFT_EDGE = 1;
	public static final int BOUNDS_BOT_RIGHT_EDGE = 2;
	public static final int BOUNDS_TOP_RIGHT_EDGE = 3;
	
	public static float[] SELECTION_SIGN_COLOR = new float[]{1.0f, 1.0f, 1.0f, 0.8f};
	public static float[] FOCUSED_SIGN_COLOR = new float[]{0.4f, 0.4f, 1.0f, 0.8f};
	public static float SELECTION_SIGN_SIZE = 0.02f;
	
	private int VAO = -1;
	private int VBO = -1;
	private int indiciesVBO = -1;
	private int indiciesCount = -1;
	
	private int ssVAO = -1;
	private int ssVBO = -1;
	private int ssIndiciesVBO = -1;
	private int ssIndiciesCount = -1;
	
	private int selectionType = SELECTION_WITH_QUAD;
	private int textureId = -1;
	private int collisionDetectionType = 0;
	private boolean collideable = true;
	private Vertex2[][] collisionLines;
	private float collisionRadius;
	private int mapTexID = -1;
	private boolean selectable = false;
	private boolean selected = false;
	private boolean focused = false;
	private World w = null;
	
	public Entity2D(long id)
	{
		super(id);
	}
	
	public void initialize()
	{
		setupGL();
	}
	
	public void initialize(Vertex2 center)
	{
		setupGL(center);
	}
	
	

	public Entity2D(long id, float x, float y, float width, float height, ShaderProgram shader) 
	{
		super(id);
		this.setX(x);
		this.setY(y);
		this.setWidth(width);
		this.setHeight(height);
		this.setShader(shader);
		setupGL();
	}
	
	public Entity2D(long id, float x, float y, float width, float height, ShaderProgram shader, VertexData[] points, byte[] indices) 
	{
		super(id);
		this.setX(x);
		this.setY(y);
		this.setWidth(width);
		this.setHeight(height);
		this.setShader(shader);
		setupGL(points, indices);
	}
	
	public Entity2D(long id, float x, float y, float width, float height, String texturePath, ShaderProgram shader) 
	{
		super(id);
		this.setX(x);
		this.setY(y);
		this.setWidth(width);
		this.setHeight(height);
		this.textureId = Utility.loadPNGTexture(texturePath, GL13.GL_TEXTURE0);
		this.setShader(shader);
		setupGL();
	}
	
	public Entity2D(long id, float x, float y, float width, float height, int textureId, ShaderProgram shader) 
	{
		super(id);
		this.setX(x);
		this.setY(y);
		this.setWidth(width);
		this.setHeight(height);
		this.textureId = textureId;
		this.setShader(shader);
		setupGL();
	}
		
	public Entity2D(long id, float x, float y, float width, float height, String texturePath, ShaderProgram shader, VertexData[] points, byte[] indices) 
	{
		super(id);
		this.setX(x);
		this.setY(y);
		this.setWidth(width);
		this.setHeight(height);
		this.textureId = Utility.loadPNGTexture(texturePath, GL13.GL_TEXTURE0);
		this.setShader(shader);
		setupGL(points, indices);
	}
	
	public Entity2D(long id, float x, float y, float width, float height, int textureId, ShaderProgram shader, VertexData[] points, byte[] indices) 
	{
		super(id);
		this.setX(x);
		this.setY(y);
		this.setWidth(width);
		this.setHeight(height);
		this.textureId = textureId;
		this.setShader(shader);
		setupGL(points, indices);
	}
	
	private void setupGL()
	{
		this.setupGL(new Vertex2(0.0f, 0.0f));
	}
	
	private void setupGL(Vertex2 center) {
		setupSelectionSign();
		
		VertexData[] points = new VertexData[]{new VertexData(new float[]{-1.0f * (this.getWidth() * 0.5f)+center.getX(),this.getHeight() * 0.5f+center.getY(),0.0f,1.0f}, 
												   Utility.idToGlColor(getId(), false), new float[]{1.0f, 0.0f}),
												   
				   							   new VertexData(new float[]{-1.0f * (this.getWidth() * 0.5f)+center.getX(),-1.0f * (this.getHeight() * 0.5f)+center.getY(),0.0f,1.0f}, 
				   								   Utility.idToGlColor(getId(), false), new float[]{1.0f, 1.0f}),
				   								   
				   							   new VertexData(new float[]{this.getWidth() * 0.5f+center.getX(),-1.0f * (this.getHeight() * 0.5f)+center.getY(),0.0f,1.0f}, 
				   								   Utility.idToGlColor(getId(), false), new float[]{0.0f, 1.0f}),
				   								   
				   							   new VertexData(new float[]{this.getWidth() * 0.5f+center.getX(),this.getHeight() * 0.5f+center.getY(),0.0f,1.0f}, 
				   								   Utility.idToGlColor(getId(), false), new float[]{0.0f, 0.0f})};
		byte[] indices = {
				0, 1, 2,
				2, 3, 0
		};
		
		setupGL(points, indices);
		
	}
	
	public void setupSelectionSign()
	{
		float[] c = new float[]{1.0f,1.0f,1.0f,1.0f};
		float[] st = new float[]{1.0f,1.0f};
		VertexData[] points = new VertexData[]{new VertexData(new float[]{this.getWidth() * -0.5f+Entity2D.SELECTION_SIGN_SIZE,this.getHeight() * 0.5f,0.0f,1.0f}, 
				   c, st),
				   
			   new VertexData(new float[]{this.getWidth() * -0.5f+Entity2D.SELECTION_SIGN_SIZE,(this.getHeight() * 0.5f)-Entity2D.SELECTION_SIGN_SIZE,0.0f,1.0f}, 
				   c, st),
				   
			   new VertexData(new float[]{this.getWidth() * -0.25f,(this.getHeight() * 0.5f)-Entity2D.SELECTION_SIGN_SIZE,0.0f,1.0f}, 
				   c, st),
				   
			   new VertexData(new float[]{this.getWidth() * -0.25f,this.getHeight() * 0.5f,0.0f,1.0f}, 
				   c, st),
				   
			   //------------------------------------------------------------------------------------
				   
			   new VertexData(new float[]{this.getWidth() * 0.5f-Entity2D.SELECTION_SIGN_SIZE,this.getHeight() * 0.5f,0.0f,1.0f}, 
				   c, st),
						   
			   new VertexData(new float[]{this.getWidth() * 0.5f-Entity2D.SELECTION_SIGN_SIZE,(this.getHeight() * 0.5f)-Entity2D.SELECTION_SIGN_SIZE,0.0f,1.0f}, 
				   c, st),
						   
			   new VertexData(new float[]{this.getWidth() * 0.25f,(this.getHeight() * 0.5f)-Entity2D.SELECTION_SIGN_SIZE,0.0f,1.0f}, 
				   c, st),
						   
			   new VertexData(new float[]{this.getWidth() * 0.25f,this.getHeight() * 0.5f,0.0f,1.0f}, 
				   c, st),
				   
			   //------------------------------------------------------------------------------------
		
			   new VertexData(new float[]{this.getWidth() * -0.5f+Entity2D.SELECTION_SIGN_SIZE,this.getHeight() * -0.5f,0.0f,1.0f}, 
				   c, st),
						   
			   new VertexData(new float[]{this.getWidth() * -0.5f+Entity2D.SELECTION_SIGN_SIZE,(this.getHeight() * -0.5f)+Entity2D.SELECTION_SIGN_SIZE,0.0f,1.0f}, 
				   c, st),
						   
			   new VertexData(new float[]{this.getWidth() * -0.25f,(this.getHeight() * -0.5f)+Entity2D.SELECTION_SIGN_SIZE,0.0f,1.0f}, 
				   c, st),
						   
			   new VertexData(new float[]{this.getWidth() * -0.25f,this.getHeight() * -0.5f,0.0f,1.0f}, 
				   c, st),		
		   
			   //------------------------------------------------------------------------------------
		
			   new VertexData(new float[]{this.getWidth() * 0.5f-Entity2D.SELECTION_SIGN_SIZE,this.getHeight() * -0.5f,0.0f,1.0f}, 
				   c, st),
						   
			   new VertexData(new float[]{this.getWidth() * 0.5f-Entity2D.SELECTION_SIGN_SIZE,(this.getHeight() * -0.5f)+Entity2D.SELECTION_SIGN_SIZE,0.0f,1.0f}, 
				   c, st),
						   
			   new VertexData(new float[]{this.getWidth() * 0.25f,(this.getHeight() * -0.5f)+Entity2D.SELECTION_SIGN_SIZE,0.0f,1.0f}, 
				   c, st),
						   
			   new VertexData(new float[]{this.getWidth() * 0.25f,this.getHeight() * -0.5f,0.0f,1.0f}, 
				   c, st),		
		   
			   //------------------------------------------------------------------------------------
		
			   new VertexData(new float[]{this.getWidth() * -0.5f,this.getHeight() * 0.5f,0.0f,1.0f}, 
				   c, st),
						   
			   new VertexData(new float[]{this.getWidth() * -0.5f,(this.getHeight() * 0.25f),0.0f,1.0f}, 
				   c, st),
						   
			   new VertexData(new float[]{this.getWidth() * -0.5f+Entity2D.SELECTION_SIGN_SIZE,(this.getHeight() * 0.25f),0.0f,1.0f}, 
				   c, st),
						   
			   new VertexData(new float[]{this.getWidth() * -0.5f+Entity2D.SELECTION_SIGN_SIZE,this.getHeight() * 0.5f,0.0f,1.0f}, 
				   c, st),
		
			   //------------------------------------------------------------------------------------
			
			   new VertexData(new float[]{this.getWidth() * 0.5f,this.getHeight() * 0.5f,0.0f,1.0f}, 
				   c, st),
						   
			   new VertexData(new float[]{this.getWidth() * 0.5f,(this.getHeight() * 0.25f),0.0f,1.0f}, 
				   c, st),
						   
			   new VertexData(new float[]{this.getWidth() * 0.5f-Entity2D.SELECTION_SIGN_SIZE,(this.getHeight() * 0.25f),0.0f,1.0f}, 
				   c, st),
						   
			   new VertexData(new float[]{this.getWidth() * 0.5f-Entity2D.SELECTION_SIGN_SIZE,this.getHeight() * 0.5f,0.0f,1.0f}, 
				   c, st),

			   //------------------------------------------------------------------------------------
		
			   new VertexData(new float[]{this.getWidth() * -0.5f,this.getHeight() * -0.5f,0.0f,1.0f}, 
				   c, st),
						   
			   new VertexData(new float[]{this.getWidth() * -0.5f,(this.getHeight() * -0.25f),0.0f,1.0f}, 
				   c, st),
						   
			   new VertexData(new float[]{this.getWidth() * -0.5f+Entity2D.SELECTION_SIGN_SIZE,(this.getHeight() * -0.25f),0.0f,1.0f}, 
				   c, st),
						   
			   new VertexData(new float[]{this.getWidth() * -0.5f+Entity2D.SELECTION_SIGN_SIZE,this.getHeight() * -0.5f,0.0f,1.0f}, 
				   c, st),
		
			   //------------------------------------------------------------------------------------
			
			   new VertexData(new float[]{this.getWidth() * 0.5f,this.getHeight() * -0.5f,0.0f,1.0f}, 
				   c, st),
						   
			   new VertexData(new float[]{this.getWidth() * 0.5f,(this.getHeight() * -0.25f),0.0f,1.0f}, 
				   c, st),
						   
			   new VertexData(new float[]{this.getWidth() * 0.5f-Entity2D.SELECTION_SIGN_SIZE,(this.getHeight() * -0.25f),0.0f,1.0f}, 
				   c, st),
						   
			   new VertexData(new float[]{this.getWidth() * 0.5f-Entity2D.SELECTION_SIGN_SIZE,this.getHeight() * -
					   0.5f,0.0f,1.0f}, 
				   c, st)};
		
		byte[] indices = {
		0, 1, 2,
		2, 3, 0,
		
		4, 5, 6,
		6, 7, 4,
		
		8, 9, 10,
		10, 11, 8,
		
		12, 13, 14,
		14, 15, 12,
		
		16, 17, 18,
		18, 19, 16,
		
		20, 21, 22,
		22, 23, 20,
		
		24, 25, 26,
		26, 27, 24,
		
		28, 29, 30,
		30, 31, 28
		
		};
		
		// Put each 'Vertex' in one FloatBuffer
				FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(points.length *
						VertexData.ELEMENT_COUNT);
				for (int i = 0; i < points.length; i++) {
					// Add position, color and texture floats to the buffer
					verticesBuffer.put(points[i].getElements());
				}
				verticesBuffer.flip();	
				
				ssIndiciesCount = indices.length;
				ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(ssIndiciesCount);
				indicesBuffer.put(indices);
				indicesBuffer.flip();
				
				// Create a new Vertex Array Object in memory and select it (bind)
				if(this.ssVAO == -1)
					ssVAO = GL30.glGenVertexArrays();
				
				GL30.glBindVertexArray(ssVAO);
				
				// Create a new Vertex Buffer Object in memory and select it (bind)
				if(this.ssVBO == -1)
					ssVBO = GL15.glGenBuffers();
				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, ssVBO);
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
				if(this.ssIndiciesVBO == -1)
					ssIndiciesVBO = GL15.glGenBuffers();
				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ssIndiciesVBO);
				GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	private void setupGL(VertexData[] points, byte[] indices)
	{
		
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
	public final void drawSelection() {		
		this.draw(true);		
	}
	
	public void draw() {
		this.draw(false);
	}
	
	public final void draw(boolean selection) {
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		this.getShader().bind();
		
		this.bindTextures();
				
		// Bind to the VAO that has all the information about the vertices
		GL30.glBindVertexArray(this.VAO);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
				
		// Bind to the index VBO that has all the information about the order of the vertices
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.indiciesVBO);
		
		this.getShader().passMatrix(this.getScalingMatrix(), MatrixTypes.SCALING);
		this.getShader().passMatrix(this.getTranslationMatrix(), MatrixTypes.TRANSLATION);
		this.getShader().passMatrix(this.getRotationMatrix(), MatrixTypes.ROTATION);
		
		int val = selection ? 1 : 0;
		this.getShader().pass1i("selection", val);
		val = 0;
		
		this.passShaderVariables();
		
		// Draw the vertices
		GL11.glDrawElements(GL11.GL_TRIANGLES, this.indiciesCount, GL11.GL_UNSIGNED_BYTE, 0);
				
		// Put everything back to default (deselect)
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		
		this.getShader().unbind();
		
		this.drawChilds();
		
		if(this.selectable && (this.selected || this.focused))
		{
			this.drawSelectionSign();
		}
	}
	
	protected void drawSelectionSign()
	{
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		Globals.getIdentityShader().bind();
				
		// Bind to the VAO that has all the information about the vertices
		GL30.glBindVertexArray(this.ssVAO);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
				
		// Bind to the index VBO that has all the information about the order of the vertices
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.ssIndiciesVBO);
		
		Globals.getIdentityShader().passMatrix(this.getScalingMatrix(), MatrixTypes.SCALING);
		Globals.getIdentityShader().passMatrix(this.getTranslationMatrix(), MatrixTypes.TRANSLATION);
		Globals.getIdentityShader().passMatrix(this.getRotationMatrix(), MatrixTypes.ROTATION);
		Globals.getIdentityShader().passMatrix(Globals.getWindowMatrix(), MatrixTypes.WINDOW);
		
		if(this.focused)
		{
			Globals.getIdentityShader().pass4f("color", Entity2D.FOCUSED_SIGN_COLOR);
		}
		else
		{
			Globals.getIdentityShader().pass4f("color", Entity2D.SELECTION_SIGN_COLOR);
		}
				
		// Draw the vertices
		GL11.glDrawElements(GL11.GL_TRIANGLES, this.ssIndiciesCount, GL11.GL_UNSIGNED_BYTE, 0);
				
		// Put everything back to default (deselect)
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
	
	protected void bindTextures()
	{
		if(this.textureId != -1)
		{
			// Bind the texture
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureId);
		}
		if(this.mapTexID != -1)
		{
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mapTexID);
		}
	}
	
	protected abstract void passShaderVariables();
	
	public final boolean collide(Entity2D e)
	{
		// Filter collision type...
		
		if(this.getCollisionDetectionType() == Entity2D.CDT_CIRCLE
		   && e.getCollisionDetectionType() == Entity2D.CDT_CIRCLE)
			return collideCC(e);
		
		else if((this.getCollisionDetectionType() == Entity2D.CDT_BOUNDING_LINES
				&& e.getCollisionDetectionType() == Entity2D.CDT_CIRCLE)
				|| (this.getCollisionDetectionType() == Entity2D.CDT_CIRCLE
				&& e.getCollisionDetectionType() == Entity2D.CDT_BOUNDING_LINES))
					return collideCBl(e);
		
		else
			return collideBlBl(e);
			
	}
	
	private final boolean collideBlBl(Entity2D e)
	{
		Vertex2[][] l = e.getCollisionLines();
		for(int i = 0; i < l.length; i++)
		{
			for(int z = 0; z < this.getCollisionLines().length; z++)
			{
				if(Utility.lineLineIntersect2D(this.getCollisionLines()[z][0], 
											   this.getCollisionLines()[z][1], 
											   l[i][0], l[i][1]));
					return true;
			}
		}
		return false;
	}
	
	private final boolean collideCBl(Entity2D e)
	{
		if(this.getCollisionDetectionType() == Entity2D.CDT_BOUNDING_LINES)
		{
			for(int i = 0; i < this.getCollisionLines().length; i++)
			{
				if(Utility.lineCircleIntersect2D(this.getCollisionLines()[i][0], 
											     this.getCollisionLines()[i][1],  
											     new Vertex2(e.getX(), e.getY()), 
											     e.getCollisionRadius()))
					return true;
			}
		}
		else
		{
			for(int i = 0; i < this.getCollisionLines().length; i++)
			{
				if(Utility.lineCircleIntersect2D(e.getCollisionLines()[i][0], 
											     e.getCollisionLines()[i][1],  
											     new Vertex2(this.getX(), this.getY()), 
											     this.getCollisionRadius()))
					return true;
			}
		}
		return false;
	}
	
	private final boolean collideCC(Entity2D e)
	{
		return Utility.circleCircleIntersect2D(new Vertex2(this.getX(), this.getY()), 
											   this.getCollisionRadius(), 
											   new Vertex2(e.getX(), e.getY()), 
											   e.getCollisionRadius());
	}
	
	@Override
	public final void finalize() {
		// TODO Auto-generated method stub
		
	}
	
	public abstract void drawChilds();

	/**
	 * @return the selectionType
	 */
	public int getSelectionType() {
		return selectionType;
	}

	/**
	 * @param selectionType the selectionType to set
	 */
	public void setSelectionType(int selectionType) {
		this.selectionType = selectionType;
	}

	/**
	 * @return the textureId
	 */
	public int getTextureId() {
		return textureId;
	}

	/**
	 * @param textureId the textureId to set
	 */
	public void setTextureId(int textureId) {
		this.textureId = textureId;
	}

	/**
	 * @return the collisionDetectionType
	 */
	public int getCollisionDetectionType() {
		return collisionDetectionType;
	}

	/**
	 * @param collisionDetectionType the collisionDetectionType to set
	 */
	public void setCollisionDetectionType(int collisionDetectionType) {
		this.collisionDetectionType = collisionDetectionType;
	}

	/**
	 * @return the collideable
	 */
	public boolean isCollideable() {
		return collideable;
	}

	/**
	 * @param collideable the collideable to set
	 */
	public void setCollideable(boolean collideable) {
		this.collideable = collideable;
	}

	/**
	 * @return the collisionLines
	 */
	public Vertex2[][] getCollisionLines() {
		return collisionLines;
	}

	/**
	 * @param collisionLines the collisionLines to set
	 */
	public void setCollisionLines(Vertex2[][] collisionLines) {
		this.collisionLines = collisionLines;
	}

	/**
	 * @return the collisionRadius
	 */
	public float getCollisionRadius() {
		return collisionRadius;
	}

	/**
	 * @param collisionRadius the collisionRadius to set
	 */
	public void setCollisionRadius(float collisionRadius) {
		this.collisionRadius = collisionRadius;
	}
	
	public Vertex2[] getBounds()
	{
		Vertex2 tl = new Vertex2(this.getX()-(this.getWidth()/2.0f), this.getY()-(this.getHeight()/2.0f));
		Vertex2 bl = new Vertex2(this.getX()-(this.getWidth()/2.0f), this.getY()+(this.getHeight()/2.0f));
		Vertex2 br = new Vertex2(this.getX()+(this.getWidth()/2.0f), this.getY()+(this.getHeight()/2.0f));
		Vertex2 tr = new Vertex2(this.getX()+(this.getWidth()/2.0f), this.getY()-(this.getHeight()/2.0f));
		
		return new Vertex2[]{tl, bl, br, tr};
	}

	/**
	 * @return the mapTexID
	 */
	public int getMapTexID() {
		return mapTexID;
	}

	/**
	 * @param mapTexID the mapTexID to set
	 */
	public void setMapTexID(int mapTexID) {
		this.mapTexID = mapTexID;
	}

	public abstract void reloadShader();

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
	
	public void select()
	{
		this.selected = true;
	}
	
	public void unselect()
	{
		this.selected = false;
	}
	
	public void focused()
	{
		this.focused = true;
	}
	
	public void unfocused()
	{
		this.focused = false;
	}



	/**
	 * @return the w
	 */
	public World getWorld() {
		return w;
	}

	/**
	 * @param w the w to set
	 */
	public void setWorld(World w) {
		this.w  = w;
	}
	
	public void damage(float value)
	{}
}
