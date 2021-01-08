package battlecode2021;
import battlecode.common.*;

import java.util.Random;

public strictfp class RobotPlayer {
    static RobotController rc;
    static final Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST,
    };

    static final RobotType[] spawnableRobot = {
            RobotType.POLITICIAN,
            RobotType.SLANDERER,
            RobotType.MUCKRAKER,
    };
    static int turnCount;
    static MapLocation enemyloc = null;
    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        RobotPlayer.rc = rc;

        turnCount = 0;

        System.out.println("I'm a " + rc.getType() + " and I just got created!");
        while (true) {
            turnCount += 1;
            // Try/catch blocks stop unhandled exceptions, which cause your robot to freeze
            try {
                // Here, we've separated the controls into a different method for each RobotType.
                // You may rewrite this into your own control structure if you wish.
                System.out.println("I'm a " + rc.getType() + "! Location " + rc.getLocation());
                switch (rc.getType()) {
                    case ENLIGHTENMENT_CENTER: enlightenmentCenter.runEnlightenmentCenter(); break;
                    case POLITICIAN:           politician.runPolitician();          break;
                    case SLANDERER:            slanderer.runSlanderer();            break;
                    case MUCKRAKER:            muckraker.runMuckraker();           break;
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns a random Direction.
     *
     * @return a random Direction
     */
    static Direction randomDirection() {
        return Util.directions[(int) (Math.random() * Util.directions.length)];
    }

    /**
     * Returns a random spawnable RobotType
     *
     * @return a random RobotType
     */
    static RobotType randomSpawnableRobotType() {
        return Util.spawnableRobot[(2)];
    }
    //static Boolean ()
    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static Direction rotate(Direction dir,int grade) {
        int pos = -1;
        for(int i = 0; i < Util.directions.length; i++) {
            if(directions[i] == dir) {
                pos = i;
                break;
            }
        }
        return directions[(pos+grade)%8];
    }
    static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
    static boolean tryMove(Direction dir) throws GameActionException {
        System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        } else {
            return false;
        }
    }
    static boolean navigate(MapLocation loc) throws GameActionException {
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
    static boolean runaway(MapLocation loc) throws GameActionException {
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
}
