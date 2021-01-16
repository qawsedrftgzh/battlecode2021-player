package battlecode2021;
import battlecode.common.*;

import java.util.ArrayList;
import java.util.Iterator;

public class EnlightenmentCenter extends Robot {
    double bid = 1;
    MapLocation neareneEC = null;
    int voteslastround=0, votesthisround=0;
    int capital; // current amount of influence
    int capital2; // capital of previous round
    int income; // income per round (capital-capital2)
    ArrayList<RobotInfo> activebots = new ArrayList<>();

    public EnlightenmentCenter(RobotController r) {
        super(r);
        for (RobotInfo bot: r.senseNearbyRobots(RobotType.ENLIGHTENMENT_CENTER.sensorRadiusSquared,enemy)){
            if (bot.type == RobotType.ENLIGHTENMENT_CENTER){
                neareneEC = bot.location;
            }
        }
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
        if (rc.getEmpowerFactor(team,1) >= 10 && capital < 70000000){
            tryBuild(RobotType.POLITICIAN,null,capital/2);
        }
        if (neareneEC != null){
            boolean buildsland = true;
            RobotInfo arghmuck = null;
            for (RobotInfo bot:rc.senseNearbyRobots(12,enemy)){
                if (bot.type == RobotType.MUCKRAKER){
                    buildsland = false;
                    arghmuck = bot;
                    break;
                }
            }
            if (buildsland && (int) (capital*0.5) >= 30) {
                tryBuild(RobotType.SLANDERER,null,(int) (capital*0.5));
            }else if (rc.getRoundNum()%2==0) {
                tryBuild(RobotType.POLITICIAN, null, (int) (capital*0.1));
            } else {
                tryBuild(RobotType.MUCKRAKER, null, (int) (capital*0.05));
            }
        }
        if (rc.getRoundNum() <=2 || (capital <200 && capital>=30)){
            System.out.println("the first few rounds");
            tryBuild(RobotType.SLANDERER, null, capital);
        }else if (rc.getRoundNum() % 3 == 0 && capital >= 50) {
            tryBuild(RobotType.SLANDERER, null, calculateBestSlandererInfluence(capital/2)); //trust me, this is a good amount
        }else if (rc.getRoundNum() % 3 == 2 && capital >= 50 && (neutralEClocs.size() != 0 || enemyEClocs.size() != 0)){
            tryBuild(RobotType.POLITICIAN,null, (int) (capital * 0.05));
        }else if (capital >= 50){
            tryBuild(RobotType.MUCKRAKER,null,(int) (capital * 0.05));
        }
        //bidding
        if (rc.getTeamVotes() <= 750 && capital > 30) {
            votesthisround = rc.getTeamVotes();
            if (votesthisround == voteslastround) {
                    if(rc.canBid((int) bid * 2)) {
                        bid = bid * 2;
                    } else if (rc.canBid( (int) (rc.getInfluence() * 1.1))) {
                        bid = (int) (rc.getInfluence() * 1.1);

                    } else if (rc.canBid(rc.getInfluence() / 4)) {
                        bid = rc.getInfluence() / 4;

                    } else if (rc.canBid(1)) {
                        bid = 1;
                    }
            } else {
                bid = (int) bid * (0.99);
            }
            bid = bid + 0 + (int)(Math.random() * 7);
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
        if (neutralEClocs.get(0) != null && neutralEClocs.get(0) == rc.getLocation()) {
            flags.sendLocationWithInfo(rc.getLocation(), InfoCodes.TEAMEC);
            enemyEClocs.remove(0);
            return;
        }
        if (enemyEClocs.get(0) != null && enemyEClocs.get(0) == rc.getLocation()) {
            flags.sendLocationWithInfo(rc.getLocation(), InfoCodes.TEAMEC);
            enemyEClocs.remove(0);
            return;
        }
        RobotInfo[] botsToCheck;
        if (activebots.size() == 0) {
            botsToCheck = rc.senseNearbyRobots(type.sensorRadiusSquared);
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
                                neutralEClocs.set(0,flags.getLocationFromFlag(flag));
                                flags.sendLocationWithInfo(neutralEClocs.get(0), InfoCodes.NEUTRALEC);
                            }
                        case(InfoCodes.ENEMYEC):
                            if (rc.getFlag(rc.getID()) == 0) {
                                enemyEClocs.set(0,flags.getLocationFromFlag(flag));
                                flags.sendLocationWithInfo(enemyEClocs.get(0), InfoCodes.ENEMYEC);
                            }
                        case(InfoCodes.TEAMEC):
                            if (flags.getLocationFromFlag(flag) == enemyEClocs.get(0)) {
                                flags.sendLocationWithInfo(enemyEClocs.get(0), InfoCodes.TEAMEC);
                                enemyEClocs.remove(0);
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
            inflGain = (int) Math.floor(((double) 1/50 + 0.03 * Math.pow(e, (-0.001 * (initialInfl + i)))) * (initialInfl + i));
            System.out.println("\n initialInflGain: " + initalInflGain + "\n inflGain: " + inflGain);
            currStep = i+1;
        }

        if (initialInfl + nextStep <= capital && nextStep < currStep) {
            return initialInfl + nextStep;
        } else if (initialInfl - currStep >= 21 && nextStep > currStep){
            return initialInfl + currStep;
        }
        if (initialInfl <20){
            initialInfl = 20;
        }
        return initialInfl;
    }
}


