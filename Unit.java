package battlecode2021;
import battlecode.common.*;

import java.util.Comparator;

public class Unit extends Robot {
    Navigation nav;
    MapLocation ECloc;
    MapLocation myloc;
    boolean move = true;

    public Unit(RobotController r) {
        super(r);
        nav = new Navigation(rc);
        ECloc = getECloc();
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        updateFlag();
        myloc = rc.getLocation();

    }

    MapLocation getECloc() {
        RobotInfo[] neabyRobots = rc.senseNearbyRobots(2, team);
        for (RobotInfo rb : neabyRobots) {
            if (rb.type == RobotType.ENLIGHTENMENT_CENTER) {
                return rb.location;
            }
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

        // update neutral EC locations
        if (neutralEClocs.size() > 0 || nearbyRobots.length > 0) {
            if (neutralEClocs.size() > 0) {
                neutralEClocs.sort(Comparator.comparingInt(x -> myloc.distanceSquaredTo(x))); // TODO check if it sorts right
                for (MapLocation loc : neutralEClocs) {
                    if (rc.canSenseLocation(loc)) {
                        RobotInfo[] neutralAtLocation = rc.senseNearbyRobots(loc, 0, Team.NEUTRAL);
                        if (neutralAtLocation.length <= 0) {
                            neutralEClocs.remove(loc);
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

        // update enemys EC locations
        else if (enemyEClocs.size() > 0 || nearbyRobots.length > 0) {
            if (enemyEClocs.size() > 0) {
                enemyEClocs.sort(Comparator.comparingInt(x -> myloc.distanceSquaredTo(x))); // TODO check if it sorts right
                for (MapLocation loc : enemyEClocs) {
                    if (rc.canSenseLocation(loc)) {
                        RobotInfo[] enemyAtLocation = rc.senseNearbyRobots(loc, 0, enemy);
                        if (enemyAtLocation.length <= 0) {
                            enemyEClocs.remove(loc);
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

        // update team EC locations
        else if (teamEClocs.size() > 0 || nearbyRobots.length > 0) {
            if (teamEClocs.size() > 0) {
                for (MapLocation loc : teamEClocs) {
                    if (rc.canSenseLocation(loc)) {
                        RobotInfo[] teamAtLocation = rc.senseNearbyRobots(loc, 0, team);
                        if (teamAtLocation.length <= 0) {
                            teamEClocs.remove(loc);
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

        // TODO send locations


    }
}
