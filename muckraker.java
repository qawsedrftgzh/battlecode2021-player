package examplefuncsplayer;
import battlecode.common.*;
import static examplefuncsplayer.RobotPlayer.*;
public strictfp class muckraker {
    static void runMuckraker() throws GameActionException {
        Team enemy = rc.getTeam().opponent();
        int actionRadius = rc.getType().actionRadiusSquared;
        for (RobotInfo robot : rc.senseNearbyRobots(actionRadius, enemy)) {
            if (robot.type.canBeExposed()) {
                // It's a slanderer... go get them!
                if (rc.canExpose(robot.location)) {
                    rc.expose(robot.location);
                    return;
                }
            }
        }
        if (tryMove(randomDirection()));
    }
}


