package nl.limakajo.numbersbackend.test;

import nl.limakajo.numbersbackend.UpdateThread;
import nl.limakajo.numberslib.numbersGame.Level;
import nl.limakajo.numberslib.utils.DatabaseScheme;
import nl.limakajo.numberslib.utils.JsonUtils;
import nl.limakajo.numberslib.utils.NetworkUtils;
import org.json.JSONObject;

public class TestUpdateInRealDatabase {

    public TestUpdateInRealDatabase() {
        JSONObject currentStateLevelsJson = NetworkUtils.queryLevel(NetworkUtils.NetworkContract.LevelData.TABLE_NAME, "005007003003004003288");
        JSONObject currentStateLevelJson = currentStateLevelsJson.getJSONObject(currentStateLevelsJson.keys().next());
        System.out.println("\nORIGINAL STATE LEVEL");
        System.out.println(currentStateLevelJson);
        int currentAverageTime = currentStateLevelJson.getInt(DatabaseScheme.KEY_AVERAGE_TIME);
        int currentTimesPlayed = currentStateLevelJson.getInt(DatabaseScheme.KEY_TIMES_PLAYED);
        System.out.println("averageTime: " + currentAverageTime);
        System.out.println("timesPlayed: " + currentTimesPlayed);

        int randomUserTime = (int) (Math.random() * 120000);
        Level completedLevel = new Level.LevelBuilder(currentStateLevelJson.getString(DatabaseScheme.KEY_NUMBERS))
                .setUserTime(randomUserTime)
                .build();
        JSONObject completedLevelJson = JsonUtils.levelToJson(completedLevel);
        NetworkUtils.insertLevel(NetworkUtils.NetworkContract.CompletedLevelData.TABLE_NAME, completedLevelJson);
        System.out.println("\nCOMPLETED LEVEL");
        System.out.println(completedLevelJson);
        int completedUserTime = completedLevelJson.getInt(DatabaseScheme.KEY_USER_TIME);
        System.out.println("userTime: " + completedUserTime);

        UpdateThread.update();

        int expectedAverageTime = (currentAverageTime * currentTimesPlayed + completedUserTime ) / (currentTimesPlayed + 1);
        JSONObject newStateLevelsJson = NetworkUtils.queryLevel(NetworkUtils.NetworkContract.LevelData.TABLE_NAME, "005007003003004003288");
        JSONObject newStateLevelJson = newStateLevelsJson.getJSONObject(newStateLevelsJson.keys().next());
        System.out.println("\nNEW STATE LEVEL");
        System.out.println(newStateLevelJson);
        System.out.println("averageTime: " + newStateLevelJson.get(DatabaseScheme.KEY_AVERAGE_TIME) + " (observed)");
        System.out.println("averageTime: " + expectedAverageTime + " (expected)");
        System.out.println("timesPlayed: " + newStateLevelJson.get(DatabaseScheme.KEY_TIMES_PLAYED) + " (observed)");
        System.out.println("timesPlayed: " + (currentStateLevelJson.getInt(DatabaseScheme.KEY_TIMES_PLAYED) + 1) + " (expected)");
    }

}
