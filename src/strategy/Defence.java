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
package strategy;

import myPackage.*;

public class Defence extends Main implements Constants{
	
	private int defender_top;
	private int defender_bottom;
	
	public Defence(int defender_top, int defender_bottom){
		this.defender_top = defender_top;
		this.defender_bottom = defender_bottom;
	}
	
	public void defend(){
		
		int centerline = FIELD_HEIGHT / 2;
		int pos_x = 500;
		
		Vector2 top = new Vector2();
		Vector2 bottom = new Vector2();
		Vector2 target = new Vector2(FIELD_WIDTH,FIELD_HEIGHT/2);
		
		top.x = pos_x;
		bottom.x = pos_x;
		
		top.y = centerline + 200 + (ball.getPosy() - (FIELD_HEIGHT / 2))*0.5;
		bottom.y = centerline - 200 - ((FIELD_HEIGHT / 2) - ball.getPosy())*0.5;
	    
		
		/* Keep defense on half-circle */
		Vector2 c = new Vector2(0,FIELD_HEIGHT/2);
		
		double vX = top.x - c.x;
		double vY = top.y - c.y;
		double magV = Math.sqrt(vX*vX + vY*vY);
		double aX = c.x + vX / magV * 800;
		double aY = c.y + vY / magV * 800;
		
		top.x = aX;
		top.y = aY;
		
		
		vX = bottom.x - c.x;
		vY = bottom.y - c.y;
		magV = Math.sqrt(vX*vX + vY*vY);
		aX = c.x + vX / magV * 800;
		aY = c.y + vY / magV * 800;
		
		bottom.x = aX;
		bottom.y = aY;
		
		if(ball.getPosition().x < 1200){
			robots[defender_top].kick(target);
			robots[defender_bottom].kick(target);
		}else{
			robots[defender_top].move(top, true, true, false);
			robots[defender_bottom].move(bottom, true, true, false);
		}
	}
}
