package battlecode2021;

import battlecode.common.*;
import static battlecode2021.RobotPlayer.*;
public strictfp class enlightenmentCenter {
    public static int slandnum = 0;
    static void runEnlightenmentCenter() throws GameActionException {
        int infl = rc.getInfluence();
        System.out.println(infl);
        RobotType toBuild = RobotType.SLANDERER;
        int influence = infl/4;
        if (influence < 40 ) {
            influence = 40;
        }if (rc.getRoundNum() % 7 == 0) {
            for (Direction dir : directions) {
                if (rc.canBuildRobot(RobotType.MUCKRAKER, dir, 10)) {
                    rc.buildRobot(RobotType.MUCKRAKER, dir, 10);
                } else {
                    break;
                }
            }
        } if (rc.getRoundNum() % 4 == 1) {
            for (Direction dir : directions) {
                if (rc.canBuildRobot(RobotType.POLITICIAN, dir, 10)) {
                    rc.buildRobot(RobotType.POLITICIAN, dir, 10);
                } else {
                    break;
                }
            }
        }else  {
            for (Direction dir : directions) {
                if (rc.canBuildRobot(toBuild, dir, influence)) {
                    rc.buildRobot(toBuild, dir, influence);
                    slandnum = slandnum + 1;
                } else {
                    break;
                }
            }
        }
        System.out.println("I will bid "+infl/5+1);
        rc.bid(infl/5+1);
    }
}


