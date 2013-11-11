#version 330 core
precision highp float;

uniform sampler2D roid;
uniform sampler2D formTexture;

in vec4 pass_Color;
in vec2 pass_TextureCoord;

out vec4 out_Color;

vec4 getColor(void)
{
	vec4 cSurface = texture2D(roid,pass_TextureCoord);
	vec4 cForm = texture2D(formTexture,pass_TextureCoord);
	return vec4(cSurface.x, cSurface.y, cSurface.z, cForm.w);
}

void main(void) {
	//out_Color = pass_Color;
	// Override out_Color with our texture pixel
	vec4 color = getColor();//texture2D(formTexture,pass_TextureCoord)*texture2D(roid,pass_TextureCoord);
	out_Color = color;
}