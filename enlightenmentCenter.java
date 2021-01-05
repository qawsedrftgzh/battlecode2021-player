package battlecode2021;

import battlecode.common.*;
import static battlecode2021.RobotPlayer.*;
public strictfp class enlightenmentCenter {
    public static int slandnum = 0;
    static void runEnlightenmentCenter() throws GameActionException {
        int infl = rc.getInfluence();
        System.out.println(infl);
        RobotType toBuild = RobotType.SLANDERER;
        int influence = 1;
        if (toBuild == RobotType.SLANDERER && (rc.getRoundNum()%2==0 || rc.getRoundNum() < 200)) {
            for (Direction dir : directions) {
                if (rc.canBuildRobot(toBuild, dir, influence)) {
                    rc.buildRobot(toBuild, dir, influence);
                    slandnum = slandnum + 1;
                } else {
                    break;
                }
            }
        } else {
            for (Direction dir : directions) {
                if (rc.canBuildRobot(RobotType.MUCKRAKER, dir, influence)) {
                    rc.buildRobot(RobotType.MUCKRAKER, dir, influence);
                    slandnum = slandnum + 1;
                } else {
                    break;
                }
            }
        }
        rc.bid(infl/5);
    }
}


