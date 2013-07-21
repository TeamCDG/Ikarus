package cdg;

import cdg.util.Matrix4x4;
import cdg.util.ShaderProgram;
import cdg.util.Utility;

public abstract class Entity {

	private long id;
	private float x;
	private float y;
	private float width;
	private float height;
	private float rotation;
	private float xScale;
	private float yScale;
	private float zScale;
	private Matrix4x4 scalingMatrix;
	private Matrix4x4 translationMatrix;
	private Matrix4x4 rotationMatrix;
	private ShaderProgram shader;
	
	
	public Entity(long id) {
		this.id = id;
		this.x = 0.0f;
		this.y = 0.0f;
		this.width = 0.0f;
		this.height = 0.0f;
		this.rotation = 0.0f;
		
		this.scalingMatrix = new Matrix4x4(1.0f, 0.0f, 0.0f, 0.0f,
				   						   0.0f, 1.0f, 0.0f, 0.0f,
				   						   0.0f, 0.0f, 1.0f, 0.0f,
				   						   0.0f, 0.0f, 0.0f, 1.0f);
		
		this.translationMatrix = new Matrix4x4(1.0f, 0.0f, 0.0f, 0.0f,
				   							   0.0f, 1.0f, 0.0f, 0.0f,
				   							   0.0f, 0.0f, 1.0f, 0.0f,
				   							   0.0f, 0.0f, 0.0f, 1.0f);
		
		this.rotationMatrix = new Matrix4x4(1.0f, 0.0f, 0.0f, 0.0f,
				   							0.0f, 1.0f, 0.0f, 0.0f,
				   							0.0f, 0.0f, 1.0f, 0.0f,
				   							0.0f, 0.0f, 0.0f, 1.0f);
	}
	
	
	public abstract void draw();
	
	public abstract void drawSelection();
	
	public abstract void doTick();
	
	public abstract void finalize();


	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}


	/**
	 * @return the x
	 */
	public float getX() {
		return x;
	}


	/**
	 * @param x the x to set
	 */
	public void setX(float x) {
		this.translationMatrix = new Matrix4x4(1.0f, 0.0f, 0.0f, 0.0f,
				   							   0.0f, 1.0f, 0.0f, 0.0f,
				   							   0.0f, 0.0f, 1.0f, 0.0f,
				   							   x,  this.y, 0.0f, 1.0f);
		this.x = x;
	}


	/**
	 * @return the y
	 */
	public float getY() {
		return y;
	}


	/**
	 * @param y the y to set
	 */
	public void setY(float y) {
		this.translationMatrix = new Matrix4x4(1.0f, 0.0f, 0.0f, 0.0f,
				   							   0.0f, 1.0f, 0.0f, 0.0f,
				   							   0.0f, 0.0f, 1.0f, 0.0f,
				   							   this.x,  y, 0.0f, 1.0f);
		this.y = y;
	}


	/**
	 * @return the width
	 */
	public float getWidth() {
		return width;
	}


	/**
	 * @param width the width to set
	 */
	public void setWidth(float width) {
		this.width = width;
	}


	/**
	 * @return the height
	 */
	public float getHeight() {
		return height;
	}


	/**
	 * @param height the height to set
	 */
	public void setHeight(float height) {
		this.height = height;
	}


	/**
	 * @return the rotation in degree
	 */
	public float getRotation() {
		return rotation;
	}


	/**
	 * @param rotation the rotation in degree to set
	 */
	public void setRotation(float rotation) {		
		this.rotation = rotation;
		
		this.rotation = this.rotation % 360.0f;
		
		this.rotationMatrix = new Matrix4x4((float) Math.cos(Utility.degToRad(this.rotation)), (float) Math.sin(Utility.degToRad(this.rotation))*-1.0f, 0.0f, 0.0f,
											(float) Math.sin(Utility.degToRad(this.rotation)), 		 (float) Math.cos(Utility.degToRad(this.rotation)), 0.0f, 0.0f,
																						 0.0f, 													  0.0f, 1.0f, 0.0f,
																						 0.0f, 													  0.0f, 0.0f, 1.0f);
	}


	/**
	 * @return the scalingMatrix
	 */
	public Matrix4x4 getScalingMatrix() {
		return scalingMatrix;
	}


	/**
	 * @param scalingMatrix the scalingMatrix to set
	 */
	public void setScalingMatrix(Matrix4x4 scalingMatrix) {
		this.scalingMatrix = scalingMatrix;
	}


	/**
	 * @return the translationMatrix
	 */
	public Matrix4x4 getTranslationMatrix() {
		return translationMatrix;
	}


	/**
	 * @param translationMatrix the translationMatrix to set
	 */
	public void setTranslationMatrix(Matrix4x4 translationMatrix) {
		this.translationMatrix = translationMatrix;
	}


	/**
	 * @return the rotationMatrix
	 */
	public Matrix4x4 getRotationMatrix() {
		return rotationMatrix;
	}


	/**
	 * @param rotationMatrix the rotationMatrix to set
	 */
	public void setRotationMatrix(Matrix4x4 rotationMatrix) {
		this.rotationMatrix = rotationMatrix;
	}


	/**
	 * @return the xScale
	 */
	public float getXScale() {

		return xScale;
	}


	/**
	 * @param xScale the xScale to set
	 */
	public void setXScale(float xScale) {
		this.scalingMatrix = new Matrix4x4(1.0f, 0.0f, 0.0f, xScale,
				   						   0.0f, 1.0f, 0.0f, this.yScale,
				   						   0.0f, 0.0f, 1.0f, this.zScale,
				   						   0.0f, 0.0f, 0.0f, 1.0f);
		this.xScale = xScale;
	}


	/**
	 * @return the yScale
	 */
	public float getYScale() {
		return yScale;
	}


	/**
	 * @param yScale the yScale to set
	 */
	public void setYScale(float yScale) {
		this.scalingMatrix = new Matrix4x4(1.0f, 0.0f, 0.0f, this.xScale,
				   						   0.0f, 1.0f, 0.0f, yScale,
				   						   0.0f, 0.0f, 1.0f, this.zScale,
				   						   0.0f, 0.0f, 0.0f, 1.0f);
		this.yScale = yScale;
	}
	
	/**
	 * @return the yScale
	 */
	public float getZScale() {
		return zScale;
	}


	/**
	 * @param yScale the yScale to set
	 */
	public void setZScale(float zScale) {
		this.scalingMatrix = new Matrix4x4(1.0f, 0.0f, 0.0f, this.xScale,
				   						   0.0f, 1.0f, 0.0f, this.yScale,
				   						   0.0f, 0.0f, 1.0f, zScale,
				   						   0.0f, 0.0f, 0.0f, 1.0f);
		this.zScale = zScale;
	}


	/**
	 * @return the shader
	 */
	public ShaderProgram getShader() {
		return shader;
	}


	/**
	 * @param shader the shader to set
	 */
	public void setShader(ShaderProgram shader) {
		this.shader = shader;
	}

}
