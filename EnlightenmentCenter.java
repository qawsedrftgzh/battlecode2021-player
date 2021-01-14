package battlecode2021;
import battlecode.common.*;

import java.util.ArrayList;

public class EnlightenmentCenter extends Robot {
    double bid = 10;
    int voteslastround=0;
    int votesthisround=0;
    int capital; // current amount of influence
    ArrayList<RobotInfo> activebots = new ArrayList<RobotInfo>();

    public EnlightenmentCenter(RobotController r) {
        super(r);
    }

    // a 2. takeTurn() function for testing
    public void takeTurn() throws GameActionException {
        super.takeTurn();
        updateActiveBots();
        updateFlag();
        capital = rc.getInfluence();

        if (enemyECloc != null) {
            for (Direction dir : Util.directions) {
                tryBuild(RobotType.POLITICIAN, dir, (int) (capital * 0.05));
            }
        }

        if (rc.getRoundNum() <=2 || (rc.getRoundNum()>=200 && capital <200 && capital>=30)){
            System.out.println("the first few rounds");
            tryBuild(RobotType.SLANDERER, null, capital);
        }else if ((capital < 1000 || rc.getRoundNum() % 4 == 0)&& capital >= 200) {
            tryBuild(RobotType.SLANDERER, null, capital/2); //trust me, this is a good amount
        }else if (capital >= 1000){
            tryBuild(RobotType.MUCKRAKER,null,capital/20);
        }
        //bidding
        if (rc.getTeamVotes() <= 750) {
            votesthisround = rc.getTeamVotes();
            if (votesthisround == voteslastround && bid < capital) {
                bid = bid * 1.01;
            }
            if (bid <= 0) {
                bid = Util.getRandomNumberInRange(5, 15);
            }
            System.out.println("I will bid " + (int) bid);
            if (rc.canBid((int) bid)) {
                rc.bid((int) bid);
            } else {
                bid = capital/4;
                rc.bid((int) bid);
            }
            voteslastround = votesthisround;
        }
    }
    boolean tryBuild(RobotType rt, Direction dir, int influence) throws GameActionException {
        if (rc.isReady()) {
            if (dir != null) {
                if (rc.canBuildRobot(rt, dir, influence)) {
                    rc.buildRobot(rt, dir, influence);
                    activebots.add(rc.senseRobotAtLocation(rc.getLocation().add(dir)));
                    return true;
                } else {
                    return false;
                }
            } else {
                for (Direction d : Util.directions) {
                    if (rc.canBuildRobot(rt, d, influence)) {
                        rc.buildRobot(rt, d, influence);
                        activebots.add(rc.senseRobotAtLocation(rc.getLocation().add(d)));
                        return true;
                    }
                }
            }
        } return false;
    }

    void updateActiveBots() throws GameActionException {
        for (RobotInfo rb : activebots) {
            if (rc.canSenseRobot(rb.ID)) {
                activebots.remove(rb);
            } else if (rb.team == rc.getTeam().opponent()) {
                activebots.remove(rb);
            }
        }
    }

    void updateFlag() throws GameActionException {
        for (RobotInfo b : activebots) {
            if (rc.canGetFlag(b.ID)) {
                int flag = rc.getFlag(b.ID);
                if (flag != 0) {
                    int info = flags.getInfoFromFlag(flag);
                    switch (info) {
                        case(InfoCodes.STARTATTACK):
                            if (rc.getFlag(rc.getID()) == 0) {
                                enemyECloc = flags.getLocationFromFlag(flag);
                                flags.sendLocationWithInfo(enemyECloc, InfoCodes.STARTATTACK);
                            }
                        case(InfoCodes.STOPATTACK):
                            if (flags.getLocationFromFlag(flag) == enemyECloc) {
                                flags.sendLocationWithInfo(enemyECloc, InfoCodes.STOPATTACK);
                                enemyECloc = null;
                            }
                    }
                }
            }
        }
    }
}


