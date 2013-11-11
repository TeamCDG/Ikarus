package cdg;

import java.util.ArrayList;
import java.util.List;

import cdg.interfaces.IEntity;
import cdg.nut.gui.Frame;
import cdg.nut.util.BitmapFont;
import cdg.nut.util.Matrix4x4;
import cdg.nut.util.ShaderProgram;
import cdg.nut.util.game.Entity2D;

public abstract class StaticManager {

	public static int SELECTION_SHADER_PROGRAM_ID = 0;
	public static int MENU_SHADER_PROGRAM_ID = 0;
	public static int TEXT_SHADER_PROGRAM_ID = 0;
	public static float[] CLEAR_COLOR = new float[]{0.0f,0.0f,0.0f,1.0f};
	public static int WINDOW_WIDTH = (1280/10)*9;
	public static int WINDOW_HEIGHT = (720/10)*9;
	public static float FOV = 60f;
	public static float ASPECT_RATIO = (float) WINDOW_WIDTH / (float) WINDOW_HEIGHT;
	public static Matrix4x4 WINDOW_MATRIX = new Matrix4x4(1/ASPECT_RATIO, 0.0f, 0.0f, 0.0f,
			  											  0.0f, 1.0f, 0.0f, 0.0f,
														  0.0f, 0.0f, 1.0f, 0.0f,
														  0.0f, 0.0f, 0.0f, 1.0f);
	public static int FONT_TEXTURE_ID = 0;
	public static int FONT_TEXTURE_UNIFORM_ID = 1337;
	public static int FONT_SCALING_MATRIX_UNIFORM_ID = 1337;
	public static int TEXT_POSITION_UNIFORM_ID = 1337;
	public static int CREDITS_PROGRAM_ID = 1337;
	public static int SPLASH_TEXTURE_ID = 1337;
	public static int MAIN_MENU_BACKGROUND_TEXTURE_ID = 1337;
	public static int SHIP_TEXTURE_ID = 1337;
	
	public static ShaderProgram DEFAULT_SHADER;
	
	public static Matrix4x4 CAMERA_MATRIX = new Matrix4x4(1.0f, 0.0f, 0.0f, 0.0f,
			  0.0f, 1.0f, 0.0f, 0.0f,
			  0.0f, 0.0f, 1.0f, 0.0f,
			  0.0f, 0.0f, 0.0f, 1.0f);
	
	public static List<Entity2D> objects = new ArrayList<Entity2D>();
	public static List<Entity2D> shoots = new ArrayList<Entity2D>();
	public static float delta = 0;
	public static Matrix4x4 VISIBLE_TRANSLATION_MATRIX = Matrix4x4.getIdentity();
	public static BitmapFont DEFAULT_FONT;
	public static GLConsole CONSOLE;
	public static Frame ACTIVE_FRAME;
}
