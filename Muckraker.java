package battlecode2021;
import battlecode.common.*;

public class Muckraker extends Unit {

    MapLocation born = rc.getLocation();
    Team enemy = rc.getTeam().opponent();
    public Muckraker(RobotController r) {
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        boolean move = true;
        int actionRadius = rc.getType().actionRadiusSquared;
        for (RobotInfo robot : rc.senseNearbyRobots(actionRadius, enemy)) {
            if (robot.type.canBeExposed()) {
                // It's a slanderer... go get them!
                // nav.navigate(robot.location);
                if (rc.canExpose(robot.location)) {
                    rc.expose(robot.location);
                    break;
                }
            } break;
        }if (move){
        for (RobotInfo robot : rc.senseNearbyRobots(30, enemy)) {
            // It's a enemy... go get them!
            if (robot.type != RobotType.MUCKRAKER) { //Dont follow muckrakers, to prevent muckracer running around theirselves
                nav.navigate(robot.location);
            }
            break;
        }
        nav.tryMove(Util.randomDirection());
        flags.main();
    }}
    public void takeTurn2() throws GameActionException{
        nav.navigate(new MapLocation(0,0));
    }
}


