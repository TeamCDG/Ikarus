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
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import cdg.interfaces.IEntity;
import cdg.interfaces.IKeyboardListener;
import cdg.interfaces.IMatrix;
import cdg.util.Matrix4x4;
import cdg.util.MusicPlayer;
import cdg.util.SoundManager;
import cdg.util.Vertex2;
import cdg.util.VertexData;
import cdg.util.StaticManager;
import cdg.util.Utility;

public class Ikarus implements IEntity, IKeyboardListener {

	private float x;
	private float y;
	private float width;
	private float height;
	private int texID;
	private IkarusTurret t1;
	private IkarusTurret t2;
	private int wML;
	private int shieldSeedUniformLocation;
	private int tML;
	private IMatrix translationMatrix;
	private int rML;
	private IMatrix rotationMatrix;
	private int shaderID;
	private float deg = 0.0f;
	private float turnRate = 0.04f;
	private float moveRate = 0.0002f;
	private float shieldLevel;
	private int shieldLevelLocation;
	
	float xway = 0.00001f;
	float yway = 0.00001f;
	
	protected int drawVAO = -1;
	protected int drawVBO = -1;
	protected int drawIndiciesVBO = -1;
	private int id;
	private int aviableShoots = 7;
	private int reload;
	private boolean shooting;
	private Vertex2 shootAt;
	private SoundManager shootSound;
	
	public Ikarus(int id) {
		this.id = id;
		this.x = 0;
		this.y = 0;
		this.width = 0.6f;
		this.height = 0.3f;
		this.texID = Utility.loadPNGTexture("res\\textures\\ship.png", GL13.GL_TEXTURE0);
		setupGL();
		loadShader();
		this.shieldLevel = 1.0f;
		this.t1 = new IkarusTurret(-0.085f, -0.004f, this.width);
		//this.shootSound = new SoundManager("res\\sound\\laser.wav", 0.25f);
	}
	
	private void loadShader()
	{
		//load vertex shader
		int vsId = Utility.loadShader("res\\shader\\ikarusVertex.glsl", GL20.GL_VERTEX_SHADER);
		//load fragment shader
		int fsId = Utility.loadShader("res\\shader\\ikarusFragment.glsl", GL20.GL_FRAGMENT_SHADER);
				
		this.shaderID = GL20.glCreateProgram();
		GL20.glAttachShader(this.shaderID, vsId);
		GL20.glAttachShader(this.shaderID, fsId);
		GL20.glLinkProgram(this.shaderID);
		
		this.wML = GL20.glGetUniformLocation(this.shaderID, "window_Matrix");
		this.rML = GL20.glGetUniformLocation(this.shaderID, "rotation_Matrix");
		this.tML = GL20.glGetUniformLocation(this.shaderID, "translation_Matrix");
		this.shieldSeedUniformLocation = GL20.glGetUniformLocation(this.shaderID, "seed");
		this.shieldLevelLocation = GL20.glGetUniformLocation(this.shaderID, "shield_Level");
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
		return this.xway;
	}

	@Override
	public void setX(float x) {
		// TODO Auto-generated method stub

	}

	@Override
	public float getY() {
		// TODO Auto-generated method stub
		return this.yway;
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
	public void tick() {
		this.deg = deg % 360.0f;
		
		if(new Random().nextInt(15) == 15)
		{
			
			IEntity e = StaticManager.objects.get(new Random().nextInt(StaticManager.objects.size()));
			if(e != this)
			{
				this.shoot(e.getX(), e.getY());
			}
		}
		
		if(this.shooting)
		{
			this.shoot(this.shootAt.getX(), this.shootAt.getY());
		}
		
		if(this.aviableShoots == 0)
		{
			this.reload = 500;
			this.aviableShoots = 7;
		}
		
		
		if(Keyboard.isKeyDown(Keyboard.KEY_W))
		{
			this.xway += this.moveRate * StaticManager.delta * (float) Math.sin(Utility.degToRad(deg+90));
			this.yway += this.moveRate * StaticManager.delta * (float) Math.cos(Utility.degToRad(deg+90));
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A))
		{
			this.deg -= this.turnRate * StaticManager.delta;			
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D))
		{
			this.deg += this.turnRate * StaticManager.delta;		
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_SUBTRACT))
		{
			this.shieldLevel -= 0.1f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_ADD))
		{
			this.shieldLevel += 0.1f;
			if(this.shieldLevel > 1.0f)
				this.shieldLevel = 1.0f;
		}
		
		
		this.rotationMatrix = new Matrix4x4((float) Math.cos(Utility.degToRad(deg)), (float) Math.sin(Utility.degToRad(deg))*-1.0f, 0.0f, 0.0f,
											(float) Math.sin(Utility.degToRad(deg)), (float) Math.cos(Utility.degToRad(deg)), 0.0f, 0.0f,
											0.0f, 0.0f, 1.0f, 0.0f,
											0.0f, 0.0f, 0.0f, 1.0f);
		
		this.translationMatrix = new Matrix4x4(1.0f, 0.0f, 0.0f, 0.0f,
											   0.0f, 1.0f, 0.0f, 0.0f,
											   0.0f, 0.0f, 1.0f, 0.0f,
											   xway, yway, 0.0f, 1.0f);
		
		if(this.reload != 0)
			this.reload -= StaticManager.delta;
		
		if(this.reload < 0)
			this.reload = 0;
		
		this.t1.doTick(deg, xway, yway);
		//this.t1.doTick(deg, xway-0.1f, yway-0.004f);
	}

	@Override
	public void draw(boolean selection) {
		
		try
		{
			GL11.glEnable(GL11.GL_BLEND);
			
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.texID);
			
			GL20.glUseProgram(this.shaderID);
			
			GL30.glBindVertexArray(this.drawVAO);
			
			FloatBuffer wmat = BufferUtils.createFloatBuffer(16);
			wmat.put(StaticManager.WINDOW_MATRIX.toArray());
			wmat.flip();
			
			GL20.glUniformMatrix4(this.wML, false, wmat);
			
			FloatBuffer rmat = BufferUtils.createFloatBuffer(16);
			rmat.put(((Matrix4x4) this.rotationMatrix).toArray());
			rmat.flip();
			
			GL20.glUniformMatrix4(this.rML, false, rmat);
			
			FloatBuffer tmat = BufferUtils.createFloatBuffer(16);
			tmat.put(((Matrix4x4) this.translationMatrix).toArray());
			tmat.flip();
			
			GL20.glUniformMatrix4(this.tML, false, tmat);
			
			GL20.glUniform2f(this.shieldSeedUniformLocation, new Random().nextInt(1338), new Random().nextInt(1338));		
			GL20.glUniform1f(this.shieldLevelLocation, this.shieldLevel);
			
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);
			
			// Bind to the index VBO that has all the information about the order of the vertices
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.drawIndiciesVBO);
			
			// Draw the vertices
			GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_BYTE, 0);
			
			// Put everything back to default (deselect)
			
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL20.glDisableVertexAttribArray(2);
			GL30.glBindVertexArray(0);
			GL20.glUseProgram(0);
			
			if(!selection)
				t1.draw(false);
		}
		catch(Exception e){};
	}

	@Override
	public void keyDown(int key, char keyChar) 
	{
		if(keyChar == 'w')
		{
			this.xway += this.moveRate * StaticManager.delta * (float) Math.sin(Utility.degToRad(deg+90));
			this.yway += this.moveRate * StaticManager.delta * (float) Math.cos(Utility.degToRad(deg+90));
		}
		else if(keyChar == 'a')
		{
			this.deg -= this.turnRate * StaticManager.delta;			
		}
		else if(keyChar == 'a')
		{
			this.deg += this.turnRate * StaticManager.delta;		
		}
		
	}

	@Override
	public float getHitCircleRadius() {
		// TODO Auto-generated method stub
		return 0.3f;
	}

	@Override
	public void damage(float f) {
		this.shieldLevel -= f;
		
	}

	public void shoot(float x, float y) 
	{
		if(this.reload <= 0)
		{
			this.aviableShoots--;
			float dx = (x) - (this.xway*(1.0f/StaticManager.ASPECT_RATIO));
			float dy = y - (this.yway);
			float w = (float)(Math.atan2(dx * (StaticManager.ASPECT_RATIO), dy) * (180/Math.PI));
			StaticManager.shoots.add(new LaserShoot(this, 0.0f, 0.0f, this.xway, this.yway, w+10.0f*new Random().nextFloat()-5.0f, 0.5f));
			//this.shootSound.play();
			if(this.aviableShoots != 0)
				this.shooting = true;
			else
				this.shooting = false;
			this.shootAt = new Vertex2(x,y);
		}
		else
		{
			this.shooting = false;
		}
		
	}


}
