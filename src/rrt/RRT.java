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

import java.util.Iterator;
import java.util.Vector;

import myPackage.Constants;
import myPackage.Vector2;

public class RRT implements Constants {

	public static final int NODES_MAX = 100;
	public static final double P_GOAL = 0.1;
	public static final double P_WAYPOINT = 0.7;
	public static final int STEP_SIZE = 100;
	public static final int GOAL_THRESHOLD = 10000;

	private State waypoints[];
	private Vector<Obstacle> obstacles;
	private Vector<Vector2> path;
	private int waypoint_chosen;
	private int waypoints_left;
	private int lastpathlength;

	private KDNode root;

	public class State {
		public Vector2 position;
		public State parent;

		State() {
			position = new Vector2();
		}
	}

	public class KDNode {
		public State state;
		public KDNode left;
		public KDNode right;

		KDNode() {
			state = new State();
		}
	}

	public class Status {
		public State state;
		Boolean extended;

		Status() {
			state = new State();
		}
	}

	public RRT() {

		obstacles = new Vector<Obstacle>();
		path = new Vector<Vector2>();
		waypoints = new State[NODES_MAX];
		waypoints_left = NODES_MAX;
		lastpathlength = NODES_MAX;
		waypoint_chosen = -1;
		for (int i = 0; i < NODES_MAX; i++) {
			waypoints[i] = randomState();
		}
	}

	private State randomState() {
		State s = new State();

		s.position.x = Math.random() * (double) (FIELD_WIDTH + 500) / 1
				- (FIELD_WIDTH + 500) / 10.0;
		s.position.y = Math.random() * (double) (FIELD_HEIGHT + 500) / 1
				- (FIELD_HEIGHT + 500) / 10.0;
		return s;
	}

	private double distance(State current, State target) {
		return Math.pow(current.position.x - target.position.x, 2)
				+ Math.pow(current.position.y - target.position.y, 2);
	}

	private Status extend(State current, State target) {
		Status status = new Status();
		State newstate = new State();

		double l = Math.sqrt(distance(current, target));
		if (l == 0) {
			status.extended = false;
			return status;
		}
		newstate.position.x = current.position.x
				+ (target.position.x - current.position.x) / l * STEP_SIZE;
		newstate.position.y = current.position.y
				+ (target.position.y - current.position.y) / l * STEP_SIZE;
		newstate.parent = current;

		status.state = newstate;

		Iterator<Obstacle> itr = obstacles.iterator();
		while (itr.hasNext()) {
			if (itr.next().collision(newstate.position)) {
				status.extended = false;
				return status;
			}
		}
		if (waypoint_chosen != -1)
			if (distance(newstate, target) < GOAL_THRESHOLD)
				waypoints_left = waypoint_chosen;

		status.extended = true;

		return status;
	}

	private KDNode addNode(KDNode node, int d, State state) {

		if (node == null) {
			if (DEBUG_RRT)
				System.out.println("Adding node...");
			KDNode newnode = new KDNode();
			newnode.state = state;
			newnode.left = null;
			newnode.right = null;
			return newnode;
		}

		if (d > 1)
			d = 0;
		if ((d == 0 && state.position.x < node.state.position.x)
				|| (d == 1 && state.position.y < node.state.position.y)) {
			node.left = addNode(node.left, d + 1, state);
			if (DEBUG_RRT)
				System.out.println("Added left node");
			return node;
		} else {
			node.right = addNode(node.right, d + 1, state);
			if (DEBUG_RRT)
				System.out.println("Added right node");
			return node;
		}

	}

	private State nearestState(KDNode node, int d, double maxdistance,
			State target) {

		if (node == null)
			return null;

		State best;
		best = node.state;

		double mindist = distance(best, target);
		if (maxdistance != -1 && maxdistance < mindist)
			mindist = maxdistance;

		Boolean left = false;
		if (d > 1)
			d = 0;
		if ((d == 0 && target.position.x < node.state.position.x)
				|| (d == 1 && target.position.y < node.state.position.y))
			left = true;

		State state1;
		if (left)
			state1 = nearestState(node.left, d + 1, mindist, target);
		else
			state1 = nearestState(node.right, d + 1, mindist, target);

		if (state1 != null) {
			double dist1 = distance(state1, target);
			if (dist1 < mindist) {
				mindist = dist1;
				best = state1;
			}
		}

		if ((left && node.right != null) || (!left && node.left != null)) {
			double planedist = 0;
			if (d == 0)
				planedist = Math.pow(target.position.x - node.state.position.x, 2);
			if (d == 1)
				planedist = Math.pow(target.position.y - node.state.position.y, 2);

			if (planedist < mindist) {
				State state2;
				if (left)
					state2 = nearestState(node.right, d + 1, mindist, target);
				else
					state2 = nearestState(node.left, d + 1, mindist, target);

				if (state2 != null) {
					double dist2 = distance(state2, target);
					if (dist2 < mindist) {
						mindist = dist2;
						best = state2;
					}

				}

			}

		}
		return best;
	}

	private State chooseTarget(State goal) {
		waypoint_chosen = -1;
		double r = Math.random();
		if (DEBUG_RRT)
			System.out.println("The random number: " + r);
		if (r < P_GOAL)
			return goal;
		else if (waypoints_left > 0 && r < P_GOAL + P_WAYPOINT) {
			int w = (int) (Math.random() * waypoints_left);
			if (w == waypoints_left)
				w--;
			waypoint_chosen = w;
			return waypoints[w];
		}
		return randomState();
	}

	private State rrtPlan(State start, State goal, double goal_threshold,
			int nodes_max) {
		State nearest, target, extended;
		root = addNode(root, 0, start);
		nearest = start;

		while (distance(nearest, goal) > goal_threshold && nodes_max > 0) {
			nodes_max--;
			target = chooseTarget(goal);
			nearest = nearestState(root, 0, -1, target);

			Status status;
			status = extend(nearest, target);

			if (status.extended) {
				if (DEBUG_RRT)
					System.out.println("X: " + status.state.position.x + " Y: "
							+ status.state.position.y);
				extended = status.state;
				root = addNode(root, 0, extended);
			}
		}
		if (distance(nearest, goal) <= goal_threshold) {
			goal.parent = nearest;
			return goal;
		}
		nearest = nearestState(root, 0, -1, goal);

		return nearest;
	}

	public Vector2 createPath(Vector2 current_pos, Vector2 target_pos,
			Vector<Obstacle> obstacle_vec, int nodes_max) {
		obstacles = obstacle_vec;

		Vector2 escape_direction_150 = new Vector2();
		Vector2 finalPosition = new Vector2();

		State start, goal;
		goal = new State();
		start = new State();

		start.position = current_pos;
		goal.position = target_pos;

		// check if start or goal is inside an obstacle

		Boolean goal_collision = false;

		Iterator<Obstacle> iter = obstacles.iterator();
		Obstacle currentObstacle;
		while (iter.hasNext()) {
			currentObstacle = iter.next();
			if (currentObstacle.collision(start.position)) {
				if (DEBUG_RRT)
					System.out.println("Start Collide!");
				// if start is inside an obstacle, return position to leave
				// obstacle area
				Vector2 escape_direction = new Vector2();
				Vector2 obstacle_center = currentObstacle.center();
				if (Math.abs(obstacle_center.x) > FIELD_WIDTH
						|| Math.abs(obstacle_center.y) > FIELD_HEIGHT) {
					escape_direction.x = obstacle_center.x * -1.0;
					escape_direction.y = obstacle_center.y * -1.0;
				} else {
					escape_direction.x = current_pos.x - obstacle_center.x;
					escape_direction.y = current_pos.y - obstacle_center.y;
				}
				escape_direction_150.copy(escape_direction);
				escape_direction_150.setMagnitude(150);

				finalPosition.x = current_pos.x + escape_direction_150.x;
				finalPosition.y = current_pos.y + escape_direction_150.y;

				return finalPosition;

			}
			if (currentObstacle.collision(goal.position)) {
				goal_collision = true;
			}
		}

		double threshold = GOAL_THRESHOLD;

		if (goal_collision) {
			threshold = GOAL_THRESHOLD + 50000.0;
		}

		// search path
		State nearest = rrtPlan(start, goal, threshold, nodes_max);
		// copy path to path vector
		Boolean refresh_waypoints = (nearest == goal);
		int w = 0;

		State current = nearest;

		if (DEBUG_RRT) {
			System.out.println("Current: " + current.position.x + " "
					+ current.position.y);
			System.out.println("Start: " + start.position.x + " "
					+ start.position.y);
		}

		while (current != start) {
			path.addElement(current.position);
			if (refresh_waypoints)
				waypoints[w++] = current;
			current = current.parent;
		}

		path.addElement(start.position);
		if (refresh_waypoints) {
			waypoints_left = w;
			lastpathlength = w;
		} else {
			waypoints_left = lastpathlength;
		}

		// smooth
		// search point to drive to
		int driveto_index = 0;

		Vector2 first_point = path.get(path.size() - 1);
		Boolean collision = true;

		while (collision && driveto_index < path.size() /*-1*/) {
			collision = false;
			for (int i = 0; i < obstacles.size(); i++)
				if ((obstacles.elementAt(i).collision(first_point, path
						.get(driveto_index)))) {
					collision = true;
				}
			driveto_index++;
		}
		if (!collision)
			driveto_index--;

		root = null;
		if (DEBUG_RRT)
			System.out.println("Path to drive to: " + path.get(driveto_index));
		return path.get(driveto_index);
	}

}
