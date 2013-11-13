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
import cdg.nut.util.*;
import cdg.interfaces.*;
import cdg.nut.gui.Component;
import cdg.nut.gui.Frame;
import cdg.nut.util.BitmapFont;
import cdg.nut.util.Globals;
import cdg.nut.util.Matrix4x4;
import cdg.nut.util.ShaderProgram;
import cdg.nut.util.Utility;
import cdg.nut.util.Vertex2;
import cdg.nut.util.VertexData;
import cdg.nut.util.game.Entity2D;
import cdg.nut.util.game.PlanetoidCreator;
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
	
	private int roidCount = 00;
	private int riBracuCount = 0;
	private int gcFrameCount = 0;
	private Roid[] roids;
	private RiBracu[] test;
	private Credits c;
	private boolean playCredits = false;
	
	private FontObject fpsFO;
	private float fps = 0.0f;
	private FontObject camXFO;
	private float camX = 0.0f;
	private FontObject camYFO;
	private float camY = 0.0f;
	private FontObject camScaleFO;
	private float camScale = 1.0f;
	private FontObject skippedFO;
	
	private float camMoveSpeed = (1.0f/3000.0f)*1;
	
	private Vertex2[] visibleScreenBounds = new Vertex2[] {new Vertex2(-1.0f, 1.0f),
														   new Vertex2(-1.0f, -1.0f),
														   new Vertex2(1.0f, -1.0f),
														   new Vertex2(1.0f, 1.0f)};
	private long lastFPS;
	private Matrix4x4 visibleAreaMatrix = Matrix4x4.getIdentity();
	
	private boolean debugOverlay = true;
	private boolean deltaF3 = false;
	
	private boolean pause = false;
	private boolean deltaP = false;
	
	private Frame activeFrame;

	
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
	
	public void setupNonGLGlobals()
	{
		Globals.setWindowTitle("Ikarus PreAlpha");
		Globals.setWindowResolution((1280/10)*9, (720/10)*9);
		
	}
	
	public void setupGLGlobals()
	{
		try {
			Globals.addFont(new BitmapFont("res\\font\\lcd.txt"));
			Globals.addFont(new BitmapFont("res\\font\\consolas.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Globals.setDefaultTextFont("consolas");
		
		Globals.addFrame("main", new MainFrame(Globals.getWindowMatrix()));
		Globals.addFrame("game", new GameFrame(Globals.getWindowMatrix()));
		
	}
	
	public Main() {
		// Initialize OpenGL (Display)

		this.setupNonGLGlobals();
		this.setupOpenGL();
		this.setupQuad();
		this.setupShaders();
		this.setupGLGlobals();
		
		
		
		//StaticManager.DEFAULT_SHADER = new ShaderProgram("res/shader/default.vert", "res/shader/default.frag");
		
		this.setupTextures();
		
		
		roids = new Roid[roidCount];
		test = new RiBracu[riBracuCount];
		
		Roid.SHADER = new ShaderProgram("res/shader/roid.vert", "res/shader/roid.frag");
		Roid.TEXTURE_ID =  Utility.loadPNGTexture("res/textures/testroid.png", GL13.GL_TEXTURE0);
		Roid.TEXTURE_ID_2 =  Utility.loadPNGTextureSmooth("res/textures/planetoid.png", GL13.GL_TEXTURE0);
		
		Roid.SURFACE_TEXTURE_IDS = new int[5];
		for(int i = 0; i < 5; i++)
		{			
			Roid.SURFACE_TEXTURE_IDS[i] = Utility.loadPNGTextureSmooth("res/textures/roid_surface/roid"+i+".png", GL13.GL_TEXTURE0);
			System.out.println("ID: "+Roid.SURFACE_TEXTURE_IDS[i]+" | Path: "+"res/textures/roid_surface/roid"+i+".png");
		}
		
		Roid.FORM_TEXTURE_IDS = new int[20];
		for(int i = 0; i < 1; i++)
		{
			Roid.FORM_TEXTURE_IDS[i] = Utility.loadBufferdImageSmooth(PlanetoidCreator.createPlanetoid(256, 100, 125, 12, 0, true, 20, -1, 2), GL13.GL_TEXTURE1);
		}
		
		RiBracu.SHADER = new ShaderProgram("res/shader/ribracu.vert", "res/shader/ribracu.frag");
		RiBracu.TEXTURE_ID =  Utility.loadPNGTexture("res/textures/ribracu_shield.png", GL13.GL_TEXTURE0);
		
		EngineExhaust.SHADER = new ShaderProgram("res/shader/ee.vert", "res/shader/ee.frag");
		EngineExhaust.TEXTURE_ID =  Utility.loadPNGTexture("res/textures/exhaust.png", GL13.GL_TEXTURE0);

		Laser.SHADER = new ShaderProgram("res/shader/laser.vert", "res/shader/laser.frag");
		Laser.TEXTURE_ID =  Utility.loadPNGTexture("res/textures/shoot.png", GL13.GL_TEXTURE0);
		
		FontObject.SHADER = new ShaderProgram("res/shader/background.vert", "res/shader/background.frag");	
		
		Component.DEFAULT_TEXT_SHADER = new ShaderProgram("res/shader/console_text.vert", "res/shader/console_text.frag");
		Component.DEFAULT_MAIN_SHADER = new ShaderProgram("res/shader/component.vert","res/shader/component.frag");
		
		
		
		for(int i = 0; i < roidCount; i++)
		{
			float roidSize = new AdvancedRandom().nextInt(10, 20)/100.0f;
			roids[i] = new Roid(i, ((new Random().nextFloat()-0.5f)*2.0f)*StaticManager.ASPECT_RATIO, (new Random().nextFloat()-0.5f)*2.0f, roidSize, roidSize);
			//roids[i] = new Roid(i, 0.0f, 0.0f, 0.6f, 0.6f);
		}
		this.exitOnGLError("setupRoids");
				
		for(int i = 0; i < riBracuCount; i++)
		{
			test[i] = new RiBracu(roids.length+i,((new Random().nextFloat()-0.5f)*2.0f)*StaticManager.ASPECT_RATIO, (new Random().nextFloat()-0.5f)*2.0f);
			//roids[i] = new Roid(i, 0.5f, 0.5f, 0.2f, 0.2f);
		}
		
		//o = new FontObject(0,0,"Hello World");
		try {
			StaticManager.DEFAULT_FONT = new BitmapFont("res\\font\\lcd.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fpsFO = new FontObject(-1.0f*StaticManager.ASPECT_RATIO, 1.0f, "Fps: "+fps+"f",StaticManager.DEFAULT_FONT);
		camXFO = new FontObject(-1.0f*StaticManager.ASPECT_RATIO, 0.92f, "Cam X: "+camX,StaticManager.DEFAULT_FONT);
		camYFO = new FontObject(-1.0f*StaticManager.ASPECT_RATIO, 0.84f, "Cam Y: "+camY,StaticManager.DEFAULT_FONT);
		camScaleFO = new FontObject(-1.0f*StaticManager.ASPECT_RATIO, 0.76f, "Cam scale: "+camScale,StaticManager.DEFAULT_FONT);
		skippedFO = new FontObject(-1.0f*StaticManager.ASPECT_RATIO, 0.68f, "skipped: 0",StaticManager.DEFAULT_FONT);
		try {
			StaticManager.CONSOLE = new GLConsole(new BitmapFont("res\\font\\consolas.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StaticManager.CONSOLE.setVisible(true);
		
		this.loadCursor();
		c  = new Credits();
		
		this.visibleScreenBounds =  new Vertex2[]{new Vertex2(-1.0f*StaticManager.ASPECT_RATIO, 1.0f),
				new Vertex2(-1.0f*StaticManager.ASPECT_RATIO, -1.0f),
				new Vertex2(1.0f*StaticManager.ASPECT_RATIO, -1.0f),
				new Vertex2(1.0f*StaticManager.ASPECT_RATIO, 1.0f)};
		
		StaticManager.ACTIVE_FRAME = new MainFrame(StaticManager.WINDOW_MATRIX);
		
		lastFPS = getTime();
		StaticManager.delta = (float) this.calculateDelta();
		System.gc();
		
		
		while (!Display.isCloseRequested()) {
			this.updateCamera();
			StaticManager.delta = (float) this.calculateDelta();
			// Do a single loop (logic/render)
			this.loopCycle();
			
			
			// Force a maximum FPS of about 60
			Display.sync(60);
			// Let the CPU synchronize with the GPU if GPU is tagging behind
			Display.update();
			
			this.updateFPS();
			if(this.debugOverlay)
			{
				float fps = (Math.round((1000/StaticManager.delta)*100)/100.0f);
				fpsFO.setText("Fps: "+fps+"f");
				float gVal = 1.0f/30.0f*fps;
				if(gVal > 1.0f)
					gVal = 1.0f;
				
				float rVal = 1.0f;
				if(fps > 30.0f)
					rVal = 1.0f/60.0f*((60-fps)*2);
				
				fpsFO.setColor(new float[]{rVal,gVal,0.0f,1.0f});
			}
			
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
	
	private void updateCamera()
	{
		if(Keyboard.isKeyDown(Keyboard.KEY_UP))
		{
			camY+=this.camMoveSpeed*StaticManager.delta;
			if(this.debugOverlay)
				camYFO.setText("Cam Y: "+camY);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_DOWN))
		{
			camY-=this.camMoveSpeed*StaticManager.delta;
			if(this.debugOverlay)
				camYFO.setText("Cam Y: "+camY);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_LEFT))
		{
			camX -=this.camMoveSpeed*StaticManager.delta;
			if(this.debugOverlay)
				camXFO.setText("Cam Y: "+camX);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
		{
			camX+=this.camMoveSpeed*StaticManager.delta;
			if(this.debugOverlay)
				camXFO.setText("Cam Y: "+camX);
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_F3) && !this.deltaF3)
		{
			this.debugOverlay = !this.debugOverlay;
			this.deltaF3 = true;
			StaticManager.CONSOLE.writeLine("toggling debug overlay toggling debug overlay toggling debug overlay");
		}
		else if(!Keyboard.isKeyDown(Keyboard.KEY_F3))
		{
			this.deltaF3 = false;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_P) && !this.deltaP)
		{
			this.pause = !this.pause;
			this.deltaP = true;
		}
		else if(!Keyboard.isKeyDown(Keyboard.KEY_P))
		{
			this.deltaP = false;
		}
		
		camScale += Mouse.getDWheel() * 0.001f;
		
		if(this.debugOverlay)
			camScaleFO.setText("Cam scale: "+camScale);
		
		StaticManager.CAMERA_MATRIX.set(camScale, 0.0f, 0.0f, 0.0f, 
				0.0f, camScale, 0.0f, 0.0f, 
				0.0f, 0.0f, 1.0f, 0.0f, 
				camX, camY, 0.0f, 1.0f);

		StaticManager.VISIBLE_TRANSLATION_MATRIX.set(1.0f, 0.0f, 0.0f, 0.0f, 
				0.0f, 1.0f, 0.0f, 0.0f, 
				0.0f, 0.0f, 1.0f, 0.0f, 
				camX, camY, 0.0f, 1.0f);
		this.visibleAreaMatrix = StaticManager.VISIBLE_TRANSLATION_MATRIX.multiply(new Matrix4x4((1.0f/camScale), 0.0f, 0.0f, 0.0f, 
				0.0f, (1.0f/camScale), 0.0f, 0.0f, 
				0.0f, 0.0f, 1.0f, 0.0f, 
				0.0f, 0.0f, 0.0f, 1.0f));
		
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
		StaticManager.MAIN_MENU_BACKGROUND_TEXTURE_ID = Utility.loadPNGTexture("res/textures/background.png", GL13.GL_TEXTURE0);
		
		this.exitOnGLError("setupTexture");
	}

	private void setupOpenGL() {
		// Setup an OpenGL context with API version 3.2
		
		// Setup an XNA like background color
		GL11.glClearColor(StaticManager.CLEAR_COLOR[0],
				StaticManager.CLEAR_COLOR[1],
				StaticManager.CLEAR_COLOR[2],
				StaticManager.CLEAR_COLOR[3]);
		
		// Map the internal OpenGL coordinate system to the entire screen
		GL11.glViewport(0, 0, Globals.getWindowWidth(), Globals.getWindowHeight());
		
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
		
		Globals.setIdentityShader(new ShaderProgram("res/shader/identity.vert", "res/shader/identity.frag"));
		Component.DEFAULT_TEXT_SHADER = new ShaderProgram("res/shader/console_text.vert", "res/shader/console_text.frag");
		Component.DEFAULT_MAIN_SHADER = new ShaderProgram("res/shader/component.vert","res/shader/component.frag");
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
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glEnable(GL11.GL_BLEND);
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		/*
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
		
		int skipped = 0;
		
		if(!this.pause)
		{
			for(int i = 0; i < roidCount; i++)
			{
				roids[i].doTick();
			}
			
			for(int i = 0; i < riBracuCount; i++)
			{
				test[i].doTick();
			}
			
			for(int i = 0; i < StaticManager.shoots.size(); i++)
			{
				StaticManager.shoots.get(i).doTick();
			}
		}
		Matrix4x4 tmp = new Matrix4x4(camScale, 0.0f, 0.0f, 0.0f, 
				0.0f, camScale, 0.0f, 0.0f, 
				0.0f, 0.0f, 1.0f, 0.0f, 
				camX, camY, 0.0f, 1.0f);
		for(int i = 0; i < roidCount; i++)
		{
			Entity2D cur = roids[i];
			Vertex2[] tmpB = cur.getBounds();
			Vertex2[] bounds = new Vertex2[]{tmp.multiply(tmpB[0]).toVertex2(),
					tmp.multiply(tmpB[1]).toVertex2(),
					tmp.multiply(tmpB[2]).toVertex2(),
					tmp.multiply(tmpB[3]).toVertex2()};
			
			if(!Utility.isOutOfVisibleArea(this.visibleScreenBounds, bounds, camX, camY))
				cur.draw();
			else
				skipped++;
				
			cur = null;
			
		}
	
		for(int i = 0; i < riBracuCount; i++)
		{
			test[i].draw();
		}
		/*
		
		for(int i = 0; i < StaticManager.shoots.size(); i++)
		{
			StaticManager.shoots.get(i).draw();
		}
		
		if(this.playCredits )
		{
			c.doTick();
			c.draw();
		}
		
		
		if(this.debugOverlay)
		{
			this.skippedFO.setText("skipped: "+skipped);
			this.fpsFO.draw();
			this.camXFO.draw();
			this.camYFO.draw();
			this.camScaleFO.draw();
			
			this.skippedFO.draw();
		}
		
		StaticManager.CONSOLE.draw();*/
		Globals.getActiveFrame().doTick();
		Globals.getActiveFrame().draw();
		
		if(this.playCredits )
		{
			c.doTick();
			c.draw();
		}
		
		if(this.debugOverlay)
		{
			this.skippedFO.setText("skipped: "+0);
			this.fpsFO.draw();
			this.camXFO.draw();
			this.camYFO.draw();
			this.camScaleFO.draw();
			
			this.skippedFO.draw();
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
			System.err.println("ERROR - " + errorMessage + ": " + GLU.gluErrorString(errorValue));
			
			if (Display.isCreated()) Display.destroy();
			System.exit(-1);
		}
		
		errorValue = 0;
	}
	
	public void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			//fpsFO.setText("FPS: "+fps+"F");
			fps = 0;
			lastFPS += 1000;
		}
		fps++;
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

}
