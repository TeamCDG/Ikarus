#version 150 core

in vec4 in_Position;
in vec4 in_Color;
in vec2 in_TextureCoord;

uniform mat4 cam_Matrix;
uniform mat4 rotation_Matrix;
uniform mat4 translation_Matrix;
uniform mat4 window_Matrix;
uniform vec2 seed;

out vec4 pass_Color;
out vec2 pass_TextureCoord;
out vec4 pass_Position;


void main(void) {

	gl_Position = cam_Matrix * window_Matrix * translation_Matrix * rotation_Matrix *  in_Position;
	pass_TextureCoord = in_TextureCoord;
	pass_Color = in_Color;
	pass_Position = window_Matrix * translation_Matrix * rotation_Matrix *  in_Position;
	
	
}