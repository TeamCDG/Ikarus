package cdg;
import java.util.Random;

import org.lwjgl.opengl.GL13;

import cdg.nut.util.MatrixTypes;
import cdg.nut.util.ShaderProgram;
import cdg.nut.util.Utility;
import cdg.nut.util.game.Entity2D;


public class Roid extends Entity2D {
	
	public static ShaderProgram SHADER;
	public static int TEXTURE_ID;
	public static int TEXTURE_ID_2;
	
	public static int[] FORM_TEXTURE_IDS;
	public static int[] SURFACE_TEXTURE_IDS;
	
	private float turnRate;
	private Random r;
	
	public Roid(long id, float x, float y, float width, float height) {
		
		
		super(id, x, y, width, height, Roid.SURFACE_TEXTURE_IDS[new Random().nextInt(Roid.SURFACE_TEXTURE_IDS.length)], Roid.SHADER);		
		r = new Random();
		this.turnRate = (r.nextInt(20)+5) * 0.001f;
		this.setMapTexID(Roid.FORM_TEXTURE_IDS[r.nextInt(Roid.FORM_TEXTURE_IDS.length)]);
	}
	
	@Override
	protected void passShaderVariables() {
		this.getShader().pass1i("formTexture", 1);
		this.getShader().passMatrix(StaticManager.WINDOW_MATRIX, MatrixTypes.WINDOW);
		this.getShader().passMatrix(StaticManager.CAMERA_MATRIX, MatrixTypes.CAMERA);
	}

	@Override
	public void doTick() 
	{
		this.addRotation(this.turnRate * StaticManager.delta);
	}

	@Override
	public void drawChilds() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reloadShader() {
		// TODO Auto-generated method stub
		
	}

}
