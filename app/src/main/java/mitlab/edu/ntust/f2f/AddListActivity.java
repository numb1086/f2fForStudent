package mitlab.edu.ntust.f2f;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;

/**
 * Created by 勇霆 on 2016/3/20.
 */
public class AddListActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tab_host);

        FragmentTabHost tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        //Add
        tabHost.addTab(tabHost.newTabSpec("Add").setIndicator("Add"),AddFragment.class, null);
        //List
        tabHost.addTab(tabHost.newTabSpec("List").setIndicator("List"),ListFragment.class, null);
    }
}
