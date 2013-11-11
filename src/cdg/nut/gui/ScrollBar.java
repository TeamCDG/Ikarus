package cdg.nut.gui;

public abstract class ScrollBar 
{
	float value;
	float maxValue;
	
	private int mainVAO = -1;
	private int mainVBO = -1;
	private int mainIVBO = -1;
	private int mainICount = -1;
	
	private int selectionVAO = -1;
	private int selectionVBO = -1;
	private int selectionIVBO = -1;
	private int selectionICount = -1;
	
	public void setValue(float value)
	{
		this.value = value;
	}
	
	public float getValue()
	{
		return this.value;
	}
	
	public void setMaxValue(float maxValue)
	{
		this.maxValue = maxValue;
	}
	
	public float getMaxValue()
	{
		return this.maxValue;
	}

	public int getMainVAO() {
		return mainVAO;
	}

	public void setMainVAO(int mainVAO) {
		this.mainVAO = mainVAO;
	}

	public int getMainVBO() {
		return mainVBO;
	}

	public void setMainVBO(int mainVBO) {
		this.mainVBO = mainVBO;
	}

	public int getMainIVBO() {
		return mainIVBO;
	}

	public void setMainIVBO(int mainIVBO) {
		this.mainIVBO = mainIVBO;
	}

	public int getMainICount() {
		return mainICount;
	}

	public void setMainICount(int mainICount) {
		this.mainICount = mainICount;
	}

	public int getSelectionVAO() {
		return selectionVAO;
	}

	public void setSelectionVAO(int selectionVAO) {
		this.selectionVAO = selectionVAO;
	}

	public int getSelectionVBO() {
		return selectionVBO;
	}

	public void setSelectionVBO(int selectionVBO) {
		this.selectionVBO = selectionVBO;
	}

	public int getSelectionIVBO() {
		return selectionIVBO;
	}

	public void setSelectionIVBO(int selectionIVBO) {
		this.selectionIVBO = selectionIVBO;
	}

	public int getSelectionICount() {
		return selectionICount;
	}

	public void setSelectionICount(int selectionICount) {
		this.selectionICount = selectionICount;
	}
	
	public abstract void draw();
	
	protected abstract void setupMainGL();
	
	protected abstract void setupSelectionGL();
}
