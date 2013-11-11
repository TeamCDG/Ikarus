package cdg;

import cdg.interfaces.IClickListener;
import cdg.nut.gui.Button;
import cdg.nut.gui.Frame;
import cdg.nut.gui.FrameLabel;
import cdg.nut.gui.Label;
import cdg.nut.gui.TextBox;
import cdg.nut.util.Globals;
import cdg.nut.util.Matrix4x4;
import cdg.nut.util.Vertex2;

public class MainFrame extends Frame 
{
	public MainFrame(final Matrix4x4 winMat)
	{
		super(winMat);
		Button newGame = new Button(-1.6f, 0.5f, Globals.getDefaultTextFont(), "New Game");
		newGame.setTextScale(2.0f);
		newGame.setWidth(1.2f);
		newGame.addClickListener(new IClickListener(){

			@Override
			public void clicked(int x, int y, int button) {
				Globals.setActiveFrame("game");		
			}
		});
		Button options = new Button(-1.6f, 0.2f, Globals.getDefaultTextFont(), "Options");
		options.setWidth(0.6f);
		options.setTextScale(2.0f);
		options.addClickListener(new IClickListener(){

			@Override
			public void clicked(int x, int y, int button) {
				Globals.setWindowResolution(1280, 720);
			}
		});
		Button credits = new Button(-1.6f, -0.1f, Globals.getDefaultTextFont(), "Credits");
		credits.setWidth(0.6f);
		credits.setTextScale(2.0f);
		credits.addClickListener(new IClickListener(){

			@Override
			public void clicked(int x, int y, int button) {
				Globals.toggleFullscreen();
			}
		});
		Button exit = new Button(-1.6f, -0.4f, Globals.getDefaultTextFont(), "Exit");
		exit.setWidth(0.6f);
		exit.setTextScale(2.0f);
		exit.addClickListener(new IClickListener(){

			@Override
			public void clicked(int x, int y, int button) {
				System.exit(1337);				
			}
		});
		
		this.add(newGame);
		this.add(options);
		this.add(credits);
		this.add(exit);
		
		
		Vertex2 hlPos = new Vertex2(-0.6f, 1f);
		Label headline = new Label(hlPos.getX(), hlPos.getY(), Globals.getDefaultTextFont(), "Ikarus");
		headline.setTextScale(5.0f);
		this.add(headline);
		
		Label underline = new Label(hlPos.getX()-0.012f, hlPos.getY(), Globals.getFont("lcd"), "_____");
		underline.setTextScale(5.0f);
		this.add(underline);
		
		TextBox t = new TextBox(0, 0, 1, 0.4f, Globals.getDefaultTextFont());
		t.setTextScale(2.0f);
		this.add(t);
		
	}
}
