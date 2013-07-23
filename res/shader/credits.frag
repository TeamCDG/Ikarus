#version 330 core
precision highp float;

uniform sampler2D ship;
uniform vec2 seed;
uniform float shield_Level;
uniform int selection;

in vec4 pass_Color;
in vec2 pass_TextureCoord;

out vec4 out_Color;

float rand(vec2 n)
{
  return 0.0 + shield_Level * fract(sin(dot(n.xy, vec2(12.9898, 78.233)))* 43758.5453);
}

void main(void) 
{
	
	
	vec4 color = texture2D(ship,pass_TextureCoord);
	if(color.w == 1.0)
	{
		out_Color = color;
	}
	else
	{
		out_Color = vec4(1.0, 0.0, 0.0, color.w * rand(vec2(sin((cos(seed.x*color.x)+color.y)/color.w),sin((cos(seed.y*color.z)+color.w)/color.x))));
	}
	
	if(selection == 1)
	{
		out_Color = pass_Color;
	}
	
	//out_Color = vec4(1.0, 1.0, 1.0, 1.0);
}
