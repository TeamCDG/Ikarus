package cdg.ikarus.ship;

import java.util.HashMap;

import org.lwjgl.opengl.GL13;

import cdg.nut.util.GLTexture;
import cdg.nut.util.ShaderProgram;
import cdg.nut.util.Utility;
import cdg.nut.util.game.Player;

public class Vashelig extends ShipEntity {

	
	public static ShaderProgram SHADER;
	public static float WIDTH;
	public static float HEIGHT;
	public static float[][] ENGINE_POS;
	public static GLTexture BASE;
	public static GLTexture SHIELD;
	public static GLTexture STRIPES;
	public static GLTexture DECORATION;
	public static final String SHIPINFO = "res/ships/vashelig/ship.txt";
	public static int rCd = 0;;
	
	public Vashelig(long id, float x, float y, Player p) 
	{
		super(id);
		HashMap<String, String> values = Utility.loadInfoTxt(Vashelig.SHIPINFO);
		
		if(Vashelig.SHADER == null)
		{
			Vashelig.SHADER = new ShaderProgram(Vashelig.SHIPINFO.replace("ship.txt", "")+values.get("vshader"), 
												Vashelig.SHIPINFO.replace("ship.txt", "")+values.get("fshader"));
		}
		if(Vashelig.BASE == null)
		{
			Vashelig.BASE = new GLTexture(Vashelig.SHIPINFO.replace("ship.txt", "")+values.get("base"), GL13.GL_TEXTURE0, false);
		}
		if(Vashelig.SHIELD == null)
		{
			Vashelig.SHIELD = new GLTexture(Vashelig.SHIPINFO.replace("ship.txt", "")+values.get("shield"), GL13.GL_TEXTURE1, false);
		}
		if(Vashelig.STRIPES == null)
		{
			Vashelig.STRIPES = new GLTexture(Vashelig.SHIPINFO.replace("ship.txt", "")+values.get("team"), GL13.GL_TEXTURE2, false);
		}
		if(Vashelig.DECORATION == null)
		{
			Vashelig.DECORATION = new GLTexture(Vashelig.SHIPINFO.replace("ship.txt", "")+values.get("decoration"), GL13.GL_TEXTURE3, false);
		}
		this.setBase(Vashelig.BASE);
		this.setShield(Vashelig.SHIELD);
		this.setStripes(Vashelig.STRIPES);
		this.setDecoration(Vashelig.DECORATION);
		this.setShader(Vashelig.SHADER);
		
		this.load(id, x, y, values, p);
	}
	
	@Override
	public void reloadShader()
	{
		this.getShader().finalize();
		this.setShader(null);
		HashMap<String, String> values = Utility.loadInfoTxt(Vashelig.SHIPINFO);
		ShaderProgram p = new ShaderProgram(Vashelig.SHIPINFO.replace("ship.txt", "")+values.get("vshader"), 
											Vashelig.SHIPINFO.replace("ship.txt", "")+values.get("fshader"));
		this.setShader(p);
		
		Vashelig.SHADER = null;
		Vashelig.SHADER = p;
	}
}
