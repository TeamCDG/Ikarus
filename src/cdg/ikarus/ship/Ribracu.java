package cdg.ikarus.ship;

import java.util.HashMap;

import org.lwjgl.opengl.GL13;

import cdg.nut.util.GLTexture;
import cdg.nut.util.ShaderProgram;
import cdg.nut.util.Utility;
import cdg.nut.util.game.Player;

public class Ribracu extends ShipEntity{

	public static ShaderProgram SHADER;
	public static float WIDTH;
	public static float HEIGHT;
	public static float[][] ENGINE_POS;
	public static GLTexture BASE;
	public static GLTexture SHIELD;
	public static GLTexture STRIPES;
	public static GLTexture DECORATION;
	public static final String SHIPINFO = "res/ships/ribracu/ship.txt";
	
	public Ribracu(long id, float x, float y, Player p) 
	{
		super(id);
		HashMap<String, String> values = Utility.loadInfoTxt(Ribracu.SHIPINFO);
		
		if(Ribracu.SHADER == null)
		{
			Ribracu.SHADER = new ShaderProgram(Ribracu.SHIPINFO.replace("ship.txt", "")+values.get("vshader"), 
											   Ribracu.SHIPINFO.replace("ship.txt", "")+values.get("fshader"));
		}
		if(Ribracu.BASE == null)
		{
			Ribracu.BASE = new GLTexture(Ribracu.SHIPINFO.replace("ship.txt", "")+values.get("base"), GL13.GL_TEXTURE0, false);
		}
		if(Ribracu.SHIELD == null)
		{
			Ribracu.SHIELD = new GLTexture(Ribracu.SHIPINFO.replace("ship.txt", "")+values.get("shield"), GL13.GL_TEXTURE1, false);
		}
		if(Ribracu.STRIPES == null)
		{
			Ribracu.STRIPES = new GLTexture(Ribracu.SHIPINFO.replace("ship.txt", "")+values.get("team"), GL13.GL_TEXTURE2, false);
		}
		if(Ribracu.DECORATION == null)
		{
			Ribracu.DECORATION = new GLTexture(Ribracu.SHIPINFO.replace("ship.txt", "")+values.get("decoration"), GL13.GL_TEXTURE3, false);
		}
		this.setBase(Ribracu.BASE);
		this.setShield(Ribracu.SHIELD);
		this.setStripes(Ribracu.STRIPES);
		this.setDecoration(Ribracu.DECORATION);
		this.setShader(Ribracu.SHADER);
		
		
		this.load(id, x, y, values, p);
	}
	
	@Override
	public void reloadShader()
	{
		this.getShader().finalize();
		this.setShader(null);
		HashMap<String, String> values = Utility.loadInfoTxt(Ribracu.SHIPINFO);
		ShaderProgram p = new ShaderProgram(Ribracu.SHIPINFO.replace("ship.txt", "")+values.get("vshader"), 
				   							Ribracu.SHIPINFO.replace("ship.txt", "")+values.get("fshader"));
		this.setShader(p);
		
		Ribracu.SHADER = null;
		Ribracu.SHADER = p;
	}

}
