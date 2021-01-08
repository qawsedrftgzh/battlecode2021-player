package battlecode2021;
import battlecode.common.*;

public class Slanderer extends Unit {

    public Slanderer(RobotController r) {
        super(r);
    }
    Team enemy = rc.getTeam().opponent();

    public void takeTurn() throws GameActionException {
        if (rc.getRoundNum() <= 50) {
            rc.setFlag(999999);
        }
        /**RobotInfo[] bots = rc.senseNearbyRobots(RobotType.SLANDERER.detectionRadiusSquared,enemy);
        for (RobotInfo bot : bots) {
            if (bot.type == RobotType.MUCKRAKER){
                tryMove(bot.location.directionTo(rc.getLocation()));
            }
        }**/ nav.tryMove(Util.randomDirection());
        flags.main();
    }
}


