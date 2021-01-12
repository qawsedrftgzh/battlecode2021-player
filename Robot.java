package battlecode2021;
import battlecode.common.*;


public class Robot {
    RobotController rc;
    Flags flags;
    int turnCount = 0;
    Team enemy, team;
    RobotType type;
    MapLocation enemyECloc;
    RobotInfo[] nearbyTeam, nearbyEnemys, attackable, all;
    int actionRadius, sensorRadius, detectionRadius;
    int round;

    public Robot(RobotController r) {
        this.rc = r;
        flags = new Flags(rc);
        team = rc.getTeam();
        enemy = team.opponent();
        type = rc.getType();
        actionRadius = type.actionRadiusSquared;
        sensorRadius = type.sensorRadiusSquared;
        detectionRadius = type.detectionRadiusSquared;

    }

    public void takeTurn() throws GameActionException {
        turnCount += 1;
        round = rc.getRoundNum();
        nearbyTeam = rc.senseNearbyRobots(sensorRadius, team);
        nearbyEnemys = rc.senseNearbyRobots(sensorRadius, enemy);
        attackable = rc.senseNearbyRobots(actionRadius, enemy);
        all = rc.senseNearbyRobots(sensorRadius);
    }
}
