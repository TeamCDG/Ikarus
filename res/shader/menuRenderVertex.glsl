#version 150 core

in vec4 in_Position;
in vec4 in_Color;
in vec2 in_TextureCoord;

uniform mat4 windowMatrix;
uniform int state;
uniform vec2 position;
uniform vec2 dim;
uniform vec2 borderDim;

out vec4 pass_Color;
out vec2 pass_TextureCoord;

#define BUTTON_BORDER_V 10
#define BUTTON_BORDER_H 11

void main(void) {

	gl_Position = windowMatrix * in_Position;
	
	pass_TextureCoord = in_TextureCoord;
	pass_Color = in_Color;
	
	if(state == BUTTON_BORDER_V)
	{
		
	}
	else if(state == BUTTON_BORDER_H)
	{
	}
	
	
}