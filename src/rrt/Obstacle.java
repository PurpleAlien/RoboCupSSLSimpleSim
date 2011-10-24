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
package rrt;

import myPackage.Vector2;

public class Obstacle {

	private Vector2 position;
	private double radius;

	public Obstacle() {
		position = new Vector2(0, 0);
		radius = 0;
	}

	public Obstacle(Vector2 pos, double r) {
		this.position = new Vector2();
		this.radius = r * r;
		this.position.x = pos.x;
		this.position.y = pos.y;
	}

	public Boolean collision(Vector2 point) {
		return ((Math.pow(point.x - position.x, 2) + Math.pow(point.y
				- position.y, 2)) / 2 < radius);
	}

	public static Boolean staticCollision(Vector2 one, Vector2 ball) {
		return ((Math.pow(one.x - ball.x, 2) + Math.pow(one.y - ball.y, 2)) / 2 < 9000);
	}

	public Boolean collision(Vector2 p1, Vector2 p2) {
		Vector2 v1 = new Vector2(p2.x - p1.x, p2.y - p1.y);
		Vector2 v2 = new Vector2(position.x - p1.x, position.y - p1.y);

		double v1len = Math.sqrt(v1.x * v1.x + v1.y * v1.y);
		double v2lenquadrat = v2.x * v2.x + v2.y * v2.y;
		double v2len = Math.sqrt(v2lenquadrat);
		double angle = Math.acos((v1.x * v2.x + v1.y * v2.y) / (v1len * v2len));

		if (angle > 3.1416 / 2.0)
			return (v2lenquadrat <= radius * 2);

		double x = Math.cos(angle) * v2len;
		if (x > v1len)
			return ((position.x - p2.x) * (position.x - p2.x)
					+ (position.y - p2.y) * (position.y - p2.y) <= radius * 2);

		double d = Math.sin(angle) * v2len;
		return (d * d <= radius * 2);
	}

	public Vector2 center() {
		return new Vector2(position.x, position.y);
	}

	public Vector2 getPosition() {
		return position;
	}

	public void setPosition(Vector2 position) {
		this.position.copy(position);
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius * radius;
	}

}
