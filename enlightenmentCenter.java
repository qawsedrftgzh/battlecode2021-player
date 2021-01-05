package examplefuncsplayer;
import battlecode.common.*;
import static examplefuncsplayer.RobotPlayer.*;
public strictfp class enlightenmentCenter {
    static void runEnlightenmentCenter() throws GameActionException {
        RobotType toBuild = randomSpawnableRobotType();
        int infl = rc.getInfluence();
        System.out.println(infl);
        if (infl <= 100) {
            toBuild = RobotType.SLANDERER;
        } else {
            toBuild = RobotType.MUCKRAKER;
        }
        int influence = rc.getInfluence()/3;
        for (Direction dir : directions) {
            if (rc.canBuildRobot(toBuild, dir, influence)) {
                rc.buildRobot(toBuild, dir, influence);
            } else {
                break;
            }
        }
        rc.bid(rc.getInfluence()/2);
    }
}


