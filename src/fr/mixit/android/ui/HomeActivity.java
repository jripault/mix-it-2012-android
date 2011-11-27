package fr.mixit.android.ui;

import fr.mixit.android.R;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class HomeActivity extends BaseActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home, menu);

        // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
            	// TODO : managing menu refresh button
                Toast.makeText(this, "Fake refreshing...", Toast.LENGTH_SHORT).show();
                getActionBarHelper().setRefreshActionItemState(true);
                getWindow().getDecorView().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                getActionBarHelper().setRefreshActionItemState(false);
                            }
                        }, 1000);
                break;

            case R.id.menu_search:
            	// TODO : managing menu search button
                Toast.makeText(this, "Tapped search", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_user:
            	// TODO : managing menu profile button
                Toast.makeText(this, "Tapped user profile", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_sponsor:
            	// TODO : managing menu sponsors button
                Toast.makeText(this, "Tapped sponsors", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_participants:
            	// TODO : managing menu participants button
                Toast.makeText(this, "Tapped participant Mix-it", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_about:
            	// TODO : managing menu about button
                Toast.makeText(this, "Tapped about", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_settings:
            	// TODO : managing menu settings button
                Toast.makeText(this, "Tapped settings", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
}