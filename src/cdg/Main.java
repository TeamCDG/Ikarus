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
import java.util.Random;

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

	// Setup variables
	private final String WINDOW_TITLE = "Me got Roids";
	// Quad variables
	private int indicesCount = 0;
	// Shader variables                          
	private int vsId = 0;
	private int fsId = 0;
	private int pId = 0;
		
	private long lastFrame;
	private int lastKey;
	private ArrayList<IKeyboardListener> keyboardListener = new ArrayList<IKeyboardListener>();
	private int backgroundVAO = -1;
	private int backgroundVBO = -1;
	private int backgroundIndiciesVBO = -1;
	
	private int roidCount = 100;
<<<<<<< HEAD
	private int riBracuCount = 10;
=======
	private int riBracuCount = 1;
>>>>>>> dafuq
	private int gcFrameCount = 0;
	private Roid[] roids;
	private RiBracu[] test;
	private Credits c;
	private boolean playCredits = false;
	
	@Override
	public double getDelta() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void showMainMenu() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showCredits() {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) {
		new Main();
	}
	
	public Main() {
		// Initialize OpenGL (Display)
		this.setupOpenGL();
		
		this.setupQuad();
		this.setupShaders();
		
		
		
		//StaticManager.DEFAULT_SHADER = new ShaderProgram("res/shader/default.vert", "res/shader/default.frag");
		
		this.setupTextures();
		
		
		roids = new Roid[roidCount];
		test = new RiBracu[riBracuCount];
		
		Roid.ROID_SHADER = new ShaderProgram("res/shader/roid.vert", "res/shader/roid.frag");
		Roid.ROID_TEXTURE_ID =  Utility.loadPNGTexture("res/textures/testroid.png", GL13.GL_TEXTURE0);
		
		RiBracu.RIBRACU_SHADER = new ShaderProgram("res/shader/ribracu.vert", "res/shader/ribracu.frag");
		RiBracu.RIBRACU_TEXTURE_ID =  Utility.loadPNGTexture("res/textures/ribracu_shield.png", GL13.GL_TEXTURE0);
		
		EngineExhaust.ENGINE_EXHAUST_SHADER = new ShaderProgram("res/shader/ee.vert", "res/shader/ee.frag");
		EngineExhaust.ENGINE_EXHAUST_TEXTURE_ID =  Utility.loadPNGTexture("res/textures/exhaust.png", GL13.GL_TEXTURE0);
		
		for(int i = 0; i < roidCount; i++)
		{
			float roidSize = new Random().nextFloat()*0.2f;
			roids[i] = new Roid(i, ((new Random().nextFloat()-0.5f)*2.0f)*StaticManager.ASPECT_RATIO, (new Random().nextFloat()-0.5f)*2.0f, roidSize, roidSize);
			//roids[i] = new Roid(i, 0.5f, 0.5f, 0.2f, 0.2f);
		}
		this.exitOnGLError("setupRoids");
		
		for(int i = 0; i < riBracuCount; i++)
		{
			test[i] = new RiBracu(roids.length+i,((new Random().nextFloat()-0.5f)*2.0f)*StaticManager.ASPECT_RATIO, (new Random().nextFloat()-0.5f)*2.0f);
			//roids[i] = new Roid(i, 0.5f, 0.5f, 0.2f, 0.2f);
		}
		
		
		
		this.loadCursor();
		c  = new Credits();
		StaticManager.delta = (float) this.calculateDelta();
		System.gc();
		
		
		while (!Display.isCloseRequested()) {
			
			StaticManager.delta = (float) this.calculateDelta();
			// Do a single loop (logic/render)
			this.loopCycle();
			
			
			// Force a maximum FPS of about 60
			Display.sync(60);
			// Let the CPU synchronize with the GPU if GPU is tagging behind
			Display.update();
			
			this.gcFrameCount++;
			if(this.gcFrameCount >= 20)
			{
				//System.gc();
				this.gcFrameCount = 0;
			}
		}
		
		// Destroy OpenGL (Display)
		this.destroyOpenGL();
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

	private void setupTextures() {
		StaticManager.MAIN_MENU_BACKGROUND_TEXTURE_ID = Utility.loadPNGTexture("res/textures/grid.png", GL13.GL_TEXTURE0);
		
		this.exitOnGLError("setupTexture");
	}

	private void setupOpenGL() {
		// Setup an OpenGL context with API version 3.2
		
		
		try {
			
			
			
			DisplayMode[] modes = Display.getAvailableDisplayModes();
			
			DisplayMode finalMode = new DisplayMode(StaticManager.WINDOW_WIDTH, StaticManager.WINDOW_HEIGHT);
			
			for (int i=0;i<modes.length;i++) {
			    DisplayMode current = modes[i];
			    if(current.getWidth() == StaticManager.WINDOW_WIDTH
			       && current.getHeight() == StaticManager.WINDOW_HEIGHT
			       && current.getBitsPerPixel() == 32
			       && current.getFrequency() == 60)
			    finalMode = current;
			}
			
			PixelFormat pixelFormat = new PixelFormat();
			ContextAttribs contextAtrributes = new ContextAttribs(3, 2)
				.withForwardCompatible(true)
				.withProfileCore(true);
			
			Display.setDisplayMode(finalMode);
			Display.setTitle(WINDOW_TITLE);
			Display.create(pixelFormat, contextAtrributes);
			
			
			GL11.glViewport(0, 0, StaticManager.WINDOW_WIDTH, StaticManager.WINDOW_HEIGHT);
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		// Setup an XNA like background color
		GL11.glClearColor(0.4f, 0.6f, 0.9f, 0f);
		
		// Map the internal OpenGL coordinate system to the entire screen
		GL11.glViewport(0, 0, StaticManager.WINDOW_WIDTH, StaticManager.WINDOW_HEIGHT);
		
		this.exitOnGLError("setupOpenGL");
	}
	
	private void setupQuad() {
		
		VertexData[] points = new VertexData[]{new VertexData(new float[]{-1.0f,1.0f,0.0f,1.0f}, new float[]{1.0f,1.0f,1.0f,1.0f}, new float[]{1.0f,0.0f}),
				   new VertexData(new float[]{-1.0f,-1.0f,0.0f,1.0f}, new float[]{1.0f,1.0f,1.0f,1.0f}, new float[]{1.0f,1.0f}),
				   new VertexData(new float[]{1.0f,-1.0f,0.0f,1.0f}, new float[]{1.0f,1.0f,1.0f,1.0f}, new float[]{0.0f,1.0f}),
				   new VertexData(new float[]{1.0f,1.0f,0.0f,1.0f}, new float[]{1.0f,1.0f,1.0f,1.0f}, new float[]{0.0f,0.0f})};
		// Put each 'Vertex' in one FloatBuffer
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(points.length *
				VertexData.ELEMENT_COUNT);
		for (int i = 0; i < points.length; i++) {
			// Add position, color and texture floats to the buffer
			verticesBuffer.put(points[i].getElements());
		}
		verticesBuffer.flip();	
		// OpenGL expects to draw vertices in counter clockwise order by default
		byte[] indices = {
				0, 1, 2,
				0, 3, 2
		};
		indicesCount = indices.length;
		ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indicesCount);
		indicesBuffer.put(indices);
		indicesBuffer.flip();
		
		// Create a new Vertex Array Object in memory and select it (bind)
		backgroundVAO = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(backgroundVAO);
		
		// Create a new Vertex Buffer Object in memory and select it (bind)
		backgroundVBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, backgroundVBO);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
		
		// Put the position coordinates in attribute list 0
		GL20.glVertexAttribPointer(0, VertexData.POSITION_ELEMENT_COUNT, GL11.GL_FLOAT, 
				false, VertexData.STRIDE, VertexData.POSITION_BYTE_OFFSET);
		// Put the color components in attribute list 1
		GL20.glVertexAttribPointer(1, VertexData.COLOR_ELEMENT_COUNT, GL11.GL_FLOAT, 
				false, VertexData.STRIDE, VertexData.COLOR_BYTE_OFFSET);
		// Put the texture coordinates in attribute list 2
		GL20.glVertexAttribPointer(2, VertexData.COLOR_ELEMENT_COUNT, GL11.GL_FLOAT, 
				false, VertexData.STRIDE, VertexData.TEXTURE_BYTE_OFFSET);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		// Deselect (bind to 0) the VAO
		GL30.glBindVertexArray(0);
		
		// Create a new VBO for the indices and select it (bind) - INDICES
		backgroundIndiciesVBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, backgroundIndiciesVBO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		this.exitOnGLError("setupQuad");
	}
	
	private void setupShaders() {		
		// Load the vertex shader
		vsId = Utility.loadShader("res\\shader\\background.vert", GL20.GL_VERTEX_SHADER);
		// Load the fragment shader
		fsId = Utility.loadShader("res\\shader\\background.frag", GL20.GL_FRAGMENT_SHADER);
		
		// Create a new shader program that links both shaders
		pId = GL20.glCreateProgram();
		GL20.glAttachShader(pId, vsId);
		GL20.glAttachShader(pId, fsId);

		// Position information will be attribute 0
		GL20.glBindAttribLocation(pId, 0, "in_Position");
		// Color information will be attribute 1
		GL20.glBindAttribLocation(pId, 1, "in_Color");
		// Textute information will be attribute 2
		GL20.glBindAttribLocation(pId, 2, "in_TextureCoord");
		
		GL20.glLinkProgram(pId);
		GL20.glValidateProgram(pId);
		
		this.exitOnGLError("setupShaders");
	}
	
	private void loopCycle() {
		
		// Render
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		
		
		GL20.glUseProgram(pId);
		
		// Bind the texture
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, StaticManager.MAIN_MENU_BACKGROUND_TEXTURE_ID);
		
		// Bind to the VAO that has all the information about the vertices
		GL30.glBindVertexArray(backgroundVAO);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		// Bind to the index VBO that has all the information about the order of the vertices
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, backgroundIndiciesVBO);
		
		
		GL20.glUniformMatrix4(GL20.glGetUniformLocation(pId, "window_Matrix"), false, Matrix4x4.getIdentity().getAsBuffer());	
	
		
		// Draw the vertices
		GL11.glDrawElements(GL11.GL_TRIANGLES, indicesCount, GL11.GL_UNSIGNED_BYTE, 0);
		
		// Put everything back to default (deselect)
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		GL20.glUseProgram(0);
		
		GL11.glEnable(GL11.GL_BLEND);
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		for(int i = 0; i < roidCount; i++)
		{
			roids[i].doTick();
		}
		
		for(int i = 0; i < riBracuCount; i++)
		{
			test[i].doTick();
		}
		
		for(int i = 0; i < roidCount; i++)
		{
			roids[i].draw();
		}

		for(int i = 0; i < riBracuCount; i++)
		{
			test[i].draw();
		}
		
		if(this.playCredits )
		{
			c.doTick();
			c.draw();
		}
		
		this.exitOnGLError("loopCycle");
	}
	
	private void destroyOpenGL() {	
		// Delete the texture
		GL11.glDeleteTextures(StaticManager.MAIN_MENU_BACKGROUND_TEXTURE_ID);
		
		// Delete the shaders
		GL20.glUseProgram(0);
		GL20.glDetachShader(pId, vsId);
		GL20.glDetachShader(pId, fsId);
		
		GL20.glDeleteShader(vsId);
		GL20.glDeleteShader(fsId);
		GL20.glDeleteProgram(pId);
		
		// Select the VAO
		GL30.glBindVertexArray(backgroundVAO);
		
		// Disable the VBO index from the VAO attributes list
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		
		// Delete the vertex VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(backgroundVBO);
		
		// Delete the index VBO
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(backgroundIndiciesVBO);
		
		// Delete the VAO
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(backgroundVAO);
		
		this.exitOnGLError("destroyOpenGL");
		
		Display.destroy();
	}
	
	
	private void exitOnGLError(String errorMessage) {
		int errorValue = GL11.glGetError();
		
		if (errorValue != GL11.GL_NO_ERROR) {
			String errorString = GLU.gluErrorString(errorValue);
			System.err.println("ERROR - " + errorMessage + ": " + errorString);
			
			if (Display.isCreated()) Display.destroy();
			System.exit(-1);
		}
		
		errorValue = 0;
	}
	
	private long getTime()
	{
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	public double calculateDelta() 
	{
		long currentTime = getTime();
		double delta = (double) currentTime - (double) lastFrame;
		this.lastFrame = getTime();
		currentTime = 0;
		return delta;
	}

	/*
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
		
		Mouse.poll();
		if(Mouse.isButtonDown(0))
		{
			for(int i = 0; i < StaticManager.objects.size(); i++)
				StaticManager.objects.get(i).draw(true);
			Vertex2 mGL = Utility.mouseTo2DGL(Mouse.getX(), Mouse.getY(), 
					StaticManager.WINDOW_WIDTH, StaticManager.WINDOW_HEIGHT);
			ikarus.shoot(mGL.getX(), mGL.getY());
		}
		
		GL11.glClearColor(StaticManager.CLEAR_COLOR[0], 
				  StaticManager.CLEAR_COLOR[1],
				  StaticManager.CLEAR_COLOR[2], 
				  StaticManager.CLEAR_COLOR[3]);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);	
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, StaticManager.MAIN_MENU_BACKGROUND_TEXTURE_ID);
		GL20.glUseProgram(StaticManager.MENU_SHADER_PROGRAM_ID);
		
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
		GL20.glUniform1i(GL20.glGetUniformLocation(StaticManager.MENU_SHADER_PROGRAM_ID, "state"), 0);
		
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
	*/

}
