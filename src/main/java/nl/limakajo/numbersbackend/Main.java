package nl.limakajo.numbersbackend;

public class Main {

    public static void main(String[] args) {
        UpdateThread updateThread = new UpdateThread();
        updateThread.start();
        updateThread.setRunning();


//        TestUpdateInRealDatabase testUpdateInRealDatabase = new TestUpdateInRealDatabase();



    }

}
