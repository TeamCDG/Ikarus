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
import cdg.util.MatrixTypes;
import cdg.util.ShaderProgram;
import cdg.util.StaticManager;
import cdg.util.Utility;
import cdg.util.Vertex2;
import cdg.util.VertexData;

public class RiBracu extends Entity2D {


	
	public static ShaderProgram SHADER;
	public static int TEXTURE_ID;
	
	private float targetDeg = -90;
	private float turnRate = 0.12f;
	private float moveRate = 0.0008f;
	private float shieldChargeRate = 0.000016f;
	private boolean turnLocked = false;
	private EngineExhaust engineLeft;
	private EngineExhaust engineRight;
	private float shieldLevel;
	
	float xway = 0.8f;
	float yway;
	
	private int collisionLocked;
	private boolean dead = false;
	private float deathRemove = 1000;
	private int deathTime;
	private Matrix4x4 deathScaleMatrix = Matrix4x4.getIdentity();
	private Random r = new Random();
	private Vertex2 target;
	private boolean moved = false;
	
	
	public RiBracu(long id, float x, float y) {
		super(id, x, y, 0.2f, 0.2f, RiBracu.TEXTURE_ID, RiBracu.SHADER);
		
		this.shieldLevel = 1.0f;
		this.engineLeft = new EngineExhaust(1,-0.04f, -0.08f);
		this.engineRight = new EngineExhaust(1,0.04f, -0.08f);
		
		this.setRotation(0.1f);
	}
	
	public void damage(float val)
	{
		this.shieldLevel -= val;
	}

	@Override
	protected void passShaderVariables() {
		this.getShader().passMatrix(StaticManager.WINDOW_MATRIX, MatrixTypes.WINDOW);
		this.getShader().passMatrix(this.deathScaleMatrix, "death_Scale_Matrix");
		this.getShader().pass2f("seed", r.nextInt(1338), r.nextInt(1338));
		this.getShader().pass1f("shield_Level", this.shieldLevel);
		this.getShader().pass1f("death_Time", this.deathTime);
	}

	@Override
	public void doTick() {
		
		
		this.moved = false;
		
		if(this.shieldLevel < 0)
		{
			this.dead = true;
		}
		else
		{
			this.dead = false;
		}
		
		if(this.dead)
		{
			this.deathRemove  -= StaticManager.delta;
			this.deathScaleMatrix.set((1.0f/1000.0f)*this.deathRemove, 0.0f, 0.0f, 0.0f,
												   0.0f, (1.0f/1000.0f)*this.deathRemove, 0.0f, 0.0f,
												   0.0f, 0.0f, 1.0f, 0.0f,
												   0.0f, 0.0f, 0.0f, 1.0f);
			if(this.deathRemove < 0)
			{
				StaticManager.objects.add(new RiBracu(this.getId(), 0.5f, new Random().nextFloat()*1.6f-0.8f));
				StaticManager.objects.remove(this);
			}
			return;
		}
			
		
		this.targetDeg = this.targetDeg % 360.0f;
		
		if(r.nextInt(20) == 15 && !this.turnLocked && this.target == null)
			this.targetDeg = r.nextInt(360);
		
		
		if(new Random().nextInt(500) == 15 && !this.turnLocked)
		{
			StaticManager.shoots.add(new Laser(this, 0, 0.085f, 0.12f, this.getX(), this.getY(), 0.02f, 0.06f, this.getRotation()));
			StaticManager.shoots.add(new Laser(this, 0, -0.09f, 0.12f, this.getX(), this.getY(), 0.02f, 0.06f, this.getRotation()));
		}
				
		if(this.target != null)
		{
			float dx = target.getX() - this.getX();
			float dy = target.getY() - this.getY();
			if(Math.sqrt((dx*dx) + (dy*dy)) <= 0.05f)
			{
				this.setX(target.getX());
				this.setY(target.getY());
			}
			this.targetDeg = (float)(Math.atan2(dx, dy) * (180/Math.PI));
		}
		
		if(this.target != null && (this.getX() == this.target.getX() && this.getY() == this.target.getY()))
		{
			
		}
		else 
		{
			this.move(this.moveRate * StaticManager.delta * (float) Math.sin(Utility.degToRad(this.getRotation())), 
					  this.moveRate * StaticManager.delta * (float) Math.cos(Utility.degToRad(this.getRotation())));
			this.moved = true;
		}
		
		if((this.getX() <= -1.05f+(-1.0f*(1.0f/StaticManager.ASPECT_RATIO)) || this.getX() >= 1.05f+(1.0f*(1.0f/StaticManager.ASPECT_RATIO))) && !this.turnLocked && this.target == null)
		{
			this.setX(this.getX() *-1.0f);
			//this.targetDeg = this.deg+(float) Math.tanh(yway / xway)+180.0f;
			//this.turnLocked = true;
		}
		else if((this.getY() <= -1.0f || this.getY() >= 1.0f) && !this.turnLocked && this.target == null)
		{
			this.setY(this.getY() *-1.0f);
			//this.targetDeg = this.deg+(float) Math.tanh(yway / xway)+180.0f;
			//this.turnLocked = true;
		}
		
		/*
		if(this.getRotation() == this.targetDeg && !((xway <= -1.05f+(-1.0f*(1.0f/StaticManager.ASPECT_RATIO)) || xway >= 1.05f+(1.0f*(1.0f/StaticManager.ASPECT_RATIO)))||(yway <= -1.0f || yway >= 1.0f)))
			this.turnLocked = false;
			*/
		
		if(this.getRotation() < this.targetDeg)
		{
			this.addRotation(this.turnRate * StaticManager.delta);
			if(this.getRotation() > this.targetDeg)
			{
				this.setRotation(this.targetDeg);
				this.turnLocked = false;
			}
		}
		else if(this.getRotation() > this.targetDeg)
		{
			this.addRotation(-1.0f * this.turnRate * StaticManager.delta);
			if(this.getRotation() < this.targetDeg)
			{
				this.setRotation(this.targetDeg);
				this.turnLocked = false;
			}
		}
		if(this.collisionLocked != 0)
			this.collisionLocked--;
		
		if(this.collisionLocked == 0)
		{
			for(int i = 0; i < StaticManager.objects.size(); i++)
			{
				/*
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
				*/
			}
		}
		
		
		
		this.shieldLevel += this.shieldChargeRate*StaticManager.delta;
		if(this.shieldLevel > 1.0f)
			this.shieldLevel = 1.0f;
		
		if(this.getTextureId() == RiBracu.TEXTURE_ID)
		{
			this.engineLeft.doTick(this.getRotation(), this.getX(), this.getY());
			this.engineRight.doTick(this.getRotation(), this.getX(), this.getY());
		}
		
		
		
	}
	
	public void setTarget(Vertex2 target)
	{
		this.target = target;
		float dx = target.getX() - this.getX();
		float dy = target.getY() - this.getY();
		
		this.targetDeg = this.getRotation();
	}
	
	public Vertex2 getTarget()
	{
		return this.target;
	}
	
	public boolean isAtTarget()
	{
		if(this.target != null)
		{
			boolean val = this.getX() == target.getX() && this.getY() == target.getY();
			
			return val;
		}
		else
			return false;
	}
	
	public void flyDeg(float deg)
	{
		this.target = null;
		this.targetDeg = deg;
		this.turnLocked = true;
	}

	@Override
	public void drawChilds() {
		if(this.getTextureId() == RiBracu.TEXTURE_ID && this.moved)
		{
			this.engineLeft.draw();
			this.engineRight.draw();
		}
		
	}

	


}
