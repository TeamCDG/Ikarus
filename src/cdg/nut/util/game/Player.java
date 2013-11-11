package cdg.nut.util.game;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import cdg.nut.util.GLTexture;

public class Player {
	
	BufferedImage logo;
	HashMap<String, GLTexture> logoTextures;
	float[] color;

	public Player(float[] color)
	{
		this.color = color;
	}
	
	public void addLogo(String shipName, String logoPath)
	{
		
	}
	
	public GLTexture getLogo(String className)
	{
		return null;
	}

	public float[] getColor() {
		// TODO Auto-generated method stub
		return this.color;
	}
}
