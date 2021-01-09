package battlecode2021;

import battlecode.common.*;

public class Navigation {
    RobotController rc;

    public Navigation(RobotController r) {
        rc = r;
    }

    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
   public boolean tryMove(Direction dir) throws GameActionException {
        System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        } else {
            return false;
        }
    }

    public Direction rotate(Direction dir,int grade) {
        int pos = -1;
        for(int i = 0; i < Util.directions.length; i++) {
            if(Util.directions[i] == dir) {
                pos = i;
                break;
            }
        }
        return Util.directions[(pos+grade)%8];
    }

    boolean navigate(MapLocation loc) throws GameActionException {
        if (loc != null) {
            MapLocation myloc = rc.getLocation();
            if (myloc == loc) {
                return true;
            } else {
                if (tryMove(myloc.directionTo(loc))) {
                    return false;
                } else {
                    tryMove(rotate(myloc.directionTo(loc), 1));
                    tryMove(rotate(myloc.directionTo(loc), 2));
                    tryMove(rotate(myloc.directionTo(loc), 3));
                    tryMove(rotate(myloc.directionTo(loc), 4));
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    boolean runaway(MapLocation loc) throws GameActionException {
        if (loc != null) {
            MapLocation myloc = rc.getLocation();
            if (myloc == loc) {
                return true;
            } else {
                Direction dir = loc.directionTo(myloc);
                if (tryMove(dir)) {
                    return false;
                } else {
                    tryMove(rotate(dir, 1));
                    tryMove(rotate(dir, 2));
                    tryMove(rotate(dir, 3));
                    tryMove(rotate(dir, 4));
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    boolean scout(MapLocation escpoint) throws GameActionException {
       MapLocation loc = rc.getLocation();
       Direction dir = loc.directionTo(escpoint).opposite();
       return navigate(loc.add(dir));
    }
}
