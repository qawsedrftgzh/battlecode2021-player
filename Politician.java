package battlecode2021;
import battlecode.common.*;

import javax.sound.sampled.Line;

public class Politician extends Unit {
    boolean superpol = false; //if the plotician has more then 200 influence
    public Politician(RobotController r) {
        super(r);
        MapLocation born = rc.getLocation();
        if (rc.getInfluence()>=200) {superpol=true;}

    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        if (enemyECloc != null) {
            attack(enemyECloc, 1);
        } else if (!nav.runaway(ECloc)) {
            if (!idleMovement() && rc.canEmpower(actionRadius)) { //there is no space anywhere; let's hold a speech
                rc.empower(actionRadius);
            }
        }
        /**
        if (!attackProtocol()) {
            if (rc.getLocation().distanceSquaredTo(ECloc) < 50) {
                nav.scout(ECloc);
            } else {
                if (!idleMovement() && rc.canEmpower(actionRadius)) { //there is no space anywhere; let's hold a speech
                    rc.empower(actionRadius);
                }
            }
        } **/
    }

    boolean attackProtocol() throws GameActionException {
        if (attackable.length != 0 && rc.canEmpower(actionRadius)) {
            rc.empower(actionRadius);
            return true;
        } else {
            for (RobotInfo bot : nearbyEnemys) {
                if (bot.type == RobotType.ENLIGHTENMENT_CENTER) {
                    enemyECloc = bot.location;
                    return true;
                }
            }
            for (RobotInfo bot : nearbyEnemys) {
                if (bot.team != rc.getTeam()) {
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

    void attack(MapLocation target, int maxDistanceSquared) throws GameActionException {
        int distanceToTarget = myloc.distanceSquaredTo(target);
        if (distanceToTarget <= maxDistanceSquared) {
            if (rc.isReady() && rc.canEmpower(distanceToTarget)) {
                rc.empower(distanceToTarget);
            }
        } else {
            nav.navigate(target);
        }
    }
}
