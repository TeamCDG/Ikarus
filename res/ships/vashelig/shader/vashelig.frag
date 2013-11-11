#version 330 core

uniform sampler2D ship;
uniform sampler2D shield;
uniform sampler2D team_stripes;
uniform sampler2D decoration;

uniform vec4 shield_color = vec4(0.4, 1.0, 0.4, 1.0);
uniform float shield_level = 1.0;
uniform vec4 team_color = vec4(0.0, 0.6, 0.0, 1.0);
uniform vec2 seed = vec2(1.0, 0.5);

uniform int selection = 0;

in vec4 pass_Color;
in vec2 pass_TextureCoord;

out vec4 out_Color;

float rand(vec2 n, float min)
{
	return 0.0 + min * fract(sin(dot(n.xy, vec2(12.9898, 78.233)))* 43758.5453);
}

void main(void) 
{
	
	vec4 base = texture2D(ship,pass_TextureCoord);
	vec4 shield = texture2D(shield,pass_TextureCoord);
	vec4 tColor = texture2D(team_stripes,pass_TextureCoord);
	vec4 dColor = texture2D(decoration,pass_TextureCoord);
	if(shield.w != 0.0)
	{
		//out_Color = vec4(1.0, 1.0, 1.0, 1.0);
		out_Color = vec4(shield_color.x, shield_color.y, shield_color.z, shield.w * shield_level * rand(vec2(sin((cos(seed.x*shield.w)+shield.y)/seed.y),sin((cos(seed.y*shield.z)+shield.w)/shield.x)),1.0));
	}
	else if(tColor.w >= 0.8)
	{
		out_Color = team_color * tColor * base;
	}
	else if(dColor.w != 0.0)
	{
		vec4 cMulti = team_color * dColor * base;
		out_Color = vec4(cMulti.x, cMulti.y, cMulti.z, 1.0);
	}
	else
	{
		out_Color = base;
	}
	
	if(selection == 1)
	{
		if(shield.w != 0.0)
		{
			out_Color = vec4(pass_Color.x, pass_Color.y, pass_Color.z, 1.0);
		}
		else if(base.w != 0.0)
		{
			out_Color = vec4(pass_Color.x, pass_Color.y, pass_Color.z, 1.0);
		}
		else
		{
			out_Color = vec4(pass_Color.x, pass_Color.y, pass_Color.z, 0.0);
		}
	}
}
