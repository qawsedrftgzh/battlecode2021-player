package battlecode2021;

import battlecode.common.*;
import static battlecode2021.RobotPlayer.*;
public strictfp class slanderer {
    public static Team enemy = rc.getTeam().opponent();
    static void runSlanderer() throws GameActionException {
        RobotInfo[] bots = rc.senseNearbyRobots(RobotType.SLANDERER.detectionRadiusSquared,enemy);
        for (RobotInfo bot : bots) {
            if (bot.type == RobotType.MUCKRAKER){
                tryMove(bot.location.directionTo(rc.getLocation()));
            }
        } tryMove(randomDirection());
    }
}

