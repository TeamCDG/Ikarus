#version 330 core
precision highp float;

uniform sampler2D ship;
uniform vec2 seed = vec2(1.0, 0.5);

uniform vec4 color = vec4(0.0, 0.7, 1.0, 1.0);
uniform int selection = 0;

in vec4 pass_Color;
in vec4 pass_Position;
in vec2 pass_TextureCoord;

out vec4 out_Color;

float rand(vec2 n)
{
  return 0.0 + 0.4 * fract(sin(dot(n.xy, vec2(12.9898, 78.233)))* 43758.5453);
}

void main(void) 
{
	
	vec4 cout = texture2D(ship,pass_TextureCoord);
	
	cout = vec4(color.x, color.y, color.z, 0.8*cout.w - rand(vec2(sin((cos(seed.x*cout.w)+cout.y)/seed.y),sin((cos(seed.y*cout.z)+cout.w)/cout.x))));
	
	out_Color = cout;
	
	if(selection == 1)
	{
		out_Color = vec4(0.0,0.0,0.0,0.0);
	}
	
}
