package cdg;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import cdg.interfaces.IKeyboardListener;
import cdg.util.*;
import cdg.interfaces.*;
import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class Main implements IGameControl{

	private long lastFrame;
	private int lastKey;
	private ArrayList<IKeyboardListener> keyboardListener = new ArrayList<IKeyboardListener>();
	private double delta;
	/**
	 * @param args
	 */
	private Ikarus ikarus;
	private int backgroundVAO = -1;
	private int backgroundVBO = -1;
	private int backgroundIndiciesVBO = -1;
	
	private MusicPlayer bg = new MusicPlayer();
	
	public static void main(String[] args) {
		new Main();

	}
	
	public Main()
	{
		
		this.initWindow();
		this.initGL();
		this.loadCursor();
		ikarus = new Ikarus(StaticManager.objects.size()+1);
		StaticManager.objects.add(ikarus);
		for(int i = 0; i < 10; i++)
			StaticManager.objects.add(new RiBracu(StaticManager.objects.size()+1));
		StaticManager.MAIN_MENU_BACKGROUND_TEXTURE_ID = Utility.loadPNGTexture("res\\textures\\background.png", GL13.GL_TEXTURE0);
		//keyboardListener.add(ikarus);
		//this.loadMenuSelectionShader();
		
		//this.loadTextShader();
		//this.loadCreditsShader();
		
		//StaticManager.FONT_TEXTURE_ID = Utility.loadPNGTexture("res//font//font.png", GL13.GL_TEXTURE0);
		//StaticManager.SPLASH_TEXTURE_ID = Utility.loadPNGTexture("res//textures//logo.png", GL13.GL_TEXTURE0);
		//StaticManager.MAIN_MENU_BACKGROUND_TEXTURE_ID = Utility.loadPNGTexture("res//textures//background.png", GL13.GL_TEXTURE0);
		//setupQuad();
		this.loadMenuRenderShader();
		this.setupBackground();
		this.lastFrame = getTime();
		bg.open("res\\sound\\background.mp3");
        bg.play();
		bg.setGain(100);
		while (!Display.isCloseRequested()) {
			
			StaticManager.delta = (float) this.calculateDelta();
			double lastFrameTime = getDelta();
			// Do a single loop (logic/render)
			this.process();
			
			// Force a maximum FPS of about 60
			Display.sync(60);
			// Let the CPU synchronize with the GPU if GPU is tagging behind
			Display.update();
		}
		bg.stop();
		bg = null;
	}
	
	private long getTime()
{
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	@SuppressWarnings("unused")
	private void loadCursor()
	{
		Image c=Toolkit.getDefaultToolkit().getImage("res\\textures\\cross.png");
	    BufferedImage biCursor=new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB);
	    while(!biCursor.createGraphics().drawImage(c,0,15,15,0,0,0,15,15,null))
	      try
	      {
	        Thread.sleep(5);
	      }
	      catch(InterruptedException e)
	      {
	      }
	    
	    int[] data=biCursor.getRaster().getPixels(0,0,16,16,(int[])null);
	    
	    IntBuffer ib=BufferUtils.createIntBuffer(16*16);
	    for(int i=0;i<data.length;i+=4)
	      ib.put(data[i] | data[i+1]<<8 | data[i+2]<<16 | data[i+3]<<24);
	    ib.flip();
		try {
			Mouse.setNativeCursor(new Cursor(16, 16, 8, 8, 1, ib, null));
		} catch (LWJGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void process()
	{
		GL11.glClearColor(0.0f, 
				  0.0f,
				  0.0f, 
				  1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);		
		
		Keyboard.poll();
		Keyboard.enableRepeatEvents(true);		
		
		while(Keyboard.next())
		{
			for(int i = 0; i < this.keyboardListener.size(); i++)
			{
				this.keyboardListener.get(i).keyDown(Keyboard.getEventKey(), Keyboard.getEventCharacter());
			}
			
			this.lastKey = Keyboard.getEventKey();
		}
		
		/*
		ikarus.tick();
		for(int i = 0; i < 10; i++)
			fighter[i].tick();
		ikarus.draw();
		for(int i = 0; i < 10; i++)
			fighter[i].draw();
		
			*/
		
		Mouse.poll();
		if(Mouse.isButtonDown(0))
		{
			for(int i = 0; i < StaticManager.objects.size(); i++)
				StaticManager.objects.get(i).draw(true);
			Vertex2 mGL = Utility.mouseTo2DGL(Mouse.getX(), Mouse.getY(), 
					StaticManager.WINDOW_WIDTH, StaticManager.WINDOW_HEIGHT);
			ikarus.shoot(mGL.getX(), mGL.getY());
			/*
			ByteBuffer pixel = ByteBuffer.allocateDirect(16);
			GL11.glReadPixels(Mouse.getX(), Mouse.getY(), 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixel);
			int gotId = Utility.glColorToId(new byte[]{pixel.get(0),pixel.get(1),pixel.get(2),pixel.get(3)}, false);
			if(gotId != 0)
			{
				for(int i = 0; i < StaticManager.objects.size(); i++)
				{
					if(StaticManager.objects.get(i).getId() == gotId)
					{
						Vertex2 mGL = Utility.mouseTo2DGL(Mouse.getX(), Mouse.getY(), 
								StaticManager.WINDOW_WIDTH, StaticManager.WINDOW_HEIGHT);
						ikarus.shoot(mGL.getX(), mGL.getY());
					}
				}  
			}
			*/
		}
		
		GL11.glClearColor(StaticManager.CLEAR_COLOR[0], 
				  StaticManager.CLEAR_COLOR[1],
				  StaticManager.CLEAR_COLOR[2], 
				  StaticManager.CLEAR_COLOR[3]);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);	
		
		
		GL20.glUseProgram(StaticManager.MENU_SHADER_PROGRAM_ID);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, StaticManager.MAIN_MENU_BACKGROUND_TEXTURE_ID);
		
		GL30.glBindVertexArray(this.backgroundVAO);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		// Bind to the index VBO that has all the information about the order of the vertices
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.backgroundIndiciesVBO);
		
		FloatBuffer mat = BufferUtils.createFloatBuffer(16);
		mat.put(new Matrix4x4(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f).toArray());
		mat.flip();
			
		GL20.glUniform1i(GL20.glGetUniformLocation(StaticManager.MENU_SHADER_PROGRAM_ID, "texture_font"), 0);
		GL20.glUniformMatrix4(GL20.glGetUniformLocation(StaticManager.MENU_SHADER_PROGRAM_ID, "windowMatrix"), false, mat);
		
		// Draw the vertices
		GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_BYTE, 0);
		
		// Put everything back to default (deselect)
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		for(int i = 0; i < StaticManager.objects.size(); i++)
			StaticManager.objects.get(i).tick();
		for(int i = 0; i < StaticManager.shoots.size(); i++)
			StaticManager.shoots.get(i).tick();
		
		for(int i = 0; i < StaticManager.objects.size(); i++)
			StaticManager.objects.get(i).draw(false);
		for(int i = 0; i < StaticManager.shoots.size(); i++)
			StaticManager.shoots.get(i).draw(false);
		//GL20.glUseProgram(StaticManager.SELECTION_SHADER_PROGRAM_ID);
		//d.draw();
		//d2.draw();
		//GL20.glUseProgram(0);
		
		this.exitOnGLError("process");
	}
	
	private void initWindow()
	{
		try 
		{
			PixelFormat pixelFormat = new PixelFormat();
			ContextAttribs contextAtrributes = new ContextAttribs(3, 2)
				.withForwardCompatible(true)
				.withProfileCore(true);
			
			Display.setDisplayMode(new DisplayMode(StaticManager.WINDOW_WIDTH, StaticManager.WINDOW_HEIGHT));
			Display.setTitle("v0.0.1 - Ikarus Launch!");
			Display.create(pixelFormat, contextAtrributes);
			
			
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	private void initGL()
	{
		GL11.glViewport(0, 0, StaticManager.WINDOW_WIDTH, StaticManager.WINDOW_HEIGHT);
		GL11.glClearColor(StaticManager.CLEAR_COLOR[0], 
						  StaticManager.CLEAR_COLOR[1],
						  StaticManager.CLEAR_COLOR[2], 
						  StaticManager.CLEAR_COLOR[3]);
	}
	
	private void setupBackground()
	{
		if(this.backgroundVAO == -1)
			this.backgroundVAO = GL30.glGenVertexArrays(); //generate id for VAO
		if(this.backgroundVBO == -1)
			this.backgroundVBO = GL15.glGenBuffers(); //generate id for VBO
		
		glBindVertexArray(this.backgroundVAO); //bind our VAO
		glBindBuffer(GL_ARRAY_BUFFER, this.backgroundVBO); //bind our VBO
		
		//create our selection frame
		VertexData[] points = new VertexData[]{new VertexData(new float[]{-1.0f,1.0f,0.0f,1.0f}, new float[]{1.0f,1.0f,1.0f,1.0f}, new float[]{0.0f,0.0f}),
											   new VertexData(new float[]{-1.0f,-1.0f,0.0f,1.0f}, new float[]{1.0f,1.0f,1.0f,1.0f}, new float[]{0.0f,1.0f/StaticManager.ASPECT_RATIO}),
											   new VertexData(new float[]{1.0f,-1.0f,0.0f,1.0f}, new float[]{1.0f,1.0f,1.0f,1.0f}, new float[]{1.0f,1.0f/StaticManager.ASPECT_RATIO}),
											   new VertexData(new float[]{1.0f,1.0f,0.0f,1.0f}, new float[]{1.0f,1.0f,1.0f,1.0f}, new float[]{1.0f,0.0f})};
		
		//buffer to store data
		FloatBuffer f = BufferUtils.createFloatBuffer(points.length * VertexData.ELEMENT_COUNT);
		for (int i = 0; i < points.length; i++) 
		{
			//add position, color and texture floats to the buffer
			f.put(points[i].getElements());
		}
		f.flip();
		
		//upload our data
		GL15.glBufferData(GL_ARRAY_BUFFER, f, GL_STATIC_DRAW);
		
		// Put the position coordinates in attribute list 0
		GL20.glVertexAttribPointer(0, VertexData.POSITION_ELEMENT_COUNT, GL11.GL_FLOAT, 
				false, VertexData.STRIDE, VertexData.POSITION_BYTE_OFFSET);
		
		// Put the color components in attribute list 1
		GL20.glVertexAttribPointer(1, VertexData.COLOR_ELEMENT_COUNT, GL11.GL_FLOAT, 
				false, VertexData.STRIDE, VertexData.COLOR_BYTE_OFFSET);
		
		// Put the texture coordinates in attribute list 2
		GL20.glVertexAttribPointer(2, VertexData.TEXTURE_ELEMENT_COUNT, GL11.GL_FLOAT, 
				false, VertexData.STRIDE, VertexData.TEXTURE_BYTE_OFFSET);
			
		//unbind buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		//buffer for storing indicies
		ByteBuffer indiciesBuffer = BufferUtils.createByteBuffer(6);
		indiciesBuffer.put(new byte[]{0, 1, 2, 2, 3, 0}); //put in indicies
		indiciesBuffer.flip();
		
		if(this.backgroundIndiciesVBO == -1)
			this.backgroundIndiciesVBO = GL15.glGenBuffers(); //generate buffer id for indicies
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.backgroundIndiciesVBO); //bind indicies buffer
		
		//upload indicies
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiciesBuffer, GL15.GL_STATIC_DRAW);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0); //unbind buffer
		
		GL30.glBindVertexArray(0); //unbind VAO
	}
	
	private void loadCreditsShader()
	{
		//load vertex shader
		int vsId = Utility.loadShader("res\\shader\\creditsVertex.glsl", GL20.GL_VERTEX_SHADER);
		//load fragment shader
		int fsId = Utility.loadShader("res\\shader\\creditsFragment.glsl", GL20.GL_FRAGMENT_SHADER);
		
		StaticManager.CREDITS_PROGRAM_ID = GL20.glCreateProgram();
		GL20.glAttachShader(StaticManager.CREDITS_PROGRAM_ID, vsId);
		GL20.glAttachShader(StaticManager.CREDITS_PROGRAM_ID, fsId);
		GL20.glLinkProgram(StaticManager.CREDITS_PROGRAM_ID);

		// Position information will be attribute 0
		GL20.glBindAttribLocation(StaticManager.CREDITS_PROGRAM_ID, 0, "in_Position");
		// Color information will be attribute 1
		GL20.glBindAttribLocation(StaticManager.CREDITS_PROGRAM_ID, 1, "in_Color");
		// Textute information will be attribute 2
		GL20.glBindAttribLocation(StaticManager.CREDITS_PROGRAM_ID, 2, "in_TextureCoord");
		
		GL20.glValidateProgram(StaticManager.CREDITS_PROGRAM_ID);
	}
	
	private void loadMenuRenderShader()
	{
		//load vertex shader
		int vsId = Utility.loadShader("res\\shader\\menuRenderVertex.glsl", GL20.GL_VERTEX_SHADER);
		//load fragment shader
		int fsId = Utility.loadShader("res\\shader\\menuRenderFragment.glsl", GL20.GL_FRAGMENT_SHADER);
		
		StaticManager.MENU_SHADER_PROGRAM_ID = GL20.glCreateProgram();
		GL20.glAttachShader(StaticManager.MENU_SHADER_PROGRAM_ID , vsId);
		GL20.glAttachShader(StaticManager.MENU_SHADER_PROGRAM_ID , fsId);
		GL20.glLinkProgram(StaticManager.MENU_SHADER_PROGRAM_ID );
		

		// Position information will be attribute 0
		GL20.glBindAttribLocation(StaticManager.MENU_SHADER_PROGRAM_ID , 0, "in_Position");
		// Color information will be attribute 1
		GL20.glBindAttribLocation(StaticManager.MENU_SHADER_PROGRAM_ID , 1, "in_Color");
		// Textute information will be attribute 2
		GL20.glBindAttribLocation(StaticManager.MENU_SHADER_PROGRAM_ID , 2, "in_TextureCoord");
		
		GL20.glValidateProgram(StaticManager.MENU_SHADER_PROGRAM_ID );
		
	}
	
	private void loadTextShader()
	{
		//load vertex shader
		int vsId = Utility.loadShader("res\\shader\\textVertex.glsl", GL20.GL_VERTEX_SHADER);
		//load fragment shader
		int fsId = Utility.loadShader("res\\shader\\textFragment.glsl", GL20.GL_FRAGMENT_SHADER);
		
		StaticManager.TEXT_SHADER_PROGRAM_ID = GL20.glCreateProgram();
		GL20.glAttachShader(StaticManager.TEXT_SHADER_PROGRAM_ID , vsId);
		GL20.glAttachShader(StaticManager.TEXT_SHADER_PROGRAM_ID , fsId);
		GL20.glLinkProgram(StaticManager.TEXT_SHADER_PROGRAM_ID );
		

		// Position information will be attribute 0
		GL20.glBindAttribLocation(StaticManager.TEXT_SHADER_PROGRAM_ID , 0, "in_Position");
		// Color information will be attribute 1
		GL20.glBindAttribLocation(StaticManager.TEXT_SHADER_PROGRAM_ID , 1, "in_Color");
		// Textute information will be attribute 2
		GL20.glBindAttribLocation(StaticManager.TEXT_SHADER_PROGRAM_ID , 2, "in_TextureCoord");
		
		GL20.glValidateProgram(StaticManager.TEXT_SHADER_PROGRAM_ID );
		
		StaticManager.FONT_TEXTURE_UNIFORM_ID = GL20.glGetUniformLocation(StaticManager.TEXT_SHADER_PROGRAM_ID, "texture_font");
		StaticManager.FONT_SCALING_MATRIX_UNIFORM_ID= GL20.glGetUniformLocation(StaticManager.TEXT_SHADER_PROGRAM_ID, "font_scaling_matrix");
		StaticManager.TEXT_POSITION_UNIFORM_ID = GL20.glGetUniformLocation(StaticManager.TEXT_SHADER_PROGRAM_ID, "position");
		
		GL20.glUseProgram(StaticManager.TEXT_SHADER_PROGRAM_ID);
		FloatBuffer mat = BufferUtils.createFloatBuffer(16);
		mat.put(StaticManager.WINDOW_MATRIX.toArray());
		mat.flip();
		GL20.glUniformMatrix4(GL20.glGetUniformLocation(StaticManager.TEXT_SHADER_PROGRAM_ID, "windowMatrix"), false, mat);
		GL20.glUseProgram(0);
	}
	
	private void exitOnGLError(String errorMessage) 
	{
		int errorValue = GL11.glGetError();
		
		if (errorValue != GL11.GL_NO_ERROR) {
			String errorString = GLU.gluErrorString(errorValue);
			System.err.println("ERROR - " + errorMessage + ": " + errorString);
			
			if (Display.isCreated()) Display.destroy();
			System.exit(-1);
		}
	}
	
	public double calculateDelta() 
	{
		long currentTime = getTime();
		double delta = (double) currentTime - (double) lastFrame;
		this.lastFrame = getTime();
		return delta;
	}

	@Override
	public double getDelta() 
	{
		return this.delta;
	}

	@Override
	public void showMainMenu() 
	{		
		GL11.glClearColor(StaticManager.CLEAR_COLOR[0], 
				  StaticManager.CLEAR_COLOR[1],
				  StaticManager.CLEAR_COLOR[2], 
				  StaticManager.CLEAR_COLOR[3]);
	}

	@Override
	public void showCredits() 
	{
		
	}

}
