package cdg.nut.util.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import cdg.nut.util.ImageUtils;

public abstract class PlanetoidCreator {

	public static final int SMOOTH_QUALITY_NEAREST_NEIGHBOUR = 0;
	public static final int SMOOTH_QUALITY_BILINIEAR = 1;
	public static final int SMOOTH_QUALITY_BICUBIC = 2;
	
	
	public static BufferedImage createPlanetoid(int size, int min, int max, int roundCount, int seed)
	{
		return PlanetoidCreator.createPlanetoid(size, min, max, roundCount, seed, false, -1, -1, -1);
	}
	
	public static BufferedImage generateRoid(int size, boolean smooth)
	{
		return PlanetoidCreator.createPlanetoid(size, (size/2)-(size/8), size/2, Math.round((1.0f/(float)size)*4000), -1, smooth, 17, PlanetoidCreator.SMOOTH_QUALITY_BICUBIC);
	}
	
	public static BufferedImage createPlanetoid(int size, int min, int max, int roundCount, int seed, boolean smooth, int smoothCount, int smoothQuality)
	{
		return PlanetoidCreator.createPlanetoid(size, min, max, roundCount, seed, smooth, smoothCount, -1, smoothQuality);
	}
	
	public static BufferedImage createPlanetoid(int size, int min, int max, int roundCount, int seed, boolean smooth, int smoothCount, int smoothMethod, int smoothQuality)
	{
		if(max > size/2)
			throw new IllegalArgumentException("max is bigger than size");
		if(min > size/2)
			throw new IllegalArgumentException("min is bigger than size");
		if(min > max)
			throw new IllegalArgumentException("min is bigger than max");
		
		BufferedImage planetoid = new BufferedImage(size, size,
                BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = planetoid.createGraphics();
		g.setColor(new Color(0, 0, 0, 0));
		g.fillRect(0, 0, size, size);
		g.setColor(Color.BLACK);
		
		Random r;		
		if(seed == 0 || seed == -1)
			r = new Random();
		else
			r = new Random(seed);
		
		int midX = size/2; int midY = size/2;
		int fX = midX + r.nextInt(max-min)+min;
		
		int lastX = -1;
		int lastY = -1;
		
		int val = roundCount;
		for(int i = val; i < 720; i+=val)
		{
			int d = r.nextInt(max-min)+min;
			int newX = midX + (int)(Math.sin(rad(i)) * d);
			int newY = midY + (int)(Math.cos(rad(i)) * d);
			
			int xP[];
			int yP[];
			if(i == val)
			{
				xP = new int[]{midX, fX, newX};
				yP = new int[]{midY, midY, newY};
			}
			else if(i == (720-val))
			{
				xP = new int[]{midX, lastX, fX};
				yP = new int[]{midY, lastY, midY};
			}
			else
			{
				xP = new int[]{midX, lastX, newX};
				yP = new int[]{midY, lastY, newY};
			}
			
			g.fillPolygon(xP, yP, 3);
			lastX = newX;
			lastY = newY;
		}
		
		if(smooth)
		{
			BufferedImage finalImg = new BufferedImage(size, size,
                BufferedImage.TYPE_INT_ARGB);
			Graphics2D fG = finalImg.createGraphics();
			fG.setColor(new Color(0, 0, 0, 0));
			fG.fillRect(0, 0, size, size);
			
			int alphaPerLayer = Math.round((255.0f/(smoothCount))*4f);			
			for(int x = 0; x < size; x++)
			{
				for(int y = 0; y < size; y++)
				{
					int[] argb = ImageUtils.getARGB(planetoid.getRGB(x, y));
					if(argb[0] != 0)
						planetoid.setRGB(x, y, ImageUtils.makeARGB(argb[1], argb[2], argb[3], alphaPerLayer));
					else
						planetoid.setRGB(x, y, ImageUtils.makeARGB(0, 0, 0, 0));
				}
			}
			
			Object q = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
			if(smoothQuality == 1)
			{
				q = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
			}
			else if(smoothQuality == 2)
			{
				q = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
			}
			
			
			for(int i = 0; i < smoothCount; i++)
			{
				BufferedImage tmp = ImageUtils.scaleExact(planetoid, size-(i*2), size-(i*2), q);
				fG.drawImage(tmp, i, i, size-(i*2), size-(i*2), null);					
			}
			
			planetoid = finalImg;
		}
		
		//System.out.println("Alpha: "+ImageUtils.getARGB(planetoid.getRGB(midX, midY))[0]);
		
		try {
			ImageIO.write(planetoid, "PNG", new File("test.png"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return planetoid;
	}
	

	public static float rad(float deg)
	{
		return (float) ((float)deg * (Math.PI/(float)180));
	}	
	

}
