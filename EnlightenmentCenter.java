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
    int actualround, polimount;
    MapLocation myloc;
    ArrayList<FlagsObj> activebots = new ArrayList<>(); // TODO: FlagsObj test
    // ArrayList<RobotInfo> activebots = new ArrayList<>();

    public EnlightenmentCenter(RobotController r) {
        super(r);
        for (RobotInfo bot: r.senseNearbyRobots(RobotType.ENLIGHTENMENT_CENTER.sensorRadiusSquared,enemy)){
            if (bot.type == RobotType.ENLIGHTENMENT_CENTER){
                neareneEC = bot.location;
            }
        }
        myloc = rc.getLocation();
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        capital = rc.getInfluence();
        income = capital - capital2;
        actualround = rc.getRoundNum();
        polimount = (int) (capital * 0.05);
        if (polimount < 20){
            polimount = 20;
        }
        updateFlag();
        System.out.println("\nbytecode after updateFlag(" + activebots.size() +") : \n" + Clock.getBytecodeNum());
        if (rc.getEmpowerFactor(team,1) >= 10 && capital < 70000000){
            tryBuild(RobotType.POLITICIAN,null,capital/2);
        }
        if (rc.senseNearbyRobots(type.sensorRadiusSquared,enemy).length != 0){
            tryBuild(RobotType.POLITICIAN,null,20);
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
            if (enemyEClocs.size() == 0 && neutralEClocs.size() == 0) {
                if (buildsland && (int) (capital * 0.5) >= 30) {
                    tryBuild(RobotType.SLANDERER, null, calculateBestSlandererInfluence((int) (capital * 0.5)));
                } else if (actualround % 3 == 0 && buildsland) {
                    tryBuild(RobotType.SLANDERER, null, 200);
                } else if (actualround % 3 == 1) {
                    tryBuild(RobotType.POLITICIAN, null, polimount);
                } else {
                    tryBuild(RobotType.MUCKRAKER, null, (int) (capital * 0.05));
                }
            }else if (arghmuck != null){
                tryBuild(RobotType.POLITICIAN,null,polimount);
            }
        }
        if (actualround <=2 || (capital <50 && capital>=30 && rc.getRoundNum() % 5 == 0)){
            System.out.println("the first few rounds");
            tryBuild(RobotType.SLANDERER, null, calculateBestSlandererInfluence(capital));
        }else if (actualround % 3 == 0 && capital >= 200) {
            tryBuild(RobotType.SLANDERER, null, 200);
        }else if (actualround % 3 == 1 && capital >= 50){
            tryBuild(RobotType.POLITICIAN,null, 30);
        }else {
            tryBuild(RobotType.MUCKRAKER,null,1);
        }
        //bidding
        if (rc.getTeamVotes() <= 750 && capital > 30) {
            votesthisround = rc.getTeamVotes();
            if (votesthisround == voteslastround) {
                if (rc.canBid((int) bid * 2)) {
                    bid = bid * 2;
                } else if (rc.canBid((int) (rc.getInfluence() * 1.1))) {
                    bid = (int) (rc.getInfluence() * 1.1);

                } else if (rc.canBid(rc.getInfluence() / 4)) {
                    bid = rc.getInfluence() * 0.25;

                } else if (rc.canBid(1)) {
                    bid = 1;
                }
                if (actualround <= 250 && bid > capital * 0.25) {
                    bid = capital * 0.25;
                } else if (actualround > 250 && actualround <= 750 && bid > capital * 0.5) {
                    bid = capital * 0.5;
                } else if (actualround >= 750 && actualround <= 750 && bid > capital * 0.75) {
                    bid = capital * 0.75;
                }
            } else {
                bid = (int) (bid * 0.95);
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
                    if (rt != RobotType.SLANDERER) {
                        if (activebots.size() > 100) {
                            if (Math.random() > 0.5) {
                                activebots.add(new FlagsObj(rc.senseRobotAtLocation(rc.getLocation().add(dir)), 0));
                            }
                        } else {
                            activebots.add(new FlagsObj(rc.senseRobotAtLocation(rc.getLocation().add(dir)), 0));
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            } else {
                for (Direction d : Util.directions) {
                    if (rc.canBuildRobot(rt, d, influence)) {
                        rc.buildRobot(rt, d, influence);
                        if (rt != RobotType.SLANDERER) {
                            if (activebots.size() > 100) {
                                if (Math.random() > 0.5) {
                                    activebots.add(new FlagsObj(rc.senseRobotAtLocation(rc.getLocation().add(d)), 0));
                                }
                            } else {
                                activebots.add(new FlagsObj(rc.senseRobotAtLocation(rc.getLocation().add(d)), 0));
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    void updateFlag() throws GameActionException {
        int myflag = 0;
        if (rc.canGetFlag(rc.getID())) {
            myflag = rc.getFlag(rc.getID());
        }

        // receive and process information
        // for (Iterator<RobotInfo> iter = activebots.iterator(); iter.hasNext();) {
        for (Iterator<FlagsObj> iter = activebots.iterator(); iter.hasNext(); ) { // TODO: FlagsObj test
            // RobotInfo b = iter.next();
            FlagsObj b = iter.next(); // TODO: FlagsObj test
            if (rc.canGetFlag(b.bot.ID)) {
                int flag = rc.getFlag(b.bot.ID);
                if (flag != 0 && flag != b.lastflag) {
                    b.lastflag = flag;
                    int info = flags.getInfoFromFlag(flag);
                    switch (info) {
                        case (InfoCodes.ENEMYEC): {
                            MapLocation loc = flags.getLocationFromFlag(flag);
                            if (!enemyEClocs.contains(loc)) {
                                enemyEClocs.add(loc);
                            }
                            if (neutralEClocs.contains(loc)) {
                                neutralEClocs.remove(loc);
                            }
                            if (teamEClocs.contains(loc)) {
                                teamEClocs.remove(loc);
                            }
                            if (!enemyECqu.contains(loc)) {
                                enemyECqu.add(loc);
                            }
                        }
                        case (InfoCodes.TEAMEC): {
                            MapLocation loc = flags.getLocationFromFlag(flag);
                            if (enemyEClocs.contains(loc)) {
                                enemyEClocs.remove(loc);
                            }
                            if (neutralEClocs.contains(loc)) {
                                neutralEClocs.remove(loc);
                            }
                            if (!teamEClocs.contains(loc)) {
                                teamEClocs.remove(loc);
                            }
                            if (!teamECqu.contains(loc)) {
                                teamECqu.add(loc);
                            }
                        }
                        case (InfoCodes.NEUTRALEC): {
                            MapLocation loc = flags.getLocationFromFlag(flag);
                            if (!neutralEClocs.contains(loc)) {
                                neutralEClocs.add(loc);
                                if (!neutralECqu.contains(loc)) {
                                    neutralECqu.add(loc);
                                }
                            }
                        }
                    }
                }
            } else {
                iter.remove();
            }
        }
        // --

        // send locations
        if (flagsQu.size() > 0) {
            RobotInfo info = flagsQu.poll();
            flags.sendLocationWithInfo(info.location, InfoCodes.convertToInfoCode(info.team, team));
        } else if (neutralECqu.size() > 0) {
            flags.sendLocationWithInfo(neutralECqu.poll(), InfoCodes.NEUTRALEC);
        } else if (teamECqu.size() > 0) {
            flags.sendLocationWithInfo(teamECqu.poll(), InfoCodes.TEAMEC);
        } else if (enemyECqu.size() > 0) {
            flags.sendLocationWithInfo(enemyECqu.poll(), InfoCodes.ENEMYEC);
        } else {
            if (rc.canSetFlag(0)) {
                rc.setFlag(0);
            }
        }
        // --
    }

    int calculateBestSlandererInfluence (int initialInfl) {
        int bc1 = Clock.getBytecodeNum();
        int nextStep = 0, currStep = 0;
        double e = 2.71828182846;
        int initalInflGain = (int) Math.floor(((double) 1/50 + 0.03 * Math.pow(e, (-0.001 * initialInfl))) * initialInfl);

        int inflGain = 0;
        for (int i = 0; inflGain <= initalInflGain; i++) {
            inflGain = (int) Math.floor(((double) 1/50 + 0.03 * Math.pow(e, (-0.001 * (initialInfl + i)))) * (initialInfl + i));
            nextStep = i;
        }

        inflGain = initalInflGain + 1;
        for (int i = 0; inflGain >= initalInflGain; i--) {
            inflGain = (int) Math.floor(((double) 1/50 + 0.03 * Math.pow(e, (-0.001 * (initialInfl + i)))) * (initialInfl + i));
            currStep = i+1;
        }
        int BCcost = Clock.getBytecodeNum() - bc1;
        if (initialInfl + nextStep <= capital && nextStep < currStep) {
            return initialInfl + nextStep;
        } else if (initialInfl - currStep >= 21 && nextStep > currStep){
            return initialInfl + currStep;
        }
        if (initialInfl <20){
            initialInfl = 21;
        }
        return initialInfl;
    }
}


