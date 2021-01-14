package battlecode2021;
import battlecode.common.*;

import javax.sound.sampled.Line;

public class Politician extends Unit {
    boolean free = false; //wenn ein politiker ausserhalb des verteidigungsringes ist
    boolean superpol = false; //if the plotician has more then 200 influence
    public Politician(RobotController r) {
        super(r);
        if (rc.getInfluence()>=200) {superpol=true;}

    }
    public void takeTurn() throws  GameActionException{
        super.takeTurn();
        if (rc.getLocation() == bornhere) {
            System.out.println("this shouldnt happen so often");
        }
        if (nearbyEnemys.length > 0) {
            tryEmpower(actionRadius);
        }
        attack(bornhere,2);
        System.out.println(bornhere);
    }
    public void takeTurn2() throws GameActionException {
        super.takeTurn();
        if (enemyECloc != null) {
            System.out.println("I am attacking a enemy EC");
            attack(enemyECloc, 1);
        } else {
            int mupol = 0; //muckracer and politicians in a radius of 10
            for (RobotInfo bot : rc.senseNearbyRobots(10,team)){
                if (bot.type == RobotType.MUCKRAKER || bot.type == RobotType.POLITICIAN){
                    mupol++;
                }
            }
            System.out.println("noncreative debug message");
            if (rc.getLocation() == bornhere){
                System.out.println("this shouldnt happen so often");
                if (!nav.tryMove(Util.randomDirection())) {
                    for (Direction dir : Util.directions) {
                        nav.tryMove(dir);
                    }
                }
            }else if ((mupol >= 5 && rc.getLocation().distanceSquaredTo(bornhere) >= 80)|| free){
                nav.runaway(bornhere);
                free = true;
            } else if (rc.getLocation().distanceSquaredTo(bornhere) <= 100) {
                nav.runaway(bornhere);
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

    boolean tryEmpower(int r) throws GameActionException {
        if (rc.isReady() && rc.canEmpower(r)) {
            rc.empower(r);
            return true;
        } return false;
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
        int distanceToTarget = rc.getLocation().distanceSquaredTo(target);
        if (distanceToTarget <= maxDistanceSquared) {
            if (rc.isReady() && rc.canEmpower(distanceToTarget)) {
                rc.empower(distanceToTarget);
            }
        } else {
            nav.navigate(target);
        }
    }
}
