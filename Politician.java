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
        RobotInfo[] nearbyEnemys = rc.senseNearbyRobots(type.sensorRadiusSquared,enemy);
        if (!idleMovement()){
            tryEmpower(1);
        }
        if (superpol) {
            if (neutralEClocs.size() != 0){
                attack(neutralEClocs.get(0), 1, true);
            }else if (enemyEClocs.size() != 0) {
                System.out.println("I am attacking a enemy EC");
                attack(enemyEClocs.get(0), 1, true);
            } else {
                nav.scout();
            }
        }else{
            if (nearbyEnemys.length > 0){
                Arrays.sort(nearbyEnemys, Comparator.comparingInt(x -> myloc.distanceSquaredTo(x.location)));
                RobotInfo bot = nearbyEnemys[0];
                attack(bot.location, 2, false);
            }
            if (neutralEClocs.size() != 0){
                attack(neutralEClocs.get(0), 1, true);
            }else if (enemyEClocs.size() != 0) {
                System.out.println("I am attacking a enemy EC");
                attack(enemyEClocs.get(0), 1, true);
            }
            else if (teamEClocs.size() != 0) {
                nav.orbit(teamEClocs.get(0), 100, 3);
            }
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
            if (distanceToTarget <= maxDistanceSquared) {
                    tryEmpower(distanceToTarget);
            } else {
                nav.navigate(target, limitPassability);
            }
        }nav.scout();
    }
}
