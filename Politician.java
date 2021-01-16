package battlecode2021;
import battlecode.common.*;
public class Politician extends Unit {
    boolean superpol = false; //if the plotician has more then 200 influence
    public Politician(RobotController r) {
        super(r);
        if (rc.getInfluence()>=200) {superpol=true;}

    }
    public void takeTurn() throws  GameActionException{
        super.takeTurn();
        for (RobotInfo bot : rc.senseNearbyRobots()) {
            if (bot.type == RobotType.ENLIGHTENMENT_CENTER && bot.team != team) {
                enemyECloc = bot.location;
            }
        }
        if (nearbyEnemys.length == 1){
            RobotInfo bot = nearbyEnemys[0];
            if (bot.location.distanceSquaredTo(rc.getLocation())<3){
                rc.empower(2);
            }else {
                nav.navigate(bot.location, false);
            }
        }
        if (neutralECloc != null){
            attack(enemyECloc, 1);
        }
        if (enemyECloc != null) {
            System.out.println("I am attacking a enemy EC");
            attack(enemyECloc, 1);
        }
        if (nearbyEnemys.length > 0) {
            tryEmpower(actionRadius);
        }
        if (!idleMovement()){
            rc.empower(1);
        }
        nav.scout();
    }


    boolean tryEmpower(int r) throws GameActionException {
        if (rc.isReady() && rc.canEmpower(r)) {
            rc.empower(r);
            return true;
        } return false;
    }

    boolean idleMovement() throws GameActionException {
        if (!rc.canMove(Util.randomDirection())) {
            for (Direction dir : Util.directions) {
                if (rc.canMove(dir)) {
                    return true;
                }
            }
        } else { return true; }
        return false;
    }

    void attack(MapLocation target, int maxDistanceSquared) throws GameActionException {
        if (target != null) {
            int distanceToTarget = rc.getLocation().distanceSquaredTo(target);
            if (distanceToTarget <= maxDistanceSquared) {
                if (rc.isReady() && rc.canEmpower(distanceToTarget)) {
                    rc.empower(distanceToTarget);
                }
            } else {
                nav.navigate(target);
            }
        }nav.scout();
    }
}
