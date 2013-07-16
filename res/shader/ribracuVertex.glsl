#version 150 core

in vec4 in_Position;
in vec4 in_Color;
in vec2 in_TextureCoord;

uniform mat4 window_Matrix;
uniform mat4 rotation_Matrix;
uniform mat4 translation_Matrix;
uniform mat4 death_Scale_Matrix;

out vec4 pass_Color;
out vec2 pass_TextureCoord;


void main(void) {

	vec4 pos = translation_Matrix * rotation_Matrix * death_Scale_Matrix * in_Position;
	
	
	gl_Position = window_Matrix * pos;
	
	pass_TextureCoord = in_TextureCoord;
	pass_Color = in_Color;
	
	
}