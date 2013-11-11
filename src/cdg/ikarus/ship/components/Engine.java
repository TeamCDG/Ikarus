package cdg.ikarus.ship.components;

import cdg.ikarus.ship.ShipEntity;
import cdg.nut.util.GLTexture;

public abstract class Engine {

	private ShipEntity parent;
	
	private float turnRate = 0.12f;
	private float moveRate = 0.0008f;
	
	public Engine(ShipEntity parent, float turnRate, float moveRate) 
	{
		this.setParent(parent);
		this.turnRate = turnRate;
		this.moveRate = moveRate;
	}

	public float getTurnRate() {
		return turnRate;
	}

	public void setTurnRate(float turnRate) {
		this.turnRate = turnRate;
	}
	
	public float getMoveRate() {
		return moveRate;
	}
	
	public void setMoveRate(float moveRate) {
		this.moveRate = moveRate;
	}

	public ShipEntity getParent() {
		return parent;
	}

	public void setParent(ShipEntity parent) {
		this.parent = parent;
	}

}
