package battlecode2021;
import battlecode.common.*;

public class Slanderer extends Unit {
    MapLocation born = rc.getLocation();
    public Slanderer(RobotController r) {
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        System.out.println("Hello i am a slanderer");
        RobotInfo[] bots = rc.senseNearbyRobots(RobotType.SLANDERER.detectionRadiusSquared, enemy);
        for (RobotInfo bot : bots) {
            if (bot.type == RobotType.MUCKRAKER) {
                nav.runaway(bot.location);

            }
        }
        System.out.println(turnCount);
        if (turnCount<=10){
            nav.scout();
        }else if(turnCount<=300) {
            nav.runaway(born);
        }else {
            nav.navigate(born);
        }
    }
}


