package cdg.nut.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

import org.lwjgl.opengl.GL13;

public class BitmapFont 
{
	private ArrayList<FontChar> font;
	private int fontTextureId;
	private String fontName;
	private float staticHeight;
	private float fontSpace = 0.005f;
	
	public BitmapFont(String fontInfoFilePath) throws IOException
	{
		File f = new File(fontInfoFilePath);
		this.font = new ArrayList<FontChar>(144);
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String line = reader.readLine();
		this.fontName = line.split(";")[0];
		this.staticHeight = Float.valueOf(line.split(";")[4]);
		while((line = reader.readLine()) != null)
		{
			font.add(new FontChar(line));
		}
		reader.close();
		
		this.fontTextureId = Utility.loadPNGTextureSmooth((f.getPath().replace(f.getName(),""))+"\\"+this.fontName+".png", GL13.GL_TEXTURE0);
		
		this.font.add(new FontChar(' ', 0, 0, this.getChar('A').getWidth(), 0));
	}
	
	public BitmapFont(String fontInfoFilePath, String imagePath) throws IOException
	{
		File f = new File(fontInfoFilePath);
		this.font = new ArrayList<FontChar>(144);
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String line = reader.readLine();
		this.fontName = line.split(";")[0];
		this.staticHeight = Float.valueOf(line.split(";")[4]);
		while((line = reader.readLine()) != null)
		{
			font.add(new FontChar(line));
		}
		reader.close();
		
		this.fontTextureId = Utility.loadPNGTextureSmooth(imagePath, GL13.GL_TEXTURE0);
		
		this.font.add(new FontChar(' ', 0, 0, this.getChar('A').getWidth(), 0));
	}
	
	public FontChar getChar(char c)
	{
		for(int i = 0; i < font.size(); i++)
		{
			if(font.get(i).getC() == c)
				return font.get(i);
		}
		return new FontChar(c, 0, 0, 0, 0);		
	}
	
	public int getFontTextureID()
	{
		return this.fontTextureId;
	}
	
	public float getX(char c)
	{
		return getChar(c).getX();
	}
	
	public float getY(char c)
	{
		return getChar(c).getY();
	}
	
	public float getWidth(char c)
	{
		return getChar(c).getWidth()+fontSpace;
	}
	
	public float getHeight(char c)
	{
		return getChar(c).getHeight();
	}

	/**
	 * @return the fontName
	 */
	public String getFontName() {
		return fontName;
	}

	/**
	 * @return the staticHeight
	 */
	public float getStaticHeight() {
		return staticHeight;
	}

}
