package cdg.ikarus.objects;

import cdg.nut.util.Globals;
import cdg.nut.util.MatrixTypes;
import cdg.nut.util.game.Entity2D;

public abstract class EntityObject extends Entity2D{

	public EntityObject(long id) {
		super(id);

	}

	@Override
	protected void passShaderVariables() {
		this.getShader().passMatrix(Globals.getWindowMatrix(), MatrixTypes.WINDOW);
		
	}

	@Override
	public void drawChilds() {
	}

}
