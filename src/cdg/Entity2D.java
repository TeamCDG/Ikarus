package cdg;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import cdg.util.Matrix4x4;
import cdg.util.MatrixTypes;
import cdg.util.ShaderProgram;
import cdg.util.StaticManager;
import cdg.util.Utility;
import cdg.util.Vertex2;
import cdg.util.VertexData;

public abstract class Entity2D extends Entity {
	
	public static final int SELECTION_WITH_SPRITE = 0;
	public static final int SELECTION_WITH_QUAD = 1;
	
	public static final int CDT_CIRCLE = 0;
	public static final int CDT_BOUNDING_LINES = 1;
	
	public static final int BOUNDS_TOP_LEFT_EDGE = 0;
	public static final int BOUNDS_BOT_LEFT_EDGE = 1;
	public static final int BOUNDS_BOT_RIGHT_EDGE = 2;
	public static final int BOUNDS_TOP_RIGHT_EDGE = 3;
	
	private int VAO = -1;
	private int VBO = -1;
	private int indiciesVBO = -1;
	private int indiciesCount = -1;
	
	private int selectionType = SELECTION_WITH_QUAD;
	private int textureId = -1;
	private int collisionDetectionType = 0;
	private boolean collideable = true;
	private Vertex2[][] collisionLines;
	private float collisionRadius;
	
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
		VertexData[] points = new VertexData[]{new VertexData(new float[]{-1.0f * (this.getWidth() * 0.5f),this.getHeight() * 0.5f,0.0f,1.0f}, 
												   Utility.idToGlColor(getId(), false), new float[]{1.0f, 0.0f}),
												   
				   							   new VertexData(new float[]{-1.0f * (this.getWidth() * 0.5f),-1.0f * (this.getHeight() * 0.5f),0.0f,1.0f}, 
				   								   Utility.idToGlColor(getId(), false), new float[]{1.0f, 1.0f}),
				   								   
				   							   new VertexData(new float[]{this.getWidth() * 0.5f,-1.0f * (this.getHeight() * 0.5f),0.0f,1.0f}, 
				   								   Utility.idToGlColor(getId(), false), new float[]{0.0f, 1.0f}),
				   								   
				   							   new VertexData(new float[]{this.getWidth() * 0.5f,this.getHeight() * 0.5f,0.0f,1.0f}, 
				   								   Utility.idToGlColor(getId(), false), new float[]{0.0f, 0.0f})};
		byte[] indices = {
				0, 1, 2,
				2, 3, 0
		};
		
		setupGL(points, indices);
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
	
	public final void draw() {
		this.draw(false);
	}
	
	public final void draw(boolean selection) {
		
		this.getShader().bind();
		
		if(this.textureId != -1)
		{
			// Bind the texture
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureId);
		}
				
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
		//TODO Implement
		return false;
	}
	
	private final boolean collideCBl(Entity2D e)
	{
		//TODO Implement
		return false;
	}
	
	private final boolean collideCC(Entity2D e)
	{
		//TODO Implement
		return false;
	}
	
	@Override
	public final void finalize() {
		// TODO Auto-generated method stub
		
	}
	
	public abstract void damage(float amount);
	
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

}
