#version 330 core
precision highp float;

uniform sampler2D texture_font;
uniform int state;
uniform float time;

in vec4 pass_Color;
in vec2 pass_TextureCoord;

out vec4 out_Color;

#define BUTTON_BORDER_V 10
#define BUTTON_BORDER_H 11

void main(void) {

	vec4 color = pass_Color*texture2D(texture_font,pass_TextureCoord);
	out_Color = color;
}