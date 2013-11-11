package cdg.interfaces;

public interface IWindowChangeListener {
	public void onWindowResolutionChange(int width, int height);
	public void onWindowTitleChange(String newTitle);
}
