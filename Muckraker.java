package battlecode2021;
import battlecode.common.*;

import java.util.Random;

public class Muckraker extends Unit {
    boolean explorer = false;
    public Muckraker(RobotController r) {
        super(r);
        if (r.getInfluence() == 1){
            explorer = true;
        }
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        int disttoenhq = 20000000; // distance to nearest enemy HQ(EC)
        if (enemyEClocs.size() > 0) {
            disttoenhq = rc.getLocation().distanceSquaredTo(enemyEClocs.get(0));
        }

        for (RobotInfo robot : attackable) {
            if (robot.type.canBeExposed()) {
                if (tryExpose(robot.ID)) {
                    break;
                }
            }
        }

        if (disttoenhq >= 5) {
            if (teamEClocs.size() > 0 && myloc.distanceSquaredTo(teamEClocs.get(0)) <= 50) {
                for (RobotInfo rbi : nearbyRobots) {
                    if (rbi.team == enemy && rbi.location.distanceSquaredTo(teamEClocs.get(0)) < 100 && rbi.location.distanceSquaredTo(teamEClocs.get(0)) > 5) {
                        nav.navigate(rbi.location.add(rbi.location.directionTo(teamEClocs.get(0))), false);
                    }
                }
            }
            if (teamEClocs.get(0).distanceSquaredTo(myloc) <= 50){ //kein muckspam am eigenen EC
                nav.runaway(ECloc);
            }

            for (RobotInfo robot : nearbyRobots) {
                // It's a enemy... go get them!
                if (robot.team == enemy && robot.type.canBeExposed()) { //Dont follow muckrakers, to prevent muckracer running around theirselves
                    nav.navigate(robot.location, true);
                }
                break;
            }

            if (enemyEClocs.size() != 0) {
                System.out.println("I am attacking a enemy EC");
                nav.navigate(enemyEClocs.get(0), false);
            }
            if (myloc == bornhere) {
                nav.scout();
            }
            if (!explorer) {
                int closest = Integer.MAX_VALUE;
                RobotInfo closestbot = null;
                if (enemyEClocs.size() != 0 && (round % 5 == 0 || round % 67 <= 50)) {
                    nav.navigate(myloc.add(myloc.directionTo(enemyEClocs.get(0))), false);
                }
                for (RobotInfo bot : rc.senseNearbyRobots(25, team)) {
                    if (bot.type == RobotType.MUCKRAKER && bot.location.distanceSquaredTo(myloc) <= closest) {
                        closest = bot.location.distanceSquaredTo(myloc);
                        closestbot = bot;
                    }
                }
                if (closestbot != null) {
                    if (!nav.runaway(closestbot.location)) {

                    }
                }
                for (Direction dir : Direction.allDirections()) {
                    if (!rc.onTheMap(myloc.add(dir).add(dir))) {
                        nav.runaway(myloc.add(dir));
                    }
                }
            } else {
                if (enemyEClocs.size() == 0) {
                    nav.scout();
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

    boolean tryExpose(int id) throws GameActionException {
        if (rc.isReady() && rc.canExpose(id)) {
            rc.expose(id);
            return true;
        }
        return false;
    }
}


