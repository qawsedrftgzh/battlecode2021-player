package battlecode2021;
import battlecode.common.*;

public class Robot {
    RobotController rc;
    Flags flags;
    int turnCount = 0;
    Team enemy, team;

    public Robot(RobotController r) {
        this.rc = r;
        flags = new Flags(rc);
        enemy = rc.getTeam().opponent();
        team = rc.getTeam();
    }

    public void takeTurn() throws GameActionException {
        turnCount += 1;
    }
}
