package consoleVersion1;

/**
 * Line class representing a line segment in a 2D space.
 * @author Zichen Liao
 */
public class Line {
	private double[] coords;		// coordinates of line (x1, y1, x2, y2)
	private double[] xy;			// xy point used in calculation
	private double gradient;	// gradient of line
	private double offset;		// offset
	private double x1, y1, x2, y2;
	/**
	 * Constructs a basic line.
	 */
	Line() {
		this(0,0,1,0);
	}
	/** 
	 * Constructs a line from x1,y1 to x2,y2.
	 * @param x1 x-coordinate of the first endpoint
	 * @param y1 y-coordinate of the first endpoint
	 * @param x2 x-coordinate of the second endpoint
	 * @param y2 y-coordinate of the second endpoint
	 */
	Line(double x1, double y1, double x2, double y2) {
		this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
		coords = new double[] {x1, y1, x2, y2};	// store end points in coords
		xy = new double[] {x1, y1};				// initialise xy to one point
	}
	/**
	 * Constructs a line whose x1, y1, x2, y2 coordinates are in array cs.
	 * @param cs array containing coordinates
	 */
	Line(double [] cs) {
		this(cs[0], cs[1], cs[2], cs[3]);
	}
	/**
	 * Constructs a line whose x1, y1, x2, y2 coordinates are in array cs.
	 * @param cs array containing coordinates
	 */
	Line(int [] cs) {
		this(cs[0], cs[1], cs[2], cs[3]);
	}
	
	/**
	 * Calculates distance from x1,y1 to x2,y2.
	 * @param x1 x-coordinate of the first point
	 * @param y1 y-coordinate of the first point
	 * @param x2 x-coordinate of the second point
	 * @param y2 y-coordinate of the second point
	 * @return distance between the two points
	 */
	static double distance(double x1, double y1, double x2, double y2) {
		return (double) Math.sqrt(((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2)));
	}
	
	/**
	 * Calculates the length of the line.
	 * @return length of the line
	 */
	public double lineLength() {
		return distance(coords[0], coords[1], coords[2], coords[3]);
	}
	/**
	 * Returns the value of the xy point.
	 * @return array of two doubles representing the coordinates
	 */
	public double[] getXY() {
		return new double[]{x1, y1, x2, y2};
	}
	/**
	 * Returns the calculated gradient of the line, m as in y = mx + c.
	 * @return gradient of the line
	 */
	public double getGradient() {
		return gradient;
	}
	/**
	 * Returns the calculated offset of the line, c as in y = mx + c.
	 * @return offset of the line
	 */
	public double getOffset() {
		return offset;
	}
	/** 
	 * Calculates the gradient and offset of the line.
	 * Assumes that the line is not vertical.
	 */
	private void calcGradOff() {
		gradient = (double) (coords[3] - coords[1]) / (double) (coords[2] - coords[0]);
		offset = coords[3] - gradient * coords[2];
	}
	/**
	 * Calculates the y value of the line, using pre-calculated gradient and offset.
	 * @param x x-coordinate
	 * @return y value calculated as mx + c
	 */
	public double calcY(double x) {
		return (double) Math.round(gradient*x + offset);
	}
	/**
	 * Tests if the line is vertical (x coordinates are the same).
	 * @return true if the line is vertical, false otherwise
	 */
	private boolean isVertical() {
		return coords[2]==coords[0];
	}
	/** 
	 * Checks if value v is between v1 and v2.
	 * @param v value to check
	 * @param v1 lower bound
	 * @param v2 upper bound
	 * @return true if v is between v1 and v2, false otherwise
	 */
	private boolean isBetween(double v, double v1, double v2) {
		if (v1>v2)  return v>=v2 && v<=v1;
		else		return v>=v1 && v<=v2;
	}
	/**
	 * Checks if point xyp is on the line (i.e., between its start and end coordinates).
	 * @param xyp array where xyp[0] is x and xyp[1] is y
	 * @return true if the point is on the line, false otherwise
	 */
	public boolean isOnLine(double[] xyp) {
		return isBetween(xyp[0], coords[0], coords[2]) && isBetween(xyp[1], coords[1], coords[3]);
	}
	/**
	 * Checks if the line intersects with another line.
	 * If so, calculates the intersection point in xyp.
	 * @param otherLine the other line to check for intersection
	 * @return true if the lines intersect, false otherwise
	 */
	public boolean findintersection (Line otherLine) {
	boolean isOne = true;
		if (isVertical()) {			// is vertical line
			if (otherLine.isVertical()) isOne = false;		// two vertical lines dont intersect
			else {
				xy[0] = coords[0];							// intersect at this x
				otherLine.calcGradOff();					// calc grad and offset of other line
				xy[1] = otherLine.calcY(coords[0]);			// so find y value of intersection
			}
		}
		else {
			calcGradOff();									// calc gradient and offset
			if (otherLine.isVertical()) {
				xy = otherLine.getXY();						// get xy associated with otherLine for x
				xy[1] = calcY(xy[0]);						// y value found using this line's grad/off
			}
			else {
				otherLine.calcGradOff();					// calc gradient and offset of other line
				double ograd = otherLine.getGradient();
				if (Math.abs(ograd-gradient)<1.0e-5)		// check not parallel lines
					isOne = false;
				else {										// calculate intersection
					xy[0] = (double) Math.round( (otherLine.getOffset() - offset) / (gradient  - ograd));
					xy[1] = otherLine.calcY(xy[0]);
				}
			}	
		}
		if (isOne) isOne = isOnLine(xy) && otherLine.isOnLine(xy);
				// if found intersection, check that it is on both lines
		return isOne;
	}
	/**
	 * Calculates the distance from this line to another line.
	 * @param otherLine the other line
	 * @return distance to the other line
	 */
	public double distintersection (Line otherLine) {
		double ans = 100000000;
		if (findintersection(otherLine)) ans = distance(xy[0], xy[1], coords[0], coords[1]);
		return ans;
	}
	/**
	 * Finds the shortest distance from point (x, y) to the line.
	 * @param x x-coordinate of the point
	 * @param y y-coordinate of the point
	 * @return shortest distance from the point to the line
	 */
	public double distanceFrom (double x, double y) {
		double sdist, sdist2;				// used for holding result
					// first calculate in xy point where perpendicular to line meets x,y
		if (coords[0] == coords[2]) {    // vertical line
			xy[0] = coords[0];				// so meet at x coordinate of line
			xy[1] = y;						// and y coordinate is value of y passed
		}
		else if (coords[1] == coords[3]) {	// if horizontal line
			xy[0] = x;						// perpendicular at x 
			xy[1] = coords[1];				// and y is y coord of line
		}
		else {
			calcGradOff();					// calc gradient and offset of line
			double offset2 = y + x / gradient;		// find offset of perpendicular
													// grad of perpendendicular is -1/gradient of this
			xy[0] = (double) Math.round((offset2 - offset)/(gradient + 1.0/gradient));
			xy[1] = (double) Math.round((offset + offset2 * gradient*gradient)/(gradient*gradient + 1.0));
		}
				// now test is intersection is on line
		if (isOnLine(xy)) 
			sdist = distance(x, y, xy[0], xy[1]);			// so answer is dist^2 from x,y to interesction
		else {											// otherwise try distance^2 to end points of line
			sdist = distance(x, y, coords[0], coords[1]);
			sdist2 = distance(x, y, coords[2], coords[3]);
			if (sdist2 < sdist) sdist = sdist2;			// select shorter of two
		}
		return sdist;
	}
}