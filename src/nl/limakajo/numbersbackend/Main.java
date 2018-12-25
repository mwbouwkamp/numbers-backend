package nl.limakajo.numbersbackend;

import nl.limakajo.numbersbackend.test.TestUpdateInRealDatabase;
import nl.limakajo.numberslib.numbersGame.Level;
import nl.limakajo.numberslib.utils.DatabaseScheme;
import nl.limakajo.numberslib.utils.JsonUtils;
import nl.limakajo.numberslib.utils.NetworkUtils;
import org.json.JSONObject;

import java.util.Iterator;

public class Main {

    public static void main(String[] args) {
        UpdateThread updateThread = new UpdateThread();
        updateThread.start();
        updateThread.setRunning();


//        TestUpdateInRealDatabase testUpdateInRealDatabase = new TestUpdateInRealDatabase();



    }

}
