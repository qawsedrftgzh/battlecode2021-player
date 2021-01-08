package battlecode2021;
import battlecode.common.*;

public class Flags {
    RobotController rc;
    Navigation nav;
    MapLocation enemyloc = null;

    public Flags(RobotController r) {
        rc = r;
        nav = new Navigation(rc);
    }

    public void main() throws GameActionException {
        RobotType type = rc.getType();
        MapLocation myloc = rc.getLocation();
        int emergenst1 = 0; //der erste wert beschreibt die priorität, 58342 -> 5 erster wert, 83 zweiter wert, 42 ist der dritte
        int emergenstflag = 0;
        int act1;
        int act2;
        int act3;
        boolean toflag = false;
        int flagtoset = 0;
        RobotInfo[] bots = rc.senseNearbyRobots(type.detectionRadiusSquared,rc.getTeam());
        int flag;
        RobotInfo[] enemys = rc.senseNearbyRobots(type.detectionRadiusSquared,rc.getTeam().opponent());
        if (enemys.length != 0) {
            toflag = true;
            MapLocation enemysloc = enemys[0].getLocation();
            flagtoset = 50000+((enemysloc.x%100)*100)+(enemysloc.y%100);
            enemyloc = enemysloc;
        }
        for (RobotInfo bot : bots) {
            flag = rc.getFlag(bot.ID);
            act1 = get1(flag);
            act2 = get2(flag);
            act3 = get3(flag);
            switch (act1) {//5 -> feindliche Roboter
                case 5:
                    enemyloc = new MapLocation(((myloc.x-myloc.x%100)+act2),((myloc.y-myloc.y%100)+act3));
            }
            if (get1(flag) >= emergenst1) {
                emergenst1 = get1(flag);
                emergenstflag = flag;
            }
        }if (!toflag){rc.setFlag(emergenstflag); } else { rc.setFlag(flagtoset);}
    }
    public static int get1(int flag){ return (flag-flag%10000)/10000; }
    public static int get2(int flag){ return ((flag%10000)-flag%100)/100; }
    public static int get3(int flag){ return flag%100; }
}
