package cdg.nut.util;

import java.util.Random;

public class AdvancedRandom extends Random 
{
	
	public AdvancedRandom()
	{
		super();
	}
	
	public AdvancedRandom(int seed)
	{
		super(seed);
	}
	
	public int nextInt(int min, int max)
	{
		return this.nextInt(max-min)+min;
	}
	
}
