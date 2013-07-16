#version 150 core

in vec4 in_Position;
in vec4 in_Color;
in vec2 in_TextureCoord;

uniform mat4 window_Matrix;
uniform mat4 ikarus_Rotation_Matrix;
uniform mat4 translation_Matrix;
uniform mat4 ntranslation_Matrix;

out vec4 pass_Color;
out vec2 pass_TextureCoord;


void main(void) {
	
	gl_Position = window_Matrix * translation_Matrix * ikarus_Rotation_Matrix *  in_Position;
	
	pass_TextureCoord = in_TextureCoord;
	pass_Color = in_Color;
	
	
}