package battlecode2021;
import battlecode.common.*;

public class Muckraker extends Unit {

    MapLocation born = rc.getLocation();

    public Muckraker(RobotController r) {
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        if (rc.getRoundNum() <= 50) {
            rc.setFlag(999999);
        }

        int actionRadius = rc.getType().actionRadiusSquared;
        for (RobotInfo robot : rc.senseNearbyRobots(actionRadius, enemy)) {
            if (robot.type.canBeExposed()) {
                // It's a slanderer... go get them!
                // nav.navigate(robot.location);
                if (rc.canExpose(robot.location)) {
                    rc.expose(robot.location);
                    return;
                }
            }
        }
        for (RobotInfo robot : rc.senseNearbyRobots(30, enemy)) {
            // It's a enemy... go get them!
            nav.navigate(robot.location);
        }
        nav.tryMove(Util.randomDirection());
        flags.main();
    }
}


