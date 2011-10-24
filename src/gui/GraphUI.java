/*
 * Copyright (C) 2007 by Johan Dams, VAMK <jd@puv.fi>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */
package gui;

import java.awt.FlowLayout;
import java.awt.event.*;
import javax.swing.*;

import myPackage.Constants;
import myPackage.Main;
import myPackage.Vector2;

public class GraphUI extends JFrame implements Constants {

	public static final long serialVersionUID = 123;
	public static FieldCanvas canvas;

	public JComboBox objects;
	public JComboBox positions;
	public JFrame controls;

	public String objectToMove;

	class SelectListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Main.ui.objectToMove = (String) Main.ui.objects.getSelectedItem();
		}
	}

	class PositionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String which = (String) Main.ui.positions.getSelectedItem();
			if (which == "Default") {
				Main.opponents[OPPONENT1].setPosition(new Vector2(500, 1800));
				Main.opponents[OPPONENT2].setPosition(new Vector2(700, 1800));
				Main.opponents[OPPONENT3].setPosition(new Vector2(1300, 1800));
				Main.opponents[OPPONENT4].setPosition(new Vector2(1800, 1800));
				Main.opponents[OPPONENT5].setPosition(new Vector2(2000, 1800));
				Main.robots[ROBOT1].setPosition(new Vector2(200, 0));
				Main.robots[ROBOT2].setPosition(new Vector2(500, 0));
				Main.robots[ROBOT3].setPosition(new Vector2(0, -500));
				Main.robots[ROBOT4].setPosition(new Vector2(0, -1000));
				Main.robots[ROBOT5].setPosition(new Vector2(0, -1500));
			} else if (which == "Other") {
				Main.opponents[OPPONENT1].setPosition(new Vector2(500, 2000));
				Main.opponents[OPPONENT2].setPosition(new Vector2(700, 1500));
				Main.opponents[OPPONENT3].setPosition(new Vector2(1000, 1000));
				Main.opponents[OPPONENT4].setPosition(new Vector2(1300, 500));
				Main.opponents[OPPONENT5].setPosition(new Vector2(1600, 1500));
				Main.robots[ROBOT1].setPosition(new Vector2(200, 100));
				Main.robots[ROBOT2].setPosition(new Vector2(500, 1000));
				Main.robots[ROBOT3].setPosition(new Vector2(3000, 500));
				Main.robots[ROBOT4].setPosition(new Vector2(4000, 1000));
				Main.robots[ROBOT5].setPosition(new Vector2(3000, 2500));

			}
		}
	}

	public GraphUI() {

		super("Field");

		canvas = new FieldCanvas();

		this.getContentPane().add(canvas);
		this.setLocationRelativeTo(null);

		controls = new JFrame();
		JPanel content = new JPanel();
		content.setLayout(new FlowLayout());

		controls.add(content);

		objects = new JComboBox();

		objects.addItem(" ");
		objects.addItem("Ball");

		objects.addItem("Robot1");
		objects.addItem("Robot2");
		objects.addItem("Robot3");
		objects.addItem("Robot4");
		objects.addItem("Robot5");

		objects.addItem("Opponent1");
		objects.addItem("Opponent2");
		objects.addItem("Opponent3");
		objects.addItem("Opponent4");
		objects.addItem("Opponent5");

		JLabel objectslabel = new JLabel("Move");
		content.add(objectslabel);

		objects.addActionListener(new SelectListener());
		objects.setVisible(true);
		content.add(objects);

		positions = new JComboBox();
		positions.addItem(" ");
		positions.addItem("Default");
		positions.addItem("Other");

		JLabel positionslabel = new JLabel("Positions");
		content.add(positionslabel);

		positions.addActionListener(new PositionListener());
		positions.setVisible(true);
		content.add(positions);

		controls.setContentPane(content);
		controls.pack();

		controls.setTitle("Control");
		controls.setVisible(true);

	}

	public void init() {

		WindowListener l = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}

			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		};

		this.addWindowListener(l);
		this.setSize(980, 680);
		this.setVisible(true);

	}

}
