package cdg.util;

import org.lwjgl.input.Keyboard;

public class TextBox extends Component {

	public TextBox(int id, float x, float y, float width, float height, BitmapFont font) {
		super(id, x, y, width, height, true, false, font, "");
	}

	public void keyDown(char c)
	{
		this.setText(this.getText()+c);
	}
	
	public void backspace()
	{
		if(this.getText().length() > 1)
		{
			this.setText(this.getText().substring(0, this.getText().length()-2));
		}
		else
		{
			this.setText("");
		}
	}
}
