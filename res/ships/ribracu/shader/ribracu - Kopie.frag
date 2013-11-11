#version 330 core
precision highp float;

uniform sampler2D ship;
uniform sampler2D shield;
uniform sampler2D team_stripes;
uniform sampler2D team_logo;

uniform vec2 seed;
uniform float shield_Level;
uniform vec4 shieldColor = vec4(0.1, 1.0, 0.1, 1.0);
uniform vec4 teamColor = vec4(0.4, 1.0, 0.4, 1.0);
uniform int selection = 0;

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
	vec4 tColor = texture2D(team_stripes,pass_TextureCoord);
	vec4 lColor = texture2D(team_logo,pass_TextureCoord);
	vec4 shield = texture2D(shield,pass_TextureCoord);
	if(tColor.w == 1.0)
	{
		out_Color = teamColor * tColor;
	}
	else if(lColor.w == 1.0)
	{
		out_Color = lColor;
	}
	else if(shield.w != 0.0)
	{
		out_Color = vec4(shieldColor.x, shieldColor.y, shieldColor.z, color.w * rand(vec2(sin((cos(seed.x*color.x)+color.y)/color.w),sin((cos(seed.y*color.z)+color.w)/color.x))));
	}
	else
	{
		vec4 base = texture2D(ship,pass_TextureCoord);
		out_Color = base;
	}
	
	if(selection == 1)
	{
		out_Color = vec4(pass_Color.x, pass_Color.y, pass_Color.z, base.w);
	}
}
