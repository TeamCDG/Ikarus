package cdg.ikarus.objects;

import java.util.ArrayList;
import java.util.HashMap;

import cdg.World;
import cdg.nut.util.Globals;
import cdg.nut.util.MatrixTypes;
import cdg.nut.util.game.Entity2D;
import cdg.nut.util.game.Player;

public abstract class EntityObject extends Entity2D{
	
	public EntityObject(long id) {
		super(id);

	}
	
	public void load(float x, float y, HashMap<String, String> values)
	{		
		this.setWidth(Float.parseFloat(values.get("width")));
		this.setHeight(Float.parseFloat(values.get("height")));
		this.setX(x);
		this.setY(y);
		this.setCollisionDetectionType(Integer.parseInt(values.get("ctype")));
		if(this.getCollisionDetectionType() == Entity2D.CDT_CIRCLE)
			this.setCollisionRadius(Float.parseFloat(values.get("cradius")));

		this.initialize();
	}

	@Override
	protected void passShaderVariables() {
		this.getShader().passMatrix(Globals.getWindowMatrix(), MatrixTypes.WINDOW);
		
	}

	@Override
	public void drawChilds() {
	}

}
