package cdg.ikarus.objects;

import java.util.HashMap;
import java.util.List;

import org.lwjgl.opengl.GL13;

import cdg.StaticManager;
import cdg.ikarus.ship.ShipEntity;
import cdg.nut.util.GLTexture;
import cdg.nut.util.ShaderProgram;
import cdg.nut.util.Utility;
import cdg.nut.util.Vertex2;
import cdg.nut.util.game.Entity2D;

public class Laser extends EntityObject {

	public static GLTexture BASE;
	public static ShaderProgram SHADER;
	public static final String SHIPINFO = "res/objects/laser/object.txt";
	private ShipEntity parent;
	
	public Laser(ShipEntity parent, float x, float y) {
		super(0);
		HashMap<String, String> values = Utility.loadInfoTxt(Laser.SHIPINFO);
		if(Laser.SHADER == null)
		{
			Laser.SHADER = new ShaderProgram(Laser.SHIPINFO.replace("object.txt", "")+values.get("vshader"), 
											   Laser.SHIPINFO.replace("object.txt", "")+values.get("fshader"));
		}
		if(Laser.BASE == null)
		{
			Laser.BASE = new GLTexture(Laser.SHIPINFO.replace("object.txt", "")+values.get("base"), GL13.GL_TEXTURE0, false);
		}
		
		this.parent = parent;
		
		this.setTextureId(Laser.BASE.getTextureId());
		this.setShader(Laser.SHADER);
		this.load(x, y, values);
	}

	@Override
	public void reloadShader() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doTick() {
		this.move(0.0012f * StaticManager.delta * (float) Math.sin(Utility.degToRad(this.getRotation())), 
				  0.0012f * StaticManager.delta * (float) Math.cos(Utility.degToRad(this.getRotation())));
		
		List<Entity2D> e = this.getWorld().getObjects();
		for(int i = 0; i < e.size(); i++)
		{
			if(e.get(i) != this.parent && e.get(i).getClass() != Laser.class && e.get(i).collide(this))
			{
				e.get(i).damage(20);
				this.getWorld().removeObject(this);
			}
		}
	}

}
