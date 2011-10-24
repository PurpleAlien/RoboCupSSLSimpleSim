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

public class Vector2 {
	public double x;
	public double y;

	static public Vector2 sum(Vector2 u, Vector2 v) {
		return new Vector2(u.x + v.x, u.y + v.y);
	}

	static public Vector2 difference(Vector2 u, Vector2 v) {
		return new Vector2(u.x - v.x, u.y - v.y);
	}

	static public Vector2 product(double r, Vector2 u) {
		return new Vector2(r * u.x, r * u.y);
	}

	static public double dotproduct(Vector2 u, Vector2 v) {
		return u.x * v.x + u.y * v.y;
	}

	static public double angle(Vector2 u, Vector2 v) {

		double turnangle = v.angle() - u.angle();
		if (turnangle > Math.PI)
			turnangle -= 2.0 * Math.PI;
		if (turnangle < Math.PI)
			turnangle += 2.0 * Math.PI;
		return turnangle;
	}

	public double angle() {
		double atanangle;
		if (!nonzero())
			return 0;
		if (x == 0) {
			if (y < 0)
				return -Math.PI / 2;
			else
				return Math.PI / 2;
		} else {
			atanangle = Math.atan(y / x); 
			if (x < 0)
				return (atanangle + Math.PI);
			return atanangle;
		}
	}

	public Vector2() {
		x = 0.0;
		y = 0.0;
	}

	public Vector2(double ix, double iy) {
		x = ix;
		y = iy;
	}

	public Vector2(Vector2 u) {
		x = u.x;
		y = u.y;
	}

	public Vector2(double angle) {
		x = Math.cos(angle);
		y = Math.sin(angle);
	}

	public boolean nonzero() {
		return (x != 0.0 && y != 0.0);
	}

	public void copy(Vector2 v) {
		x = v.x;
		y = v.y;
	}

	public void set(double ix, double iy) {
		x = ix;
		y = iy;
	}

	public void set(double angle) {
		x = Math.cos(angle);
		y = Math.sin(angle);
	}

	public double magnitude() {
		return Math.sqrt(x * x + y * y);
	}

	public double distanceTo(Vector2 u) {
		Vector2 dv = Vector2.difference(this, u);
		return dv.magnitude();
	}

	public double setMagnitude(double newmag) 
	{
		double oldmag = magnitude();
		double multiplier;
		if (oldmag == 0.0)
			set(newmag, 0.0);
		else {
			multiplier = newmag / oldmag;
			multiply(multiplier);
		}
		return oldmag;
	}

	public double normalize()
	{
		return setMagnitude(1.0);
	}

	public static Vector2 normalize(Vector2 vector) {
		double length = vector.magnitude();

		Vector2 result = new Vector2();
		result.x = vector.x / length;
		result.y = vector.y / length;

		return result;

	}

	public static Vector2 scale(Vector2 vector, double scale) {
		Vector2 result = new Vector2();

		result.x = scale * vector.x;
		result.y = scale * vector.y;

		return result;

	}

	public void setzero() {
		set(0.0, 0.0);
	}

	public void add(Vector2 v) {
		x += v.x;
		y += v.y;
	}

	public void subtract(Vector2 v) {
		x -= v.x;
		y -= v.y;
	}

	public void multiply(double r) {
		x *= r;
		y *= r;
	}

	public void turn(double angle) {
		double c = Math.cos(angle), s = Math.sin(angle);
		double newx; 
		newx = c * x - s * y;
		y = s * x + c * y;
		x = newx;
	}

	public static double angle(Vector2 target) {
		double angle = Math.atan2(target.y, target.x);
		return angle;
	}

	public String toString() {
		return "(" + (int) x + ", " + (int) y + ")";
	}
}