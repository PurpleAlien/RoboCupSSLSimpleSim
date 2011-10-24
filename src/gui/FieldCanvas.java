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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import myPackage.Constants;
import myPackage.Main;
import myPackage.Vector2;

public class FieldCanvas extends JPanel implements MouseListener, Constants {

	public static final long serialVersionUID = 123;

	double xcenter, ycenter, width, height;
	double scaleFactorX, scaleFactorY;

	public FieldCanvas() {
		this.setBackground(new Color(100, 200, 50));
		this.addMouseListener(this);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		RenderingHints renderHints = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		renderHints.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		Graphics2D g2d = (Graphics2D) g; // always!

		g2d.setRenderingHints(renderHints);

		width = (float) this.getSize().width - 100;
		height = (float) this.getSize().height - 100;
		xcenter = (float) (this.getSize().width - 100) / 2;
		ycenter = (float) (this.getSize().height - 100) / 2;

		g2d.translate(50.0, height + 50); // Move the origin to the lower left
		g2d.scale(1.0, -1.0); // Flip the sign of the coordinate system

		scaleFactorX = width / FIELD_WIDTH;
		scaleFactorY = height / FIELD_HEIGHT;

		// call method to paint the lines of the field
		paintField(g2d);
		drawRobot(g2d);
		drawOpponent(g2d);
		drawBall(g2d);
	}

	public void paintField(Graphics2D g2d) {

		g2d.setColor(Color.white); // setting context

		// render the Field
		g2d.draw(new Rectangle2D.Double(0, 0, width, height));

		g2d.draw(new Line2D.Double(xcenter, 0, xcenter, height));

		g2d
				.draw(new Ellipse2D.Double(xcenter - 500 * scaleFactorX,
						ycenter - 500 * scaleFactorY, 1000 * scaleFactorX,
						1000 * scaleFactorY));

		g2d.draw(new Arc2D.Double(-400 * scaleFactorX, ycenter - 400
				* scaleFactorY, 800 * scaleFactorX, 800 * scaleFactorY, -90,
				180, 0));
		g2d.draw(new Arc2D.Double(-400 * scaleFactorX + width, ycenter - 400
				* scaleFactorY, 800 * scaleFactorX, 800 * scaleFactorY, 90,
				180, 0));
	}

	public void drawRobot(Graphics2D g2d) {

		Color[] colors = { Color.blue, Color.cyan, Color.magenta, Color.orange,
				Color.red };

		for (int i = 0; i < OWNROBOTCOUNT; i++) {
			g2d.setColor(colors[i]); // setting context

			AffineTransform saveXform = g2d.getTransform();

			g2d.translate((Main.robots[i].getPosx()) * scaleFactorX,
					(Main.robots[i].getPosy()) * scaleFactorY);
			g2d.rotate(Math.PI);
			g2d.scale(-1, 1);

			g2d.draw(new Ellipse2D.Double(-90 * scaleFactorX, -90
					* scaleFactorY, 180 * scaleFactorX, 180 * scaleFactorY));

			String s = "R" + Integer.toString(i + 1);
			g2d.drawString(s, -6, 5);

			Line2D line = new Line2D.Double(0, 0, 30, 0);
			g2d.rotate(-Main.robots[i].getOrientation());
			g2d.draw(line);

			g2d.setTransform(saveXform);
		}
	}

	public void drawOpponent(Graphics2D g2d) {

		Color color = Color.blue;

		for (int i = 0; i < OPPROBOTCOUNT; i++) {
			g2d.setColor(color); // setting context

			AffineTransform saveXform = g2d.getTransform();

			g2d.translate((Main.opponents[i].getPosx()) * scaleFactorX,
					(Main.opponents[i].getPosy()) * scaleFactorY);
			g2d.rotate(Math.PI);
			g2d.scale(-1, 1);

			Ellipse2D opp = new Ellipse2D.Double(-90 * scaleFactorX, -90
					* scaleFactorY, 180 * scaleFactorX, 180 * scaleFactorY);

			g2d.fill(opp);
			g2d.draw(opp);

			g2d.setColor(Color.white);
			String s = Integer.toString(i + 1);
			g2d.drawString(s, -4, 5);

			g2d.setTransform(saveXform);
		}
	}

	public void drawBall(Graphics2D g2d) {

		Color color = Color.orange;

		g2d.setColor(color); // setting context

		AffineTransform saveXform = g2d.getTransform();

		g2d.translate((Main.ball.getPosx()) * scaleFactorX, (Main.ball
				.getPosy())
				* scaleFactorY);
		g2d.rotate(Math.PI);
		g2d.scale(-1, 1);

		Ellipse2D ball = new Ellipse2D.Double(-25 * scaleFactorX, -25
				* scaleFactorY, 50 * scaleFactorX, 50 * scaleFactorY);
		g2d.fill(ball);

		g2d.draw(ball);

		g2d.setTransform(saveXform);

	}

	public void mouseClicked(MouseEvent e) {
		double upscaleFactorX = FIELD_WIDTH / width;
		double upscaleFactorY = FIELD_HEIGHT / height;

		int x = (int) ((e.getX() - 50) * upscaleFactorX);
		int y = (int) (FIELD_HEIGHT - ((e.getY() - 50) * upscaleFactorY));

		if (Main.ui.objectToMove == "Ball") {
			Main.ball.setPosition(new Vector2(x, y));
			Main.ball.setSpeed(new Vector2(0, 0));
		} else if (Main.ui.objectToMove == "Robot1")
			Main.robots[0].setPosition(new Vector2(x, y));
		else if (Main.ui.objectToMove == "Robot2")
			Main.robots[1].setPosition(new Vector2(x, y));
		else if (Main.ui.objectToMove == "Robot3")
			Main.robots[2].setPosition(new Vector2(x, y));
		else if (Main.ui.objectToMove == "Robot4")
			Main.robots[3].setPosition(new Vector2(x, y));
		else if (Main.ui.objectToMove == "Robot5")
			Main.robots[4].setPosition(new Vector2(x, y));
		else if (Main.ui.objectToMove == "Opponent1")
			Main.opponents[0].setPosition(new Vector2(x, y));
		else if (Main.ui.objectToMove == "Opponent2")
			Main.opponents[1].setPosition(new Vector2(x, y));
		else if (Main.ui.objectToMove == "Opponent3")
			Main.opponents[2].setPosition(new Vector2(x, y));
		else if (Main.ui.objectToMove == "Opponent4")
			Main.opponents[3].setPosition(new Vector2(x, y));
		else if (Main.ui.objectToMove == "Opponent5")
			Main.opponents[4].setPosition(new Vector2(x, y));

	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
