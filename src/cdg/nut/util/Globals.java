package cdg.nut.util;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import cdg.interfaces.IWindowChangeListener;
import cdg.nut.gui.Frame;

public abstract class Globals {
	
	private static Matrix4x4 windowMatrix = Matrix4x4.getIdentity();
	
	//---- REGION VERSION -----
	private static int majorVersion = 0;
	private static int minorVersion = 0;
	private static int buildVersion = 21;
	private static String versionIdentifier = "a";
	//-------------------------
	
	//---- REGION WINDOW -----
	private static int windowHeight = 1;
	private static int windowWidth = 1;
	private static float aspectRatio;
	private static String windowTitle;
	private static boolean fullscreen = false;
	private static ArrayList<IWindowChangeListener> windowChangeListener = new ArrayList<IWindowChangeListener>();
	//------------------------
	
	//---- REGION SHADER -----
	private static ShaderProgram defaultTextShader;
	private static ShaderProgram identityShader;
	//------------------------
	
	//---- REGION FRAMES -----
	private static Frame activeFrame;
	private static HashMap<String, Frame> frames = new HashMap<String, Frame>();
	//------------------------
	
	//---- REGION FONTS -----
	private static BitmapFont defaultTextFont;
	private static HashMap<String, BitmapFont> avaibleFonts = new HashMap<String, BitmapFont>();
	//-----------------------
	
	public static void setWindowMatrix(Matrix4x4 mat)
	{
		Globals.windowMatrix = mat;
	}
	
	public static Matrix4x4 getWindowMatrix()
	{
		return Globals.windowMatrix;
	}

	/**
	 * @return the windowTitle
	 */
	public static String getWindowTitle() {
		return windowTitle;
	}

	/**
	 * @param windowTitle the windowTitle to set
	 */
	public static void setWindowTitle(String windowTitle) {
		Globals.windowTitle = windowTitle;
		Display.setTitle(Globals.windowTitle + " - " + Globals.majorVersion + "." + Globals.minorVersion +
						 "." + Globals.buildVersion+Globals.versionIdentifier);
							
	}

	/**
	 * @return the windowHeight
	 */
	public static int getWindowHeight() {
		return windowHeight;
	}

	/**
	 * @param windowHeight the windowHeight to set
	 */
	public static void setWindowHeight(int windowHeight) {
		Globals.setWindowResolution(Globals.windowWidth, windowHeight);
	}

	/**
	 * @return the windowWidth
	 */
	public static int getWindowWidth() {
		return windowWidth;
	}

	/**
	 * @param windowWidth the windowWidth to set
	 */
	public static void setWindowWidth(int windowWidth) {
		Globals.setWindowResolution(windowWidth, Globals.windowHeight);
	}
	
	public static void toggleFullscreen()
	{
		Globals.fullscreen = !Globals.fullscreen;
		try {
			Display.setFullscreen(Globals.fullscreen);
			Display.update();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	public static void setWindowResolution(int width, int height){
		Globals.windowWidth = width;
		Globals.windowHeight = height;
		Globals.aspectRatio = (float) Globals.windowWidth / (float) Globals.windowHeight;
		Globals.windowMatrix.set(1/Globals.aspectRatio, 0.0f, 0.0f, 0.0f,
												  0.0f, 1.0f, 0.0f, 0.0f,
												  0.0f, 0.0f, 1.0f, 0.0f,
												  0.0f, 0.0f, 0.0f, 1.0f);
		
		for(IWindowChangeListener lis : Globals.windowChangeListener)
		{
			lis.onWindowResolutionChange(Globals.windowWidth, Globals.windowHeight);
		}
		
		try {
			DisplayMode[] modes = Display.getAvailableDisplayModes();
			
			DisplayMode finalMode = new DisplayMode(Globals.getWindowWidth(), Globals.getWindowHeight());
			
			for (int i=0;i<modes.length;i++) {
			    DisplayMode current = modes[i];
			    if(current.getWidth() == Globals.getWindowWidth()
			       && current.getHeight() == Globals.getWindowHeight()
			       && current.getBitsPerPixel() == 32
			       && current.getFrequency() == 60)
			    finalMode = current;
			}
			
			Display.setDisplayMode(finalMode);
			
			if(!Display.isCreated())
			{
				PixelFormat pixelFormat = new PixelFormat();
				ContextAttribs contextAtrributes = new ContextAttribs(3, 2)
					.withForwardCompatible(true)
					.withProfileCore(true);
				Display.create(pixelFormat, contextAtrributes);
			}
			
			GL11.glViewport(0, 0, Globals.getWindowWidth(), Globals.getWindowHeight());
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		Display.update();
	}
	/**
	 * @return the aspectRatio
	 */
	public static float getAspectRatio() {
		return aspectRatio;
	}

	public static ShaderProgram getIdentityShader() {
		return Globals.identityShader;
	}
	
	/**
	 * @param identityShader the identityShader to set
	 */
	public static void setIdentityShader(ShaderProgram identityShader) {
		Globals.identityShader = identityShader;
	}

	/**
	 * @return the defaultTextShader
	 */
	public static ShaderProgram getDefaultTextShader() {
		return Globals.defaultTextShader;
	}

	/**
	 * @param defaultTextShader the defaultTextShader to set
	 */
	public static void setDefaultTextShader(ShaderProgram defaultTextShader) {
		Globals.defaultTextShader = defaultTextShader;
	}

	public static void setActiveFrame(Frame frame) {
		Globals.activeFrame = frame;
		if(!Globals.frames.containsValue(frame) && frame.getTitle() != null)
		{
			Globals.frames.put(frame.getTitle(), frame);
		}
		
	}
	
	public static void setActiveFrame(String identifier) {
		Globals.activeFrame = Globals.frames.get(identifier);
		
	}
	
	public static Frame getActiveFrame()
	{
		return Globals.activeFrame;
	}
	
	public static void addFrame(String identifier, Frame f)
	{
		Globals.frames.put(identifier, f);
		if(Globals.activeFrame == null)
		{
			Globals.activeFrame = f;
		}
	}
	
	public static HashMap<String, Frame> getAllFrames()
	{
		return Globals.frames;
	}

	public static Frame getFrame(String identifier)
	{
		return Globals.frames.get(identifier);
	}
	
	public static void resetFrame(String identifier, Frame f)
	{
		Globals.frames.remove(identifier);
		Globals.addFrame(identifier, f);
	}

	/**
	 * @return the defaultTextFont
	 */
	public static BitmapFont getDefaultTextFont() {
		return defaultTextFont;
	}

	/**
	 * @param defaultTextFont the defaultTextFont to set
	 */
	public static void setDefaultTextFont(BitmapFont defaultTextFont) {
		Globals.defaultTextFont = defaultTextFont;
	}

	/**
	 * @return the avaibleFonts
	 */
	public static HashMap<String, BitmapFont> getAvaibleFonts() {
		return avaibleFonts;
	}

	/**
	 * @param avaibleFonts the avaibleFonts to set
	 */
	public static void addFont(BitmapFont f) {
		Globals.avaibleFonts.put(f.getFontName(), f);
	}

	public static void setDefaultTextFont(String string) {
		Globals.defaultTextFont = Globals.avaibleFonts.get(string);
		
	}
	
	public static BitmapFont getFont(String name)
	{
		return Globals.avaibleFonts.get(name);
	}

}
