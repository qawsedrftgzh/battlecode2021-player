package battlecode2021;

import battlecode.common.*;
import static battlecode2021.RobotPlayer.*;
public strictfp class politician {
    public static MapLocation born = rc.getLocation();
    static void runPolitician() throws GameActionException {
        Team enemy = rc.getTeam().opponent();

        int actionRadius = rc.getType().actionRadiusSquared;
        RobotInfo[] attackable = rc.senseNearbyRobots(actionRadius, enemy);
        for (RobotInfo bot : attackable) {
            if (bot.type == RobotType.ENLIGHTENMENT_CENTER) {
                rc.empower(rc.getLocation().distanceSquaredTo(bot.location));
            }
        }
        if (attackable.length != 0 && rc.canEmpower(actionRadius)) {
            rc.empower(actionRadius);
            return;
        }
        boolean good = tryMove(randomDirection());
        if (good == false){
            for (Direction dir : directions) {
                if (tryMove(dir) == true) {
                    good = true;
                    break;
                }
            }
        } if (good == false) { //there is no space anywhere; let's hold a speech
            rc.empower(actionRadius);
        }
    }
}
