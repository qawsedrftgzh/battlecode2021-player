package battlecode2021;

import battlecode.common.*;
import static battlecode2021.RobotPlayer.*;
public strictfp class muckraker {
    public static MapLocation born = rc.getLocation();
    static void runMuckraker() throws GameActionException {
        Team enemy = rc.getTeam().opponent();
        int actionRadius = rc.getType().actionRadiusSquared;
        for (RobotInfo robot : rc.senseNearbyRobots(actionRadius, enemy)) {
            if (robot.type.canBeExposed()) {
                // It's a slanderer... go get them!
                navigate(robot.location);
                if (rc.canExpose(robot.location)) {
                    rc.expose(robot.location);
                    return;
                }
            }
        }if (born != rc.getLocation()) {
            if (!tryMove(born.directionTo(rc.getLocation()))){
                tryMove(randomDirection());
            }
        } else {
            tryMove(randomDirection());
        }
    }
}


