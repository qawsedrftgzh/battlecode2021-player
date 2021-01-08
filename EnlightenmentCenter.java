package battlecode2021;
import battlecode.common.*;

public strictfp class EnlightenmentCenter extends Unit {
    public static int slandnum = 0;
    public static double bid = 10;
    public static int voteslastround=0;
    public static int votesthisround=0;

    public EnlightenmentCenter(RobotController r) {
        super(r);
    }


    // TODO fix problems

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        if (rc.getRoundNum() <= 50) {
            rc.setFlag(999999);
        }
        votesthisround = rc.getTeamVotes();
        int infl = rc.getInfluence();
        System.out.println(infl);
        RobotType toBuild = RobotType.SLANDERER;
        int influence = infl/4;
        if (influence < 20 ) {
            influence = 20;
        }if ((rc.getRoundNum() % 5 == 4 && rc.getRoundNum() < 200) || rc.getRoundNum() % 12 == 0) {
            for (Direction dir : Util.directions) {
                if (rc.canBuildRobot(RobotType.MUCKRAKER, dir, influence/2)) {
                    rc.buildRobot(RobotType.MUCKRAKER, dir, influence/2);
                } else {
                    break;
                }
            }
        } if (rc.getRoundNum() % 5 == 3 || rc.getRoundNum() % 5 == 1) {
            for (Direction dir : Util.directions) {
                if (rc.canBuildRobot(RobotType.POLITICIAN, dir, influence)) {
                    rc.buildRobot(RobotType.POLITICIAN, dir, influence);
                } else {
                    break;
                }
            }
        }else  {
            for (Direction dir : Util.directions) {
                if (rc.canBuildRobot(toBuild, dir, influence)) {
                    rc.buildRobot(toBuild, dir, influence);
                    slandnum = slandnum + 1;
                } else {
                    break;
                }
            }
        }
        System.out.println(voteslastround+"   "+votesthisround);
        if (votesthisround != voteslastround) {
            bid = bid * 1.05;
        } else if (rc.getRoundNum() % Util.getRandomNumberInRange(2,8) == 0) {
            bid = bid-1;
        } if (bid <= 5) {
            bid = Util.getRandomNumberInRange(10,20);
        }
        System.out.println("I will bid"+bid);
        if (rc.canBid((int) bid)) {
            rc.bid((int) bid);
        }
        voteslastround = votesthisround;
    }
}


