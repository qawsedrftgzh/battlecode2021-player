package battlecode2021;
import battlecode.common.*;

public class Unit extends Robot {
    Navigation nav;
    MapLocation ECloc;
    MapLocation myloc;

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
                        case(InfoCodes.STARTATTACK):
                            if (rc.getFlag(rc.getID()) == 0) {
                                enemyECloc = flags.getLocationFromFlag(flag);
                                flags.sendLocationWithInfo(enemyECloc, InfoCodes.STARTATTACK);
                            }
                        case(InfoCodes.STOPATTACK):
                            if (flags.getLocationFromFlag(flag) == enemyECloc) {
                                flags.sendLocationWithInfo(enemyECloc, InfoCodes.STOPATTACK);
                                enemyECloc = null;
                            }
                    }
                }
            }
        }
        if (enemyECloc != null && rc.getFlag(rc.getID()) == 0) {
            flags.sendLocationWithInfo(enemyECloc, InfoCodes.STARTATTACK);
        }
        if (enemyECloc != null && rc.canSenseLocation(enemyECloc)) {
            RobotInfo[] botAtLocation = rc.senseNearbyRobots(enemyECloc, 0, enemy);
            if (botAtLocation.length != 0) {
                if (botAtLocation[0].type != RobotType.ENLIGHTENMENT_CENTER) {
                    flags.sendLocationWithInfo(enemyECloc, InfoCodes.STOPATTACK);
                    enemyECloc = null;
                }
            }
        }
    }
}
