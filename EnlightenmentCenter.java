package battlecode2021;
import battlecode.common.*;

public class EnlightenmentCenter extends Robot {
    public static int slandnum = 0;
    public static double bid = 10;
    public static int voteslastround=0;
    public static int votesthisround=0;

    public EnlightenmentCenter(RobotController r) {
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        if (rc.getRoundNum() <= 50) {
            rc.setFlag(999999);
        }

        votesthisround = rc.getTeamVotes();
        int infl = rc.getInfluence();
        System.out.println("Influence: " + infl);
        int influence = infl/4;
        if (influence < 20 ) {
            influence = 20;
        }

        if ((rc.getRoundNum() % 5 == 4 && rc.getRoundNum() < 200) || rc.getRoundNum() % 12 == 0) {
            tryBuild(RobotType.MUCKRAKER, null, influence/2);

        } if (rc.getRoundNum() % 5 == 3 || rc.getRoundNum() % 5 == 1) {
            tryBuild(RobotType.POLITICIAN, null, influence);

        } else  {
            tryBuild(RobotType.SLANDERER, null, influence);
            slandnum++;
        }

        System.out.println(voteslastround+"   "+votesthisround);
        if (votesthisround != voteslastround) {
            bid = bid * 1.05;
        } else if (rc.getRoundNum() % Util.getRandomNumberInRange(2,8) == 0) {
            bid = bid-1;
        } if (bid <= 5) {
            bid = Util.getRandomNumberInRange(10,20);
        }
        System.out.println("I will bid " + (int) bid);
        if (rc.canBid((int) bid)) {
            rc.bid((int) bid);
        }
        voteslastround = votesthisround;
    }

    boolean tryBuild(RobotType rt, Direction dir, int influence) throws GameActionException {
        if (rc.isReady()) {
            if (dir != null) {
                if (rc.canBuildRobot(rt, dir, influence)) {
                    rc.buildRobot(rt, dir, influence);
                    return true;
                } else {
                    return false;
                }
            } else {
                for (Direction d : Util.directions) {
                    if (rc.canBuildRobot(rt, d, influence)) {
                        rc.buildRobot(rt, d, influence);
                        return true;
                    }
                }
            }
        } return false;
    }
}


