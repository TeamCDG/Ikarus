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
import cdg.util.StaticManager;
import cdg.util.Utility;
import cdg.util.Vertex2;
import cdg.util.VertexData;

public class RiBracu implements IEntity {

	private float x;
	private float y;
	private float width;
	private float height;
	private int texID;
	private int wML;
	private int tML;
	private IMatrix translationMatrix;
	private int rML;
	private IMatrix rotationMatrix;
	private int dSML;
	private IMatrix deathScaleMatrix;
	private int shaderID;
	private float hitCircleRadius = 0.12f;
	private float deg = -90;
	private float targetDeg = -90;
	private float turnRate = 0.12f;
	private float moveRate = 0.0008f;
	private float shieldChargeRate = 0.000016f;
	private boolean turnLocked = false;
	private EngineExhaust engineLeft;
	private EngineExhaust engineRight;
	private float shieldLevel;
	private int shieldLevelLocation;
	
	float xway = 0.8f;
	float yway;
	
	protected int drawVAO = -1;
	protected int drawVBO = -1;
	protected int drawIndiciesVBO = -1;
	private int collisionLocked;
	private int id;
	private boolean dead = false;
	private float deathRemove = 1000;
	private int deathTime;
	
	public RiBracu(int id) {
		this.id = id;
		this.x = 0;
		this.y = 0;
		this.width = 0.2f;
		this.height = 0.2f;
		//this.xway = new Random().nextFloat()*2-1;
		this.yway = new Random().nextFloat()*2-1;
		this.deg = new Random().nextInt(360);
		this.texID = Utility.loadPNGTexture("res\\textures\\ribracu_shield.png", GL13.GL_TEXTURE0);
		setupGL();
		loadShader();
		this.shieldLevel = 1.0f;
		this.engineLeft = new EngineExhaust(-0.04f, -0.08f, 0.0f);
		this.engineRight = new EngineExhaust(0.04f, -0.08f, 0.0f);
		
	}
	
	private void loadShader()
	{
		//load vertex shader
		int vsId = Utility.loadShader("res\\shader\\ribracuVertex.glsl", GL20.GL_VERTEX_SHADER);
		//load fragment shader
		int fsId = Utility.loadShader("res\\shader\\ribracuFragment.glsl", GL20.GL_FRAGMENT_SHADER);
				
		this.shaderID = GL20.glCreateProgram();
		GL20.glAttachShader(this.shaderID, vsId);
		GL20.glAttachShader(this.shaderID, fsId);
		GL20.glLinkProgram(this.shaderID);
		
		this.wML = GL20.glGetUniformLocation(this.shaderID, "window_Matrix");
		this.rML = GL20.glGetUniformLocation(this.shaderID, "rotation_Matrix");
		this.tML = GL20.glGetUniformLocation(this.shaderID, "translation_Matrix");
		this.shieldLevelLocation = GL20.glGetUniformLocation(this.shaderID, "shield_Level");
		this.deathTime = GL20.glGetUniformLocation(this.shaderID, "death_Time");
		this.dSML = GL20.glGetUniformLocation(this.shaderID, "death_Scale_Matrix");
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
		VertexData[] points = new VertexData[]{new VertexData(new float[]{this.x-(this.width/2.0f),this.y+(this.height/2.0f),0.0f,1.0f}, Utility.idToGlColor(id, false), new float[]{0.0f, 0.0f}),
											   new VertexData(new float[]{this.x-(this.width/2.0f),this.y-(this.height/2.0f),0.0f,1.0f}, Utility.idToGlColor(id, false), new float[]{0.0f, 1.0f}),
											   new VertexData(new float[]{this.x+(this.width/2.0f),this.y-(this.height/2.0f),0.0f,1.0f}, Utility.idToGlColor(id, false), new float[]{1.0f, 1.0f}),
											   new VertexData(new float[]{this.x+(this.width/2.0f),this.y+(this.height/2.0f),0.0f,1.0f}, Utility.idToGlColor(id, false), new float[]{1.0f, 0.0f})};
		
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
		return this.id;
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
		
		if(this.shieldLevel < 0)
		{
			this.dead = true;
		}
		else
		{
			this.dead = false;
		}
		
		this.deathScaleMatrix = new Matrix4x4(1.0f, 0.0f, 0.0f, 0.0f,
				   0.0f, 1.0f, 0.0f, 0.0f,
				   0.0f, 0.0f, 1.0f, 0.0f,
				   0.0f, 0.0f, 0.0f, 1.0f);
		
		if(this.dead)
		{
			this.deathRemove  -= StaticManager.delta;
			this.deathScaleMatrix = new Matrix4x4((1.0f/1000.0f)*this.deathRemove, 0.0f, 0.0f, 0.0f,
												   0.0f, (1.0f/1000.0f)*this.deathRemove, 0.0f, 0.0f,
												   0.0f, 0.0f, 1.0f, 0.0f,
												   0.0f, 0.0f, 0.0f, 1.0f);
			if(this.deathRemove < 0)
			{
				StaticManager.objects.add(new RiBracu(this.id));
				StaticManager.objects.remove(this);
			}
			return;
		}
			
		
		this.targetDeg = this.targetDeg % 360.0f;
		
		if(new Random().nextInt(20) == 15 && !this.turnLocked)
			this.targetDeg = new Random().nextInt(360);
		
		if(new Random().nextInt(16) == 15 && !this.turnLocked)
		{
			StaticManager.shoots.add(new LaserShoot(this, 0.085f, 0.12f, this.xway, this.yway, deg, 0.75f));
			StaticManager.shoots.add(new LaserShoot(this, -0.09f, 0.12f, this.xway, this.yway, deg, 0.75f));
		}
		/*
		if(new Random().nextInt(100) == 15 && !this.turnLocked)
			this.targetDeg = this.deg + new Random().nextInt(90);
		else if(new Random().nextInt(100) == 15 && !this.turnLocked)
			this.targetDeg = this.deg - new Random().nextInt(90);
		*/
		
		this.xway += this.moveRate * StaticManager.delta * (float) Math.sin(Utility.degToRad(deg));
		this.yway += this.moveRate * StaticManager.delta * (float) Math.cos(Utility.degToRad(deg));
		
		if((xway <= -1.05f+(-1.0f*(1.0f/StaticManager.ASPECT_RATIO)) || xway >= 1.05f+(1.0f*(1.0f/StaticManager.ASPECT_RATIO))) && !this.turnLocked)
		{
			xway *= -1;
			//this.targetDeg = this.deg+(float) Math.tanh(yway / xway)+180.0f;
			//this.turnLocked = true;
		}
		else if((yway <= -1.0f || yway >= 1.0f) && !this.turnLocked)
		{
			yway *= -1;
			//this.targetDeg = this.deg+(float) Math.tanh(yway / xway)+180.0f;
			//this.turnLocked = true;
		}
		
		if(this.deg == this.targetDeg && !((xway <= -1.05f+(-1.0f*(1.0f/StaticManager.ASPECT_RATIO)) || xway >= 1.05f+(1.0f*(1.0f/StaticManager.ASPECT_RATIO)))||(yway <= -1.0f || yway >= 1.0f)))
			this.turnLocked = false;
		
		if(this.deg < this.targetDeg)
		{
			this.deg += this.turnRate * StaticManager.delta;
			if(this.deg > this.targetDeg)
			{
				this.deg = this.targetDeg;
				this.turnLocked = false;
			}
		}
		else if(this.deg > this.targetDeg)
		{
			this.deg -= this.turnRate * StaticManager.delta;
			if(this.deg < this.targetDeg)
			{
				this.deg = this.targetDeg;
				this.turnLocked = false;
			}
		}
		//System.out.println(deg+"/"+targetDeg+"/"+turnLocked+"/"+!((xway <= -1.05f+(-1.0f*(1.0f/StaticManager.ASPECT_RATIO)) || xway >= 1.05f+(1.0f*(1.0f/StaticManager.ASPECT_RATIO)))||(yway <= -1.0f || yway >= 1.0f)));
		
		this.deg = deg % 360.0f;
		
		this.rotationMatrix = new Matrix4x4((float) Math.cos(Utility.degToRad(deg)), (float) Math.sin(Utility.degToRad(deg))*-1.0f, 0.0f, 0.0f,
				(float) Math.sin(Utility.degToRad(deg)), (float) Math.cos(Utility.degToRad(deg)), 0.0f, 0.0f,
				0.0f, 0.0f, 1.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 1.0f);

		this.translationMatrix = new Matrix4x4(1.0f, 0.0f, 0.0f, 0.0f,
				   0.0f, 1.0f, 0.0f, 0.0f,
				   0.0f, 0.0f, 1.0f, 0.0f,
				   xway, yway, 0.0f, 1.0f);
		if(this.collisionLocked != 0)
			this.collisionLocked--;
		
		if(this.collisionLocked == 0)
		{
			for(int i = 0; i < StaticManager.objects.size(); i++)
			{
				float radSum = this.hitCircleRadius + StaticManager.objects.get(i).getHitCircleRadius();
				if(StaticManager.objects.get(i).getClass() != RiBracu.class)
				{
					float disx = this.xway - StaticManager.objects.get(i).getX();
					float disy = this.yway - StaticManager.objects.get(i).getY();
					float dis = (float) Math.sqrt((disx*disx)+(disy*disy));
					if(dis <= radSum && dis != 0.0f)
					{					
						
						//this.damage(10.0f);
						//StaticManager.objects.get(i).damage(0.25f);
						this.collisionLocked = 60;
						
					}
				}
			}
		}
		
		
		
		this.shieldLevel += this.shieldChargeRate*StaticManager.delta;
		if(this.shieldLevel > 1.0f)
			this.shieldLevel = 1.0f;
		
		this.engineLeft.doTick(deg, xway, yway);
		this.engineRight.doTick(deg, xway, yway);
		
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
			
			FloatBuffer dsmat = BufferUtils.createFloatBuffer(16);
			dsmat.put(((Matrix4x4) this.deathScaleMatrix).toArray());
			dsmat.flip();
			
			GL20.glUniformMatrix4(this.dSML, false, dsmat);
			
			
			FloatBuffer tmat = BufferUtils.createFloatBuffer(16);
			tmat.put(((Matrix4x4) this.translationMatrix).toArray());
			tmat.flip();
			
			GL20.glUniform2f( GL20.glGetUniformLocation(this.shaderID, "seed"), new Random().nextInt(1338), new Random().nextInt(1338));	
			
			GL20.glUniformMatrix4(this.tML, false, tmat);
			if(selection && !this.dead)
				GL20.glUniform1i(GL20.glGetUniformLocation(this.shaderID, "selection"), 1);
			else if(this.dead)
				GL20.glUniform1i(GL20.glGetUniformLocation(this.shaderID, "selection"), 2);
			else
				GL20.glUniform1i(GL20.glGetUniformLocation(this.shaderID, "selection"), 0);
				
			GL20.glUniform1f(this.shieldLevelLocation, this.shieldLevel);
			GL20.glUniform1f(this.deathTime, this.deathRemove);
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
			
			if(!selection && !this.dead)
			{
				this.engineLeft.draw(false);
				this.engineRight.draw(false);
			}
		}
		catch(Exception e){}
	}

	@Override
	public float getHitCircleRadius() {
		// TODO Auto-generated method stub
		if(!this.dead)
			return this.hitCircleRadius;
		else
			return 0.0f;
	}
	
	public void damage(float val)
	{
		this.shieldLevel -= val;
	}

}
