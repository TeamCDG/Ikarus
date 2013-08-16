#version 330 core
precision highp float;

uniform sampler2D ship;
uniform vec2 seed;


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
	
	vec4 color = pass_Color*texture2D(ship,pass_TextureCoord);
	
	color = vec4(0.0, 0.7, 1.0, color.w /*- rand(vec2(sin((cos(seed.x*color.x)+pass_Position.y)/color.w),sin((cos(seed.y*color.z)+pass_Position.w)/color.x)))*/);
	
	out_Color = color;
}
