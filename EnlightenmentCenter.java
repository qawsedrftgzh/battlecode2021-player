package battlecode2021;
import battlecode.common.*;

import java.util.ArrayList;
import java.util.Iterator;

public class EnlightenmentCenter extends Robot {
    double bid = 1;
    int voteslastround=0, votesthisround=0;
    int capital; // current amount of influence
    int capital2; // capital of previous round
    int income; // income per round (capital-capital2)
    ArrayList<RobotInfo> activebots = new ArrayList<>();

    public EnlightenmentCenter(RobotController r) {
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        capital = rc.getInfluence();
        income = capital - capital2;
        int polimount = (int) (capital * 0.05);
        if (polimount < 20){
            polimount = 20;
        }
        updateActiveBots();
        updateFlag();
        if (nearbyEnemys.length != 0){
            if (round % 3 == 0){
                tryBuild(RobotType.SLANDERER, null, calculateBestSlandererInfluence((int) (capital * 0.2)));
            } else {
                tryBuild(RobotType.POLITICIAN, null, polimount);
            }
        } else if (enemyECloc != null || neutralECloc != null) {
            if (round%4==0){
                tryBuild(RobotType.MUCKRAKER, null, 10);
            }else if (round%4==1 || round%4==2){
                tryBuild(RobotType.POLITICIAN, null, polimount);
            } else {
                tryBuild(RobotType.SLANDERER, null, calculateBestSlandererInfluence((int) (capital * 0.2)));
            }
        } else {
            if (rc.getRoundNum() <= 2 || (rc.getRoundNum() >= 200 && capital < 200 && capital >= 30)) {
                System.out.println("the first few rounds");
                tryBuild(RobotType.SLANDERER, null, calculateBestSlandererInfluence(capital));
            } else if (rc.getRoundNum() % 4 == 0 && capital >= 200) {
                tryBuild(RobotType.SLANDERER, null, calculateBestSlandererInfluence(capital / 4)); //trust me, this is a good amount
            } else if (rc.getRoundNum() % 4 == 1 || rc.getRoundNum() % 4 == 3 || rc.getRoundNum() % 4 == 0) {
                tryBuild(RobotType.MUCKRAKER, null, 1);
            } else if (rc.getRoundNum() % 4 == 2){
                tryBuild(RobotType.POLITICIAN, null, polimount);
            } else {
                System.out.println("Ok lol NOOO");
                tryBuild(RobotType.SLANDERER,null,calculateBestSlandererInfluence(capital));
            }
        }
        //bidding
        if (rc.getTeamVotes() <= 750) {
            votesthisround = rc.getTeamVotes();
            if (votesthisround == voteslastround) {
                    if(rc.canBid((int) bid * 2)) {
                        bid = bid * 2;
                    } else if (rc.canBid( (int) (rc.getInfluence() * 1.1))) {
                        bid = (int) rc.getInfluence() * 1.1;

                    } else if (rc.canBid(rc.getInfluence() / 4)) {
                        bid = rc.getInfluence() / 4;

                    } else if (rc.canBid(1)) {
                        bid = 1;
                    }
            }
            if (rc.canBid((int) bid)) {
                System.out.println("I will bid " + (int) bid);
                rc.bid((int) bid);
            }
            voteslastround = votesthisround;
        }

        capital2 = capital;
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
        }for (Direction d : Util.directions) {
            if (rc.canBuildRobot(RobotType.SLANDERER, dir, capital)) {
                rc.buildRobot(RobotType.SLANDERER, dir, capital);
                activebots.add(rc.senseRobotAtLocation(rc.getLocation().add(dir)));
                return true;
            }
        }
        return false;
    }

    void updateActiveBots() {
        if (activebots.size() != 0) {
            for (Iterator<RobotInfo> iter = activebots.iterator(); iter.hasNext(); ) {
                RobotInfo info = iter.next();
                if (!rc.canSenseRobot(info.ID)) {
                    iter.remove();
                } else if (info.team == enemy) {
                    iter.remove();
                }
            }
        }
    }

    void updateFlag() throws GameActionException {
        if (neutralECloc != null && neutralECloc == rc.getLocation()) {
            flags.sendLocationWithInfo(rc.getLocation(), InfoCodes.TEAMEC);
            enemyECloc = null;
            return;
        }
        if (enemyECloc != null && enemyECloc == rc.getLocation()) {
            flags.sendLocationWithInfo(rc.getLocation(), InfoCodes.TEAMEC);
            enemyECloc = null;
            return;
        }
        RobotInfo[] botsToCheck;
        if (activebots.size() == 0) {
            botsToCheck = nearbyTeam;
        } else {
            botsToCheck = new RobotInfo[activebots.size()];
            botsToCheck = activebots.toArray(botsToCheck);
        }
        for (RobotInfo b : botsToCheck) {
            if (rc.canGetFlag(b.ID)) {
                int flag = rc.getFlag(b.ID);
                if (flag != 0) {
                    int info = flags.getInfoFromFlag(flag);
                    switch (info) {
                        case(InfoCodes.NEUTRALEC):
                            if (rc.getFlag(rc.getID()) == 0) {
                                neutralECloc = flags.getLocationFromFlag(flag);
                                flags.sendLocationWithInfo(neutralECloc, InfoCodes.NEUTRALEC);
                            }
                        case(InfoCodes.ENEMYEC):
                            if (rc.getFlag(rc.getID()) == 0) {
                                enemyECloc = flags.getLocationFromFlag(flag);
                                flags.sendLocationWithInfo(enemyECloc, InfoCodes.ENEMYEC);
                            }
                        case(InfoCodes.TEAMEC):
                            if (flags.getLocationFromFlag(flag) == enemyECloc) {
                                flags.sendLocationWithInfo(enemyECloc, InfoCodes.TEAMEC);
                                enemyECloc = null;
                            }
                    }
                }
            }
        }
    }

    int calculateBestSlandererInfluence (int initialInfl) {
        int nextStep = 0, currStep = 0;
        double e = 2.71828182846;
        int initalInflGain = (int) Math.floor(((double) 1/50 + 0.03 * Math.pow(e, (-0.001 * initialInfl))) * initialInfl);

        int inflGain = 0;
        for (int i = 0; inflGain <= initalInflGain; i++) {
            inflGain = (int) Math.floor(((double) 1/50 + 0.03 * Math.pow(e, (-0.001 * (initialInfl + i)))) * (initialInfl + i));
            System.out.println("\n initialInflGain: " + initalInflGain + "\n inflGain: " + inflGain);
            nextStep = i;
        }

        inflGain = initalInflGain + 1;
        for (int i = 0; inflGain >= initalInflGain; i--) {
            System.out.println("wp 3");
            inflGain = (int) Math.floor(((double) 1/50 + 0.03 * Math.pow(e, (-0.001 * (initialInfl + i)))) * (initialInfl + i));
            System.out.println("\n initialInflGain: " + initalInflGain + "\n inflGain: " + inflGain);
            currStep = i+1;
        }

        if (initialInfl + nextStep <= capital && nextStep < currStep) {
            return initialInfl + nextStep;
        } else if (initialInfl - currStep >= 21 && nextStep > currStep){
            return initialInfl + currStep;
        }
        return initialInfl;
    }
}


