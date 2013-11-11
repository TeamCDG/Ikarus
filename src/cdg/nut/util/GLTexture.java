package cdg.nut.util;

import java.awt.image.BufferedImage;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class GLTexture 
{
	
	private int textureId;
	private int textureUnit;
	
	public GLTexture(String filename, int textureUnit)
	{
		this(filename, textureUnit, false);
	}
	
	public GLTexture(String filename, int textureUnit, boolean smooth)
	{
		if(!smooth)
			this.textureId = Utility.loadPNGTexture(filename, textureUnit);
		else
			this.textureId = Utility.loadPNGTextureSmooth(filename, textureUnit);
		
		this.textureUnit = textureUnit;
	}
	
	public GLTexture(BufferedImage img, int textureUnit)
	{
		this(img, textureUnit, false);
	}
	
	public GLTexture(BufferedImage img, int textureUnit, boolean smooth)
	{
		if(!smooth)
			this.textureId = Utility.loadBufferdImage(img, textureUnit);
		else
			this.textureId = Utility.loadBufferdImageSmooth(img, textureUnit);
		
		this.textureUnit = textureUnit;
	}
	
	public int getTextureId()
	{
		return this.textureId;
	}
	
	public int getTextureUnit()
	{
		return this.textureUnit;
	}
	
	public void bind()
	{
		GL13.glActiveTexture(this.textureUnit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureId);
	}
	
	public void unbind()
	{
		GL13.glActiveTexture(this.textureUnit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
}
