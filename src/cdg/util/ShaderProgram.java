package cdg.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import cdg.interfaces.IMatrix;

public final class ShaderProgram {
	
	private int shaderProgrammId;
	private int vertexShaderId;
	private int fragmentShaderId;
	
	private int scalingMatrixLocation;
	private int translationMatrixLocation;
	private int rotationMatrixLocation;
	private int windowMatrixLocation;
	
	public ShaderProgram(String vertexShaderPath, String fragmentShaderPath) 
	{
		// Load the vertex shader
		this.vertexShaderId = ShaderProgram.loadShader(vertexShaderPath, GL20.GL_VERTEX_SHADER);
		// Load the fragment shader
		this.fragmentShaderId = ShaderProgram.loadShader(fragmentShaderPath, GL20.GL_FRAGMENT_SHADER);
				
		// Create a new shader program that links both shaders
		this.shaderProgrammId = GL20.glCreateProgram();
		
		GL20.glAttachShader(this.shaderProgrammId, this.vertexShaderId);
		GL20.glAttachShader(this.shaderProgrammId, this.fragmentShaderId);
		
		
		// Position information will be attribute 0
		GL20.glBindAttribLocation(this.shaderProgrammId, 0, "in_Position");
		// Color information will be attribute 1
		GL20.glBindAttribLocation(this.shaderProgrammId, 1, "in_Color");
		// Textute information will be attribute 2
		GL20.glBindAttribLocation(this.shaderProgrammId, 2, "in_TextureCoord");		
				
		
		GL20.glLinkProgram(this.shaderProgrammId);
		GL20.glValidateProgram(this.shaderProgrammId);
		
		this.scalingMatrixLocation = GL20.glGetUniformLocation(this.shaderProgrammId, "scaling_Matrix");
		this.translationMatrixLocation = GL20.glGetUniformLocation(this.shaderProgrammId, "translation_Matrix");
		this.rotationMatrixLocation = GL20.glGetUniformLocation(this.shaderProgrammId, "rotation_Matrix");
		this.windowMatrixLocation = GL20.glGetUniformLocation(this.shaderProgrammId, "window_Matrix");
	}
	
	@SuppressWarnings("deprecation")
	/**
	 * @param filename location of the shader source file
	 * @param type type of the shader: either GL_VERTEX_SHADER or GL_FRAGMENT_SHADER
	 */
	public static int loadShader(String filename, int type) 
	{
		StringBuilder shaderSource = new StringBuilder();
		int shaderID = 0;
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Could not read file.");
			e.printStackTrace();
			System.exit(-1);
		}
		
		shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		
		if (GL20.glGetShader(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.err.println("Could not compile shader.");
			System.exit(-1);
		}
		
		return shaderID;
	}
	
	/**
	 * Passes a standard matrix object to the shader
	 * @param mat the matrix to pass
	 * @param type the type of the matrix (standard matrices only)
	 */
	public void passMatrix(IMatrix mat, MatrixTypes type)
	{
		FloatBuffer matBuf = BufferUtils.createFloatBuffer(16);
		matBuf.put(mat.toArray());
		matBuf.flip();		
		
		if(type == MatrixTypes.SCALING)
			GL20.glUniformMatrix4(this.scalingMatrixLocation, false, matBuf);
		else if(type == MatrixTypes.TRANSLATION)
			GL20.glUniformMatrix4(this.translationMatrixLocation, false, matBuf);
		else if(type == MatrixTypes.ROTATION)
			GL20.glUniformMatrix4(this.rotationMatrixLocation, false, matBuf);
		else if(type == MatrixTypes.WINDOW)
			GL20.glUniformMatrix4(this.windowMatrixLocation, false, matBuf);
	}
	
	/**
	 * Passes a matrix object to the shader
	 * @param mat the matrix to pass
	 * @param type name of the matrix location
	 */
	public void passMatrix(IMatrix mat, String locationName)
	{
		FloatBuffer matBuf = BufferUtils.createFloatBuffer(16);
		matBuf.put(mat.toArray());
		matBuf.flip();	
		
		GL20.glUniformMatrix4(GL20.glGetUniformLocation(this.shaderProgrammId, locationName), false, matBuf);
	}
	
	public void bind()
	{
		GL20.glUseProgram(this.shaderProgrammId);
	}
	
	public void unbind()
	{
		GL20.glUseProgram(0);
	}
	
	/**
	 * Finalizes a ShaderProgram object and deletes all used resources
	 */
	public void finalize()
	{
		this.unbind();
		
		GL20.glDetachShader(this.shaderProgrammId, this.vertexShaderId);
		GL20.glDetachShader(this.shaderProgrammId, this.fragmentShaderId);
		
		GL20.glDeleteShader(this.vertexShaderId);
		GL20.glDeleteShader(this.fragmentShaderId);
		GL20.glDeleteProgram(this.shaderProgrammId);	
	}

	/**
	 * @return the shaderProgrammId
	 */
	public int getShaderProgrammId() {
		return shaderProgrammId;
	}

}
