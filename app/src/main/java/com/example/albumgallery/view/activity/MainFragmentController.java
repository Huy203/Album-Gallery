    package com.example.albumgallery.view.activity;

    import android.os.Bundle;

    import androidx.annotation.Nullable;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.fragment.app.Fragment;
    import androidx.fragment.app.FragmentManager;
    import androidx.fragment.app.FragmentTransaction;

    import com.example.albumgallery.R;
    import com.example.albumgallery.databinding.ActivityFragmentControllerBinding;
    import com.example.albumgallery.databinding.ActivityHomeScreenBinding;
    import com.example.albumgallery.view.fragment.AlbumsMainFragment;
    import com.example.albumgallery.view.fragment.HomeScreenFragment;

    public class MainFragmentController extends AppCompatActivity {
        ActivityFragmentControllerBinding binding;
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityFragmentControllerBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            // fragment đầu tiên khi vừa vào app
            replaceFragment(new HomeScreenFragment());

            binding.bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if(itemId == R.id.photos) {
                    replaceFragment(new HomeScreenFragment());
                } else if (itemId == R.id.albums) {
                    replaceFragment(new AlbumsMainFragment());
                } else if (itemId == R.id.favorites) {
                    // xử lý cho màn hình favorites
                }
                return true;
            });


        }
        private void replaceFragment(Fragment fragment) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }
    }
