package battlecode2021;

import battlecode.common.*;

public class Navigation {
    RobotController rc;
    Direction scoutDir;
    double passabilityLimit = 0.1;
    int failed = 0; // number of failed attempts to move exactly in given direction

    public Navigation(RobotController r) {
        rc = r;
        scoutDir = Util.randomDirection();
    }

    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    public boolean tryMove(Direction dir) throws GameActionException {
        if (rc.canMove(dir) && rc.sensePassability(rc.getLocation().add(dir)) > passabilityLimit) {
            rc.move(dir);
            return true;
        } else {
            return false;
        }
    }

    public boolean tryMove(Direction dir, boolean limitPassability) throws GameActionException {
        double passabilityLimit_local;
        if (limitPassability) { passabilityLimit_local = passabilityLimit; }
        else { passabilityLimit_local = 0.0; }
        if (rc.canMove(dir) && rc.sensePassability(rc.getLocation().add(dir)) > passabilityLimit_local) {
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

    boolean navigate3(MapLocation loc) throws GameActionException{
        MapLocation myloc = rc.getLocation();
        if (myloc == loc) {
            return true;
        } else {
            if (tryMove(myloc.directionTo(loc))) {
                return false;
            } else {
                tryMove(rotate(myloc.directionTo(loc),1));
                tryMove(rotate(myloc.directionTo(loc),7));
                tryMove(rotate(myloc.directionTo(loc),2));
                tryMove(rotate(myloc.directionTo(loc),6));
                return false;
            }
        }
    }

    boolean navigate2(MapLocation dest) throws GameActionException {
       MapLocation myloc = rc.getLocation();
       if (myloc == dest) {
           return true;
       }
       Direction dir = myloc.directionTo(dest);
       if (tryMove(dir)) { return true; }
       if (tryMove(dir.rotateRight())) { return true; }
       if (tryMove(dir.rotateLeft())) { return true; }
       if (tryMove(dir.rotateRight().rotateRight())) { return true; }
       if (tryMove(dir.rotateLeft().rotateLeft())) { return true; }
       return false;
    }

    boolean navigate(MapLocation dest, boolean limitPassability) throws GameActionException {
        MapLocation myloc = rc.getLocation();
        if (myloc == dest) {
            return true;
        }
        if (rc.sensePassability(myloc) > passabilityLimit || failed >= 5) {
            limitPassability = false;
            failed = 0;
        }
        Direction dir = myloc.directionTo(dest);
        if (tryMove(dir, limitPassability)) { if (failed >= 1) { failed -= 1; } return true; }
        if (tryMove(dir.rotateRight(), limitPassability)) { failed += 1; return true; }
        if (tryMove(dir.rotateLeft(), limitPassability)) { failed += 1; return true; }
        if (tryMove(dir.rotateRight().rotateRight(), limitPassability)) { failed += 1; return true; }
        if (tryMove(dir.rotateLeft().rotateLeft(), limitPassability)) { failed += 1; return true; }
        failed += 1;
        return false;
    }

    boolean runaway2(MapLocation loc) throws GameActionException {
        if (loc != null) {
            MapLocation myloc = rc.getLocation();
            if (myloc == loc) {
                return true;
            } else {
                Direction dir = loc.directionTo(myloc);
                if (tryMove(dir)) {
                    return false;
                } else {
                    if(!tryMove(rotate(dir, 1))){
                    if(!tryMove(rotate(dir, 7))){
                    if(!tryMove(rotate(dir, 2))){
                    if(!tryMove(rotate(dir, 6))){
                    }}}}
                    scout();
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
                if (navigate(myloc.add(dir), false)) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    void scout() throws GameActionException {
       if (rc.isReady()) {
           if (rc.canMove(scoutDir)) {
               rc.move(scoutDir);
               System.out.println("scouted towards " + scoutDir);
           } else {
               scoutDir = Util.randomDirection();
               System.out.println("changed scouting direction to " + scoutDir);
           }
       }
    }

    void orbit(MapLocation center, int maxDistance, int minDistance) throws GameActionException {
        MapLocation myloc = rc.getLocation();
        int distanceToCenter = myloc.distanceSquaredTo(center);
        if (distanceToCenter < minDistance) {
            navigate(myloc.add(myloc.directionTo(center).opposite()), false);
        } else if (distanceToCenter > maxDistance) {
            navigate(myloc.add(myloc.directionTo(center)), false);
        }
        Direction dir = myloc.directionTo(center).rotateLeft().rotateLeft();
        if (myloc.add(dir).distanceSquaredTo(center) > maxDistance) {
            dir = dir.rotateRight();
        } else if (myloc.add(dir).distanceSquaredTo(center) < minDistance) {
            dir = dir.rotateLeft();
        }

        if (navigate(myloc.add(dir), false)) {
            Clock.yield();
        }
    }
}
