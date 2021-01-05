package examplefuncsplayer;
import battlecode.common.*;
import static examplefuncsplayer.RobotPlayer.*;
public strictfp class politician {
    static void runPolitician() throws GameActionException {
        Team enemy = rc.getTeam().opponent();
        int actionRadius = rc.getType().actionRadiusSquared;
        RobotInfo[] attackable = rc.senseNearbyRobots(actionRadius, enemy);
        if (attackable.length != 0 && rc.canEmpower(actionRadius)) {
            rc.empower(actionRadius);
            return;
        }
        if (tryMove(randomDirection()));
    }
}
