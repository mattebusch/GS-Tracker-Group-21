package lee.gs_tracker.gsCustom;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Joe Paul on 11/24/2015.
 */
public class CustomData
{
   static class GameListEntry
   {
      String name;
      int type;
      String stringType;
      long id;
      GameListEntry(String n, int t, long d)
                                 {name = n; type = t; stringType = GAMETYPE [t]; id = d; }
   }

   public static final int SPORTS = 0;
   public static final int SHOOTER = 1;
   public static final int RPG = 2;
   public static final String[] GAMETYPE = {"Sports", "Shooter", "RPG"};

   private static Context context;
   private static Random rnd = null;
   private static JSONArray data = null;
   private static int pi;

   private static ArrayList<GameListEntry> gameList = null;

   public static void SetContext(Context c)
   {
      context = c;
   }

   public static void NewData()
   {
      data = new JSONArray();
   }

   public static boolean ReadDataInit()
   {
      if (data == null)
      {
         try
         {
            FileInputStream fis = context.openFileInput("customdata");
            byte[] byteData = new byte[(int) fis.getChannel().size()];
            fis.read(byteData);
            data = new JSONArray(new String(byteData));
            fis.close();
            Toast.makeText(context, "loaded " + (data.length()) + " profiles", Toast.LENGTH_SHORT)
                  .show();
         } catch (Exception e)
         {
            e.printStackTrace();
            Toast.makeText(
                  context, "unable to load custom data; " + e.toString(),
                  Toast.LENGTH_LONG
            ).show();
            data = new JSONArray();
            return false;
         }
      }
      RefreshGameList();
      return true;
   }

   public static boolean WriteData()
   {
      try
      {
         FileOutputStream fos = context.openFileOutput("customdata",
                                Context.MODE_PRIVATE);
         fos.write(data.toString().getBytes());
         fos.close();
         Toast.makeText(context, "written " + data.length() + " profiles", Toast.LENGTH_SHORT)
               .show();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         Toast.makeText(context, "unable to save custom data; " + e.toString()
               , Toast.LENGTH_LONG).show();
         return false;
      }
      return true;
   }

   public static void RefreshGameList()
   {
      gameList = new ArrayList<GameListEntry>();
      try
      {
         for (int i = 0; i < data.length(); i++)
            gameList.add(new GameListEntry(GetProfileName(i), GetProfileType(i), GetProfileId(i)));
         //Toast.makeText(context, "game list size: " + (gameList.size()), Toast.LENGTH_SHORT).show();
      }
      catch (Exception e)
      {
         //Toast.makeText(context, "unable to refresh game list", Toast.LENGTH_SHORT).show();
      }
   }

   public static ArrayList<GameListEntry> GetGameList()
   {
      RefreshGameList();
      return gameList;
   }

   public static int GetNumProfiles()
   {
      return data != null ? data.length() : 0;
   }

   public static String GetProfileName(int index)
   {
      try
      {
         return data.getJSONObject(index).getString("gamename");
      } catch (Exception e) {return e.toString();}
   }

   public static int GetProfileType(int index)
   {
      try
      {
         return data.getJSONObject(index).getInt("type");
      } catch (Exception e) {return 0;}
   }

   public static long GetProfileId(int index)
   {
      try
      {
         return data.getJSONObject(index).getInt("id");
      } catch (Exception e) {return 0;}
   }

   private static long GenerateNewId()
   {
      RefreshGameList();
      long newId;
      long[] allIds = new long [gameList.size()];
      for (int i = 0; i < gameList.size(); i++)
      {
         try
         {
            allIds [i] = data.getJSONObject(i).getInt("id");
         } catch (Exception e){};
         allIds [i] = gameList.get(i).id;
      }
      if (rnd == null)
         rnd = new Random(System.currentTimeMillis());
      boolean contains;
      do
      {
         newId = rnd.nextLong();
         contains = false;
         for (long id : allIds)
            if (id == newId)
               contains = true;
      } while (contains);
      return newId;
   }

   public static boolean AddProfile(String gamename, int type)
   {
      if (gamename == null || gamename.length() == 0 || type < 0 || type >= GAMETYPE.length)
         return false;
      try
      {
         JSONObject profile = new JSONObject();
         profile.put("gamename", gamename);
         profile.put("type", type);
         profile.put("id", GenerateNewId());
         data.put(profile);
         Toast.makeText(context, "added profile (\"" + gamename + "\" + \"" + type + ")",
                        Toast.LENGTH_LONG).show();
         RefreshGameList();
         return true;
      }
      catch (Exception e)
      {
         Toast.makeText(context, "failed add profile (\"" + gamename + "\" + \"" + type + ")",
                        Toast.LENGTH_LONG).show();
         return false;
      }
   }

   public static boolean RemoveProfile(int index)
   {
      if (index < 0 || index >= GetNumProfiles())
         return false;
      try
      {
         JSONArray newData = new JSONArray();
         for (int i = 0; i < data.length(); i++)
            if (i != index)
               newData.put(data.get(i));
         data = newData;
         gameList.remove(index);
      } catch(Exception e) {return false;}
      return true;
   }

   public static void RemoveAll()
   {
      data = new JSONArray();
      gameList = new ArrayList<GameListEntry>();
   }

   public static int GetProfileIndex(long id)
   {
      int index = -1;
      try
      {
         for (int j, i = 0; index == -1 && i < data.length(); i++)
         {
            if ((j = data.getJSONObject(i).getInt("id")) == id)
               index = j;
         }
      } catch (Exception e){}

      return index;
   }

   public static void SetActiveProfile(int index)
   {
      pi = index;
   }

   public static int ActiveProfile()
   {
      return pi;
   }

   static class Sports
   {
      static class Game
      {
         int month, day, year, yourScore, opponentScore;
         String yourTeam, opponentTeam;
      }

      private static ArrayList<Game> games = null;

      public static boolean LoadInit()
      {
         games = new ArrayList<Game>();

         try
         {
            if (data.getJSONObject(pi).getInt("type") != CustomData.SPORTS)
            {
               Toast.makeText(context, "not a sports profile", Toast.LENGTH_SHORT).show();
               return false;
            }
            JSONArray jGames = new JSONArray(data.getJSONObject(pi).getString("games"));
            for (int i = 0; i < jGames.length(); i++)
            {
               JSONObject jGame = jGames.getJSONObject(i);
               Game game = new Game();
               game.month = jGame.getInt("month");
               game.day = jGame.getInt("day");
               game.year = jGame.getInt("year");
               game.yourTeam = jGame.getString("yourteam");
               game.opponentTeam = jGame.getString("opponentteam");
               game.yourScore = jGame.getInt("yourscore");
               game.opponentScore = jGame.getInt("opponentscore");
               games.add(game);
            }
         }
         catch (Exception e)
         {
            e.printStackTrace();
            Toast.makeText(GSCustom.instance, "error loading sports profiles:\n" + e.toString(),
                           Toast.LENGTH_LONG).show();
         }

         return true;
      }

      public static int UpdateUnload()
      {
         int resCount = 0;
         try
         {
            JSONArray jGames = new JSONArray();
            for (int i = 0; i < games.size(); i++)
            {
               Game g = games.get(i);
               JSONObject jGame = new JSONObject();
               jGame.put("month", g.month);
               jGame.put("day", g.day);
               jGame.put("year", g.year);
               jGame.put("yourscore", g.yourScore);
               jGame.put("opponentscore", g.opponentScore);
               jGame.put("yourteam", g.yourTeam);
               jGame.put("opponentteam", g.opponentTeam);
               jGames.put(jGame);
            }
            data.getJSONObject(pi).put("games", jGames);
            resCount = games.size();
         } catch (Exception e)
         {
            Toast.makeText(
                  context, "failed to save profile data\n" + e.toString(),
                  Toast.LENGTH_LONG
            ).show();
            resCount = -1;
         }
         games = null;

         return resCount;
      }

      static void AddGame(int m, int d, int y, String you, String they, int youScore, int theyScore)
      {
         Game g = new Game();
         g.month = m; g.day = d; g.year = y; g.yourTeam = you; g.opponentTeam = they;
         g.yourScore = youScore;
         g.opponentScore = theyScore;
         games.add(g);
      }

      public static String[] GetAllTeams()
      {
         ArrayList<String> teamsAll = new ArrayList<String>();
         for (Game g : games)
         {
            if (!teamsAll.contains(g.yourTeam))
               teamsAll.add(g.yourTeam);
            if (!teamsAll.contains(g.opponentTeam))
               teamsAll.add(g.opponentTeam);
         }
         String[] teamsArray = new String[teamsAll.size()];
         return teamsAll.toArray(teamsArray);
      }

      public static String[] GetAllTeamsPlayedAs()
      {
         ArrayList<String> teamsPlayed = new ArrayList<String>();
         for (Game g : games)
         {
            if (!teamsPlayed.contains(g.yourTeam))
               teamsPlayed.add(g.yourTeam);
         }
         String[] teamsArray = new String[teamsPlayed.size()];
         return teamsPlayed.toArray(teamsArray);
      }

      public static String[] GetAllTeamsPlayedAgainst()
      {
         ArrayList<String> teamsAgainst = new ArrayList<String>();
         for (Game g : games)
         {
            if (!teamsAgainst.contains(g.opponentTeam))
               teamsAgainst.add(g.opponentTeam);
         }
         String[] teamsArray = new String[teamsAgainst.size()];
         return teamsAgainst.toArray(teamsArray);
      }

      public static int GetTotalGamesPlayed()
      {
         return games.size();
      }

      public static int GetTotalWins(String yourTeam, String opponentTeam)
      {
         int wins = 0;
         for (Game g : games)
            if (g.yourScore > g.opponentScore)
            {
               if ((opponentTeam == null || g.opponentTeam.compareToIgnoreCase(opponentTeam) == 0)
                     && (yourTeam == null || g.yourTeam.compareToIgnoreCase(yourTeam) == 0))
                  wins++;
            }
         return wins;
      }

      public static int GetTotalLosses(String yourTeam, String opponentTeam)
      {
         int losses = 0;
         for (Game g : games)
            if (g.yourScore < g.opponentScore)
            {
               if ((opponentTeam == null || g.opponentTeam.compareToIgnoreCase(opponentTeam) == 0) &&
                     (yourTeam == null || g.yourTeam.compareToIgnoreCase(yourTeam) == 0))
                  losses++;
            }
         return losses;
      }

      public static int GetTotalDraws(String yourTeam, String opponentTeam)
      {
         int draws = 0;
         for (Game g : games)
            if (g.yourScore == g.opponentScore)
            {
               if ((opponentTeam == null || g.opponentTeam.compareToIgnoreCase(opponentTeam) == 0)
                     && (yourTeam == null || g.yourTeam.compareToIgnoreCase(yourTeam) == 0))
                  draws++;
            }
         return draws;
      }

      public static int GetMinScore(String yourTeam, String opponentTeam)
      {
         int minScore = Integer.MAX_VALUE;
         for (Game g : games)
            if ((opponentTeam == null || g.opponentTeam.compareToIgnoreCase(opponentTeam) == 0) &&
                  (yourTeam == null || g.yourTeam.compareToIgnoreCase(yourTeam) == 0) &&
                  g.yourScore < minScore)
               minScore = g.yourScore;
         return minScore;
      }

      public static int GetMaxScore(String yourTeam, String opponentTeam)
      {
         int maxScore = Integer.MIN_VALUE;
         for (Game g : games)
            if ((opponentTeam == null || g.opponentTeam.compareToIgnoreCase(opponentTeam) == 0) &&
                  (yourTeam == null || g.yourTeam.compareToIgnoreCase(yourTeam) == 0) &&
                  g.yourScore > maxScore)
               maxScore = g.yourScore;
         return maxScore;
      }

      public static double GetAveScore()
      {
         double scoreSum = 0;
         for (Game g : games)
            scoreSum += g.yourScore;
         return scoreSum / games.size();
      }

      public static double GetAveOppScore()
      {
         double scoreSum = 0;
         for (Game g : games)
            scoreSum += g.opponentScore;
         return scoreSum / games.size();
      }

      public static double GetScoreRatio()
      {
         return GetAveScore() / GetAveOppScore();
      }

      public static ArrayList<Game> GetGamesList()
      {
         return games;
      }

      public static int DeleteIndexes(int[] ii)
      {
         ArrayList<Game> newGames = new ArrayList<Game>();
         int numRemoved = 0;
         for (int i = 0; i < games.size(); i++)
         {
            boolean contains = false;
            for (int j : ii)
               if (i == j)
               {
                  contains = true;
                  numRemoved++;
               }
            if (!contains)
               newGames.add(games.get(i));
         }
         games = newGames;
         return numRemoved;
      }

      static void DeleteIndex(int index)
      {
         DeleteIndexes(new int[]{index});
      }
   }

   static class Rpg
   {
      static class Quest
      {
         String name;
         String note;
         boolean complete;
      }

      static ArrayList<Quest> quests;

      public static boolean LoadInit()
      {
         quests = new ArrayList<Quest>();

         try
         {
            if (data.getJSONObject(pi).getInt("type") != CustomData.RPG)
            {
               Toast.makeText(context, "not an rpg profile", Toast.LENGTH_SHORT).show();
               return false;
            }
            JSONArray jQuests = new JSONArray(data.getJSONObject(pi).getString("quests"));
            for (int i = 0; i < jQuests.length(); i++)
            {
               JSONObject jGame = jQuests.getJSONObject(i);
               Quest q = new Quest();
               q.name = jGame.getString("name");
               q.note = jGame.getString("note");
               q.complete = jGame.getBoolean("complete");
               quests.add(q);
            }
         }
         catch (Exception e)
         {
            e.printStackTrace();
            Toast.makeText(GSCustom.instance, "error loading rpg profiles:\n" + e.toString(),
                           Toast.LENGTH_LONG).show();
         }

         return true;
      }

      public static int UpdateUnload()
      {
         int resCount = 0;
         try
         {
            JSONArray jQuests = new JSONArray();
            for (int i = 0; i < quests.size(); i++)
            {
               Quest q = quests.get(i);
               JSONObject jQuest = new JSONObject();
               jQuest.put("name", q.name);
               jQuest.put("note", q.note);
               jQuest.put("complete", q.complete);
               jQuests.put(jQuest);
            }
            data.getJSONObject(pi).put("quests", jQuests);
            resCount = quests.size();
         } catch (Exception e)
         {
            Toast.makeText(
                  context, "failed to save profile data\n" + e.toString(),
                  Toast.LENGTH_LONG
            ).show();
            resCount = -1;
         }
         quests = null;

         return resCount;
      }

      public static boolean AddQuest(String qName)
      {
         if (GetQuest(qName) != null)
            return false;
         Quest q = new Quest();
         q.name = qName;
         q.note = "";
         q.complete = false;
         quests.add(q);
         return true;
      }

      public static int GetNumQuests()
      {
         return quests.size();
      }

      public static int GetNumCompleteQuests()
      {
         int compCount = 0;
         for (Quest q : quests)
            if (q.complete)
               compCount++;
         return compCount;
      }

      public static Quest GetQuest(String qName)
      {
         for (Quest q : quests)
            if (q.name.compareToIgnoreCase(qName) == 0)
               return q;
         return null;
      }

      public static boolean RemoveQuest(String qName)
      {
         boolean removed = false;
         for (int i = 0; i < quests.size();)
            if (quests.get(i).name.compareToIgnoreCase(qName) == 0)
            {
               quests.remove(i);
               removed = true;
            }
            else
               i++;
         return removed;
      }

      public static void MarkQuestComplete(String qName, boolean isComplete)
      {
         Quest q;
         if ((q = GetQuest(qName)) != null)
            q.complete = isComplete;
      }

      public static String[] GetAllQuestNames()
      {
         String[] qNames = new String[GetNumQuests()];
         for (int i = 0; i < GetNumQuests(); i++)
            qNames [i] = quests.get(i).name;
         return qNames;
      }
   }
}
