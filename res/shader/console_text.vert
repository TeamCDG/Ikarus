#version 150 core

uniform mat4 window_Matrix;
uniform mat4 scaling_Matrix = mat4(1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0);
uniform mat4 translation_Matrix = mat4(1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0);

in vec4 in_Position;
in vec4 in_Color;
in vec2 in_TextureCoord;

out vec4 pass_Position;
out vec4 pass_Color;
out vec2 pass_TextureCoord;

void main(void) {
	vec4 pos = window_Matrix * translation_Matrix * scaling_Matrix * in_Position;
	gl_Position = pos;
	
	pass_Color = in_Color;
	pass_TextureCoord = in_TextureCoord;
	pass_Position = translation_Matrix * scaling_Matrix * in_Position;
}