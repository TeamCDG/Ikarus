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
	
	public Roid(long id, float x, float y, float width, float height) {
		
		super(id, x, y, width, height, Roid.ROID_TEXTURE_ID, Roid.ROID_SHADER);
		
		this.turnRate = (new Random().nextInt(20)+5) * 0.001f;
		System.out.println(this.turnRate);
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
		this.setRotation(this.getRotation() + this.turnRate * StaticManager.delta);
		System.out.println(this.getRotation() + "/" + this.turnRate * StaticManager.delta);
	}

	@Override
	public void finalize() {
		// TODO Auto-generated method stub

	}

}
