package battlecode2021;
import battlecode.common.*;

public class Unit extends Robot {
    Navigation nav;
    MapLocation ECloc;
    MapLocation myloc;
    boolean move = true;

    public Unit(RobotController r) {
        super(r);
        nav = new Navigation(rc);
        ECloc = getECloc();
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        updateFlag();
        myloc = rc.getLocation();

    }

    MapLocation getECloc() {
        RobotInfo[] neabyRobots = rc.senseNearbyRobots(2, team);
        for (RobotInfo rb : neabyRobots) {
            if (rb.type == RobotType.ENLIGHTENMENT_CENTER) {
                return rb.location;
            }
        }
        return null;
    }

    void updateFlag() throws GameActionException {
        for (RobotInfo b : nearbyTeam) {
            if (rc.canGetFlag(b.ID)) {
                int flag = rc.getFlag(b.ID);
                if (flag != 0) {
                    int info = flags.getInfoFromFlag(flag);
                    switch (info) {
                        case(InfoCodes.ENEMYEC):
                            if (rc.getFlag(rc.getID()) == 0) {
                                enemyECloc = flags.getLocationFromFlag(flag);
                                flags.sendLocationWithInfo(enemyECloc, InfoCodes.ENEMYEC);
                            }
                        case(InfoCodes.TEAMEC):
                            if (flags.getLocationFromFlag(flag) == enemyECloc) {
                                flags.sendLocationWithInfo(enemyECloc, InfoCodes.TEAMEC);
                                enemyECloc = null;
                            }
                        case(InfoCodes.NEUTRALEC):
                            if (rc.getFlag(rc.getID()) == 0) {
                                neutralECloc = flags.getLocationFromFlag(flag);
                                flags.sendLocationWithInfo(neutralECloc, InfoCodes.NEUTRALEC);
                            }
                    }
                }
            }
        }
        if (enemyECloc != null && rc.getFlag(rc.getID()) == 0) {
            flags.sendLocationWithInfo(enemyECloc, InfoCodes.ENEMYEC);
        }
        if (enemyECloc != null && rc.canSenseLocation(enemyECloc)) {
            RobotInfo[] enemyAtLocation = rc.senseNearbyRobots(enemyECloc, 0, enemy);
            if (enemyAtLocation.length != 0) {
                if (enemyAtLocation[0].type != RobotType.ENLIGHTENMENT_CENTER) {
                    flags.sendLocationWithInfo(enemyECloc, InfoCodes.TEAMEC);
                    enemyECloc = null;
                }
            } else {
                RobotInfo[] teamAtLocation = rc.senseNearbyRobots(enemyECloc, 0, team);
                if (teamAtLocation.length != 0) {
                    flags.sendLocationWithInfo(enemyECloc, InfoCodes.TEAMEC);
                    enemyECloc = null;
                }
            }
        }
        if (enemyECloc == null && nearbyEnemys.length != 0) {
            for (RobotInfo b : nearbyEnemys) {
                if (b.type == RobotType.ENLIGHTENMENT_CENTER) {
                    if (!(nearbyTeam.length == 0)) {
                        enemyECloc = b.location;
                    }
                    flags.sendLocationWithInfo(b.location, InfoCodes.ENEMYEC);
                    break;
                }
            }
        }
        if (all.length != 0) {
            for (RobotInfo b : all) {
                if (b.type == RobotType.ENLIGHTENMENT_CENTER && b.team == Team.NEUTRAL) {
                    if (!(nearbyTeam.length == 0)) {
                        neutralECloc = b.location;
                    }
                    flags.sendLocationWithInfo(b.location, InfoCodes.NEUTRALEC);
                    break;
                }
            }
        }
    }
}
