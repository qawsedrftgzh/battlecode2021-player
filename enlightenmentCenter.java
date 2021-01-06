package battlecode2021;

import battlecode.common.*;
import static battlecode2021.RobotPlayer.*;
public strictfp class enlightenmentCenter {
    public static int slandnum = 0;
    public static double bid = 10;
    public static int voteslastround=0;
    public static int votesthisround=0;
    static void runEnlightenmentCenter() throws GameActionException {
        rc.getTeamVotes();
        int infl = rc.getInfluence();
        System.out.println(infl);
        RobotType toBuild = RobotType.SLANDERER;
        int influence = infl/4;
        if (influence < 20 ) {
            influence = 20;
        }if ((rc.getRoundNum() % 3 == 2 && rc.getRoundNum() < 200) || rc.getRoundNum() % 12 == 0) {
            for (Direction dir : directions) {
                if (rc.canBuildRobot(RobotType.MUCKRAKER, dir, influence/2)) {
                    rc.buildRobot(RobotType.MUCKRAKER, dir, influence/2);
                } else {
                    break;
                }
            }
        } if (rc.getRoundNum() % 3 == 1) {
            for (Direction dir : directions) {
                if (rc.canBuildRobot(RobotType.POLITICIAN, dir, influence)) {
                    rc.buildRobot(RobotType.POLITICIAN, dir, influence);
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

        if (votesthisround != voteslastround) {
            bid = bid * 1.5;
        } else if (rc.getRoundNum() % 5 == 0) {
            bid = bid-1;
        } if (bid < 5) {
            bid = getRandomNumberInRange(5,10);
        }
        System.out.println("I will bid"+bid);
        if (rc.canBid((int) bid)) {
            rc.bid((int) bid);
        }
        voteslastround = votesthisround;
    }
}


