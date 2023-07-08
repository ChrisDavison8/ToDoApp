package chris.davison.todoapp.ui.fragments;

import android.os.Bundle;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.Timer;
import java.util.TimerTask;

import chris.davison.todoapp.R;

public class SplashScreen extends Fragment {

    final private Handler handler = new Handler();

    public SplashScreen() {}

    public static SplashScreen newInstance() {
        return new SplashScreen();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash_screen, container, false);

        initViews();

        Thread timer = new Thread(() -> {
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(() -> {
                        Navigation.findNavController(requireView())
                                .navigate(R.id.action_splashScreen_to_welcomeScreen);
                    });
                }
            }, 2500);
        });
        timer.start();

        return view;
    }

    public void initViews() {
        MaterialToolbar materialToolbar = requireActivity().findViewById(R.id.mainActivityTlbr);
        materialToolbar.setVisibility(View.GONE);
        DrawerLayout drawerLayout = requireActivity().findViewById(R.id.mainActivityMenuDl);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }
}