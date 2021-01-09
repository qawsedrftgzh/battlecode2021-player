package battlecode2021;
import battlecode.common.*;

public class Unit extends Robot {

    Navigation nav;

    static MapLocation enemyloc = null;
    MapLocation ECloc;

    public Unit(RobotController r) {
        super(r);
        nav = new Navigation(rc);
        ECloc = getECloc();
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

    }

    MapLocation getECloc() {
        RobotInfo[] neabyRobots = rc.senseNearbyRobots(2, rc.getTeam());
        for (RobotInfo rb : neabyRobots) {
            if (rb.type == RobotType.ENLIGHTENMENT_CENTER) {
                return rb.location;
            }
        }
        return null;
    }
}
