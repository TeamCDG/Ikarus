package cdg.util;

import java.util.ArrayList;
import java.util.List;

import cdg.interfaces.IEntity;

public abstract class StaticManager {

	public static int SELECTION_SHADER_PROGRAM_ID = 0;
	public static int MENU_SHADER_PROGRAM_ID = 0;
	public static int TEXT_SHADER_PROGRAM_ID = 0;
	public static float[] CLEAR_COLOR = new float[]{0.0f,0.0f,0.1f,1.0f};
	public static int WINDOW_WIDTH = 1280;
	public static int WINDOW_HEIGHT = 720;
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
	
	
	
	
	
	public static List<IEntity> objects = new ArrayList<IEntity>();
	public static List<IEntity> shoots = new ArrayList<IEntity>();
	public static float delta;

}
