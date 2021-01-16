package battlecode2021;
import battlecode.common.*;

import java.util.Random;

public class Muckraker extends Unit {
    boolean free = false; //wenn ein muckraker ausserhalb des verteidigungsringes ist
    boolean explorer = false;
    public Muckraker(RobotController r) {
        super(r);
        if (r.getRoundNum() <=  300){
            explorer = true;
        }
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        int disttoenhq = 20000000;
        if (enemyECloc != null) {
            disttoenhq = rc.getLocation().distanceSquaredTo(enemyECloc);
        }
        if (disttoenhq >= 5) {
            for (RobotInfo robot : attackable) {
                if (robot.type.canBeExposed()) {
                    // It's a slanderer... go get them!
                    // nav.navigate(robot.location);
                    if (rc.canExpose(robot.location)) {
                        rc.expose(robot.location);
                        break;
                    }
                }
                break;
            }
            for (RobotInfo robot : nearbyEnemys) {
                // It's a enemy... go get them!
                if (robot.type.canBeExposed()) { //Dont follow muckrakers, to prevent muckracer running around theirselves
                    nav.navigate(robot.location, true);
                }
                break;
            }
            for (RobotInfo robot : nearbyEnemys) {
                if (robot.type == RobotType.ENLIGHTENMENT_CENTER) {
                    nav.navigate(robot.location, false);
                    enemyECloc = robot.location;
                }
            }
            if (enemyECloc != null) {
                System.out.println("I am attacking a enemy EC");
                nav.navigate(enemyECloc, false);
            }
            if (rc.getLocation() == bornhere) {
                if (!nav.tryMove(Util.randomDirection())) {
                    for (Direction dir : Util.directions) {
                        nav.tryMove(dir);
                    }
                }
            }
            if (!explorer) {
                int closest = 20000;
                RobotInfo closestbot = null;
                for (RobotInfo bot : rc.senseNearbyRobots(25, team)) {
                    if (bot.type == RobotType.MUCKRAKER && bot.location.distanceSquaredTo(myloc) <= closest) {
                        closest = bot.location.distanceSquaredTo(myloc);
                        closestbot = bot;
                    }
                }
                if (closestbot != null) {
                    nav.runaway(closestbot.location);
                }
                for (Direction dir : Direction.allDirections()) {
                    if (!rc.onTheMap(myloc.add(dir).add(dir))) {
                        nav.runaway(myloc.add(dir));
                    }
                }
            } else {
                if (enemyECloc == null) {
                    nav.scout();
                } else {
                    nav.navigate(ECloc,true);
                }
            }
        } else {
            for (RobotInfo robot : attackable) {
                if (robot.type.canBeExposed()) {
                    // It's a slanderer... go get them!
                    // nav.navigate(robot.location);
                    if (rc.canExpose(robot.location)) {
                        rc.expose(robot.location);
                        break;
                    }
                }
            }
        }
    }
}


