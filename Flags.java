package battlecode2021;
import battlecode.common.*;

public class Flags {
    RobotController rc;
    MapLocation enemyloc = null;

    public Flags(RobotController r) {
        rc = r;
    }
    public int get1(int flag){ return (flag-flag%10000)/10000; }
    public int get2(int flag){ return ((flag%10000)-flag%100)/100; }
    public int get3(int flag){ return flag%100; }

    int getInfoFromFlag(int flag) {
        int info = flag / 1000 / 1000;
        return info;
    }

    boolean sendLocationWithInfo(MapLocation loc, int infocode) throws GameActionException {
        int x = loc.x, y = loc.y;
        int encodedLocation = (x % 1000) * 1000 + (y % 1000) + infocode * 1000 * 1000;
        if (rc.canSetFlag(encodedLocation)) {
            System.out.println("setting flag to " + encodedLocation);
            rc.setFlag(encodedLocation);
            return true;
        } return false;
    }


    MapLocation getLocationFromFlag(int flag) throws GameActionException {
        int y = flag % 1000;
        int x = (flag / 1000) % 1000;

        MapLocation myloc = rc.getLocation();
        int offsetX = (int) (Math.floor(myloc.x / 1000.0) * 1000);
        int offsetY = (int) (Math.floor(myloc.y / 1000.0) * 1000);

        MapLocation _final = new MapLocation(x + offsetX, y + offsetY);
        return _final;
    }

    boolean tryFlag(int flag) throws GameActionException {
        if (rc.canSetFlag(flag)) {
            rc.setFlag(flag);
            return true;
        } else {
            return false;
        }
    }
}
