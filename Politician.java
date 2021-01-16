package battlecode2021;
import battlecode.common.*;

import java.util.Arrays;
import java.util.Comparator;

public class Politician extends Unit {
    boolean superpol = false; //if the plotician has more then 200 influence
    public Politician(RobotController r) {
        super(r);
        if (rc.getInfluence()>=200) {superpol=true;}

    }
    public void takeTurn() throws  GameActionException{
        super.takeTurn();

        if (neutralECloc != null) {
            attack(neutralECloc, 2);
        } else if (nearbyEnemys.length > 0) {
            Arrays.sort(nearbyEnemys, Comparator.comparing(x -> myloc.distanceSquaredTo(x.location)));
            for (RobotInfo bot : nearbyEnemys) {
                if (bot.type != RobotType.SLANDERER && bot.type != RobotType.ENLIGHTENMENT_CENTER) {
                    attack(bot.location, 2);
                }
            }
        } else { nav.scout(); }
    }


    boolean tryEmpower(int r) throws GameActionException {
        if (rc.isReady() && rc.canEmpower(r)) {
            rc.empower(r);
            return true;
        } return false;
    }

    void attack(MapLocation target, int maxDistanceSquared) throws GameActionException {
        if (target != null) {
            int distanceToTarget = rc.getLocation().distanceSquaredTo(target);
            if (distanceToTarget <= maxDistanceSquared) {
                tryEmpower(distanceToTarget);
            } else {
                nav.navigate(target, true);
            }
        }nav.scout();
    }
}
