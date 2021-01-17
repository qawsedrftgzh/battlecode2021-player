package battlecode2021;
import battlecode.common.*;

public class Slanderer extends Unit {
    MapLocation born = rc.getLocation();
    MapLocation muckloc = null;
    MapLocation neareneloc = null;
    public Slanderer(RobotController r) {
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        for (RobotInfo bot : rc.senseNearbyRobots()){
            if (bot.type == RobotType.ENLIGHTENMENT_CENTER){
                if (bot.team == enemy){
                    neareneloc = bot.location;
                } else if (bot.team == Team.NEUTRAL){
                    neutralEClocs.add(bot.location);
                }

            }else if (bot.type == RobotType.MUCKRAKER && bot.team == enemy) {
                muckloc=bot.location;

            }
        }
        if (neareneloc != null) {
            if (rc.getLocation().distanceSquaredTo(neareneloc) >= 60) {
                nav.orbit(ECloc, 200, 5);
            } else {
                nav.runaway(neareneloc);
            }
        }
        if (muckloc == null) {
            if (teamEClocs.size() > 0) {
                nav.orbit(teamEClocs.get(0), 200, 5);
            }
        }
        else{
            if (rc.getLocation().distanceSquaredTo(muckloc) >= 30) {
                nav.orbit(ECloc, 200, 5);
                muckloc = null;
            } else {
                nav.runaway(muckloc);
            }
        }
    }
}