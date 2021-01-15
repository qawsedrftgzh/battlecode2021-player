package battlecode2021;
import battlecode.common.*;

public class Slanderer extends Unit {
    MapLocation born = rc.getLocation();
    MapLocation muckloc = null;
    public Slanderer(RobotController r) {
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        System.out.println("Hello i am a slanderer");
        for (RobotInfo bot : nearbyEnemys) {
            if (bot.type == RobotType.MUCKRAKER) {
                nav.runaway(bot.location);
                muckloc=bot.location;

            }
        }
        System.out.println(turnCount);
        if (muckloc == null) {
            System.out.println("First");
            System.out.println("First second");
            nav.orbit(ECloc, 200, 5);
        }
        else{
            System.out.println("First");
            if (rc.getLocation().distanceSquaredTo(muckloc) >= 25) {
                System.out.println("First first");
                nav.orbit(ECloc, 200, 5);
                muckloc = null;
            } else {
                nav.runaway(muckloc);
                System.out.println("second");
            }
        }
    }
}