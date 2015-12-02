package lee.gs_tracker.Starcraft;

import org.json.simple.JSONObject;

/**
 * Created by Lee on 12/1/2015.
 */
public class StarcraftAPIUser {
    public static String getProfileName(org.json.simple.JSONObject User){
        try{
            return User.get("displayName").toString();
        }
        catch(Exception e){
            return "ERROR";
        }
    }

    public static org.json.simple.JSONObject getCareer(org.json.simple.JSONObject User){
        try{
            return (JSONObject) User.get("career");
        }
        catch(Exception E){
            return null;
        }
    }

    public static String getPrimaryRace(org.json.simple.JSONObject Career){
        try{
            return Career.get("primaryRace").toString();
        }
        catch(Exception E){
            return "ERROR";
        }
    }

    public static String getTerranWins(org.json.simple.JSONObject Career){
        try{
            return Career.get("terranWins").toString();
        }
        catch(Exception E){
            return "ERROR";
        }
    }

    public static String getprotossWins(org.json.simple.JSONObject Career){
        try{
            return Career.get("protossWins").toString();
        }
        catch(Exception E){
            return "ERROR";
        }
    }

    public static String getzergWins(org.json.simple.JSONObject Career){
        try{
            return Career.get("zergWins").toString();
        }
        catch(Exception E){
            return "ERROR";
        }
    }

    public static String getSoloRank(org.json.simple.JSONObject Career){
        try{
            return Career.get("highest1v1Rank").toString();
        }
        catch(Exception E){
            return "ERROR";
        }
    }

    public static String getTeamRank(org.json.simple.JSONObject Career){
        try{
            return Career.get("highestTeamRank").toString();
        }
        catch(Exception E){
            return "ERROR";
        }
    }
    public static String getTotalGames(org.json.simple.JSONObject Career){
        try{
            return Career.get("careerTotalGames").toString();
        }
        catch(Exception E){
            return "ERROR";
        }
    }

}
