package battlecode2021;


import battlecode.common.Team;

public class InfoCodes {
    public static final int ENEMYEC = 1;
    public static final int TEAMEC = 2;
    public static final int NEUTRALEC = 3;
    public static final int ATTACKENEMY = 4;

    public static int convertToInfoCode(Team team, Team myteam) {
        if (team != null && myteam != null) {
            if (team==myteam) {
                return InfoCodes.TEAMEC;
            } else if (team.opponent() == myteam) {
                return InfoCodes.ENEMYEC;
            } else {
                return InfoCodes.NEUTRALEC;
            }
        } return 0;
    }
}
