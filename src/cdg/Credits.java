package cdg;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cdg.util.ShaderProgram;
import cdg.util.StaticManager;
import cdg.util.Vertex2;

public class Credits 
{
	private	List<RiBracu> cShips = new ArrayList<RiBracu>();
	
	private	List<RiBracu> lShips = new ArrayList<RiBracu>();
	
	private	List<RiBracu> cdShips = new ArrayList<RiBracu>();
	
	private	List<RiBracu> rShips = new ArrayList<RiBracu>();
	
	private	List<RiBracu> hShips = new ArrayList<RiBracu>();
	private int selector = 0;
	private int cd = 1000;
	private ShaderProgram cShader = new ShaderProgram("res/shader/ribracu.vert", "res/shader/credits.frag");
	
	
	private Vertex2[] credits = new Vertex2[]{
			new Vertex2(-1.4f, 0.9f),
			new Vertex2(-1.4f, 0.7f),
			new Vertex2(-1.4f, 0.5f),
			new Vertex2(-1.4f, 0.3f),
			new Vertex2(-1.4f, 0.1f),
			
			new Vertex2(-1.2f, 0.9f),
			new Vertex2(-1.2f, 0.1f),
			
			new Vertex2(-1.0f, 0.9f),
			new Vertex2(-1.0f, 0.1f),
			
			new Vertex2(-0.8f, -0.1f),
			new Vertex2(-0.8f, -0.3f),
			new Vertex2(-0.8f, -0.5f),
			new Vertex2(-0.8f, -0.7f),
			new Vertex2(-0.8f, -0.9f),
			
			new Vertex2(-0.6f, 0.9f),
			new Vertex2(-0.6f, 0.7f),
			new Vertex2(-0.6f, 0.5f),
			new Vertex2(-0.6f, 0.3f),
			new Vertex2(-0.6f, 0.1f),
			
			new Vertex2(-0.4f, 0.9f),
			new Vertex2(-0.4f, 0.5f),
			new Vertex2(-0.4f, -0.1f),
			
			new Vertex2(-0.2f, 0.7f),
			new Vertex2(-0.2f, 0.3f),
			new Vertex2(-0.2f, 0.1f),
			new Vertex2(-0.2f, -0.1f),
			new Vertex2(-0.2f, -0.3f),
			new Vertex2(-0.2f, -0.5f),
			new Vertex2(-0.2f, -0.7f),
			new Vertex2(-0.2f, -0.9f),
			
			new Vertex2(0.0f, -0.1f),
			
			new Vertex2(0.2f, 0.9f),
			new Vertex2(0.2f, 0.7f),
			new Vertex2(0.2f, 0.5f),
			new Vertex2(0.2f, 0.3f),
			new Vertex2(0.2f, 0.1f),
			
			new Vertex2(0.4f, 0.9f),
			new Vertex2(0.4f, 0.5f),
			new Vertex2(0.4f, 0.1f),
			new Vertex2(0.4f, -0.1f),
			new Vertex2(0.4f, -0.3f),
			new Vertex2(0.4f, -0.5f),
			new Vertex2(0.4f, -0.9f),
			
			new Vertex2(0.6f, 0.9f),
			new Vertex2(0.6f, 0.1f),
			new Vertex2(0.6f, -0.1f),
			new Vertex2(0.6f, -0.5f),
			new Vertex2(0.6f, -0.9f),
			
			new Vertex2(0.8f, -0.1f),
			new Vertex2(0.8f, -0.5f),
			new Vertex2(0.8f, -0.7f),
			new Vertex2(0.8f, -0.9f),
			
			new Vertex2(1.0f, 0.9f),
			new Vertex2(1.0f, 0.7f),
			new Vertex2(1.0f, 0.5f),
			new Vertex2(1.0f, 0.3f),
			new Vertex2(1.0f, 0.1f),
			
			new Vertex2(1.2f, 0.9f),
			new Vertex2(1.2f, 0.1f),
			
			new Vertex2(1.4f, 0.7f),
			new Vertex2(1.4f, 0.5f),
			new Vertex2(1.4f, 0.3f)
	};
	
	private Vertex2[] logo = new Vertex2[]{

			new Vertex2(-1f, 0.1f),
			new Vertex2(-1f, -0.1f),
			
			new Vertex2(-0.8f, 0.5f),
			new Vertex2(-0.8f, 0.3f),
			new Vertex2(-0.8f, -0.1f),
			new Vertex2(-0.8f, -0.3f),
			new Vertex2(-0.8f, -0.5f),
			
			new Vertex2(-0.6f, 0.7f),
			new Vertex2(-0.6f, -0.1f),
			new Vertex2(-0.6f, -0.7f),
			
			new Vertex2(-0.4f, 0.7f),
			new Vertex2(-0.4f, -0.1f),
			new Vertex2(-0.4f, -0.7f),
			
			new Vertex2(-0.2f, 0.9f),
			new Vertex2(-0.2f, -0.1f),
			new Vertex2(-0.2f, -0.9f),
			
			new Vertex2(0.0f, 0.9f),
			new Vertex2(0.0f, 0.7f),
			new Vertex2(0.0f, 0.5f),
			new Vertex2(0.0f, 0.3f),
			new Vertex2(0.0f, 0.1f),
			new Vertex2(0.0f, -0.1f),
			new Vertex2(0.0f, -0.9f),
			
			new Vertex2(0.2f, 0.9f),
			new Vertex2(0.2f, -0.1f),
			new Vertex2(0.2f, -0.9f),
			
			new Vertex2(0.4f, 0.7f),
			new Vertex2(0.4f, -0.1f),
			new Vertex2(0.4f, -0.7f),
			
			new Vertex2(0.6f, 0.7f),
			new Vertex2(0.6f, -0.1f),
			new Vertex2(0.6f, -0.7f),
			
			new Vertex2(0.8f, 0.5f),
			new Vertex2(0.8f, 0.3f),
			new Vertex2(0.8f, -0.1f),
			new Vertex2(0.8f, -0.3f),
			new Vertex2(0.8f, -0.5f),
			
			new Vertex2(1.0f, 0.1f),
			new Vertex2(1.0f, -0.1f)
	};
	
	private Vertex2[] cdg = new Vertex2[]{

		
			new Vertex2(-1f, 0.4f),
			new Vertex2(-1f, 0.2f),
			new Vertex2(-1f, 0.0f),
			new Vertex2(-1f, -0.2f),
			new Vertex2(-1f, -0.4f),
			
			new Vertex2(-0.8f, 0.4f),
			new Vertex2(-0.8f, -0.4f),
			
			new Vertex2(-0.6f, 0.4f),
			new Vertex2(-0.6f, -0.4f),
			
			new Vertex2(-0.2f, 0.4f),
			new Vertex2(-0.2f, 0.2f),
			new Vertex2(-0.2f, 0.0f),
			new Vertex2(-0.2f, -0.2f),
			new Vertex2(-0.2f, -0.4f),
			
			new Vertex2(0.0f, 0.4f),
			new Vertex2(0.0f, -0.4f),
			
			new Vertex2(0.2f, 0.2f),
			new Vertex2(0.2f, 0.0f),
			new Vertex2(0.2f, -0.2f),
			
			new Vertex2(0.6f, 0.4f),
			new Vertex2(0.6f, 0.2f),
			new Vertex2(0.6f, 0.0f),
			new Vertex2(0.6f, -0.2f),
			new Vertex2(0.6f, -0.4f),
			
			new Vertex2(0.8f, 0.4f),
			new Vertex2(0.8f, 0.0f),
			new Vertex2(0.8f, -0.4f),
			
			new Vertex2(1.0f, 0.4f),
			new Vertex2(1.0f, 0.0f),
			new Vertex2(1.0f, -0.2f),
			new Vertex2(1.0f, -0.4f)
	};
	
	private Vertex2[] heart = new Vertex2[]{

			new Vertex2(-0.8f, -0.1f),
			new Vertex2(-0.8f, -0.3f),
			
			new Vertex2(-0.6f, 0.1f),
			new Vertex2(-0.6f, -0.5f),
			
			new Vertex2(-0.4f, 0.1f),
			new Vertex2(-0.4f, -0.7f),
			
			new Vertex2(-0.2f, -0.1f),
			new Vertex2(-0.2f, -0.9f),
			
			new Vertex2(0.0f, 0.1f),
			new Vertex2(0.0f, -0.7f),
			
			new Vertex2(0.2f, 0.1f),
			new Vertex2(0.2f, -0.5f),
			
			new Vertex2(0.4f, -0.1f),
			new Vertex2(0.4f, -0.3f)
	};
	
	private Vertex2[] ricy = new Vertex2[]{

			
			new Vertex2(-1.6f, 0.9f),
			new Vertex2(-1.6f, 0.7f),
			new Vertex2(-1.6f, 0.5f),
			new Vertex2(-1.6f, 0.3f),
			
			new Vertex2(-1.4f, 0.9f),
			new Vertex2(-1.4f, 0.5f),
			
			new Vertex2(-1.2f, 0.7f),
			new Vertex2(-1.2f, 0.3f),			
			new Vertex2(-1.2f, -0.1f),
			new Vertex2(-1.2f, -0.3f),
			new Vertex2(-1.2f, -0.5f),
			new Vertex2(-1.2f, -0.7f),
			new Vertex2(-1.2f, -0.9f),
						
			new Vertex2(-0.8f, 0.9f),
			new Vertex2(-0.8f, 0.7f),
			new Vertex2(-0.8f, 0.5f),
			new Vertex2(-0.8f, 0.3f),
			
			new Vertex2(-0.4f, 0.9f),
			new Vertex2(-0.4f, 0.7f),
			new Vertex2(-0.4f, 0.5f),
			new Vertex2(-0.4f, 0.3f),
			
			new Vertex2(-0.2f, 0.9f),
			new Vertex2(-0.2f, 0.3f),
			
			new Vertex2(0.0f, 0.9f),
			new Vertex2(0.0f, 0.3f),
			
			new Vertex2(0.4f, 0.9f),
			new Vertex2(0.4f, 0.7f),
			new Vertex2(0.4f, 0.5f),
			new Vertex2(0.4f, 0.3f),
			
			new Vertex2(0.6f, 0.9f),
			new Vertex2(0.6f, 0.3f),
			
			new Vertex2(0.8f, 0.9f),
			new Vertex2(0.8f, 0.3f),
			
			new Vertex2(0.8f, -0.1f),
			new Vertex2(0.8f, -0.3f),
			new Vertex2(0.8f, -0.5f),
			new Vertex2(0.8f, -0.7f),
			new Vertex2(0.8f, -0.9f),
						
			new Vertex2(1.0f, -0.9f),
			
			new Vertex2(1.2f, 0.9f),
			new Vertex2(1.2f, 0.7f),
			new Vertex2(1.2f, -0.1f),
			new Vertex2(1.2f, -0.3f),
			new Vertex2(1.2f, -0.5f),
			new Vertex2(1.2f, -0.7f),
			new Vertex2(1.2f, -0.9f),
			
			new Vertex2(1.4f, 0.5f),
			new Vertex2(1.4f, 0.3f),
			
			new Vertex2(1.6f, 0.9f),
			new Vertex2(1.6f, 0.7f)
	};
	
	public void doTick()
	{
		System.out.println(cd);
		if(cd <= 0)
		{
			selector++;
			cd = 1000;
		}
		
		if(cd != 1000)
		{
			cd-=5;
		}
		
		if(selector == 0 && cShips.size() == 0 )
		{
			for(int i = 0; i < this.credits.length; i++)
			{
				RiBracu r = new RiBracu(1,((new Random().nextFloat()+4.0f)), (new Random().nextFloat()-0.5f)*8.0f);
				r.setTarget(credits[i]);
				this.cShips.add(r);
			}
		}
		else if(selector == 0 && cShips.size() != 0 && isAllFinished(cShips) && cd == 1000)
		{
			cd--;
		}		
		else if(selector == 1 && lShips.size() == 0)
		{
			for(int i = 0; i < this.logo.length; i++)
			{
				RiBracu r = new RiBracu(1,((new Random().nextFloat()+4.0f)), (new Random().nextFloat()-0.5f)*8.0f);
				r.setTarget(logo[i]);
				this.lShips.add(r);
			}
			
			for(int i = 0; i < cShips.size(); i++)
			{
				cShips.get(i).setTarget(rPos());
			}
		}
		else if(selector == 1 && lShips.size() != 0 && isAllFinished(lShips) && cd == 1000)
		{
			cd--;
		}	
		else if(selector == 2 && cdShips.size() == 0)
		{
			for(int i = 0; i < this.cdg.length; i++)
			{
				RiBracu r = new RiBracu(1,((new Random().nextFloat()+4.0f)), (new Random().nextFloat()-0.5f)*8.0f);
				r.setTarget(cdg[i]);
				this.cdShips.add(r);
			}
			
			for(int i = 0; i < lShips.size(); i++)
			{
				lShips.get(i).setTarget(rPos());
			}
		}
		else if(selector == 2 && cdShips.size() != 0 && isAllFinished(cdShips) && cd == 1000)
		{
			cd--;
		}
		else if(selector == 3 && rShips.size() == 0)
		{
			for(int i = 0; i < this.ricy.length; i++)
			{
				RiBracu r = new RiBracu(1,((new Random().nextFloat()+4.0f)), (new Random().nextFloat()-0.5f)*8.0f);
				r.setTarget(ricy[i]);
				this.rShips.add(r);
			}
			
			for(int i = 0; i < this.heart.length; i++)
			{
				RiBracu r = new RiBracu(1,((new Random().nextFloat()+4.0f)), (new Random().nextFloat()-0.5f)*8.0f);
				r.setTarget(heart[i]);
				r.setTextureId(Roid.TEXTURE_ID);
				r.setShader(this.cShader);
				this.hShips.add(r);
			}
			
			for(int i = 0; i < cdShips.size(); i++)
			{
				cdShips.get(i).setTarget(rPos());
			}
		}
		else if(selector == 3 && isAllFinished(rShips) && cd == 1000)
		{
			cd--;
		}
		else if(selector == 4)
		{			
			for(int i = 0; i < rShips.size(); i++)
			{
				rShips.get(i).setTarget(rPos());
			}
			
			for(int i = 0; i < hShips.size(); i++)
			{
				Vertex2 t = hShips.get(i).getTarget();
				hShips.get(i).setTarget(new Vertex2(t.getX()+0.2f,t.getY()+0.3f));
			}
			
			selector++;
		}
		else if(selector == 5 && cd == 1000)
		{
			if(isAllFinished(hShips))
			{
				cd--;
			}
		}
		else if(selector == 6)
		{
			Vertex2 off = rPos();
			for(int i = 0; i < hShips.size(); i++)
			{
				Vertex2 t = hShips.get(i).getTarget();
				hShips.get(i).setTarget(new Vertex2(t.getX()+off.getX(),t.getY()+off.getY()));
			}
			selector++;
		}
		
		if(selector == 1)
		{
			if(isAllFinished(cShips))
			{
				cShips.clear();
			}
		}
		else if(selector == 2)
		{
			if(isAllFinished(lShips))
			{
				lShips.clear();
			}
			if(isAllFinished(cShips))
			{
				cShips.clear();
			}
		}
		else if(selector == 3)
		{
			if(isAllFinished(cdShips))
			{
				cdShips.clear();
			}
			if(isAllFinished(lShips))
			{
				lShips.clear();
			}
			if(isAllFinished(cShips))
			{
				cShips.clear();
			}
		}
		else if(selector == 5 || selector == 6)
		{
			if(isAllFinished(rShips))
			{
				rShips.clear();
			}
			if(isAllFinished(cdShips))
			{
				cdShips.clear();
			}
			if(isAllFinished(lShips))
			{
				lShips.clear();
			}
			if(isAllFinished(cShips))
			{
				cShips.clear();
			}
		}
		else if(selector == 7 && isAllFinished(hShips))
		{
			rShips.clear();
			cdShips.clear();
			lShips.clear();
			cShips.clear();
			hShips.clear();
		}
		
		
		for(int i = 0; i < cShips.size(); i++)
		{
			cShips.get(i).doTick();
		}
		
		for(int i = 0; i < cdShips.size(); i++)
		{
			cdShips.get(i).doTick();
		}
		
		for(int i = 0; i < hShips.size(); i++)
		{
			hShips.get(i).doTick();
		}
		
		for(int i = 0; i < lShips.size(); i++)
		{
			lShips.get(i).doTick();
		}
		
		for(int i = 0; i < rShips.size(); i++)
		{
			rShips.get(i).doTick();
		}
	}
	
	private boolean isAllFinished(List<RiBracu> r)
	{
		for(int i = 0; i < r.size(); i++)
		{
			if(!r.get(i).isAtTarget())
			{
				return false;
			}
			
		}
		
		return true;
	}
	
	private Vertex2 rPos()
	{
		float x = (new Random().nextInt(4000)+2000)/1000.0f;
		float y = (new Random().nextInt(4000)+2000)/1000.0f;
		
		if(new Random().nextBoolean())
			x *= -1.0f;
		
		if(new Random().nextBoolean())
			y *= -1.0f;
		
		return new Vertex2(x,y);
	}
	
	public void draw()
	{
		for(int i = 0; i < cShips.size(); i++)
		{
			cShips.get(i).draw();
		}
		
		for(int i = 0; i < cdShips.size(); i++)
		{
			cdShips.get(i).draw();
		}
		
		for(int i = 0; i < hShips.size(); i++)
		{
			hShips.get(i).draw();
		}
		
		for(int i = 0; i < lShips.size(); i++)
		{
			lShips.get(i).draw();
		}
		
		for(int i = 0; i < rShips.size(); i++)
		{
			rShips.get(i).draw();
		}
	}
}
