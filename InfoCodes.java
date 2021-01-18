package battlecode2021;


import battlecode.common.Team;

public class InfoCodes {
    public static final int ENEMYEC = 1;
    public static final int TEAMEC = 2;
    public static final int NEUTRALEC = 3;

    public static int convertToInfoCode(Team team, Team myteam) {
        if (team != null && myteam != null) {
            if (team==myteam) {
                return InfoCodes.TEAMEC;
            } else if (myteam.opponent() == team) {
                return InfoCodes.ENEMYEC;
            } else {
                return InfoCodes.NEUTRALEC;
            }
        } return 0;
    }
}
