package cdg;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cdg.ikarus.ship.Ribracu;
import cdg.ikarus.ship.ShipEntity;
import cdg.ikarus.ship.Vashelig;
import cdg.interfaces.IClickListener;
import cdg.nut.gui.Button;
import cdg.nut.gui.Component;
import cdg.nut.gui.Frame;
import cdg.nut.util.Globals;
import cdg.nut.util.Matrix4x4;
import cdg.nut.util.MouseButtons;
import cdg.nut.util.Utility;
import cdg.nut.util.game.Entity2D;
import cdg.nut.util.game.Player;

public class GameFrame extends Frame{

	private World w;
	private Player p;
	
	private ArrayList<Entity2D> selectedEntities;
	private ArrayList<Entity2D> focusedEntities;
	
	private boolean dF5 = false;
	private boolean dESC;
	
	private Component selectedComponent = null;
	
	public GameFrame(Matrix4x4 winMat) {
		super(winMat);
		this.selectedEntities = new ArrayList<Entity2D>();
		this.focusedEntities = new ArrayList<Entity2D>();
		this.setMaxSelectSkip(5);
		Button b = new Button(Globals.getAspectRatio()-0.6f, 1, Globals.getDefaultTextFont(), "Menu");
		b.setWidth(0.3f);
		b.addClickListener(new IClickListener(){

			@Override
			public void clicked(int x, int y, int button) {
				Globals.setActiveFrame("main");
				
			}});
		this.add(b);
		Button ex = new Button(Globals.getAspectRatio()-0.3f, 1f, Globals.getDefaultTextFont(), "Exit");
		ex.setWidth(0.3f);
		ex.addClickListener(new IClickListener(){

			@Override
			public void clicked(int x, int y, int button) {
				System.exit(-1);
				
			}});
		this.add(ex);
		p = new Player(new float[]{0.0f, 1.0f, 0.0f, 1.0f});
		w = new World();
		w.addObject(new Ribracu(this.getNextId(), -0.8f, 0, p));
		this.setNextId(this.getNextId()+1);
		w.addObject(new Vashelig(this.getNextId(), 0, 0, p));
		this.setNextId(this.getNextId()+1);
	}
	
	@Override
	public void drawComponents()
	{
		Mouse.poll();
		if((this.isMouseLeftPressed() && !this.isMouseGrabbed()) || (this.getDeltaMouseGrabbed() && !this.isMouseGrabbed()))
		{
			this.select();
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_F5) && !this.dF5)
		{
			for(int i = 0; i < w.getObjects().size(); i++)
			{
				w.getObjects().get(i).reloadShader();
			}
			this.dF5 = true;
		}
		else if(!Keyboard.isKeyDown(Keyboard.KEY_F5))
		{
			this.dF5 = false;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && !this.dESC)
		{
			Globals.setActiveFrame("main");
			this.dESC = true;
		}
		else if(!Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
		{
			this.dESC = false;
		}
		
		
		w.drawObjects();
		
		for(int i = 0; i < this.getComponents().size(); i++)
		{
			this.getComponents().get(i).draw();
		}
		
	}
	
	@Override
	public void select()
	{
		
		w.drawObjectSelection();
		
		for(int i = 0; i < this.getComponents().size(); i++)
		{
			this.getComponents().get(i).drawSelection();
		}
		
		int xDif = Mouse.getX() - this.getMouseGrabX();
		if(xDif < 0) xDif *= -1; if(xDif == 0) xDif = 1;
		
		int yDif = Mouse.getY() - this.getMouseGrabY();
		if(yDif < 0) yDif *= -1; if(yDif == 0) yDif = 1;
		
		ByteBuffer pixel = ByteBuffer.allocateDirect(yDif * xDif * 16);
		GL11.glReadPixels(Math.min(Mouse.getX(), this.getMouseGrabX()), Math.min(Mouse.getY(), this.getMouseGrabY()), xDif, yDif, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixel);
		//int gotId = Utility.glColorToId(new byte[]{pixel.get(0),pixel.get(1),pixel.get(2),pixel.get(3)}, false);
		
		for(int i = 0; i < this.selectedEntities.size(); i++)
		{
			this.selectedEntities.get(i).unselect();
			if((this.getDeltaMouseGrabbed() && !this.isMouseGrabbed()))
			{
				this.selectedEntities.get(i).focused();
				this.focusedEntities.add(this.selectedEntities.get(i));
			}
		}
		this.selectedEntities.clear();
		
		if(this.selectedComponent != null)
		{
			this.selectedComponent.unselected();
		}
		
		if(this.isMouseLeftPressed())
		{
			for(int i = 0; i < this.focusedEntities.size(); i++)
			{
				this.focusedEntities.get(i).unfocused();
			}
			this.focusedEntities.clear();
		}
		
		ArrayList<Integer> ids = Utility.filterIds(pixel);
		
		for(int i = 0; i < ids.size(); i++)
		{			
			Entity2D en = w.getObjectById(ids.get(i));
			
			if(en == null)
				continue;
			
			en.select();
			this.selectedEntities.add(en);			
			
			if(this.isMouseLeftPressed() && !this.isMouseGrabbed())
			{
				en.focused();
				this.focusedEntities.add(en);
			}
		}
		
		if(ids.size() > 0 && this.getComponentById(ids.get(0))!=null)
		{
			Component c = this.getComponentById(ids.get(0));
			c.selected();
			
			if(this.isMouseLeftPressed() && !this.isMouseGrabbed())
				c.clicked(Mouse.getX(),Mouse.getY(), MouseButtons.LEFT, false);
			
			this.selectedComponent = c;
		}
	}

	

}
