package battlecode2021;
import battlecode.common.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class Robot {
    RobotController rc;
    Flags flags;
    int turnCount = 0;
    Team enemy, team;
    RobotType type;
    ArrayList<MapLocation> enemyEClocs = new ArrayList<>();
    ArrayList<MapLocation> neutralEClocs = new ArrayList<>();
    ArrayList<MapLocation> teamEClocs = new ArrayList<>();
    Queue<MapLocation> neutralECqu = new LinkedList<>();
    Queue<MapLocation> enemyECqu = new LinkedList<>();
    Queue<MapLocation> teamECqu = new LinkedList<>();
    MapLocation bornhere;
    RobotInfo[]  nearbyRobots, attackable;
    int actionRadius, sensorRadius, detectionRadius;
    int round;

    public Robot(RobotController r) {
        this.rc = r;
        flags = new Flags(rc);
        team = rc.getTeam();
        enemy = team.opponent();
        type = rc.getType();
        if (!(type == RobotType.ENLIGHTENMENT_CENTER)){
            bornhere = rc.getLocation();
        }
        actionRadius = type.actionRadiusSquared;
        sensorRadius = type.sensorRadiusSquared;
        detectionRadius = type.detectionRadiusSquared;
    }

    public void takeTurn() throws GameActionException {
        turnCount += 1;
        round = rc.getRoundNum();
        // TODO \/ rewrite if there are bytecode problems
        nearbyRobots = rc.senseNearbyRobots(sensorRadius);
        attackable = rc.senseNearbyRobots(actionRadius, enemy);
    }
}
