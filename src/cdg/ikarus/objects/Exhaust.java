package cdg.ikarus.objects;

import java.util.HashMap;
import java.util.Random;

import org.lwjgl.opengl.GL13;

import cdg.ikarus.ship.Ribracu;
import cdg.nut.util.GLTexture;
import cdg.nut.util.Globals;
import cdg.nut.util.MatrixTypes;
import cdg.nut.util.ShaderProgram;
import cdg.nut.util.Utility;
import cdg.nut.util.game.Entity2D;

public class Exhaust extends EntityObject {

	public static GLTexture BASE;
	public static ShaderProgram SHADER;
	public static final String SHIPINFO = "res/objects/exhaust/object.txt";
	private float yPos;
	private float xPos;

	private Entity2D parent;
	
	public Exhaust(float x, float y, float width, float height, Entity2D parent) {
		super(0);
		HashMap<String, String> values = Utility.loadInfoTxt(Exhaust.SHIPINFO);
		if(Exhaust.SHADER == null)
		{
			Exhaust.SHADER = new ShaderProgram(Exhaust.SHIPINFO.replace("object.txt", "")+values.get("vshader"), 
											   Exhaust.SHIPINFO.replace("object.txt", "")+values.get("fshader"));
		}
		if(Exhaust.BASE == null)
		{
			Exhaust.BASE = new GLTexture(Exhaust.SHIPINFO.replace("object.txt", "")+values.get("base"), GL13.GL_TEXTURE0, false);
		}
		
		this.parent = parent;
		this.yPos = y;
		this.xPos = x;
		
		System.out.println(BASE.getTextureId());
		this.setTextureId(Exhaust.BASE.getTextureId());
		this.setShader(Exhaust.SHADER);
		this.setX(x);
		this.setY(y);
		this.setWidth(width);
		this.setHeight(height);
		this.initialize();
	}

	@Override
	public void reloadShader() {
		this.getShader().finalize();
		this.setShader(null);
		HashMap<String, String> values = Utility.loadInfoTxt(Exhaust.SHIPINFO);
		ShaderProgram p = new ShaderProgram(Exhaust.SHIPINFO.replace("ship.txt", "")+values.get("vshader"), 
											Exhaust.SHIPINFO.replace("ship.txt", "")+values.get("fshader"));
		this.setShader(p);
		
		Exhaust.SHADER = null;
		Exhaust.SHADER = p;
	}
	
	@Override
	protected void passShaderVariables() {
		this.setX(this.parent.getX()+this.xPos);
		this.setY(this.parent.getY()+this.yPos);
		this.getShader().passMatrix(Globals.getWindowMatrix(), MatrixTypes.WINDOW);
		this.getShader().passMatrix(this.getTranslationMatrix(), MatrixTypes.TRANSLATION);
		this.getShader().passMatrix(this.parent.getRotationMatrix(), MatrixTypes.ROTATION);
		this.getShader().passMatrix(this.parent.getScalingMatrix(), MatrixTypes.SCALING);
		this.getShader().pass2f("seed", new Random().nextInt(1338), new Random().nextInt(1338));
	}

	@Override
	public void doTick() {
		// TODO Auto-generated method stub

	}

}
