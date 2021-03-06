package battlecode2021;
import battlecode.common.*;

import java.util.Arrays;
import java.util.Comparator;

public class Politician extends Unit {
    public Politician(RobotController r) {
        super(r);

    }
    public void takeTurn() throws  GameActionException{
        super.takeTurn();
        RobotInfo[] nearbyEnemys = rc.senseNearbyRobots(type.sensorRadiusSquared,enemy);
        if (rc.getEmpowerFactor(team,0)>=5){
            tryEmpower(type.actionRadiusSquared);
        }
        if (nearbyEnemys.length > 0 && rc.getInfluence() < 100){
            Arrays.sort(nearbyEnemys, Comparator.comparingInt(x -> myloc.distanceSquaredTo(x.location)));
            for (RobotInfo bot:nearbyEnemys) {
                if (bot.type == RobotType.MUCKRAKER || (rc.getEmpowerFactor(team,1)>= 2 && bot.type == RobotType.POLITICIAN)) { //do not attack, slanderers, but create poliwaves
                     attack(bot.location, 1, false);
                }
            }
        }
        if (neutralEClocs.size() != 0){
            attack(neutralEClocs.get(0), 1, true);
        }else if (enemyEClocs.size() != 0) {
            System.out.println("I am attacking a enemy EC");
            attack(enemyEClocs.get(0), 1, true);
        } else {
            nav.orbit(teamEClocs.get(0),51,49);
        }
    }


    boolean tryEmpower(int r) throws GameActionException {
        if (rc.isReady() && rc.canEmpower(r)) {
            rc.empower(r);
            return true;
        } return false;
    }

    boolean idleMovement(){
        if (!rc.canMove(Util.randomDirection())) {
            for (Direction dir : Util.directions) {
                if (rc.canMove(dir)) {
                    return true;
                }
            }
        } else { return true; }
        return false;
    }
    int highestempower(int rounds) {
        double highestempowerfaktor = 0;
        int thei = 0;
        for (int i = 0;i <= rounds; i++){
            double nowfakt = rc.getEmpowerFactor(team,i);
            if (nowfakt > highestempowerfaktor) {
                highestempowerfaktor = nowfakt;
                thei = i;
            }
        }
        return thei;
    }
    void attack(MapLocation target, int maxDistanceSquared, boolean limitPassability) throws GameActionException {
        if (target != null) {
            int distanceToTarget = rc.getLocation().distanceSquaredTo(target);
            int robotsaurnd = rc.detectNearbyRobots(1).length;
            if (distanceToTarget <= maxDistanceSquared) {
                if (maxDistanceSquared != 1) {
                    tryEmpower(distanceToTarget);
                } else if (robotsaurnd == 1 || robotsaurnd == 4){ //be effective or be in a crowd
                    tryEmpower(distanceToTarget);
                } else {
                    nav.orbit(target,2,1);
                }
            } else {
                nav.navigate(target, limitPassability);
            }
        }nav.scout();
    }
}
