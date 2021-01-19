package battlecode2021;
import battlecode.common.*;
import java.util.*;

public class Unit extends Robot {
    ArrayList<FlagsObj> ECID = new ArrayList<>();
    Navigation nav;
    MapLocation ECloc;
    MapLocation myloc;
    boolean move = true;

    public Unit(RobotController r) {
        super(r);
        nav = new Navigation(rc);
        getECInfo();
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        myloc = rc.getLocation();
        updateFlag();
        sortEClocs();
    }

    void sortEClocs() {
        teamEClocs.sort(Comparator.comparingInt(x -> myloc.distanceSquaredTo(x)));
        enemyEClocs.sort(Comparator.comparingInt(x -> myloc.distanceSquaredTo(x)));
        neutralEClocs.sort(Comparator.comparingInt(x -> myloc.distanceSquaredTo(x)));
    }

    void getECInfo() {
        RobotInfo[] nearbyRobots = rc.senseNearbyRobots(2, team);
        for (RobotInfo rb : nearbyRobots) {
            if (rb.type == RobotType.ENLIGHTENMENT_CENTER) {
                teamEClocs.add(rb.location);
                ECID.add(new FlagsObj(rb, 0));
            }
        }
    }

    FlagsObj getNearestECID() {
        if (ECID.size() > 1) {
            ECID.sort(Comparator.comparing(x -> myloc.distanceSquaredTo(x.bot.location)));
            return ECID.get(0);
        } else if (ECID.size() == 1) {
            return ECID.get(0);
        }
        return null;
    }

    void updateFlag() throws GameActionException {
        int myflag = 0;
        if (rc.canGetFlag(rc.getID())) {
            myflag = rc.getFlag(rc.getID());
        }

        // receive and process information
        FlagsObj nextECID = getNearestECID();
        if (nextECID != null && nextECID.bot.ID != 0) {
            int ID = nextECID.bot.ID;
            if (rc.canGetFlag(ID)) {
                int flag = rc.getFlag(ID);
                if (flag != 0 && flag != nextECID.lastflag) {
                    nextECID.lastflag = flag;
                    int info = flags.getInfoFromFlag(flag);
                    MapLocation loc = flags.getLocationFromFlag(flag);
                    switch (info) {
                        case (InfoCodes.ENEMYEC):
                            if (!enemyEClocs.contains(loc)) {
                                enemyEClocs.add(loc);
                            }
                            if (neutralEClocs.contains(loc)) {
                                neutralEClocs.remove(loc);
                            }
                            if (teamEClocs.contains(loc)) {
                                teamEClocs.remove(loc);
                            }
                            break;

                        case (InfoCodes.TEAMEC):
                            if (enemyEClocs.contains(loc)) {
                                enemyEClocs.remove(loc);
                            }
                            if (neutralEClocs.contains(loc)) {
                                neutralEClocs.remove(loc);
                            }
                            if (!teamEClocs.contains(loc)) {
                                teamEClocs.remove(loc);
                            }
                            break;

                        case (InfoCodes.NEUTRALEC):
                            if (!neutralEClocs.contains(loc)) {
                                neutralEClocs.add(loc);
                            }
                            break;

                    }
                }
            } else {
                ECID.remove(0);
                updateFlag();
            }
        }
        // --

        if (nearbyRobots.length > 0) {

            // check saved neutral EC location
            if (neutralEClocs.size() > 0) {
                for (Iterator<MapLocation> iter = neutralEClocs.iterator(); iter.hasNext(); ) {
                    MapLocation loc = iter.next();
                    if (rc.canSenseLocation(loc)) {
                        RobotInfo botAtLocation = rc.senseRobotAtLocation(loc);
                        if (botAtLocation.type == RobotType.ENLIGHTENMENT_CENTER) {
                            if (botAtLocation.team != Team.NEUTRAL) {
                                iter.remove();
                                neutralECqu.remove(loc);
                                if (botAtLocation.team == team && !teamEClocs.contains(loc)) {
                                    teamEClocs.add(loc);
                                    teamECqu.add(loc);
                                } else if (botAtLocation.team == enemy && !enemyEClocs.contains(loc)) {
                                    enemyEClocs.add(loc);
                                    enemyECqu.add(loc);
                                }
                            }
                        } else { iter.remove(); }
                    }
                }
            }
            // --

            // check saved enemy EC locations
            else if (enemyEClocs.size() > 0) {
                enemyEClocs.sort(Comparator.comparingInt(x -> myloc.distanceSquaredTo(x))); // TODO check if it sorts right
                for (Iterator<MapLocation> iter = enemyEClocs.iterator(); iter.hasNext(); ) {
                    MapLocation loc = iter.next();
                    if (myloc.distanceSquaredTo(loc) <= type.sensorRadiusSquared && rc.canSenseLocation(loc)) {
                        RobotInfo botAtLocation = rc.senseRobotAtLocation(loc);
                        if (botAtLocation != null && botAtLocation.type == RobotType.ENLIGHTENMENT_CENTER) {
                            if (botAtLocation.team != enemy) {
                                iter.remove();
                                if (enemyECqu.contains(loc)) {
                                    enemyECqu.remove(loc);
                                }
                                if (!teamEClocs.contains(loc)) {
                                    teamEClocs.add(loc);
                                    teamECqu.add(loc);
                                }
                            }
                        } else { iter.remove(); }
                    }
                }
            }
            // --

            // check saved team EC locations
            else if (teamEClocs.size() > 0) {
                teamEClocs.sort(Comparator.comparingInt(x -> -myloc.distanceSquaredTo(x)));
                for (Iterator<MapLocation> iter = teamEClocs.iterator(); iter.hasNext(); ) {
                    MapLocation loc = iter.next();
                    if (myloc.distanceSquaredTo(loc) <= type.sensorRadiusSquared && rc.canSenseLocation(loc)) {
                        RobotInfo botAtLocation = rc.senseRobotAtLocation(loc);
                        if (botAtLocation != null && botAtLocation.type == RobotType.ENLIGHTENMENT_CENTER) {
                            if (botAtLocation.team != team) {
                                iter.remove();
                                if (teamECqu.contains(loc)) {
                                    teamECqu.remove(loc);
                                }
                                if (!enemyEClocs.contains(loc)) {
                                    enemyEClocs.add(loc);
                                    enemyECqu.add(loc);
                                }
                            }
                        } else { iter.remove(); }
                    }
                }
            }
            // --

            // check for new EC locations
            for (RobotInfo b : nearbyRobots) {
                if (b.type == RobotType.ENLIGHTENMENT_CENTER) {
                    if (b.team == Team.NEUTRAL && !neutralEClocs.contains(b.location)) {
                        neutralEClocs.add(b.location);
                        neutralECqu.add(b.location);
                    } else if (b.team == enemy && !enemyEClocs.contains(b.location)) {
                        enemyEClocs.add(b.location);
                        enemyECqu.add(b.location);
                    } else if (b.team == team && !teamEClocs.contains(b.location)) {
                        teamEClocs.add(b.location);
                        teamECqu.add(b.location);
                    }
                }
            }
            // --

            // sending locations
            if (neutralECqu.size() > 0) {
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
    }
}
