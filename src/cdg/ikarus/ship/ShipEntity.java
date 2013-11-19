package cdg.ikarus.ship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.lwjgl.opengl.GL13;

import cdg.StaticManager;
import cdg.World;
import cdg.ikarus.objects.Exhaust;
import cdg.ikarus.objects.Turret;
import cdg.ikarus.ship.components.Engine;
import cdg.interfaces.IVertex;
import cdg.nut.util.GLTexture;
import cdg.nut.util.Globals;
import cdg.nut.util.MatrixTypes;
import cdg.nut.util.ShaderProgram;
import cdg.nut.util.Utility;
import cdg.nut.util.Vertex2;
import cdg.nut.util.game.Entity2D;
import cdg.nut.util.game.Player;

public abstract class ShipEntity extends Entity2D {

	private Engine engine;
	private Exhaust[] exhaust;
	private Vertex2 targetPos;
	private int command = -1;
	private Player owner;
	private float shieldLevel = 1.0f;
	private float hullPoints;
	
	private String className;
	
	private GLTexture stripes;
	private GLTexture logo;
	private GLTexture shield;
	private GLTexture base;
	private GLTexture decoration;
	
	private ArrayList<cdg.ikarus.objects.Turret> turrets;
	private float[] shieldColor = new float[]{0.4f, 1.0f, 0.4f, 1.0f};
	private float targetDeg;
	private float turnRate = 0.048f;
	private World w = null;
	private Entity2D target;
	private float shieldCapacity = 400.0f;
	private static final float MAX_SHIELD_CAPACITY = 400.0f;
	private int dir = -1;
	private float startDir = -1.0f;
	private float startRot = -1.0f;
	private Vertex2 startPos;
	private int minTicks = 20;
	private float rChange = 0.0f;
		
	
	public ShipEntity(long id, float x, float y, float width, float height,
			ShaderProgram shader) {
		super(id, x, y, width, height, shader);
		// TODO Auto-generated constructor stub
	}
	
	public ShipEntity(long id)
	{
		super(id);
		this.setSelectable(true);
	}
	
	public void load(float x, float y, HashMap<String, String> values, Player p)
	{		
		this.className = values.get("name");
		this.setWidth(Float.parseFloat(values.get("width")));
		this.setHeight(Float.parseFloat(values.get("height")));
		this.hullPoints = Float.parseFloat(values.get("hull"));
		this.owner = p;
		this.setX(x);
		this.setY(y);
		this.turrets = new ArrayList<Turret>();
		
		String[] engineStrings = values.get("engine").split("/");
		this.exhaust = new Exhaust[engineStrings.length];
		for(int i = 0; i < engineStrings.length; i++)
		{
			String[] en = engineStrings[i].split(";");
			
			this.exhaust[i] = new Exhaust(Float.parseFloat(en[0]),
												Float.parseFloat(en[1]), 
												Float.parseFloat(en[2]), //TODO implement maxSpeedScaling
												0.18f, this);
		}
		
		
		if(values.get("turret") != null)
		{
			String[] turretStrings = values.get("turret").split("/");
			for(int i = 0; i < turretStrings.length; i++)
			{
				String[] en = turretStrings[i].split(";");
				float xp = Float.parseFloat(en[0]);
				float yp = Float.parseFloat(en[1]);
				this.turrets.add(new Turret(this, xp, yp));
			}
		}
		
		this.setCollisionDetectionType(Integer.parseInt(values.get("ctype")));
		if(this.getCollisionDetectionType() == Entity2D.CDT_CIRCLE)
			this.setCollisionRadius(Float.parseFloat(values.get("cradius")));

		this.initialize();
	}
	
	public ShipEntity(long id, float x, float y, String infoPath, Player p) 
	{		
		super(id);

		HashMap<String, String> values = Utility.loadInfoTxt(infoPath);
		this.className = values.get("name");
		this.setWidth(Float.parseFloat(values.get("width")));
		this.setHeight(Float.parseFloat(values.get("height")));
		this.hullPoints = Float.parseFloat(values.get("hull"));
		this.base = new GLTexture(infoPath.replace("ship.txt", "")+values.get("base"), GL13.GL_TEXTURE0, true);
		this.shield = new GLTexture(infoPath.replace("ship.txt", "")+values.get("shield"), GL13.GL_TEXTURE1, true);
		this.stripes = new GLTexture(infoPath.replace("ship.txt", "")+values.get("team"), GL13.GL_TEXTURE2, true);
		this.setShader(new ShaderProgram(infoPath.replace("ship.txt", "")+values.get("vshader"), 
										 infoPath.replace("ship.txt", "")+values.get("fshader")));
		this.setX(x);
		this.setY(y);
		//this.logo = p.getLogo(this.className);
		String[] engineStrings = values.get("engine").split("/");
		for(int i = 0; i < engineStrings.length; i++)
		{
			String[] en = engineStrings[i].split(";");
			/*
			this.exhaust[i] = new EngineExhaust(Float.parseFloat(en[0]),
												Float.parseFloat(en[1]), 
												0.1f, //TODO implement maxSpeedScaling
												Float.parseFloat(en[2]));*/
		}
		this.initialize();
	}

	@Override
	public void damage(float amount) {
		this.shieldCapacity -= amount;
		
		if(this.shieldCapacity < 0)
		{
			this.shieldCapacity = 0;			
		}
		
		this.shieldLevel = (1.0f/ShipEntity.MAX_SHIELD_CAPACITY)*this.shieldCapacity;
	}

	@Override
	public void drawChilds() {
		for(int i = 0; i < this.exhaust.length; i++)
		{
			this.exhaust[i].draw();
		}
		for(int i = 0; i < this.turrets.size(); i++)
		{
			this.turrets.get(i).draw();
		}
		
	}

	@Override
	public void doTick() {
		for(int i = 0; i < this.turrets.size(); i++)
		{
			this.turrets.get(i).doTick();
		}
		
		if(this.command == Command.MOVE)
		{
			
			
			if(this.targetPos != null)
			{
				this.move(0.0004f * StaticManager.delta * (float) Math.sin(Utility.degToRad(this.getRotation())), 
						  0.0004f * StaticManager.delta * (float) Math.cos(Utility.degToRad(this.getRotation())));
				float dx = targetPos.getX() - this.getX();
				float dy = targetPos.getY() - this.getY();
				
				//if(Math.sqrt(dx*dx+dy*dy) <= )
				
				this.targetDeg = (float)(Math.atan2(dx, dy) * (180/Math.PI));
				
				if(this.targetDeg < 0)
				{
					this.targetDeg = 360.0f + this.targetDeg;
				}
				
				if(this.dir == -1)
				{
					this.startDir = this.targetDeg;
					this.startRot = this.getRotation();
					this.startPos = new Vertex2(this.getX(),this.getY());
				}
				
				/*
				this.minTicks--;
				
				if(this.minTicks <= 0)
				{
					float xdif = this.getX() - this.startPos.getX();
					float ydif = this.getY() - this.startPos.getY();
					
					if(Math.sqrt(xdif*xdif + ydif*ydif) <= 0.01f)
					{
						if(this.dir == 0)
						{
							this.dir = 1;
						}
						else if(this.dir == 1)
						{
							this.dir = 0;
						}
						this.minTicks = 20;
					}
				}
				*/
				
				//if((Math.abs(this.targetDeg - this.startDir) <= 0.2f || Math.abs(this.targetDeg - this.startDir) >= 359.8f ) && 
				//   (Math.abs(this.getRotation() - this.startRot) <= 0.2f || Math.abs(this.getRotation() - this.startRot) >= 359.8f) && this.dir != -1)
				//{
				//	if(this.dir == 0)
				//	{
				//		this.dir = 1;
				//	}
				//	else if(this.dir == 1)
				//	{
				//		this.dir = 0;
				//	}
				//}
				
				if(this.dir != -1 && this.rChange >= 360)
				{
					if(this.dir == 0)
					{
						this.dir = 1;
					}
					else if(this.dir == 1)
					{
						this.dir = 0;
					}
					

					System.out.println("DIR CHANGE!!!! @"+this.rChange);
					this.rChange = 0;
				}
				
				if(this.getRotation() < this.targetDeg && this.dir == -1)
				{
					this.dir = 0;
				}
				else if(this.dir == -1)
				{
					this.dir = 1;
				}
				
			
				
				if(Math.sqrt((dx*dx) + (dy*dy)) <= 0.05f)
				{
					this.setX(targetPos.getX());
					this.setY(targetPos.getY());
					this.targetDeg = this.getRotation();
					this.targetPos = null;
					this.dir = -1;
					this.startPos = null;
					this.minTicks = 20;
					this.rChange = 0;
				}
			}
			
			if(this.dir == 0)
			{
				this.addRotation(this.turnRate * StaticManager.delta);
				//System.out.println(Math.round(this.getRotation()*100)/100 +"/"+Math.round(this.targetDeg*100)/100+"/"+Math.abs(this.getRotation() - this.startRot)+"/"+Math.abs(this.targetDeg - this.startDir)+"/"+Math.abs(this.getRotation() - this.targetDeg)+"/"+this.rChange);
			}
			else if(this.dir == 1)
			{
				this.addRotation(-1.0f * this.turnRate * StaticManager.delta);
				//System.out.println(Math.round(this.getRotation()*100)/100 +"/"+Math.round(this.targetDeg*100)/100+"/"+Math.abs(this.getRotation() - this.startRot)+"/"+Math.abs(this.targetDeg - this.startDir)+"/"+Math.abs(this.getRotation() - this.targetDeg)+"/"+this.rChange);
			}
			
			if(Math.abs(this.getRotation() - this.targetDeg) <= 1.0f)
			{
				this.setRotation(this.targetDeg);
				this.rChange = 0;
				
				if(this.dir != -1)
					this.dir = 3;
			}
			else
			{
				this.rChange += this.turnRate * StaticManager.delta;
			}
		}
	}
	
	@Override
	public void bindTextures(){
		this.base.bind();
		this.shield.bind();
		this.stripes.bind();
		this.decoration.bind();
	}
	
	@Override
	public void passShaderVariables()
	{
		this.getShader().pass2f("seed", new Random().nextFloat(), new Random().nextFloat());
		this.getShader().pass1i("ship", 0);
		this.getShader().pass1i("shield", 1);
		this.getShader().pass1i("team_stripes", 2);
		this.getShader().pass1i("decoration", 3);
		this.getShader().pass4f("team_color",this.owner.getColor());
		this.getShader().pass1f("shield_level", this.shieldLevel);
		this.getShader().pass4f("shield_color", this.shieldColor);
		this.getShader().passMatrix(Globals.getWindowMatrix(), MatrixTypes.WINDOW);
		this.getShader().passMatrix(this.getTranslationMatrix(), MatrixTypes.TRANSLATION);
		this.getShader().passMatrix(this.getRotationMatrix(), MatrixTypes.ROTATION);
		this.getShader().passMatrix(this.getScalingMatrix(), MatrixTypes.SCALING);
	}

	public Engine getEngine() {
		return engine;
	}

	public void setEngine(Engine engine) {
		this.engine = engine;
	}

	public Exhaust[] getExhaust() {
		return exhaust;
	}

	public void setExhaust(Exhaust[] exhaust) {
		this.exhaust = exhaust;
	}

	public Vertex2 getTargetPos() {
		return targetPos;
	}

	public void setTargetPos(Vertex2 targetPos) {
		this.targetPos = new Vertex2(targetPos.getX()*Globals.getAspectRatio(), targetPos.getY());
		this.dir = -1;
		this.rChange = 0;
	}

	public int getCommand() {
		return command;
	}

	public void setCommand(int command) {
		this.command = command;
	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public float getShieldLevel() {
		return shieldLevel;
	}

	public void setShieldLevel(float shieldLevel) {
		this.shieldLevel = shieldLevel;
	}

	public float getHullPoints() {
		return hullPoints;
	}

	public void setHullPoints(float hullPoints) {
		this.hullPoints = hullPoints;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public GLTexture getStripes() {
		return stripes;
	}

	public void setStripes(GLTexture stripes) {
		this.stripes = stripes;
	}

	public GLTexture getLogo() {
		return logo;
	}

	public void setLogo(GLTexture logo) {
		this.logo = logo;
	}

	public GLTexture getShield() {
		return shield;
	}

	public void setShield(GLTexture shield) {
		this.shield = shield;
	}

	public GLTexture getBase() {
		return base;
	}

	public void setBase(GLTexture base) {
		this.base = base;
	}

	public ArrayList<Turret> getTurrets() {
		return turrets;
	}

	public void setTurrets(ArrayList<Turret> turrets) {
		this.turrets = turrets;
	}

	/**
	 * @return the decoration
	 */
	public GLTexture getDecoration() {
		return decoration;
	}

	/**
	 * @param decoration the decoration to set
	 */
	public void setDecoration(GLTexture decoration) {
		this.decoration = decoration;
	}
	
	public float[] getShieldColor()
	{
		return this.shieldColor;
	}

	public void setShieldColor(float[] fs) {
		this.shieldColor = fs;
		
	}

	public Vertex2 getTurretPos(int turretId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setTarget(Entity2D t)
	{
		this.target = t;
	}
	
	public Entity2D getTarget()
	{
		return this.target;
	}

}
