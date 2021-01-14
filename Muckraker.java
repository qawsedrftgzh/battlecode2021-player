package battlecode2021;
import battlecode.common.*;

import java.util.Random;

public class Muckraker extends Unit {
    boolean free = false; //wenn ein muckraker ausserhalb des verteidigungsringes ist
    MapLocation myenemyECloc = null;
    Team enemy = rc.getTeam().opponent();
    public Muckraker(RobotController r) {
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        for (RobotInfo robot : attackable) {
            if (robot.type.canBeExposed()) {
                // It's a slanderer... go get them!
                // nav.navigate(robot.location);
                if (rc.canExpose(robot.location)) {
                    rc.expose(robot.location);
                    break;
                }
            } break;
        }

        if (myenemyECloc != null) {
            nav.navigate2(myenemyECloc);
        } else {
        for (Direction dir: Direction.allDirections()){
            if(!rc.onTheMap(myloc.add(dir).add(dir))){
                nav.runaway(myloc.add(dir));
            }
        }
        RobotInfo[] bots = rc.senseNearbyRobots();
        int closest = 20000;
        RobotInfo closestbot = null;
        for (RobotInfo bot:bots){
            if (bot.team == team && bot.type == RobotType.MUCKRAKER && bot.location.distanceSquaredTo(myloc)<=closest){
                closest = bot.location.distanceSquaredTo(myloc);
                closestbot = bot;
            }
        }
        if (closestbot!=null) {
            nav.runaway(closestbot.location);
        }
        for (RobotInfo bot:nearbyEnemys){
            if (bot.type==RobotType.ENLIGHTENMENT_CENTER){
                MapLocation myenemyECloc = bot.location;
            }
        }
        }

        /**
        for (RobotInfo robot : rc.senseNearbyRobots(30, enemy)) {
            // It's a enemy... go get them!
            if (robot.type.canBeExposed()) { //Dont follow muckrakers, to prevent muckracer running around theirselves
                nav.navigate(robot.location);
            }
            break;
        }

        int mupol = 0; //muckracer and politicians in a radius of 10
        for (RobotInfo bot : rc.senseNearbyRobots(10,team)){
            if (bot.type == RobotType.MUCKRAKER || bot.type == RobotType.POLITICIAN){
                mupol++;
            }
        }
        if (rc.getLocation() == born){
            if (!nav.tryMove(Util.randomDirection())) {
                for (Direction dir : Util.directions) {
                    nav.tryMove(dir);
                }
            }
        }else if ((mupol >= 5 && rc.getLocation().distanceSquaredTo(born) >= 80)|| free){
            nav.runaway(born);
            free = true;
        } else if (rc.getLocation().distanceSquaredTo(born) <= 100) {
            nav.runaway(born);
        }**/
    }
    public void takeTurn2() throws GameActionException{
        nav.navigate(new MapLocation(0,0));
    }
}


