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
package myPackage;

import gui.GraphUI;
import strategy.*;
import java.util.concurrent.*;

import objects.Ball;
import objects.Opponent;
import objects.Robot;

import rrt.Obstacle;

public class Main implements Constants {

	/* Semaphores for the two Video threads */
	public static final Semaphore sem_video1 = new Semaphore(1, true);
	public static final Semaphore sem_video2 = new Semaphore(1, true);

	/* Strategy semaphore, to indicate video has been processed */
	public static final Semaphore sem_strategy = new Semaphore(0, true);
	/*
	 * Command sender semaphore, to indicate strategy is computed and command
	 * can be sent
	 */
	public static final Semaphore sem_send = new Semaphore(0, true);

	/* Our Robots */
	public static final Robot robots[] = new Robot[OWNROBOTCOUNT];
	/* The Opponents */
	public static final Opponent opponents[] = new Opponent[OPPROBOTCOUNT];
	/* The ball */
	public static final Ball ball = new Ball();

	/* Gui */
	public static GraphUI ui;

	/* Strategy stuff */
	public static Goalkeep goalkeeper;
	public static Defence defenders;

	public static void init() {

		/* Initialize array of own Robots, and start each robot thread */
		for (int i = 0; i < OWNROBOTCOUNT; i++) {
			robots[i] = new Robot(i);
			new Thread(robots[i]).start();
		}
		/* initialize array of Opponent robots */
		for (int i = 0; i < OPPROBOTCOUNT; i++) {
			opponents[i] = new Opponent();
		}

		goalkeeper = new Goalkeep(ROBOT5);
		defenders = new Defence(ROBOT1, ROBOT2);
	}

	public static void main(String[] args) {

		/* Initialize robots and opponents */
		init();

		/* Timing */
		long start, stop, elapsed;

		/* Init Gui */
		ui = new GraphUI();
		ui.init();


		/* This is where the strategies are to be calculated */

		while (true) {

			start = System.currentTimeMillis();

			/* Put ball, robots and opponents as found */
			/* In reality, this would be done by the vision system */

			ball.setFound(true);

			robots[ROBOT1].setFound(true);
			robots[ROBOT2].setFound(true);
			robots[ROBOT3].setFound(true);
			robots[ROBOT4].setFound(true);
			robots[ROBOT5].setFound(true);

			opponents[OPPONENT1].setFound(true);
			opponents[OPPONENT2].setFound(true);
			opponents[OPPONENT3].setFound(true);
			opponents[OPPONENT4].setFound(true);
			opponents[OPPONENT5].setFound(true);

			/* Actual strategy stuff */

			/* Target == Goal */
			Vector2 target = new Vector2();

			target.y = FIELD_HEIGHT / 2;
			target.x = 0;

			robots[ROBOT3].kick(target);

			robots[ROBOT4].setKick(1);
			robots[ROBOT4].setKickStrength(800);
			robots[ROBOT4].face(target);

			goalkeeper.goalkeeping();
			defenders.defend();

			/* Update the simulation loop */
			for(int i = 0; i < OWNROBOTCOUNT; i++){
				robots[i].simUpdate();
			}
			
			/* Some statistics */
			stop = System.currentTimeMillis();
			elapsed = stop - start;


			/* Ball Physics: R = I - 2(N.I)N */
			Boolean collision = false;
			for (int i = 0; i < OPPROBOTCOUNT; i++) {
				if (Obstacle.staticCollision(opponents[i].getPosition(), ball.getPosition())) {
					collision = true;
					Vector2 normal = Vector2.difference(opponents[i].getPosition(), ball.getPosition());
					double factor = Vector2.dotproduct(normal, ball.getSpeed()) * 2;
					Vector2 scaled = Vector2.scale(normal, factor);
					Vector2 reflect = Vector2.difference(ball.getSpeed(), scaled);
					reflect.setMagnitude(ball.getSpeed().magnitude() * 0.7);
					ball.setSpeed(reflect);
					Vector2 posdiff = Vector2.difference(opponents[i].getPosition(), ball.getPosition());

					if (posdiff.magnitude() < 90) {
						ball.setPosition(Vector2.sum(ball.getPosition(),
								Vector2.scale(posdiff, -1)));
					}

					break;
				}
			}

			if (collision == false) {
				for (int i = 0; i < OWNROBOTCOUNT; i++) {
					if (robots[i].getKick() == 0) {
						if (Obstacle.staticCollision(robots[i].getPosition(), ball.getPosition())) {
							collision = true;
							Vector2 normal = Vector2.difference(robots[i].getPosition(), ball.getPosition());
							double factor = Vector2.dotproduct(normal, ball.getSpeed()) * 2;
							Vector2 scaled = Vector2.scale(normal, factor);
							Vector2 reflect = Vector2.difference(ball.getSpeed(), scaled);
							reflect.setMagnitude(ball.getSpeed().magnitude() * 0.7);
							ball.setSpeed(reflect);
							Vector2 posdiff = Vector2.difference(robots[i].getPosition(), ball.getPosition());

							if (posdiff.magnitude() < 90) {
								ball.setPosition(Vector2.sum(ball.getPosition(), Vector2.scale(posdiff, -1)));
							}

							break;
						}
					}
				}
			}
			ball.setSpeed(Vector2.scale(ball.getSpeed(), 0.97));
			ball.setPosition(Vector2.sum(Vector2.scale(ball.getSpeed(),0.05), ball.getPosition()));

			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			/* Repaint canvas */
			GraphUI.canvas.repaint();

			if (DEBUG) {
				System.out.println("Main loop: " + elapsed + " ms");
			}
		}
	}
}
