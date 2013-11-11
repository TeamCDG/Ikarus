package cdg.nut.util;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import cdg.nut.util.game.Entity2D;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public abstract class Utility 
{
	
	public static HashMap<String, String> loadInfoTxt(String filename)
	{
		HashMap<String, String> values = new HashMap<String, String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    try {
	        String line = br.readLine();

	        while (line != null) {
	        	String[] sp = line.split(":");
	        	values.put(sp[0], sp[1]);
	            line = br.readLine();
	        }
	        
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	        try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
		return values;
	}
	
	public static final float GL_COLOR_PER_BIT = 0.00390625f;
	
	public static float[] idToGlColor(long l, boolean useAlpha)
	{
		byte[] val = new byte[4];

		val[0] = (byte) (l >> 24);
		val[1] = (byte) (l >> 16);
		val[2] = (byte) (l >> 8);
		val[3] = (byte) (l >> 0);
		
		float[] col;
		if(useAlpha)
			col = new float[] { (float)val[3]*GL_COLOR_PER_BIT,
								(float)val[2]*GL_COLOR_PER_BIT,
								(float)val[1]*GL_COLOR_PER_BIT,
								(float)val[0]*GL_COLOR_PER_BIT};
		else
			col = new float[] { (float)val[3]*GL_COLOR_PER_BIT,
								(float)val[2]*GL_COLOR_PER_BIT,
								(float)val[1]*GL_COLOR_PER_BIT,
								1.0f};
		
		return col;
	}
	
	public static int glColorToId(byte[] color, boolean useAlpha)
	{
		if(useAlpha)
		{
			byte[] fin = new byte[]{color[0], color[1], color[2], color[3]};
			
			return   fin[0] & 0xFF |
		            (fin[1] & 0xFF) << 8 |
		            (fin[2] & 0xFF) << 16 |
		            (fin[3] & 0xFF) << 24;
		}
		else
		{
			byte[] fin = new byte[]{color[0], color[1], color[2]};
			
			return   fin[0] & 0xFF |
		            (fin[1] & 0xFF) << 8 |
		            (fin[2] & 0xFF) << 16|
		            (0 & 0xFF) << 24;
		}
		
	}
	
	@SuppressWarnings("deprecation")
	public static int loadShader(String filename, int type) 
	{
		StringBuilder shaderSource = new StringBuilder();
		int shaderID = 0;
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Could not read file.");
			e.printStackTrace();
			System.exit(-1);
		}
		
		shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		
		if (GL20.glGetShader(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.err.println("Could not compile shader.");
			System.exit(-1);
		}
		
		return shaderID;
	}
	
	public static int loadPNGTexture(String filename, int textureUnit) 
	{
		ByteBuffer buf = null;
		int tWidth = 0;
		int tHeight = 0;
		
		try {
			// Open the PNG file as an InputStream
			InputStream in = new FileInputStream(filename);
			// Link the PNG decoder to this stream
			PNGDecoder decoder = new PNGDecoder(in);
			
			// Get the width and height of the texture
			tWidth = decoder.getWidth();
			tHeight = decoder.getHeight();
			
			
			// Decode the PNG file in a ByteBuffer
			buf = ByteBuffer.allocateDirect(
					4 * decoder.getWidth() * decoder.getHeight());
			decoder.decode(buf, decoder.getWidth() * 4, Format.RGBA);
			buf.flip();
			
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		// Create a new texture object in memory and bind it
		int texId = GL11.glGenTextures();
		GL13.glActiveTexture(textureUnit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
		
		// All RGB bytes are aligned to each other and each component is 1 byte
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		
		// Upload the texture data and generate mip maps (for scaling)
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, tWidth, tHeight, 0, 
				GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		
		// Setup the ST coordinate system
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		
		// Setup what to do when the texture has to be scaled
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, 
				GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, 
				GL11.GL_LINEAR);
		
		return texId;
	}
	
	public static int loadPNGTextureSmooth(String filename, int textureUnit) 
	{
		ByteBuffer buf = null;
		int tWidth = 0;
		int tHeight = 0;
		
		try {
			// Open the PNG file as an InputStream
			InputStream in = new FileInputStream(filename);
			// Link the PNG decoder to this stream
			PNGDecoder decoder = new PNGDecoder(in);
			
			// Get the width and height of the texture
			tWidth = decoder.getWidth();
			tHeight = decoder.getHeight();
			
			
			// Decode the PNG file in a ByteBuffer
			buf = ByteBuffer.allocateDirect(
					4 * decoder.getWidth() * decoder.getHeight());
			decoder.decode(buf, decoder.getWidth() * 4, Format.RGBA);
			buf.flip();
			
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		// Create a new texture object in memory and bind it
		int texId = GL11.glGenTextures();
		GL13.glActiveTexture(textureUnit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
		
		// All RGB bytes are aligned to each other and each component is 1 byte
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		
		// Upload the texture data and generate mip maps (for scaling)
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, tWidth, tHeight, 0, 
				GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		
		// Setup the ST coordinate system
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		
		// Setup what to do when the texture has to be scaled
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, 
				GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, 
				GL11.GL_LINEAR_MIPMAP_LINEAR);
		
		return texId;
	}
	
	public static int loadBufferdImage(BufferedImage img, int textureUnit)
	{
	    ByteBuffer buf = BufferUtils.createByteBuffer(img.getWidth() * img.getHeight() * 4); //4 for RGBA, 3 for RGB
	    
	    for(int y = 0; y < img.getHeight(); y++) {
	    	for(int x = 0; x < img.getWidth(); x++) {
	    		
	    		int[] argb = ImageUtils.getARGB(img.getRGB(x, y));
	            buf.put((byte) argb[1]);    // Red component
	            buf.put((byte) argb[2]);    // Green component
	            buf.put((byte) argb[3]);	// Blue component
	            buf.put((byte) argb[0]);    // Alpha component. Only for RGBA
	        }
	    }

	    buf.flip();
		
		// Create a new texture object in memory and bind it
		int texId = GL11.glGenTextures();
		GL13.glActiveTexture(textureUnit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
		
		// All RGB bytes are aligned to each other and each component is 1 byte
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		
		// Upload the texture data and generate mip maps (for scaling)
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, img.getWidth(), img.getHeight(), 0, 
				GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		
		// Setup the ST coordinate system
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		
		// Setup what to do when the texture has to be scaled
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, 
				GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, 
				GL11.GL_LINEAR);
		
		return texId;
	}
	
	public static int loadBufferdImageSmooth(BufferedImage img, int textureUnit)
	{
	    ByteBuffer buf = BufferUtils.createByteBuffer(img.getWidth() * img.getHeight() * 4); //4 for RGBA, 3 for RGB
	    
	    for(int y = 0; y < img.getHeight(); y++) {
	    	for(int x = 0; x < img.getWidth(); x++) {
	    		
	    		int[] argb = ImageUtils.getARGB(img.getRGB(x, y));
	            buf.put((byte) argb[1]);    // Red component
	            buf.put((byte) argb[2]);    // Green component
	            buf.put((byte) argb[3]);	// Blue component
	            buf.put((byte) argb[0]);    // Alpha component. Only for RGBA
	        }
	    }

	    buf.flip();
		
		// Create a new texture object in memory and bind it
		int texId = GL11.glGenTextures();
		GL13.glActiveTexture(textureUnit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
		
		// All RGB bytes are aligned to each other and each component is 1 byte
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		
		// Upload the texture data and generate mip maps (for scaling)
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, img.getWidth(), img.getHeight(), 0, 
				GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		
		// Setup the ST coordinate system
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		
		// Setup what to do when the texture has to be scaled
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, 
				GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, 
				GL11.GL_LINEAR_MIPMAP_NEAREST);
		
		return texId;
	}
	
	public static void printFloatArray(float[] f)
	{
		for(int i = 0; i < f.length; i++)
		{
			System.out.print(f[i]+"; ");
		}
		System.out.print("\n");
		System.out.flush();
	}
	
	public static float degToRad(float deg)
	{
		return deg*((float)Math.PI/180.0f);
	}
	
	public static Vertex2 mouseTo2DGL(int x, int y, int screenWidth, int screenHeight)
	{
		float cX = 2.0f/screenWidth;
		float cY = 2.0f/screenHeight;
		
		float glX = x * cX - 1.0f;
		float glY = y * cY -1.0f;
				
		return new Vertex2(glX, glY);
	}
	
	public static boolean isOutOfVisibleArea(Vertex2[] visibleArea, Vertex2[] bounds, float cx, float cy)
	{
		
		for(int i = 0; i < bounds.length; i++)
		{
			bounds[i] = new Vertex2(bounds[i].getX()+cx, bounds[i].getY()+cy);
		}
		
		/*
		System.out.println(visibleArea[0].getX() + ">" + bounds[2].getX() + "|" +
						   visibleArea[0].getY() + "<" + bounds[2].getY() + ", " + 
						   (bounds[Entity2D.BOUNDS_BOT_RIGHT_EDGE].getX() < visibleArea[Entity2D.BOUNDS_TOP_LEFT_EDGE].getX() ||
							bounds[Entity2D.BOUNDS_BOT_RIGHT_EDGE].getY() > visibleArea[Entity2D.BOUNDS_TOP_LEFT_EDGE].getY())
						   + "; " +
				
							visibleArea[1].getX() + ">" + bounds[3].getX() + "|" +
							visibleArea[1].getY() + ">" + bounds[3].getY() + ", " +
						   (bounds[Entity2D.BOUNDS_TOP_RIGHT_EDGE].getX() < visibleArea[Entity2D.BOUNDS_BOT_LEFT_EDGE].getX() ||
						   bounds[Entity2D.BOUNDS_TOP_RIGHT_EDGE].getY() < visibleArea[Entity2D.BOUNDS_BOT_LEFT_EDGE].getY())
						   + "; " +
						   
							visibleArea[2].getX() + "<" + bounds[0].getX() + "|" +
							visibleArea[2].getY() + ">" + bounds[0].getY() + ", " + 
						   (bounds[Entity2D.BOUNDS_TOP_LEFT_EDGE].getX() > visibleArea[Entity2D.BOUNDS_BOT_RIGHT_EDGE].getX() ||
							bounds[Entity2D.BOUNDS_TOP_LEFT_EDGE].getY() < visibleArea[Entity2D.BOUNDS_BOT_RIGHT_EDGE].getY())
							+ "; " +
						   
							visibleArea[3].getX() + "<" + bounds[1].getX() + "|" +
							visibleArea[3].getY() + "<" + bounds[1].getY() + ", " +
						   (bounds[Entity2D.BOUNDS_BOT_LEFT_EDGE].getX() > visibleArea[Entity2D.BOUNDS_TOP_RIGHT_EDGE].getX() ||
							bounds[Entity2D.BOUNDS_BOT_LEFT_EDGE].getY() > visibleArea[Entity2D.BOUNDS_TOP_RIGHT_EDGE].getY())
						   + "; ");
		*/
		
		
		if((bounds[Entity2D.BOUNDS_BOT_RIGHT_EDGE].getX() < visibleArea[Entity2D.BOUNDS_TOP_LEFT_EDGE].getX() ||
		   bounds[Entity2D.BOUNDS_BOT_RIGHT_EDGE].getY() > visibleArea[Entity2D.BOUNDS_TOP_LEFT_EDGE].getY())
		   
		   ||
		   
		   (bounds[Entity2D.BOUNDS_TOP_LEFT_EDGE].getX() > visibleArea[Entity2D.BOUNDS_BOT_RIGHT_EDGE].getX() ||
		   bounds[Entity2D.BOUNDS_TOP_LEFT_EDGE].getY() < visibleArea[Entity2D.BOUNDS_BOT_RIGHT_EDGE].getY())

		   ||
		   
		   (bounds[Entity2D.BOUNDS_BOT_LEFT_EDGE].getX() > visibleArea[Entity2D.BOUNDS_TOP_RIGHT_EDGE].getX() ||
		   bounds[Entity2D.BOUNDS_BOT_LEFT_EDGE].getY() > visibleArea[Entity2D.BOUNDS_TOP_RIGHT_EDGE].getY())

		   ||
		   
		   (bounds[Entity2D.BOUNDS_TOP_RIGHT_EDGE].getX() < visibleArea[Entity2D.BOUNDS_BOT_LEFT_EDGE].getX() ||
		   bounds[Entity2D.BOUNDS_TOP_RIGHT_EDGE].getY() < visibleArea[Entity2D.BOUNDS_BOT_LEFT_EDGE].getY()))
			
			return true;
		else
			return false;
	}

	public static ArrayList<Integer> filterIds(ByteBuffer pixel) {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		//byte[] px = pixel.array();
		for(int i = 0; i < pixel.limit(); i+=4)
		{
			int pId = Utility.glColorToId(new byte[]{pixel.get(i), pixel.get(i+1), pixel.get(i+2), pixel.get(i+3)}, false);
			if(pId != 0)
			{
				ids.add(pId);
			}
		}
		
		//if(ids.size() == 0)
		//{
		//	return null;
		//}
		
		return ids;
	}
	
	public static boolean between(float x, float v1, float v2)
	{
		return x >= Math.min(v1, v2) && x <= Math.max(v1, v2);
	}
	
	public static boolean lineLineIntersect2D(Vertex2 p1, Vertex2 p2, Vertex2 p3, Vertex2 p4)
	{
		float xDif1 = p1.getX() - p2.getX();
		float yDif1 = p1.getY() - p2.getY();
		float xDif2 = p3.getX() - p4.getX();
		float yDif2 = p3.getY() - p4.getY();
		
		if(xDif1 == 0 && xDif2 == 0)
			return p1.getX() == p3.getX();
		else if(xDif1 == 0 && xDif2 != 0)
		{
			float m2 = yDif2 / xDif2;
			float n2 = p3.getY() - (m2 * p3.getX());
			float ty = m2 * p1.getX() + n2;
			return Utility.between(ty, p1.getY(), p2.getY()) && 
				   Utility.between(p1.getX(), p3.getX(), p4.getX());
		}
		else if(xDif2 == 0 && xDif1 != 0)
		{
			float m1 = yDif1 / xDif1;
			float n1 = p1.getY() - (m1 * p1.getX());
			float ty = m1 * p3.getX() + n1;
			return Utility.between(ty, p3.getY(), p4.getY()) && 
				   Utility.between(p3.getX(), p1.getX(), p2.getX());
		}
		else
		{
			float m1 = yDif1 / xDif1;
			float m2 = yDif2 / xDif2;
			float n1 = p1.getY() - (m1 * p1.getX());
			float n2 = p3.getY() - (m2 * p3.getX());
			
			if(m1 == m2)
				return n1 == n2;
			
			float xCol = (n1-n2)/(m2-m1);
			//float yCol = m1 * xCol + n1;
			
			return Utility.between(xCol, p1.getX(), p2.getX()) && 
				   Utility.between(xCol, p3.getX(), p4.getX());
		}
	}
	
	public static boolean lineCircleIntersect2D(Vertex2 p1, Vertex2 p2, Vertex2 m, float r)
	{
		Vertex2 p1s = new Vertex2(p1.getX() - m.getX(), p1.getY()-m.getY());
		Vertex2 p2s = new Vertex2(p2.getX() - m.getX(), p2.getY()-m.getY());
		
		float dx = p2s.getX() - p1s.getX();
		float dy = p2s.getY() - p1s.getY();
		float dr = (float) Math.sqrt(Math.pow(dx,2)+ Math.pow(dy, 2));
		float D = (p1s.getX() * p2s.getY()) - (p2s.getX() * p1s.getY());
		
		return ((Math.pow(r, 2) * Math.pow(dr, 2))-Math.pow(D, 2)) >= 0;
	}
	
	public static boolean circleCircleIntersect2D(Vertex2 m1, float r1, Vertex2 m2, float r2)
	{
		float dx = m2.getX() - m1.getX();
		float dy = m2.getY() - m1.getY();
		
		return Math.sqrt(Math.pow(dx,2)+ Math.pow(dy, 2)) <= (r1 + r2);
	}
}
