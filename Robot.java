package battlecode2021;
import battlecode.common.*;


public class Robot {
    RobotController rc;
    Flags flags;
    int turnCount = 0;
    Team enemy, team;
    RobotType type;
    MapLocation enemyECloc;
    RobotInfo[] nearbyTeam, nearbyEnemys, attackable;
    int actionRadius;

    public Robot(RobotController r) {
        this.rc = r;
        flags = new Flags(rc);
        team = rc.getTeam();
        enemy = team.opponent();
        type = rc.getType();
        actionRadius = rc.getType().actionRadiusSquared;

    }

    public void takeTurn() throws GameActionException {
        turnCount += 1;
        nearbyTeam = rc.senseNearbyRobots(type.sensorRadiusSquared, team);
        nearbyEnemys = rc.senseNearbyRobots(type.sensorRadiusSquared, enemy);
        attackable = rc.senseNearbyRobots(actionRadius, enemy);
    }
}
