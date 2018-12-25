package nl.limakajo.numbersbackend;

import nl.limakajo.numberslib.utils.DatabaseScheme;
import nl.limakajo.numberslib.utils.NetworkUtils;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class UpdateThread extends Thread {

    private boolean running;
    private static final int WAIT_TIME = 15;

    @Override
    public void run() {
        while (running) {
            try {
                TimeUnit.SECONDS.sleep(WAIT_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            update();
        }

    }

    void setRunning() {
        running = true;
    }

    public static void update() {
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
            completedLevelJson.put(DatabaseScheme.KEY_TIMES_PLAYED, timesPlayed);

            //add to completedLevelsToUpdateJson
            completedLevelsToUpdateJson.put(key, completedLevelJson);
        }
        JSONObject successfullyUpdatedLevelsJson = NetworkUtils.updateLevelAverageTime(NetworkUtils.NetworkContract.LevelData.TABLE_NAME, completedLevelsToUpdateJson);
        JSONObject successFullyDeletedLevelsJson = NetworkUtils.deleteLevels(NetworkUtils.NetworkContract.CompletedLevelData.TABLE_NAME, successfullyUpdatedLevelsJson);
        if (!successFullyDeletedLevelsJson.toString().equals("{}")) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            System.out.println("TIMESTAMP: " + dtf.format(now) + " UPDATED: " + successFullyDeletedLevelsJson);
        }
        else {
            System.out.println(".");
        }
    }

}
