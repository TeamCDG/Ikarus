#version 330 core
precision highp float;

uniform sampler2D ship;
uniform vec2 seed;
uniform float shield_Level;
uniform int selection;
uniform float death_Time;

in vec4 pass_Color;
in vec2 pass_TextureCoord;

out vec4 out_Color;

float rand(vec2 n)
{
  return 0.0 + shield_Level * fract(sin(dot(n.xy, vec2(12.9898, 78.233)))* 43758.5453);
}

float rand2(vec2 n)
{
  return 0.4 + 0.6 * fract(sin(dot(n.xy, vec2(12.9898, 78.233)))* 43758.5453);
}

void main(void) 
{
	
	
	vec4 color = texture2D(ship,pass_TextureCoord);
	//out_Color = color;
	
	if(color.w == 1.0)
	{
		out_Color = color;
	}
	else
	{
		out_Color = vec4(0.1, 1.0, 0.1, color.w * rand(vec2(sin((cos(seed.x*color.x)+color.y)/color.w),sin((cos(seed.y*color.z)+color.w)/color.x))));
	}
	
	if(selection == 2)
	{
		if(color.w == 1.0)
		{
			float r = rand2(vec2(sin((cos(seed.y*color.x)+color.y)/color.x),sin((cos(seed.y*color.z)+color.w)/color.x)));
			float g = rand2(vec2(sin((sin(seed.x*color.x)+color.y)/color.w),tan((cos(seed.y*color.z)+color.x)/color.x)));
			float b = rand2(vec2(sin((cos(seed.x*color.w)+color.y)/color.w),sin((tan(seed.x*color.w)+color.w)/color.y)));
			float a = rand2(vec2(tan((cos(seed.y*color.x)+color.z)/color.w),sin((cos(seed.y*color.z)+color.w)/color.x)));
			out_Color = vec4(0.1+r, 0.1+g, b-0.4, color.w * a * ((1.0/1000.0)*death_Time));
		}
		else
		{
			out_Color = vec4(1.0, 1.0, 1.0, 0.0);
		}
	}
	
	if(selection == 1)
	{
		out_Color = pass_Color;
	}
	
	
	//out_Color = color;
}
