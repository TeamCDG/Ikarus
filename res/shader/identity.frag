#version 150 core

uniform vec4 color = vec4(1.0, 1.0, 1.0, 1.0);

in vec4 pass_Color;
in vec2 pass_TextureCoord;

out vec4 out_Color;

void main(void) {
	out_Color = color * pass_Color;
}