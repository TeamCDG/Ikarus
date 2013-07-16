#version 150 core

uniform mat4 windowMatrix;
uniform sampler2D texture_font;

in vec4 pass_Color;
in vec2 pass_TextureCoord;

out vec4 out_Color;

void main(void) {

	vec4 color = vec4(1.0, 0.0, 0.0, 1.0);
	out_Color = color;
}