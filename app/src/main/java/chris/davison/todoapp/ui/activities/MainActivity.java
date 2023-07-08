package chris.davison.todoapp.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.customview.widget.Openable;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import chris.davison.todoapp.R;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mainActivityFcv);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        Openable openable = findViewById(R.id.mainActivityMenuDl);
        MaterialToolbar materialToolbar = findViewById(R.id.mainActivityTlbr);
        NavigationView navigationView = findViewById(R.id.mainActivityNV);

        appBarConfiguration = new AppBarConfiguration.Builder(R.id.myListScreen,
                R.id.welcomeScreen, R.id.splashScreen, R.id.createTodoScreen)
                .setOpenableLayout(openable).build();

        setSupportActionBar(materialToolbar);

        NavigationUI.setupActionBarWithNavController(this, navController,
                appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}