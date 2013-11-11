package cdg.ikarus.ship;

import cdg.nut.util.GLTexture;
import cdg.nut.util.ShaderProgram;
import cdg.nut.util.game.Entity2D;

public class EngineExhaust extends Entity2D {
	
	public static ShaderProgram SHADER = null;
	public static GLTexture TEXTURE = null;

	public EngineExhaust(float x, float y, float width, float height) {
		super(0, x, y, width, height, EngineExhaust.TEXTURE.getTextureId(), EngineExhaust.SHADER);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void passShaderVariables() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void drawChilds() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doTick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reloadShader() {
		// TODO Auto-generated method stub
		
	}

}
