package cdg.interfaces;

public interface IEntity {
	
	public float getX();
	
	public void setX(float x);
	
	public float getY();
	
	public void setY(float y);
	
	public float getWidth();
	
	public void setWidth(float width);
	
	public float getHeight();
	
	public void setHeight(float height);
	
	public int getId();
	
	public int getTexture();
	
	public void setTexture(int texID);
	
	public void tick();
	
	public void draw(boolean selection);
	
	public float getHitCircleRadius();

	public void damage(float f);
}
