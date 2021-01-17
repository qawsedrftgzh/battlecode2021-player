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
        updateFlag2();
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
        for (RobotInfo b : nearbyRobots) {
            if (b.team == team) {
                if (rc.canGetFlag(b.ID)) {
                    int flag = rc.getFlag(b.ID);
                    if (flag != 0) {
                        int info = flags.getInfoFromFlag(flag);
                        switch (info) {
                            case (InfoCodes.ENEMYEC): {
                                MapLocation loc = flags.getLocationFromFlag(flag);
                                if (!enemyEClocs.contains(loc)) {
                                    enemyEClocs.add(loc);
                                }
                            }
                            case (InfoCodes.TEAMEC): {
                                MapLocation loc = flags.getLocationFromFlag(flag);
                                if (enemyEClocs.contains(loc)) {
                                    enemyEClocs.remove(loc);
                                } else if (neutralEClocs.contains(loc)) {
                                    neutralEClocs.remove(loc);
                                }
                                if (!teamEClocs.contains(loc)) {
                                    teamEClocs.remove(loc);
                                }
                            }
                            case (InfoCodes.NEUTRALEC): {
                                MapLocation loc = flags.getLocationFromFlag(flag);
                                if (!neutralEClocs.contains(loc)) {
                                    neutralEClocs.add(loc);
                                }
                            }
                        }
                    }
                }
            }
        }
        // --

        // check saved neutral EC locations
        if (neutralEClocs.size() > 0 || nearbyRobots.length > 0) {
            if (neutralEClocs.size() > 0) {
                //neutralEClocs.sort(Comparator.comparingInt(x -> myloc.distanceSquaredTo(x))); // TODO check if it sorts right
                //Arrays.sort(neutralEClocs, Comparator.comparingInt(x -> myloc.distanceSquaredTo(x)));
                neutralEClocs.sort(Comparator.comparingInt(x -> myloc.distanceSquaredTo(x)));
                for (Iterator<MapLocation> iter = neutralEClocs.iterator(); iter.hasNext();) {
                    MapLocation loc = iter.next();
                    if (rc.canSenseLocation(loc)) {
                        RobotInfo[] neutralAtLocation = rc.senseNearbyRobots(loc, 0, Team.NEUTRAL);
                        if (neutralAtLocation.length <= 0) {
                            iter.remove();
                            RobotInfo[] teamAtLocation = rc.senseNearbyRobots(loc, 0, team);
                            if (teamAtLocation.length > 0) {
                                if (!teamEClocs.contains(loc)) {
                                    teamEClocs.add(loc);
                                }
                            } else {
                                if (!enemyEClocs.contains(loc)) {
                                    enemyEClocs.add(loc);
                                }
                            }
                        }
                    }
                }
            }
            if (nearbyRobots.length > 0) {
                for (RobotInfo b : nearbyRobots) {
                    if (b.type == RobotType.ENLIGHTENMENT_CENTER && b.team == Team.NEUTRAL && !neutralEClocs.contains(b.location)) {
                        neutralEClocs.add(b.location);
                    }
                }
            }
        }
        // --

        // check saved enemys EC locations
        else if (enemyEClocs.size() > 0 || nearbyRobots.length > 0) {
            if (enemyEClocs.size() > 0) {
                enemyEClocs.sort(Comparator.comparingInt(x -> myloc.distanceSquaredTo(x))); // TODO check if it sorts right
                for (Iterator<MapLocation> iter = enemyEClocs.iterator(); iter.hasNext();) {
                    MapLocation loc = iter.next();
                    if (rc.canSenseLocation(loc)) {
                        RobotInfo[] enemyAtLocation = rc.senseNearbyRobots(loc, 0, enemy);
                        if (enemyAtLocation.length <= 0) {
                            iter.remove();
                            if (!teamEClocs.contains(loc)) {
                                teamEClocs.add(loc);
                            }
                        }
                    }
                }
            }
            if (nearbyRobots.length > 0) {
                for (RobotInfo b : nearbyRobots) {
                    if (b.type == RobotType.ENLIGHTENMENT_CENTER && b.team == enemy && !enemyEClocs.contains(b.location)) {
                        enemyEClocs.add(b.location);
                    }
                }
            }
        }
        // --

        // check saved team EC locations
        else if (teamEClocs.size() > 0 || nearbyRobots.length > 0) {
            if (teamEClocs.size() > 0) {
                teamEClocs.sort(Comparator.comparingInt(x -> -myloc.distanceSquaredTo(x)));
                for (Iterator<MapLocation> iter = teamEClocs.iterator(); iter.hasNext();) {
                    MapLocation loc = iter.next();
                    if (rc.canSenseLocation(loc)) {
                        RobotInfo[] teamAtLocation = rc.senseNearbyRobots(loc, 0, team);
                        if (teamAtLocation.length <= 0) {
                            iter.remove();
                            if (!enemyEClocs.contains(loc)) {
                                enemyEClocs.add(loc);
                            }
                        }
                    }
                }
            }
            if (nearbyRobots.length > 0) {
                for (RobotInfo b : nearbyRobots) {
                    if (b.type == RobotType.ENLIGHTENMENT_CENTER && b.team == team && !teamEClocs.contains(b.location)) {
                        teamEClocs.add(b.location);
                    }
                }
            }
        }
        // --

        // send location info
        if (teamEClocs.size() > 0) {
            teamEClocs.sort(Comparator.comparingInt(x -> myloc.distanceSquaredTo(x)));
            flags.sendLocationWithInfo(teamEClocs.get(0), InfoCodes.TEAMEC);

        } else if (neutralEClocs.size() > 0) {
            neutralEClocs.sort(Comparator.comparingInt(x -> myloc.distanceSquaredTo(x)));
            flags.sendLocationWithInfo(neutralEClocs.get(0), InfoCodes.NEUTRALEC);

        } else if (enemyEClocs.size() > 0) {
            enemyEClocs.sort(Comparator.comparingInt((x -> myloc.distanceSquaredTo(x))));
            flags.sendLocationWithInfo(enemyEClocs.get(0), InfoCodes.ENEMYEC);
        }
    }

    void updateFlag2() throws GameActionException {
        int myflag = 0;
        if (rc.canGetFlag(rc.getID())) {
            myflag = rc.getFlag(rc.getID());
        }

        // receive and process information
        FlagsObj nextECID = getNearestECID();
        if (nextECID.bot.ID != 0) {
            int ID = nextECID.bot.ID;
            if (rc.canGetFlag(ID)) {
                int flag = rc.getFlag(ID);
                if (flag != 0 && flag != nextECID.lastflag) {
                    nextECID.lastflag = flag;
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
                        }
                        case (InfoCodes.NEUTRALEC): {
                            MapLocation loc = flags.getLocationFromFlag(flag);
                            if (!neutralEClocs.contains(loc)) {
                                neutralEClocs.add(loc);
                            }
                        }
                    }
                }
            } else {
                ECID.remove(0);
                updateFlag2();
            }
        }
        // --

        if (nearbyRobots.length > 0) {

            // check saved neutral EC location
            if (neutralEClocs.size() > 0) {
                for (Iterator<MapLocation> iter = neutralEClocs.iterator(); iter.hasNext(); ) {
                    MapLocation loc = iter.next();
                    if (myloc.distanceSquaredTo(loc) <= type.sensorRadiusSquared && rc.canSenseLocation(loc)) {
                        RobotInfo botAtLocation = rc.senseRobotAtLocation(loc);
                        if (botAtLocation != null && botAtLocation.type == RobotType.ENLIGHTENMENT_CENTER ) {
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
