package com.example.qltc.views.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;

import com.example.qltc.adapters.ViewPagerAdapter;
import com.example.qltc.models.Transaction;
import com.example.qltc.utils.Constants;
import com.example.qltc.utils.Utility;
import com.example.qltc.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private MaterialToolbar toolBar;
    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        toolBar.setTitle("Chi tiêu");
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.transactions).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.stats).setChecked(true);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.transactions) {
                    viewPager.setCurrentItem(0);
                } else if (id == R.id.stats) {
                    viewPager.setCurrentItem(1);
                } else if (id == R.id.more) {
                    showMenu();
                }
                return true;
            }
        });

        Constants.setCategories();

//        calendar = Calendar.getInstance();

//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.content, new TransactionsFragment());
//        transaction.commit();
//
        bottomNavigationView.setItemIconTintList(null);

//        Transaction transaction = new Transaction("THU", "Lương", "Bank", "TEST NOTE", Calendar.getInstance().getTime(), 100000, "123d123");
//        createTransaction(transaction);

//        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                if(item.getItemId() == R.id.transactions) {
//                    getSupportFragmentManager().popBackStack();
//                } else if(item.getItemId() == R.id.stats){
//                    transaction.replace(R.id.content, new StatsFragment());
//                    transaction.addToBackStack(null);
//                } else if(item.getItemId() == R.id.more) {
//                    showMenu();
//                }
//                transaction.commit();
//                return true;
//            }
//        });

    }

    private void showMenu() {
        PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.more));
        popupMenu.getMenu().add("Đăng xuất");
        popupMenu.setOnMenuItemClickListener(it -> {
            if (Objects.equals(it.getTitle(), "Đăng xuất")) {
                Utility.signOut(this);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void initView() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        viewPager = findViewById(R.id.viewPager);
        toolBar = findViewById(R.id.toolBar);
    }

    private void createTransaction(Transaction transaction) {
            Utility.addTransaction(transaction, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}