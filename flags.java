package battlecode2021;

import battlecode.common.*;
import static battlecode2021.RobotPlayer.*;
public strictfp class flags {
    public static void main() throws GameActionException {
        RobotType type = rc.getType();
        int emergenst2 = 0; //der zweite wert beschreibt die priorität
        int emergenstflag = 0;
        RobotInfo[] bots = rc.senseNearbyRobots(type.detectionRadiusSquared,rc.getTeam());
        int flag;
        for (RobotInfo bot : bots) {
            flag = rc.getFlag(bot.ID);
            if (get2(flag) >= emergenst2) {
                emergenst2 = get2(flag);
                emergenstflag = flag;
            }
        }rc.setFlag(emergenstflag);
    }
    public static int get1(int flag){
        return (flag-flag%65536)/65536;
    }
    public static int get2(int flag){
        return ((flag%65536)-flag%256)/256;
    }
    public static int get3(int flag){
        return flag%256;
    }
}
