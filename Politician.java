package battlecode2021;
import battlecode.common.*;

public class Politician extends Unit {
    boolean superpol = false; //if the plotician has more then 200 influence
    public Politician(RobotController r) {
        super(r);
        MapLocation born = rc.getLocation();
        if (rc.getInfluence()>=200) {superpol=true;}

    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        flags.main();
        if (!attackProtocol()) {
            if (rc.getLocation().distanceSquaredTo(ECloc) < 50) {
                nav.scout(ECloc);
            } else {
                if (!idleMovement() && rc.canEmpower(actionRadius)) { //there is no space anywhere; let's hold a speech
                    rc.empower(actionRadius);
                }
            }
        }
    }

    public void takeTurn2() throws GameActionException {
        super.takeTurn();


        if (!attackProtocol()) {
            if (!idleMovement() && rc.canEmpower(actionRadius)) { //there is no space anywhere; let's hold a speech
                rc.empower(actionRadius);
            }
            //flags.main();
        }
    }

    boolean attackProtocol() throws GameActionException {
        RobotInfo[] neabyRobots = rc.senseNearbyRobots(4);
        for (RobotInfo rb : neabyRobots) {
            if (rb.type == RobotType.ENLIGHTENMENT_CENTER && rc.canEmpower(2) && rb.team != rc.getTeam()){
                System.out.println("Enemy EC found");
                rc.empower(4);
                return true;
            }
        }
        RobotInfo[] attackable = rc.senseNearbyRobots(actionRadius,rc.getTeam().opponent());
        if (attackable.length != 0 && rc.canEmpower(actionRadius)) {
            rc.empower(actionRadius);
            return true;
        } else {
            RobotInfo[] allenmt = rc.senseNearbyRobots(type.detectionRadiusSquared);
            for (RobotInfo bot : allenmt) {
                if (bot.team != rc.getTeam() && bot.type == RobotType.ENLIGHTENMENT_CENTER) {
                    nav.navigate(bot.location);
                    return true;
                }
            }if (enemyloc!=null){nav.navigate(enemyloc);}
            for (RobotInfo bot : allenmt) {
                if (bot.team != rc.getTeam()) {
                    nav.navigate(bot.location);
                    return true;
                }
            }
        }
        return false;
    }

    boolean idleMovement() throws GameActionException {
        if (!nav.tryMove(Util.randomDirection())) {
            for (Direction dir : Util.directions) {
                if (nav.tryMove(dir)) {
                    return true;
                }
            }
        } else { return true; }
        return false;
    }
}
