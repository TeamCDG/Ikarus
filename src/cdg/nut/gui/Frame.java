package cdg.nut.gui;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cdg.interfaces.IMatrix;
import cdg.nut.util.Globals;
import cdg.nut.util.Matrix4x4;
import cdg.nut.util.MouseButtons;
import cdg.nut.util.Utility;

public abstract class Frame 
{
	private ArrayList<Component> components;
	
	private int oldMouseX;
	private int oldMouseY;
	private int mouseGrabX;
	private int mouseGrabY;
	private boolean mouseLeftPressed;
	
	private int nextId = 1;
	
	private int maxSelectSkip = 2;
	private int selectSkip = 0;

	private Component active = null;
	private int lastId = 0;

	private Matrix4x4 windowMatrix = Matrix4x4.getIdentity();

	private boolean mouseGrabbed;

	private boolean deltaMouseGrabbed;

	private boolean firstFrame = true;

	private String title;

	private boolean mouseRightPressed;
	
	public Frame(Matrix4x4 winMat)
	{
		this.components = new ArrayList<Component>();
		this.windowMatrix = winMat;
	}
	
	public void draw()
	{
		this.selectSkip++;
		
		Mouse.poll();
		this.deltaMouseGrabbed = this.mouseGrabbed;
		if(((this.oldMouseX != Mouse.getX() || this.oldMouseY != Mouse.getY()) && this.selectSkip > this.maxSelectSkip && Mouse.isInsideWindow()) || this.firstFrame )
		{
			this.selectSkip = 0;
			this.select();

			GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			
			firstFrame = false;
			
		}
		
		if(Mouse.isButtonDown(MouseButtons.LEFT))
		{
			
			
			//System.out.println("did="+deltaId+" | lid="+lastId);
			
			if(this.active != null && this.active.getId() != lastId)
			{
				this.active.setActive(false);
			}
			
			Component c = this.getComponentById(this.lastId);
			if(lastId != 0 && c != null)
				c.clicked(Mouse.getX(), Mouse.getY(), MouseButtons.LEFT, this.isMouseLeftPressed());
			this.active = c;
			this.setMouseLeftPressed(true);
		}
		else if(!Mouse.isButtonDown(MouseButtons.LEFT))
		{
			this.setMouseLeftPressed(false);
			this.mouseGrabbed = false;
		}
		
		if(Mouse.isButtonDown(MouseButtons.RIGHT))
		{
			this.setMouseRightPressed(true);
		}
		else if(!Mouse.isButtonDown(MouseButtons.RIGHT))
		{
			this.setMouseRightPressed(false);
		}
		
		Keyboard.enableRepeatEvents(true);
		Keyboard.poll();
		while(Keyboard.next())
		{
			if(Keyboard.getEventKeyState() && this.active!=null)
				this.active.keyDown(Keyboard.getEventKey(), Keyboard.getEventCharacter());
		}
		
		if((this.oldMouseX != Mouse.getX() || this.oldMouseY != Mouse.getY()) && !this.deltaMouseGrabbed )
		{
			this.oldMouseX = Mouse.getX();
			this.oldMouseY = Mouse.getY();
			this.mouseGrabX = Mouse.getX();
			this.mouseGrabY = Mouse.getY();
			this.mouseGrabbed = this.mouseLeftPressed;
		}
		else if(this.deltaMouseGrabbed)
		{
			this.oldMouseX = Mouse.getX();
			this.oldMouseY = Mouse.getY();
			this.mouseGrabbed = this.mouseLeftPressed;
		}

		this.drawBackground();
		this.drawComponents();
		
		
	}
	
	public void drawBackground()
	{
		//TODO: add bg
	}
	
	public void drawComponents()
	{
		for(int i = 0; i < this.components.size(); i++)
		{
			this.components.get(i).draw();
		}
	}
	
	public void select()
	{
		for(int i = 0; i < this.components.size(); i++)
		{
			this.components.get(i).drawSelection();
		}
		
		ByteBuffer pixel = ByteBuffer.allocateDirect(16);
		GL11.glReadPixels(Mouse.getX(), Mouse.getY(), 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixel);
		int gotId = Utility.glColorToId(new byte[]{pixel.get(0),pixel.get(1),pixel.get(2),pixel.get(3)}, false);
		
		//System.out.println("selected: "+gotId);
		
		if(gotId == lastId)
			return; //my work here is done
		
		for(int i = 0; i < this.components.size(); i++)
		{
			this.components.get(i).select(gotId);		
		}
		
		
		this.lastId = gotId;
	}
	
	protected void add(Component comp)
	{
		comp.setId(nextId);
		if(comp.isScrollable())
			this.nextId+=5;
		else
			this.nextId++;
		comp.setParent(this);
		this.components.add(comp);
	}
	
	protected void remove(int index)
	{
		this.components.remove(0);
	}
	
	protected void remove(Component comp)
	{
		this.components.remove(comp);
	}
	
	protected void setComponent(int index, Component comp)
	{
		int id = this.components.get(index).getId();
		comp.setId(id);
		comp.setParent(this);
		this.components.set(index, comp);
	}
	
	protected Component getComponentById(int id)
	{
		for(int i = 0; i < this.components.size(); i++)
		{
			if(this.components.get(i).getId() == id)
				return this.components.get(i);
		}
		return null;
	}
	
	protected Component getComponent(int index)
	{
		return this.components.get(index);
	}
	
	protected List<Component> getComponents()
	{                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      
		return this.components;
	}

	public Matrix4x4 getWindowMatrix() {
		// TODO Auto-generated method stub
		return this.windowMatrix;
	}

	/**
	 * @return the mouseLeftPressed
	 */
	public boolean isMouseLeftPressed() {
		return mouseLeftPressed;
	}

	/**
	 * @param mouseLeftPressed the mouseLeftPressed to set
	 */
	public void setMouseLeftPressed(boolean mouseLeftPressed) {
		this.mouseLeftPressed = mouseLeftPressed;
	}
	
	public boolean isMouseRightPressed() {
		return this.mouseRightPressed;
	}

	/**
	 * @param mouseLeftPressed the mouseLeftPressed to set
	 */
	public void setMouseRightPressed(boolean mouseRightPressed) {
		this.mouseRightPressed = mouseRightPressed;
	}
	
	public boolean isMouseGrabbed() {
		// TODO Auto-generated method stub
		return this.mouseGrabbed;
	}
	
	public void setMouseGrabbes(boolean grabbed)
	{
		this.mouseGrabbed = grabbed;
	}
	
	public int getMouseGrabX()
	{
		if(this.mouseGrabbed)
			return this.mouseGrabX;
		else
			return Mouse.getX();
	}
	
	public int getMouseGrabY()
	{
		if(this.mouseGrabbed)
			return this.mouseGrabY;
		else
			return Mouse.getY();
	}
	
	public boolean getDeltaMouseGrabbed()
	{
		return this.deltaMouseGrabbed;
	}

	/**
	 * @return the maxSelectSkip
	 */
	public int getMaxSelectSkip() {
		return maxSelectSkip;
	}

	/**
	 * @param maxSelectSkip the maxSelectSkip to set
	 */
	public void setMaxSelectSkip(int maxSelectSkip) {
		this.maxSelectSkip = maxSelectSkip;
	}

	/**
	 * @return the nextId
	 */
	public int getNextId() {
		return nextId;
	}

	/**
	 * @param nextId the nextId to set
	 */
	public void setNextId(int nextId) {
		this.nextId = nextId;
	}
	
	public void setTitle(String s)
	{
		this.title = s;
	}
	
	public String getTitle()
	{
		return this.title;
	}
	
	public void show()
	{
		Globals.setActiveFrame(this);
	}
	
	public void doTick()
	{
		
	}
}
