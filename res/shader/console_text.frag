#version 150 core

uniform sampler2D texture_diffuse;
uniform vec4 color = vec4(1.0, 1.0, 1.0, 1.0);
uniform vec4 visible_Area = vec4(0.0, 0.0, 0.8, -0.5);

in vec4 pass_Position;
in vec4 pass_Color;
in vec2 pass_TextureCoord;

out vec4 out_Color;

void main(void) {	
	
	vec2 pos = vec2(pass_Position.x, pass_Position.y);
	
	if((pos.x >= visible_Area.x && pos.x <= visible_Area.z &&
	   pos.y <= visible_Area.y && pos.y >= visible_Area.w))
	{
		out_Color = color*texture2D(texture_diffuse, pass_TextureCoord);
	}
	else
	{
		out_Color = vec4(1.0, 0.0, 0.0, 0.0);
	}
}