package battlecode2021;

import battlecode.common.*;
import static battlecode2021.RobotPlayer.*;
public strictfp class politician {
    public static MapLocation born = rc.getLocation();
    static void runPolitician() throws GameActionException {
        Team enemy = rc.getTeam().opponent();
        int actionRadius = rc.getType().actionRadiusSquared;
        RobotInfo[] attackable = rc.senseNearbyRobots(actionRadius, enemy);
        if (attackable.length != 0 && rc.canEmpower(actionRadius)) {
            rc.empower(actionRadius);
            return;
        }
        if (born != rc.getLocation()) {
            if (!tryMove(born.directionTo(rc.getLocation()))){
                tryMove(randomDirection());
            }
        } else {
            tryMove(randomDirection());
        }
    }
}
