#version 150 core

uniform sampler2D texture_diffuse;
uniform vec4 color = vec4(1.0, 1.0, 1.0, 1.0);
uniform vec4 frame_color = vec4(1.0, 1.0, 1.0, 1.0);
uniform vec4 background_color = vec4(1.0, 1.0, 1.0, 1.0);

in vec4 pass_Color;
in vec2 pass_TextureCoord;

out vec4 out_Color;

void main(void) {
	out_Color = pass_Color;//*texture2D(texture_diffuse, pass_TextureCoord);
	if(pass_Color.x == 1.0 && pass_Color.y == 1.0 && pass_Color.z == 1.0 && pass_Color.w == 1.0)
	{
		out_Color = background_color;
	}
	else if(pass_Color.x == 0.0 && pass_Color.y == 0.0 && pass_Color.z == 0.0 && pass_Color.w == 0.0)
	{
		out_Color = frame_color;
	}
	// Override out_Color with our texture pixel
	//out_Color = pass_Color*color*texture2D(texture_diffuse, pass_TextureCoord);
}