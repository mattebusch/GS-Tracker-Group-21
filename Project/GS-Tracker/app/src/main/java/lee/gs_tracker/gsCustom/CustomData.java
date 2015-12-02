package lee.gs_tracker.gsCustom;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Joe Paul on 11/24/2015.
 *
 * manages data for all custom profiles
 * main class reads/writes data to/from disk and nested subclasses to manage data for each
 *    profile type
 *
 * data is stored in the CustomData class in the form of a JSONObject which is read and written
 *    to/from a text file
 * data in each profile subclass is extracted from a JSONObject in the gameList array to a more
 *    familiar java representation and written back to a JSONObject in the array upon calling
 *    ProfileSubclass.UpdateUnload
 *
 * CustomData, all subclasses and supporting objects are static and mostly public so that they may
 *    be referenced by any class or method within any scope
 */
public class CustomData
{
   // game profile entry, contains a name, profile type, and unique id
   static class GameListEntry
   {
      String name;
      int type;
      String stringType;
      long id;
      GameListEntry(String n, int t, long d)
                                 {name = n; type = t; stringType = GAMETYPE [t]; id = d; }
   }

   // constants corresponding to profile types
   public static final int SPORTS = 0;
   public static final int SHOOTER = 1;
   public static final int RPG = 2;
   public static final String[] GAMETYPE = {"Sports", "Shooter", "RPG"};

   // context used for displaying Toast messages
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

   // read compressed data from disk to JSONArray data
   public static boolean ReadDataInit()
   {
      if (data == null)
      {
         try
         {
            FileInputStream fis = context.openFileInput("customdata");
            GZIPInputStream gis = new GZIPInputStream(fis);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int i, n;
            while ((n = gis.read(buf, 0, 4096)) != -1)
               bos.write(buf, 0, n);
            gis.close();
            fis  = null; // attempt at memory efficiency
            gis = null;
            buf = bos.toByteArray();
            String str = new String(buf);
            buf = null;
            data = new JSONArray(str);
            str = null;
            /*Toast.makeText(context, "loaded " + (data.length()) + " profiles", Toast.LENGTH_SHORT)
                  .show();*/
         } catch (Exception e)
         {
            /*e.printStackTrace();
            Toast.makeText(
                  context, "unable to load custom data; " + e.toString(),
                  Toast.LENGTH_LONG
            ).show();*/

            // creates a new JSONArray game list if error
            data = new JSONArray();
            return false;
         }
      }
      RefreshGameList();
      return true;
   }

   // write JSONArray data out to compressed text file on disk
   public static boolean WriteData()
   {
      try
      {
         FileOutputStream fos = context.openFileOutput("customdata",
                                Context.MODE_PRIVATE);
         GZIPOutputStream gos = new GZIPOutputStream(fos);
         byte[] bytes = data.toString().getBytes();
         for (int i = 0; bytes.length - i > 0; i += 4096)
            gos.write(bytes, i, bytes.length < 4096 ? bytes.length - i : 4096);
         gos.close();
         /*Toast.makeText(context, "written " + data.length() + " profiles", Toast.LENGTH_SHORT)
               .show();*/
      }
      catch (Exception e)
      {
         e.printStackTrace();
         Toast.makeText(context, "unable to save custom data; " + e.toString()
               , Toast.LENGTH_SHORT).show();
         return false;
      }
      return true;
   }

   // updates ArrayList<GameListEntry> gameList (used for convenience) from JSONArray data
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

   // returns the entire gameList
   public static ArrayList<GameListEntry> GetGameList()
   {
      RefreshGameList();
      return gameList;
   }

   // returns number of profiles
   public static int GetNumProfiles()
   {
      return data != null ? data.length() : 0;
   }

   // returns the profile name at a certain index
   public static String GetProfileName(int index)
   {
      try
      {
         return data.getJSONObject(index).getString("gamename");
      } catch (Exception e) {return e.toString();}
   }

   // returns the profile type at a certain index
   public static int GetProfileType(int index)
   {
      try
      {
         return data.getJSONObject(index).getInt("type");
      } catch (Exception e) {return 0;}
   }

   // returns the unique profile id at a certain index
   public static long GetProfileId(int index)
   {
      try
      {
         return data.getJSONObject(index).getInt("id");
      } catch (Exception e) {return 0;}
   }

   // private method to generate a new random unique id
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

   // adds a profile with a specified game name and profile type
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
         /*Toast.makeText(context, "added profile (\"" + gamename + "\" + \"" + type + ")",
                        Toast.LENGTH_LONG).show();*/
         RefreshGameList();
         return true;
      }
      catch (Exception e)
      {
         /*Toast.makeText(context, "failed add profile (\"" + gamename + "\" + \"" + type + ")",
                        Toast.LENGTH_LONG).show();*/
         return false;
      }
   }

   // removes a profile at a specified index
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

   // removes all profiles
   public static void RemoveAll()
   {
      data = new JSONArray();
      gameList = new ArrayList<GameListEntry>();
   }

   // returns the index of a the profile for a unique id
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

   // sets the active profile, called from GSCustom before invoking any of the Game Profile
   //    activities, lets specific game profile subclasses know where in the array to read/write
   //    out their data to
   public static void SetActiveProfile(int index)
   {
      pi = index;
   }

   // returns the index of the active profile
   public static int ActiveProfile()
   {
      return pi;
   }

   // manages all data for sports game profiles
   static class Sports
   {
      // used for convenience, java repreentation of data in an ArrayList<Game> games
      static class Game
      {
         int month, day, year, yourScore, opponentScore;
         String yourTeam, opponentTeam;
      }

      private static ArrayList<Game> games = null;

      // extracts the data from the JSONObject in the JSONArray CustomData.data at the active
      //    profile index and stores it in a more familiar java representation
      public static boolean LoadInit()
      {
         games = new ArrayList<Game>();

         try
         {
            if (data.getJSONObject(pi).getInt("type") != CustomData.SPORTS)
            {
               Toast.makeText(context, "error: not a sports profile", Toast.LENGTH_SHORT).show();
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
            /*Toast.makeText(GSCustom.instance, "error loading sports profiles:\n" + e.toString(),
                           Toast.LENGTH_LONG).show();*/
         }

         return true;
      }

      // writes the ArrayList<Game> back a JSONObject in the JSONArray CustomData.data at the
      //    specified active profile index
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
                  context, "failed to save sports profile data\n" + e.toString(),
                  Toast.LENGTH_SHORT
            ).show();
            resCount = -1;
         }
         games = null;

         return resCount;
      }

      // adds a game specifying a date, your team name, your score, opponent team name, and
      //    opponent score
      static void AddGame(int m, int d, int y, String you, String they, int youScore, int theyScore)
      {
         Game g = new Game();
         g.month = m; g.day = d; g.year = y; g.yourTeam = you; g.opponentTeam = they;
         g.yourScore = youScore;
         g.opponentScore = theyScore;
         games.add(g);
      }

      // gets all unique teams, used with AutoCompleteTextView
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

      // gets all unique teams you have played
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

      // gets all unique teams you ahve played against
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

      // returns number of total games played
      public static int GetTotalGamesPlayed()
      {
         return games.size();
      }

      // returns number of total wins
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

      // returns number of total losses
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

      // returns number of total draws
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

      // returns your minimum score
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

      // returns your maximum score
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

      // returns your average score
      public static double GetAveScore()
      {
         double scoreSum = 0;
         for (Game g : games)
            scoreSum += g.yourScore;
         return scoreSum / games.size();
      }

      // returns the average opponent score
      public static double GetAveOppScore()
      {
         double scoreSum = 0;
         for (Game g : games)
            scoreSum += g.opponentScore;
         return scoreSum / games.size();
      }

      // returns the score ratio
      public static double GetScoreRatio()
      {
         return GetAveScore() / GetAveOppScore();
      }

      // returns the Game ArrayList
      public static ArrayList<Game> GetGamesList()
      {
         return games;
      }

      // deletes a set of indexes from ArrayList<Game>
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

      // deletes a single index from ArrayList<Game>
      static void DeleteIndex(int index)
      {
         DeleteIndexes(new int[]{index});
      }
   }

   // manages data for rpg game profiles, similar setup
   // stores an activeQuest index which it uses to manipulate data
   static class Rpg
   {
      static class Quest
      {
         String name;
         String note;
         boolean complete;
      }

      static ArrayList<Quest> quests;
      private static int activeQuest = -1;

      // extract data from JSONOBject
      public static boolean LoadInit()
      {
         quests = new ArrayList<Quest>();

         try
         {
            if (data.getJSONObject(pi).getInt("type") != CustomData.RPG)
            {
               Toast.makeText(context, "error: not an rpg profile", Toast.LENGTH_SHORT).show();
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
            activeQuest = data.getJSONObject(pi).getInt("activequest");
         }
         catch (Exception e)
         {
            e.printStackTrace();
            /*Toast.makeText(GSCustom.instance, "error loading rpg profiles:\n" + e.toString(),
                           Toast.LENGTH_LONG).show();*/
         }

         return true;
      }

      // write data back to JSONObject
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
            data.getJSONObject(pi).put("activequest", activeQuest);
            resCount = quests.size();
         } catch (Exception e)
         {
            Toast.makeText(
                  context, "failed to save rpg profile data\n" + e.toString(),
                  Toast.LENGTH_SHORT
            ).show();
            resCount = -1;
         }
         quests = null;
         activeQuest = -1;

         return resCount;
      }

      // add a quest with a specified name
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

      // returns the number of total quests
      public static int GetNumQuests()
      {
         return quests.size();
      }

      // returns the number of quests marked as complete
      public static int GetNumCompleteQuests()
      {
         int compCount = 0;
         for (Quest q : quests)
            if (q.complete)
               compCount++;
         return compCount;
      }

      // gets the quest object by a specified quest name
      public static Quest GetQuest(String qName)
      {
         if (qName != null && qName.length() != 0)
            for (Quest q : quests)
               if (q.name.compareToIgnoreCase(qName) == 0)
                  return q;
         return null;
      }

      // gets the index of a quest object of a specified quest name
      public static int GetQuestIndex(String qName)
      {
         if (qName != null && qName.length() != 0)
            for (int i = 0; i < quests.size(); i++)
               if (quests.get(i).name.compareToIgnoreCase(qName) == 0)
                  return i;
         return -1;
      }

      // sets the activeQuest to the index of the quest object with the specified quest name
      public static void SetActiveQuest(String qName)
      {
         activeQuest = GetQuestIndex(qName);
      }

      // returns the activeQuest index value
      public static int GetActiveQuest()
      {
         return activeQuest;
      }

      // removes a quest object by a specified name
      public static boolean RemoveQuest(String qName)
      {
         boolean removed = false;
         if (GetQuestIndex(qName) == activeQuest)
            activeQuest = -1;
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

      // removes a quest at a specified index
      public static boolean RemoveQuest(int index)
      {
         if (index < 0 || index >= quests.size())
            return false;
         if (activeQuest == index)
            activeQuest = -1;
         quests.remove(index);
         return true;
      }

      // removes the quest at the activeQuest index
      public static boolean RemoveActiveQuest()
      {
         return RemoveQuest(activeQuest);
      }

      // gets all quest names (for use with AutoCompleteTextView)
      public static String[] GetAllQuestNames()
      {
         String[] qNames = new String[GetNumQuests()];
         for (int i = 0; i < GetNumQuests(); i++)
            qNames [i] = quests.get(i).name;
         return qNames;
      }

      // gets the name of the quest at the activeQuest index
      public static String GetName()
      {
         if (activeQuest >= 0 && activeQuest < quests.size())
            return quests.get(activeQuest).name;
         return null;
      }

      // gets the quest note at the activeQuest index
      public static String GetNote()
      {
         if (activeQuest >= 0 && activeQuest < quests.size())
            return quests.get(activeQuest).note;
         return null;
      }

      // returns whether the quest at the activeQuest index was marked as complete
      public static boolean GetComplete()
      {
         if (activeQuest >= 0 && activeQuest < quests.size())
            return quests.get(activeQuest).complete;
         return false;
      }

      // sets the note at the activeQuest index
      public static void SetNote(String note)
      {
         if (activeQuest >= 0 && activeQuest < quests.size())
            quests.get(activeQuest).note = note;
      }

      // marks the quest at the activeQuest index as isComplete
      public static void SetComplete(boolean isComplete)
      {
         if (activeQuest >= 0 && activeQuest < quests.size())
            quests.get(activeQuest).complete = isComplete;
      }
   }

   // class for managing shooter profile data
   static class Shooter
   {
      static String playerName = null;
      static String weapon = null;
      static int numKills = 0;
      static int numDeaths = 0;

      // extract from JSONObject
      public static boolean LoadInit()
      {
         try
         {
            playerName = data.getJSONObject(pi).getString("playername");
            weapon = data.getJSONObject(pi).getString("weapon");
            numKills = data.getJSONObject(pi).getInt("numkills");
            numDeaths = data.getJSONObject(pi).getInt("numdeaths");
         }
         catch (Exception e)
         {
            e.printStackTrace();
            /*Toast.makeText(GSCustom.instance, "error loading shooter profile:\n" + e.toString(),
                           Toast.LENGTH_SHORT).show();*/
         }

         return true;
      }

      // write back to JSONObject
      public static boolean UpdateUnload()
      {
         try
         {
            if (playerName == null)
               playerName = "";
            if (weapon == null)
               weapon = "";
            data.getJSONObject(pi).put("playername", playerName);
            data.getJSONObject(pi).put("weapon", weapon);
            data.getJSONObject(pi).put("numkills", numKills);
            data.getJSONObject(pi).put("numdeaths", numDeaths);
         } catch (Exception e)
         {
            Toast.makeText(
                  context, "failed to save shooter profile data\n" + e.toString(),
                  Toast.LENGTH_SHORT
            ).show();
            return false;
         }
         playerName = null;
         weapon = null;
         numKills = 0;
         numDeaths = 0;

         return true;
      }
   }
}
