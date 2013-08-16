package cdg;

import cdg.util.MatrixTypes;
import cdg.util.ShaderProgram;
import cdg.util.StaticManager;
import cdg.util.Utility;
import cdg.util.VertexData;

public class Laser extends Entity2D {
	
	public static ShaderProgram SHADER;
	public static int TEXTURE_ID;
	
	private float moveRate = 0.002f;
	private float forceDepletionRate = 0.0006f;
	
	public Laser(Entity2D shooter, long id, float px, float py, float x, float y, float width, float height,
			float direction) 
	
	{
		super(id, x, y, width, height, Laser.TEXTURE_ID, Laser.SHADER, 
				new VertexData[] {new VertexData(new float[]{px-(width/2.0f),py+(height/2.0f),0.0f,1.0f}, new float[]{1.0f,1.0f,1.0f,1.0f}, new float[]{1.0f, 0.0f}),
				   				  new VertexData(new float[]{px-(width/2.0f),py-(height/2.0f),0.0f,1.0f}, new float[]{1.0f,1.0f,1.0f,1.0f}, new float[]{1.0f, 1.0f}),
				   				  new VertexData(new float[]{px+(width/2.0f),py-(height/2.0f),0.0f,1.0f}, new float[]{1.0f,1.0f,1.0f,1.0f}, new float[]{0.0f, 1.0f}),
				   				  new VertexData(new float[]{px+(width/2.0f),py+(height/2.0f),0.0f,1.0f}, new float[]{1.0f,1.0f,1.0f,1.0f}, new float[]{0.0f, 0.0f})}, 
			new byte[]{0,1,2,2,3,0});
		this.setRotation(direction);
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
	public void drawChilds() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doTick() {
		this.move(this.moveRate * StaticManager.delta * (float) Math.sin(Utility.degToRad(this.getRotation())), 
				  this.moveRate * StaticManager.delta * (float) Math.cos(Utility.degToRad(this.getRotation())));

	}

}
