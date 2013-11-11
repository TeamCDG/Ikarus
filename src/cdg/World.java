package cdg;

import java.util.ArrayList;
import java.util.List;

import cdg.nut.gui.Component;
import cdg.nut.util.game.Entity2D;

public class World {
	
	private List<Entity2D> objects;
	
	public World()
	{
		this.objects = new ArrayList<Entity2D>();
	}
	
	public void drawObjects()
	{
		for(int i = 0; i < this.objects.size(); i++)
		{
			this.objects.get(i).draw();
		}
	}	
	
	public void drawObjectSelection()
	{
		for(int i = 0; i < this.objects.size(); i++)
		{
			this.objects.get(i).draw(true);
		}
	}
	
	public void addObject(Entity2D e)
	{
		this.objects.add(e);
	}

	public Entity2D getObjectById(int id)
	{
		for(int i = 0; i < this.objects.size(); i++)
		{
			if(this.objects.get(i).getId() == id)
			{
				return this.objects.get(i);
			}
		}
		return null;
	}

	public List<Entity2D> getObjects() {
		return this.objects;
	}
}
