package cdg;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import cdg.interfaces.IEntity;
import cdg.interfaces.IMatrix;
import cdg.util.Matrix4x4;
import cdg.util.StaticManager;
import cdg.util.Utility;
import cdg.util.Vertex2;
import cdg.util.VertexData;

public class IkarusTurret implements IEntity {

	private float x = 0;
	private float y = 0;
	private float width;
	private float height;
	private int texID;
	private int wML;
	private int shieldSeedUniformLocation;
	private int tML;
	private IMatrix translationMatrix;
	private int otML;
	private IMatrix ownTranslationMatrix;
	private int shaderID;
	private float deg = 45.0f;
	private float turnDeg = 0.0f;
	private float hyp = 0.100079968f;
	private float turnRate = 0.04f;
		
	protected int drawVAO = -1;
	protected int drawVBO = -1;
	protected int drawIndiciesVBO = -1;
	private int irML;
	private Matrix4x4 ikarusRotationMatrix;
	private float xway;
	private float yway;
	private float xoff;
	private float yoff;
	private IMatrix negativeIkarusTranslationMatrix;
	private int nitML;
	private Matrix4x4 ntranslationMatrix;
	private int ntML;
	
	public IkarusTurret(float x, float y, float parentWidth) {
		this.xoff = x;
		this.yoff = y;
		this.width = parentWidth/16.0f;
		this.height = parentWidth/16.0f;
		this.texID = Utility.loadPNGTexture("res\\textures\\turret.png", GL13.GL_TEXTURE0);
		setupGL();
		loadShader();
		System.out.println(x+"/"+y+"/"+width+"/"+height+"/"+xoff+"/"+yoff);
	}
	
	private void loadShader()
	{
		//load vertex shader
		int vsId = Utility.loadShader("res\\shader\\ikarusTurretVertex.glsl", GL20.GL_VERTEX_SHADER);
		//load fragment shader
		int fsId = Utility.loadShader("res\\shader\\ikarusTurretFragment.glsl", GL20.GL_FRAGMENT_SHADER);
				
		this.shaderID = GL20.glCreateProgram();
		GL20.glAttachShader(this.shaderID, vsId);
		GL20.glAttachShader(this.shaderID, fsId);
		GL20.glLinkProgram(this.shaderID);
		
		this.wML = GL20.glGetUniformLocation(this.shaderID, "window_Matrix");
		this.irML = GL20.glGetUniformLocation(this.shaderID, "ikarus_Rotation_Matrix");
		this.tML = GL20.glGetUniformLocation(this.shaderID, "translation_Matrix");
		this.ntML = GL20.glGetUniformLocation(this.shaderID, "ntranslation_Matrix");
		this.shieldSeedUniformLocation = GL20.glGetUniformLocation(this.shaderID, "seed");
		// Position information will be attribute 0
		GL20.glBindAttribLocation(this.shaderID, 0, "in_Position");
		// Color information will be attribute 1
		GL20.glBindAttribLocation(this.shaderID, 1, "in_Color");
		// Textute information will be attribute 2
		GL20.glBindAttribLocation(this.shaderID, 2, "in_TextureCoord");
				
		GL20.glValidateProgram(this.shaderID);	
	}
	
	private void setupGL()
	{
		if(this.drawVAO == -1)
			this.drawVAO = GL30.glGenVertexArrays(); //generate id for VAO
		if(this.drawVBO == -1)
			this.drawVBO = GL15.glGenBuffers(); //generate id for VBO
		
		glBindVertexArray(this.drawVAO); //bind our VAO
		glBindBuffer(GL_ARRAY_BUFFER, this.drawVBO); //bind our VBO
		
		//create our selection frame
		VertexData[] points = new VertexData[]{new VertexData(new float[]{this.x-(this.width/2.0f),this.y+(this.height/2.0f),0.0f,1.0f}, new float[]{1.0f,1.0f,1.0f,1.0f}, new float[]{0.0f, 0.0f}),
											   new VertexData(new float[]{this.x-(this.width/2.0f),this.y-(this.height/2.0f),0.0f,1.0f}, new float[]{1.0f,1.0f,1.0f,1.0f}, new float[]{0.0f, 1.0f}),
											   new VertexData(new float[]{this.x+(this.width/2.0f),this.y-(this.height/2.0f),0.0f,1.0f}, new float[]{1.0f,1.0f,1.0f,1.0f}, new float[]{1.0f, 1.0f}),
											   new VertexData(new float[]{this.x+(this.width/2.0f),this.y+(this.height/2.0f),0.0f,1.0f}, new float[]{1.0f,1.0f,1.0f,1.0f}, new float[]{1.0f, 0.0f})};
		
		//buffer to store data
		FloatBuffer f = BufferUtils.createFloatBuffer(points.length * VertexData.ELEMENT_COUNT);
		for (int i = 0; i < points.length; i++) 
		{
			//add position, color and texture floats to the buffer
			f.put(points[i].getElements());
		}
		f.flip();
		
		//upload our data
		GL15.glBufferData(GL_ARRAY_BUFFER, f, GL_STATIC_DRAW);
		
		// Put the position coordinates in attribute list 0
		GL20.glVertexAttribPointer(0, VertexData.POSITION_ELEMENT_COUNT, GL11.GL_FLOAT, 
				false, VertexData.STRIDE, VertexData.POSITION_BYTE_OFFSET);
		
		// Put the color components in attribute list 1
		GL20.glVertexAttribPointer(1, VertexData.COLOR_ELEMENT_COUNT, GL11.GL_FLOAT, 
				false, VertexData.STRIDE, VertexData.COLOR_BYTE_OFFSET);
		
		// Put the texture coordinates in attribute list 2
		GL20.glVertexAttribPointer(2, VertexData.TEXTURE_ELEMENT_COUNT, GL11.GL_FLOAT, 
				false, VertexData.STRIDE, VertexData.TEXTURE_BYTE_OFFSET);
			
		//unbind buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		//buffer for storing indicies
		ByteBuffer indiciesBuffer = BufferUtils.createByteBuffer(6);
		indiciesBuffer.put(new byte[]{0, 1, 2, 2, 3, 0}); //put in indicies
		indiciesBuffer.flip();
		
		if(this.drawIndiciesVBO == -1)
			this.drawIndiciesVBO = GL15.glGenBuffers(); //generate buffer id for indicies
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.drawIndiciesVBO); //bind indicies buffer
		
		//upload indicies
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiciesBuffer, GL15.GL_STATIC_DRAW);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0); //unbind buffer
		
		GL30.glBindVertexArray(0); //unbind VAO
	}

	@Override
	public float getX() {
		// TODO Auto-generated method stub
		return this.x;
	}

	@Override
	public void setX(float x) {
		// TODO Auto-generated method stub

	}

	@Override
	public float getY() {
		// TODO Auto-generated method stub
		return this.y;
	}

	@Override
	public void setY(float y) {
		// TODO Auto-generated method stub

	}

	@Override
	public float getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setWidth(float width) {
		// TODO Auto-generated method stub

	}

	@Override
	public float getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setHeight(float height) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTexture() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setTexture(int texID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tick() 
	{
		Vertex2 mp = Utility.mouseTo2DGL(Mouse.getX(), Mouse.getY(), StaticManager.WINDOW_WIDTH, StaticManager.WINDOW_HEIGHT);
		float dx = mp.getX() - (this.xway*(1.0f/StaticManager.ASPECT_RATIO));
		float dy = mp.getY() - (this.yway);
		
		this.deg = (float)(Math.atan2(dx * (StaticManager.ASPECT_RATIO), dy) * (180/Math.PI))-90.0f;
		
		this.ikarusRotationMatrix = new Matrix4x4((float) Math.cos(Utility.degToRad(deg)), (float) Math.sin(Utility.degToRad(deg))*-1.0f, 0.0f, 0.0f,
											(float) Math.sin(Utility.degToRad(deg)), (float) Math.cos(Utility.degToRad(deg)), 0.0f, 0.0f,
											0.0f, 0.0f, 1.0f, 0.0f,
											0.0f, 0.0f, 0.0f, 1.0f);
		
		float xpos = (float) (Math.sin(Utility.degToRad(this.turnDeg)) * hyp);
		float ypos = (float) (Math.cos(Utility.degToRad(this.turnDeg)) * hyp);
		this.translationMatrix = new Matrix4x4(1.0f, 0.0f, 0.0f, 0.0f,
				   0.0f, 1.0f, 0.0f, 0.0f,
				   0.0f, 0.0f, 1.0f, 0.0f,
				   xway, yway, 0.0f, 1.0f);
		
		this.ntranslationMatrix = new Matrix4x4(1.0f, 0.0f, 0.0f, 0.0f,
				   0.0f, 1.0f, 0.0f, 0.0f,
				   0.0f, 0.0f, 1.0f, 0.0f,
				   (xway+xoff)*-1, (yway+yoff)*-1, 0.0f, 1.0f);
	}

	@Override
	public void draw(boolean selection) {
		
		GL11.glEnable(GL11.GL_BLEND);
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.texID);
		
		GL20.glUseProgram(this.shaderID);
		
		GL30.glBindVertexArray(this.drawVAO);
		
		FloatBuffer wmat = BufferUtils.createFloatBuffer(16);
		wmat.put(StaticManager.WINDOW_MATRIX.toArray());
		wmat.flip();
		
		GL20.glUniformMatrix4(this.wML, false, wmat);
		
		
		FloatBuffer irmat = BufferUtils.createFloatBuffer(16);
		irmat.put(((Matrix4x4) this.ikarusRotationMatrix).toArray());
		irmat.flip();
		
		GL20.glUniformMatrix4(this.irML, false, irmat);
		
		FloatBuffer tmat = BufferUtils.createFloatBuffer(16);
		tmat.put(((Matrix4x4) this.translationMatrix).toArray());
		tmat.flip();
		
		GL20.glUniformMatrix4(this.tML, false, tmat);
		
		FloatBuffer ntmat = BufferUtils.createFloatBuffer(16);
		ntmat.put(((Matrix4x4) this.ntranslationMatrix).toArray());
		ntmat.flip();
		
		GL20.glUniformMatrix4(this.ntML, false, ntmat);
		
		GL20.glUniform2f(this.shieldSeedUniformLocation, new Random().nextInt(1338), new Random().nextInt(1338));		
		
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		// Bind to the index VBO that has all the information about the order of the vertices
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.drawIndiciesVBO);
		
		// Draw the vertices
		GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_BYTE, 0);
		
		// Put everything back dwto default (deselect)
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		GL20.glUseProgram(0);
	}
	
	public void doTick(float deg, float xmove, float ymove)
	{
		this.deg = deg+this.turnDeg;
		//this.turnDeg += 0.5f;
		this.xway = xmove;
		this.yway = ymove;
		//System.out.println(deg+"/"+y+"/"+((float)Math.cos(Utility.degToRad(deg))*hyp)+"/"+((float)Math.sin(Utility.degToRad(deg))*hyp)+"/"+xway+"/"+yway);
		this.tick();
	}

	@Override
	public float getHitCircleRadius() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void damage(float f) {
		// TODO Auto-generated method stub
		
	}

}
