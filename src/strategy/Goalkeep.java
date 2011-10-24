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


public class Goalkeep extends Main implements Constants{
	
	private int goalkeeper;
	private Vector2 target;
	
	public Goalkeep(int goalkeeper){
		this.goalkeeper = goalkeeper;
		target = new Vector2();
	}
	
	public void goalkeeping(){
		
		target.x = 120;
		
		target.y = (FIELD_HEIGHT / 2) + (ball.getPosy() - (FIELD_HEIGHT / 2))*0.5;
		
		
		if(target.y > FIELD_HEIGHT/2 + 220){
			target.y = FIELD_HEIGHT/2 - 220;
		}
		else if(target.y < FIELD_HEIGHT/2 - 220){
			target.y = FIELD_HEIGHT/2 + 220;
		}
		
		robots[goalkeeper].move(target,false,false,false);
	
		robots[goalkeeper].face(ball.getPosition());
		
	}

	public int getGoalkeeper() {
		return goalkeeper;
	}

	public void setGoalkeeper(int goalkeeper) {
		this.goalkeeper = goalkeeper;
	}
	
	
	
}
