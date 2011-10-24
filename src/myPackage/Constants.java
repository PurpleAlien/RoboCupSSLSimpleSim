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

public interface Constants {

	public static final int YELLOW = 0;
	public static final int BLUE = 1;

	public static final int TEAMCOLOUR = YELLOW;

	public static final Boolean DEBUG = false;
	public static final Boolean DEBUG_ROBOT = false;
	public static final Boolean DEBUG_RRT = false;

	public static final int OWNROBOTCOUNT = 5;
	public static final int OPPROBOTCOUNT = 5;

	public static final int ROBOT1 = 0;
	public static final int ROBOT2 = 1;
	public static final int ROBOT3 = 2;
	public static final int ROBOT4 = 3;
	public static final int ROBOT5 = 4;

	public static final int OPPONENT1 = 0;
	public static final int OPPONENT2 = 1;
	public static final int OPPONENT3 = 2;
	public static final int OPPONENT4 = 3;
	public static final int OPPONENT5 = 4;

	public static final int BALL_TYPE = 1;
	public static final int ROBOT_TYPE = 8;
	public static final int OPPONENT_TYPE = 2;

	public static final Boolean SWAP = false;

	public static final int FIELD_WIDTH = 4800;
	public static final int FIELD_HEIGHT = 3300;
	public static final int FIELD_OVERSHOOT = 200;

	public static final int XOFFSET = FIELD_WIDTH / 2;
	public static final int KICKALIGNDISTANCE = 300;

	public static final int ROBOTMAXVELOCITY = 200;
	public static final int ROBOTMAXROTATEVELOCITY = 300;
}
