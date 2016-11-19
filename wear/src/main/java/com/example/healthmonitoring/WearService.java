package com.example.healthmonitoring;

import android.content.Intent;
import android.util.Log;

/**
 * Created by A-team on 10/07/2016.
 */
public class WearService extends TeleportService{



    @Override
    public void onCreate() {
        super.onCreate();

        //The quick way is to use setOnGetMessageTask, and set a new task
        setOnGetMessageTask(new StartActivityTask());


        //alternatively, you can use the Builder to create new Tasks
        /*
        setOnGetMessageTaskBuilder(new OnGetMessageTask.Builder() {
            @Override
            public OnGetMessageTask build() {
                return new OnGetMessageTask() {
                    @Override
                    protected void onPostExecute(String path) {
                        if (path.equals("startActivity")){

                            Intent startIntent = new Intent(getBaseContext(), WearActivity.class);
                            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(startIntent);
                        }

                    }
                };
            }
        });
        */

    }

    //Task that shows the path of a received message
    public class StartActivityTask extends TeleportService.OnGetMessageTask {

        @Override
        protected void onPostExecute(String  path) {

       if (path.equals("startActivity")){
           Log.d("WearService", path);

            Intent startIntent = new Intent(getBaseContext(), MainActivity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startIntent);
           //let's reset the task (otherwise it will be executed only once)
           setOnGetMessageTask(new StartActivityTask());
         }



        }
    }


}
