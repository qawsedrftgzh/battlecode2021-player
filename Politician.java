package battlecode2021;
import battlecode.common.*;

public class Politician extends Unit {
    int actionRadius = rc.getType().actionRadiusSquared;

    public Politician(RobotController r) {
        super(r);
        MapLocation born = rc.getLocation();

    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        if (!attackProtocol()) {
            if (rc.getLocation().distanceSquaredTo(ECloc) < 50) {
                nav.scout(ECloc);
            } else {
                if (!idleMovement() && rc.canEmpower(actionRadius)) { //there is no space anywhere; let's hold a speech
                    rc.empower(actionRadius);
                }
            }
        }
    }

    public void takeTurn2() throws GameActionException {
        super.takeTurn();


        if (!attackProtocol()) {
            if (!idleMovement() && rc.canEmpower(actionRadius)) { //there is no space anywhere; let's hold a speech
                rc.empower(actionRadius);
            }
            //flags.main();
        }
    }

    boolean attackProtocol() throws GameActionException {
        RobotInfo[] attackable = rc.senseNearbyRobots(actionRadius);
        if (attackable.length != 0 && rc.isReady()) {
            for (RobotInfo bot : attackable) {
                if (bot.type == RobotType.ENLIGHTENMENT_CENTER && rc.canEmpower(rc.getLocation().distanceSquaredTo(bot.location)) && bot.team != rc.getTeam()) {
                    rc.empower(rc.getLocation().distanceSquaredTo(bot.location));
                    return true;
                }
            }
        } else if (attackable.length != 0 && rc.canEmpower(actionRadius)) {
            rc.empower(actionRadius);
            return true;
        } else {
            RobotInfo[] allenmt = rc.senseNearbyRobots(type.detectionRadiusSquared);
            for (RobotInfo bot : allenmt) {
                if (bot.team != rc.getTeam() && bot.type == RobotType.ENLIGHTENMENT_CENTER) {
                    nav.navigate(bot.location);
                    return true;
                }
            }
        }
        return false;
    }

    boolean idleMovement() throws GameActionException {
        if (!nav.tryMove(Util.randomDirection())) {
            for (Direction dir : Util.directions) {
                if (nav.tryMove(dir)) {
                    return true;
                }
            }
        } else { return true; }
        return false;
    }
}
