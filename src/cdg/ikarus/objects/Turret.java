package cdg.ikarus.objects;

import java.util.HashMap;

import org.lwjgl.opengl.GL13;

import cdg.ikarus.ship.ShipEntity;
import cdg.nut.util.GLTexture;
import cdg.nut.util.ShaderProgram;
import cdg.nut.util.Utility;
import cdg.nut.util.Vertex2;
import cdg.nut.util.game.Entity2D;

public class Turret extends EntityObject {

	public static GLTexture BASE;
	public static ShaderProgram SHADER;
	public static final String SHIPINFO = "res/objects/turret/object.txt";
	private ShipEntity parent;
	private int turretId;
	private Entity2D target;
	private float xpos;
	private float ypos;
	
	public Turret(ShipEntity parent, float x, float y) {
		super(0);
		HashMap<String, String> values = Utility.loadInfoTxt(Turret.SHIPINFO);
		if(Turret.SHADER == null)
		{
			Turret.SHADER = new ShaderProgram(Turret.SHIPINFO.replace("object.txt", "")+values.get("vshader"), 
											   Turret.SHIPINFO.replace("object.txt", "")+values.get("fshader"));
		}
		if(Turret.BASE == null)
		{
			Turret.BASE = new GLTexture(Turret.SHIPINFO.replace("object.txt", "")+values.get("base"), GL13.GL_TEXTURE0, false);
		}
		
		this.parent = parent;
		this.xpos = x;
		this.ypos = y;
		
		System.out.println(BASE.getTextureId());
		this.setTextureId(Turret.BASE.getTextureId());
		this.setShader(Turret.SHADER);
		this.setWidth(Float.parseFloat(values.get("width")));
		this.setHeight(Float.parseFloat(values.get("height")));
		Vertex2 pos = parent.getRotationMatrix().multiply(parent.getTranslationMatrix().multiply(new Vertex2(this.xpos, this.ypos))).toVertex2();
		this.setX(pos.getX());
		this.setY(pos.getY());
		this.initialize();
	}

	@Override
	public void reloadShader() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doTick() {
		Vertex2 pos = parent.getRotationMatrix().multiply(parent.getTranslationMatrix().multiply(new Vertex2(this.xpos, this.ypos))).toVertex2();
		this.setX(pos.getX());
		this.setY(pos.getY());
		this.addRotation(0.5f);
	}

	public void setTarget(Entity2D target)
	{
		
	}
}
