package cdg.nut.gui;

import cdg.nut.util.BitmapFont;

public class FrameLabel extends Component {

	public FrameLabel(float x, float y, BitmapFont font, String text) {
		this(x, y, 1.0f, 1.0f, font, text);
	}
	
	public FrameLabel(float x, float y, float width, float height, BitmapFont font, String text) {
		super(x, y, width, height, true, false, font, text);
		this.setAutosizeWithText(true);
	}

}
