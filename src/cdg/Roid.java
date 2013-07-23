package cdg;
import java.util.Random;

import org.lwjgl.opengl.GL13;

import cdg.util.MatrixTypes;
import cdg.util.ShaderProgram;
import cdg.util.StaticManager;
import cdg.util.Utility;


public class Roid extends Entity2D {

	public static ShaderProgram ROID_SHADER;
	public static int ROID_TEXTURE_ID;
	
	private float turnRate;
	private Random r;
	
	public Roid(long id, float x, float y, float width, float height) {
		
		super(id, x, y, width, height, Roid.ROID_TEXTURE_ID, Roid.ROID_SHADER);
		r = new Random();
		this.turnRate = (r.nextInt(20)+5) * 0.001f;
	}
	
	@Override
	protected void passShaderVariables() {
		this.getShader().passMatrix(StaticManager.WINDOW_MATRIX, MatrixTypes.WINDOW);

	}

	@Override
	public void damage(float amount) {
		// TODO Auto-generated method stub

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

}
