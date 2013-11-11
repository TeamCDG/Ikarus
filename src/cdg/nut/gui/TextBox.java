package cdg.nut.gui;

import org.lwjgl.input.Keyboard;

import cdg.interfaces.IKeyListener;
import cdg.nut.util.BitmapFont;

public class TextBox extends Component {

	public TextBox(float x, float y, float width, float height, BitmapFont font) {
		super(x, y, width, height, true, true, font, "");
		this.setCenterText(false);
		this.setClickToSelect(true);
		this.setSelectable(true);
		this.addKeyListener(new IKeyListener(){

			@Override
			public void keyDown(int key, char c) {
			
				if(key == Keyboard.KEY_BACK)
					backspace();
				else if(key == Keyboard.KEY_RETURN)
					key('\n');
				else if(key == Keyboard.KEY_LEFT)
					left();
				else if(key == Keyboard.KEY_RIGHT)
					right();
				else if(key == Keyboard.KEY_UP)
					up();
				else if(key == Keyboard.KEY_DOWN)
					down();
				else 
					key(c);
				
			}
		});
	}


	protected void down() {
		// TODO Auto-generated method stub
		
	}


	protected void up() {
		// TODO Auto-generated method stub
		
	}


	protected void right() {
		// TODO Auto-generated method stub
		
	}


	protected void left() {
		// TODO Auto-generated method stub
		
	}


	public void key(char c)
	{
		//System.out.println("Key Down: '"+c+"'");
		if(this.getText().length() >= 1)
			this.setText(this.getText()+c);
		else
			this.setText(""+c);
	}
	
	public void backspace()
	{
		if(this.getText().length() > 1)
		{
			this.setText(this.getText().substring(0, this.getText().length()-1));
		}
		else
		{
			this.setText("");
		}
		//System.out.println("TEXT: "+this.getText());
	}
}
