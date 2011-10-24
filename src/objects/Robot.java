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
package objects;

import java.util.Vector;

import myPackage.Constants;
import myPackage.Main;
import myPackage.Vector2;

import rrt.Obstacle;
import rrt.RRT;


public class Robot extends Main implements Runnable, Constants {

	private Vector2 position;
	private double orientation;
	private Boolean found;

	private Vector2 velocity;
	private double angleVelocity = 0;
	private int kick = 0;
	private int chipkick = 0;
	private int kickStrength = 0;

	private RRT pathplanner;
	private Vector<Obstacle> obs;

	private int id;

	public Robot(int id) {
		this.id = id;
		position = new Vector2();
		velocity = new Vector2();
		obs = new Vector<Obstacle>();
		found = false;
	}

	public Vector2 limitTarget(Vector2 target) {
		/* If target would be outside field, limit it to be inside the field. */
		Vector2 limitedTarget = new Vector2();

		limitedTarget.copy(target);

		if (limitedTarget.x < -FIELD_OVERSHOOT)
			limitedTarget.x = -FIELD_OVERSHOOT;
		else if (limitedTarget.x > FIELD_OVERSHOOT + FIELD_WIDTH)
			limitedTarget.x = FIELD_OVERSHOOT + FIELD_WIDTH;
		if (limitedTarget.y < -FIELD_OVERSHOOT)
			limitedTarget.y = -FIELD_OVERSHOOT;
		else if (limitedTarget.y > FIELD_OVERSHOOT + FIELD_HEIGHT)
			limitedTarget.y = FIELD_OVERSHOOT + FIELD_HEIGHT;

		return limitedTarget;
	}

	public void move(Vector2 target, Boolean opp_obstacle,
			Boolean own_obstacle, Boolean ball_obstacle) {

		Vector2 limitedTarget = null;
		Vector2 stepVector = null;
		if (this.getFound()) {
			/* Target */
			if (DEBUG_ROBOT) {
				System.out.println("Target: " + target.x + " , " + target.y);
				System.out.println("Robot at : " + this.getPosition());
			}

			/* Make sure target is within the field */
			limitedTarget = limitTarget(target);

			/* Vector to target from current location */
			Vector2 toTarget = Vector2.difference(target, this.getPosition());

			if (DEBUG_ROBOT)
				System.out.println("Vector to Target: " + toTarget.x + " , "
						+ toTarget.y);

			if (DEBUG_ROBOT)
				System.out.println("Limited Target: " + limitedTarget.x + ","
						+ limitedTarget.y);

			/* RRT Path Planning */
			pathplanner = new RRT();

			Vector2 from = this.getPosition();
			Vector2 to = limitedTarget;

			/* Obstacle Vector */
			obs.clear();

			if (opp_obstacle) {
				for (int i = 0; i < OPPROBOTCOUNT; i++) {
					if (opponents[i].getFound()) {
						obs.add(new Obstacle(opponents[i].getPosition(), 150));
					}
				}
			}
			if (own_obstacle) {
				for (int i = 0; i < OWNROBOTCOUNT; i++) {
					if (robots[i].getFound()
							&& robots[i].getID() != this.getID()) {
						obs.add(new Obstacle(robots[i].getPosition(), 150));
					}
				}
			}
			if (ball_obstacle)
				obs.add(new Obstacle(ball.getPosition(), 150));

			Vector2 result = pathplanner.createPath(from, to, obs, 100);

			if (DEBUG_ROBOT)
				System.out.println("Result from pathplanner: " + result);

			stepVector = Vector2.difference(result, from);

			if (DEBUG_ROBOT)
				System.out.println("Take step: " + stepVector);

			pathplanner = null;
			/* end RRT */

			this.velocity.copy(stepVector);

			/* Limit speed to maximum robot speed */
			if (this.velocity.magnitude() > ROBOTMAXVELOCITY) {
				this.velocity.setMagnitude(ROBOTMAXVELOCITY);
			}

			/* Simulation */
			Vector2 real = new Vector2();
			real.x = this.getVelocity().x / 10;
			real.y = this.getVelocity().y / 10;
			this.setPosition(Vector2.sum(this.getPosition(), real));
			
		}
	}

	public void face(Vector2 target) {
		if (this.getFound()) {
			if (DEBUG_ROBOT)
				System.out.println("Current robot: " + this.id);

			double currentOrientation;
			double targetOrientation;
			double diff;

			Vector2 toTarget = new Vector2();

			/* get current orientation (radians) */
			currentOrientation = this.getOrientation() + Math.PI;

			/* Vector to target from current location */
			toTarget = Vector2.difference(target, this.getPosition());

			/* get the angle to the target */
			targetOrientation = Vector2.angle(toTarget);

			if (DEBUG_ROBOT) {
				System.out.println("Facing Target: " + toTarget.x + " , "
						+ toTarget.y);
				System.out.println("Current: " + currentOrientation
						+ " Target: " + targetOrientation);
			}

			/* Take care of the rollover you get with radians and atan2 */
			if (Math.abs(targetOrientation - currentOrientation - 2 * Math.PI) < Math
					.abs(targetOrientation - currentOrientation))
				targetOrientation -= 2 * Math.PI;
			if (Math.abs(targetOrientation - currentOrientation + 2 * Math.PI) < Math
					.abs(targetOrientation - currentOrientation))
				targetOrientation += 2 * Math.PI;

			/*
			 * get the difference between current and target angle used to damp
			 * the rotation velocity
			 */
			diff = Math.abs(targetOrientation - currentOrientation);

			this.setOrientation(targetOrientation);

			if (currentOrientation < targetOrientation) {
				this.angleVelocity = -ROBOTMAXROTATEVELOCITY * (Math.PI - diff);
			} else {
				this.angleVelocity = ROBOTMAXROTATEVELOCITY * (Math.PI - diff);
			}
		} else {
			this.halt();
		}
	}

	public Boolean positionForKick(Vector2 target) {

		Vector2 positionAligned;
		Vector2 ballToTarget;
		Vector2 toTarget;

		Vector2 targetToBall;
		Vector2 normTargetToBall;
		Vector2 ballToAlignedPosition;
		if (this.getFound()) {
			targetToBall = Vector2.difference(target, ball.getPosition());
			normTargetToBall = Vector2.normalize(targetToBall);
			ballToAlignedPosition = Vector2.scale(normTargetToBall,
					-KICKALIGNDISTANCE);
			positionAligned = Vector2.sum(ball.getPosition(),
					ballToAlignedPosition);

			ballToTarget = Vector2.difference(ball.getPosition(), target);
			toTarget = Vector2.difference(this.getPosition(), target);

			double distanceToBall = Vector2.difference(this.getPosition(),
					ball.getPosition()).magnitude();
			double distanceBallToTarget = ballToTarget.magnitude();
			double distanceToTarget = toTarget.magnitude();

			/* Aligned */
			if ((distanceToBall + distanceBallToTarget) < (distanceToTarget + 3)) { // 20
				this.face(target); // this.face(target); this.face(target);
				return true;
			} else {
				this.move(positionAligned, true, true, true);
				return false;
			}
		} else {
			this.halt();
			return null;
		}

	}

	public void kick(Vector2 target) {
		this.chipkick = 0;
		this.kick = 1;
		this.kickStrength = 500;

		if (this.getFound()) {
			if (this.positionForKick(target)) {
				Vector2 scaledToBall;
				Vector2 positionKick;
				Vector2 toBall;

				toBall = Vector2.difference(this.getPosition(), ball
						.getPosition());
				scaledToBall = Vector2.scale(toBall, -0.25);
				positionKick = Vector2.sum(this.getPosition(), scaledToBall);
				this.move(positionKick, true, true, false);
			}
		} else {
			this.halt();
		}
	}


	public void halt() {
		this.velocity.x = 0;
		this.velocity.y = 0;
		this.angleVelocity = 0;
	}
	

	/* For Simulation Only */
	public void simUpdate(){
		/* Kick ball, or bump against it */
		if (Obstacle.staticCollision(this.getPosition(), ball
				.getPosition())) {
			if (this.getKick() >= 1) {

				Vector2 kickdir;
				if((this.getOrientation()-2 * Math.PI) <= Math.PI/2 && 
						(this.getOrientation()-2 * Math.PI) >= -Math.PI/2){
					kickdir = new Vector2(1, Math.tan(this.getOrientation()));
				}else{
					kickdir = new Vector2(-1, -Math.tan(this.getOrientation()));
				}

				kickdir.setMagnitude(2);
				ball.setSpeed(Vector2.scale(kickdir,
						this.kickStrength * 3));
			} else {
				Vector2 sum = new Vector2();
				sum.add(Vector2.scale(this.getVelocity(), -5*0.8));
				sum.add(ball.getSpeed());
				//ball.setSpeed(Vector2.scale(this.getVelocity(), -5*0.8));
				ball.setSpeed(sum);
			}
		}
	}
	
	/* Getters and Setters */

	public double getPosx() {
		return position.x;
	}

	public void setPosx(double posx) {
		this.position.x = posx;
	}

	public double getPosy() {
		return position.y;
	}

	public Vector2 getPosition() {
		return position;
	}

	public void setPosy(double posy) {
		this.position.y = posy;
	}

	public void setPosition(Vector2 pos) {
		this.position.x = pos.x;
		this.position.y = pos.y;
	}

	public double getOrientation() {
		return orientation;
	}

	public void setOrientation(double orientation) {
		this.orientation = orientation;
	}

	public Vector2 getVelocity() {
		return this.velocity;
	}

	public double getAngleVelocity() {
		return this.angleVelocity;
	}

	public int getKick() {
		return kick;
	}

	public void setKick(int kick) {
		this.kick = kick;
	}

	public int getChipkick() {
		return chipkick;
	}

	public void setChipkick(int chipkick) {
		this.chipkick = chipkick;
	}

	public int getKickStrength() {
		return this.kickStrength;
	}

	public void setKickStrength(int kickStrength) {
		this.kickStrength = kickStrength;
	}

	public int getID() {
		return this.id;
	}

	public Boolean getFound() {
		return found;
	}

	public void setFound(Boolean found) {
		this.found = found;
	}

	/* Run method for Thread */

	public void run() {

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
