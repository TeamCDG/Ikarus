#version 330 core
precision highp float;

uniform sampler2D roid;

in vec4 pass_Color;
in vec2 pass_TextureCoord;

out vec4 out_Color;

void main(void) {
	//out_Color = pass_Color;
	// Override out_Color with our texture pixel
	vec4 color = texture2D(roid,pass_TextureCoord);
	out_Color = color;
}