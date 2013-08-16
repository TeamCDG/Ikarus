#version 150 core

uniform sampler2D texture_diffuse;
uniform vec4 color = vec4(1.0, 1.0, 1.0, 1.0);

in vec4 pass_Color;
in vec2 pass_TextureCoord;

out vec4 out_Color;

void main(void) {
	out_Color = pass_Color;//*texture2D(texture_diffuse, pass_TextureCoord);
	// Override out_Color with our texture pixel
	//out_Color = pass_Color*color*texture2D(texture_diffuse, pass_TextureCoord);
}