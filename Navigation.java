package battlecode2021;

import battlecode.common.*;

public class Navigation {
    RobotController rc;
    Direction scoutDir;

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
        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        } else {
            return false;
        }
    }

    public Direction rotate(Direction dir,int grade) {
        if (grade==0){return dir;}
        else{
            if (grade>0){
                for (int i = 0;i<grade;i++) {
                    dir.rotateRight();
                }
            } else {
                for (int i = 0;i<grade;i++) {
                    dir.rotateLeft();
                }
            }
        }return dir;
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
                    tryMove(rotate(myloc.directionTo(loc), -1));
                    tryMove(rotate(myloc.directionTo(loc), 2));
                    tryMove(rotate(myloc.directionTo(loc), -2));
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
                    if(!tryMove(rotate(dir, 1))){
                    if(!tryMove(rotate(dir, -1))){
                    if(!tryMove(rotate(dir, 2))){
                    if(!tryMove(rotate(dir, -2))){
                    }}}}
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
}
