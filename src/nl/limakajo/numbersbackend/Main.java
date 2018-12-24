package nl.limakajo.numbersbackend;

import nl.limakajo.numberslib.utils.DatabaseScheme;
import nl.limakajo.numberslib.utils.NetworkUtils;
import org.json.JSONObject;

import java.util.Iterator;

public class Main {

    public static void main(String[] args) {

        update();



    }

    private static void update() {
        JSONObject completedLevelsJson = NetworkUtils.queryLevels(NetworkUtils.NetworkContract.CompletedLevelData.TABLE_NAME);
        JSONObject completedLevelsToUpdateJson = new JSONObject();
        Iterator<String> keys = completedLevelsJson.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            JSONObject completedLevelJson = completedLevelsJson.getJSONObject(key);

            //get the level from table leveldata
            JSONObject currentStateLevelsJson = NetworkUtils.queryLevel(NetworkUtils.NetworkContract.LevelData.TABLE_NAME, completedLevelJson.getString(DatabaseScheme.KEY_NUMBERS));
            JSONObject currentStateLevelJson = currentStateLevelsJson.getJSONObject(currentStateLevelsJson.keys().next()); //currentStateLevelJson only has one member

            //update averageTime in completedLevelsJson based on earlier results
            int timesPlayed = currentStateLevelJson.getInt(DatabaseScheme.KEY_TIMES_PLAYED);
            int totalTime = timesPlayed * currentStateLevelJson.getInt(DatabaseScheme.KEY_AVERAGE_TIME);
            totalTime += completedLevelJson.getInt(DatabaseScheme.KEY_USER_TIME);
            timesPlayed++;
            int newAverageTime = totalTime / timesPlayed;
            completedLevelJson.put(DatabaseScheme.KEY_AVERAGE_TIME, newAverageTime);

            //add to completedLevelsToUpdateJson
            completedLevelsToUpdateJson.put(key, completedLevelJson);
        }
        JSONObject successfullyUpdatedLevelsJson = NetworkUtils.updateLevelAverageTime(NetworkUtils.NetworkContract.LevelData.TABLE_NAME, completedLevelsToUpdateJson);
        JSONObject successFullyDeletedLevelsJson = NetworkUtils.deleteLevels(NetworkUtils.NetworkContract.CompletedLevelData.TABLE_NAME, successfullyUpdatedLevelsJson);
    }

}
