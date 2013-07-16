#version 330 core
precision highp float;

uniform sampler2D ship;
uniform vec2 seed;

in vec4 pass_Color;
in vec2 pass_TextureCoord;

out vec4 out_Color;

float rand(vec2 n)
{
  return 0.0 + 1.0 * fract(sin(dot(n.xy, vec2(12.9898, 78.233)))* 43758.5453);
}

void main(void) 
{
	
	vec4 color = pass_Color*texture2D(ship,pass_TextureCoord);
	out_Color = color;
	//out_Color = vec4(0.0, 0.0, 1.0, 1.0);
}
