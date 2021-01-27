package battlecode2021;
import battlecode.common.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class EnlightenmentCenter extends Robot {
    double bid = 1;
    MapLocation neareneEC = null;
    int voteslastround=0, votesthisround=0;
    int capital; // current amount of influence
    int capital2; // capital of previous round
    int income; // income per round (capital-capital2)
    int actualround, polimount;
    int muckraker; // amount of muckraker spawned
    boolean vote = true; // if EC should vote this round
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
        updateFlag();
        vote = true;
        boolean buildsland = true;
        capital = rc.getInfluence();
        income = capital - capital2;
        actualround = rc.getRoundNum();
        polimount = (int) (capital * 0.05);
        if (polimount < 20){
            polimount = 20;
        }
        int enemylength = rc.senseNearbyRobots(type.sensorRadiusSquared,enemy).length;

        // building
        if (enemylength > nearbyRobots.length/2){
            tryBuild(RobotType.POLITICIAN,null, 20);
        }
        if (neareneEC != null){
            buildsland = false;
            RobotInfo arghmuck = null;
            for (RobotInfo bot:rc.senseNearbyRobots(12,enemy)){
                if (bot.type == RobotType.MUCKRAKER){
                    buildsland = false;
                    arghmuck = bot;
                    break;
                }
            }
            if (arghmuck != null){
                tryBuild(RobotType.POLITICIAN,null,polimount);
            } else {
                tryBuild(RobotType.MUCKRAKER,null,2);
            }
        } if (neutralEClocs.size() > 0 && capital + income * 10 == 501) {
            System.out.println(neutralEClocs); // TODO: remove
            MapLocation nextNeutralEC = neutralEClocs.get(0);
            if(!tryBuild(RobotType.POLITICIAN, myloc.directionTo(nextNeutralEC), 501)) {
                if (tryBuild(RobotType.POLITICIAN, null, 501)) {
                    neutralECqu.add(nextNeutralEC);
                }
            } else { neutralECqu.addFirst(nextNeutralEC); }
        } if (enemyEClocs.size() == 0 && neutralEClocs.size() == 0) {
            if ((actualround % 3 == 0 || income < 10) && capital >= 41 && enemylength == 0) {
                tryBuild(RobotType.SLANDERER, null, 41);
            } else if (actualround % 3 == 1) {
                tryBuild(RobotType.POLITICIAN, null, polimount);
            } else {
                if (muckraker < 21) {
                    tryBuild(RobotType.MUCKRAKER, null, 1);
                    muckraker++;
                } else {
                    tryBuild(RobotType.MUCKRAKER, null, 2);
                    muckraker++;
                }
            }
        }else {
             if ((actualround % 3 == 0 || income < 10) && capital >= 41 && enemylength == 0) {
                 int tobuild = 41;
                 if (capital > 82) {
                     tobuild = (int) (capital * 0.5);
                 }
                tryBuild(RobotType.SLANDERER, null, tobuild);
            } else if (actualround % 3 == 1 && capital >= 50) {
                tryBuild(RobotType.POLITICIAN, null, 30);
            } else {
                if (muckraker < 21) {
                    tryBuild(RobotType.MUCKRAKER, null, 1);
                    muckraker++;
                } else {
                    tryBuild(RobotType.MUCKRAKER, null, 2);
                    muckraker++;
                }
            }
        }
        //bidding
        if (rc.getTeamVotes() <= 750) {
            votesthisround = rc.getTeamVotes();
            if (votesthisround == voteslastround) {
                if (rc.canBid((int) bid * 2+1)) {
                    bid = bid * 2 + 1;
                } else if (rc.canBid((int) (rc.getInfluence() * 1.1+1))) {
                    bid = (int) (rc.getInfluence() * 1.1 +1);

                } else if (rc.canBid(rc.getInfluence() / 4+1)) {
                    bid = rc.getInfluence() * 0.25 +1;

                } else if (rc.canBid(1)) {
                    bid = 3; //evrybody thinks that everybody will bid 1 so evrybody will 2 and we will bid 3, lol
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
            if (capital < 150) { bid = 2; }
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
                    System.out.println("i builded"+rt+dir+influence);
                    if (rt != RobotType.SLANDERER) {
                        if (activebots.size() > 100) {
                            if (Math.random() > 0.75 || (influence ==2 && rt == RobotType.MUCKRAKER)) {
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
                        System.out.println("i builded"+rt+dir+influence);
                        if (rt != RobotType.SLANDERER) {
                            if (activebots.size() > 100) {
                                if (Math.random() > 0.75 || (influence ==2 && rt == RobotType.MUCKRAKER)) {
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
        for (Iterator<FlagsObj> iter = activebots.iterator(); iter.hasNext(); ) {
            FlagsObj b = iter.next(); // TODO: FlagsObj test
            if (rc.canGetFlag(b.bot.ID)) {
                int flag = rc.getFlag(b.bot.ID);
                if (flag != 0 && flag != b.lastflag) {
                    b.lastflag = flag;
                    int info = flags.getInfoFromFlag(flag);
                    MapLocation loc = flags.getLocationFromFlag(flag);
                    switch (info) {
                        case (InfoCodes.ENEMYEC):
                            if (!enemyEClocs.contains(loc)) {
                                enemyEClocs.add(loc);
                            }
                            if (!enemyECqu.contains(loc)) {
                                enemyECqu.add(loc);
                            }
                            neutralECqu.removeIf(x -> x == loc);
                            neutralEClocs.removeIf(x -> x == loc);
                            teamEClocs.removeIf(x -> x == loc);
                            teamECqu.removeIf(x -> x == loc);
                            LPqu.removeIf(x -> x.location == loc && (x.team == Team.NEUTRAL || x.team == team));
                            break;

                        case (InfoCodes.TEAMEC):
                            if (!teamEClocs.contains(loc)) {
                                teamEClocs.add(loc);
                            }
                            if (!teamECqu.contains(loc)) {
                                teamECqu.add(loc);
                            }
                            enemyEClocs.removeIf(x -> x == loc);
                            enemyECqu.removeIf(x -> x == loc);
                            neutralEClocs.removeIf(x -> x == loc);
                            neutralECqu.removeIf(x -> x == loc);
                            LPqu.removeIf(x -> x.location == loc && (x.team == enemy || x.team == Team.NEUTRAL));
                            break;

                        case (InfoCodes.NEUTRALEC):
                            if (!neutralEClocs.contains(loc)) {
                                neutralEClocs.add(loc);
                            }
                            if (!neutralECqu.contains(loc)) {
                                neutralECqu.add(loc);
                            }
                            break;
                    }
                }
            } else {
                iter.remove();
            }
        }
        // --
        System.out.println("\nneutralECqu: " + neutralECqu.size() + "\nteamECqu: " + teamECqu.size() + "\nenemyECqu: " + enemyECqu.size());
        // send locations
        if (enemyECqu.size() > 0 && muckraker < 30) {
            MapLocation head = enemyECqu.poll();
            flags.sendLocationWithInfo(head, InfoCodes.ENEMYEC);
            LPqu.add(new RobotInfo(0, enemy, null, 0, 0, head));
        } else if (neutralECqu.size() > 0) {
            MapLocation head = neutralECqu.poll();
            flags.sendLocationWithInfo(head, InfoCodes.NEUTRALEC);
            LPqu.add(new RobotInfo(0,Team.NEUTRAL, null, 0, 0, head));
        } else if (teamECqu.size() > 0) {
            MapLocation head = teamECqu.poll();
            flags.sendLocationWithInfo(head, InfoCodes.TEAMEC);
            LPqu.add(new RobotInfo(0, team, null, 0, 0, head));
        }  else if (LPqu.size() > 0) {
            flags.sendLocationWithInfo(LPqu.peek().location, InfoCodes.convertToInfoCode(LPqu.peek().team, team));
            LPqu.add(LPqu.poll());
        } else {
            if (rc.canSetFlag(0)) {
                rc.setFlag(0);
            }
        }
        // --
    }

    int calculateBestSlandererInfluence (int initialInfl) {
        int currStep = 0, inflGain = 0;
        double e = 2.71828182846;
        int initalInflGain = (int) Math.floor(((double) 1/50 + 0.03 * Math.pow(e, (-0.001 * initialInfl))) * initialInfl);

        inflGain = initalInflGain + 1;
        for (int i = 0; inflGain >= initalInflGain; i--) {
            inflGain = (int) Math.floor(((double) 1/50 + 0.03 * Math.pow(e, (-0.001 * (initialInfl + i)))) * (initialInfl + i));
            currStep = i+1;
        }
        if (initialInfl - currStep >= 21){
            return initialInfl - currStep;
        } else {
            return 21;
        }
    }
}


