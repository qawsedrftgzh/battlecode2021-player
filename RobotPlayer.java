package battlecode2021;
import battlecode.common.*;

public strictfp class RobotPlayer {

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {
        Robot me = null;

        switch (rc.getType()) {
            case ENLIGHTENMENT_CENTER:  me = new EnlightenmentCenter(rc); break;
            case POLITICIAN:            me = new Politician(rc);          break;
            case SLANDERER:             me = new Slanderer(rc);           break;
            case MUCKRAKER:             me = new Muckraker(rc);           break;
        }

        while (true) {
            try {
                if (me.type == RobotType.SLANDERER && me.turnCount == 300) {
                    me = new Politician(rc);
                }
                me.takeTurn();
                Clock.yield();
            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();
            }
        }
    }
}
