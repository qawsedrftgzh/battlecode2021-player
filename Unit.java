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
        // TODO send locations


        // update neutral EC locations
        if (neutralEClocs.size() > 0) {

        }

        // updating enemys EC locations
        else if (enemyEClocs.size() > 0) {
            enemyEClocs.sort(Comparator.comparingInt(a -> myloc.distanceSquaredTo(a))); // TODO check if it sorts right
            for (MapLocation loc : enemyEClocs) {
                if (rc.canSenseLocation(loc)) {

                }
            }
        }
        /** if (enemyECloc != null && myflag == 0) {
            flags.sendLocationWithInfo(enemyECloc, InfoCodes.ENEMYEC);
        }
        if (enemyECloc != null && rc.canSenseLocation(enemyECloc)) {
            RobotInfo[] enemyAtLocation = rc.senseNearbyRobots(enemyECloc, 0, enemy);
            if (enemyAtLocation.length != 0) {
                if (enemyAtLocation[0].type != RobotType.ENLIGHTENMENT_CENTER) {
                    flags.sendLocationWithInfo(enemyECloc, InfoCodes.TEAMEC);
                    enemyECloc = null;
                }
            } else {
                RobotInfo[] teamAtLocation = rc.senseNearbyRobots(enemyECloc, 0, team);
                if (teamAtLocation.length != 0) {
                    flags.sendLocationWithInfo(enemyECloc, InfoCodes.TEAMEC);
                    enemyECloc = null;
                }
            }
        }
        if (enemyECloc == null && nearbyEnemys.length != 0) {
            for (RobotInfo b : nearbyEnemys) {
                if (b.type == RobotType.ENLIGHTENMENT_CENTER) {
                    if (!(nearbyTeam.length == 0)) {
                        enemyECloc = b.location;
                    }
                    flags.sendLocationWithInfo(b.location, InfoCodes.ENEMYEC);
                    break;
                }
            }
        } **/
        // --

        // update neutral EC location
        if (neutralECloc != null && myflag == 0) {
            flags.sendLocationWithInfo(neutralECloc, InfoCodes.NEUTRALEC);
        }
        if (neutralECloc != null && rc.canSenseLocation(neutralECloc)) {
            RobotInfo[] neutralAtLocation = rc.senseNearbyRobots(neutralECloc, 0, Team.NEUTRAL);
            if (neutralAtLocation.length == 0) {
                RobotInfo[] teamAtLocation = rc.senseNearbyRobots(neutralECloc, 0, team);
                if (teamAtLocation.length > 0) {
                    flags.sendLocationWithInfo(neutralECloc, InfoCodes.TEAMEC);
                    neutralECloc = null;
                } else {
                    if (nearbyTeam.length > 0) {
                        enemyECloc = neutralECloc;
                    }
                    flags.sendLocationWithInfo(neutralECloc, InfoCodes.ENEMYEC);
                }
            }
        }
        if (neutralECloc == null && nearbyNeutrals.length != 0) {
            for (RobotInfo b : nearbyEnemys) {
                if (b.type == RobotType.ENLIGHTENMENT_CENTER) {
                    if (!(nearbyTeam.length == 0)) {
                        enemyECloc = b.location;
                    }
                    flags.sendLocationWithInfo(b.location, InfoCodes.ENEMYEC);
                    break;
                }
            }
        }
        /**
        if (neutralECloc != null && rc.canSenseLocation(neutralECloc)) {
            for (RobotInfo b : all) {
                if (b.type == RobotType.ENLIGHTENMENT_CENTER && b.team == Team.NEUTRAL) {
                    if (!(nearbyTeam.length == 0)) {
                        neutralECloc = b.location;
                    }
                    flags.sendLocationWithInfo(b.location, InfoCodes.NEUTRALEC);
                    break;
                }
            }
        } **/
    }
}
