package battlecode2021;
import battlecode.common.*;

public class Politician extends Unit {
    MapLocation born = rc.getLocation();

    public Politician(RobotController r) {
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        if (rc.getRoundNum() <= 50) {
            rc.setFlag(999999);
        }
        Team enemy = rc.getTeam().opponent();

        int actionRadius = rc.getType().actionRadiusSquared;
        RobotInfo[] attackable = rc.senseNearbyRobots(actionRadius, enemy);
        for (RobotInfo bot : attackable) {
            if (bot.type == RobotType.ENLIGHTENMENT_CENTER && rc.canEmpower(rc.getLocation().distanceSquaredTo(bot.location))) {
                rc.empower(rc.getLocation().distanceSquaredTo(bot.location));
            }
        }
        if (attackable.length != 0 && rc.canEmpower(actionRadius)) {
            rc.empower(actionRadius);
            return;
        }
        boolean good = tryMove(Util.randomDirection());
        if (good == false){
            for (Direction dir : Util.directions) {
                if (tryMove(dir) == true) {
                    good = true;
                    break;
                }
            }
        } if (good == false && rc.canEmpower(actionRadius)) { //there is no space anywhere; let's hold a speech
            rc.empower(actionRadius);
        }
        flags.main();
    }
}
