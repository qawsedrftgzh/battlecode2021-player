package battlecode2021;
import battlecode.common.*;

public class Robot {
    RobotController rc;
    Flags flags;
    int turnCount = 0;

    public Robot(RobotController r) {
        this.rc = r;
        flags = new Flags(rc);
    }

    public void takeTurn() throws GameActionException {
        turnCount += 1;
    }
}
