package battlecode2021;
import battlecode.common.*;

import java.util.*;


public class Robot {
    RobotController rc;
    Flags flags;
    int turnCount = 0;
    Team enemy, team;
    RobotType type;
    ArrayList<MapLocation> enemyEClocs = new ArrayList<>();
    ArrayList<MapLocation> neutralEClocs = new ArrayList<>();
    ArrayList<MapLocation> teamEClocs = new ArrayList<>();
    // ArrayList<RobotInfo> enemyEClocs = new ArrayList<>();
    // ArrayList<RobotInfo> neutralEClocs = new ArrayList<>();
    // ArrayList<RobotInfo> teamEClocs = new ArrayList<>();
    Queue<RobotInfo> flagsQu = new LinkedList<>();
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

    MapLocation getNearestLocation(List<MapLocation> list) {
        if (list.size() > 0) {
            list.sort(Comparator.comparingInt(x -> rc.getLocation().distanceSquaredTo(x)));
            return list.get(0);
        } return null;
    }
}
