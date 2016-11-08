package com.techsupportapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techsupportapp.databaseClasses.User;
import com.techsupportapp.fragments.BottomSheetFragment;
import com.techsupportapp.fragments.UserActionsFragments;
import com.techsupportapp.utility.DatabaseVariables;
import com.techsupportapp.utility.Globals;

import java.util.ArrayList;

public class UserActionsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private ViewPager viewPager;
    private UserActionsFragments.SectionsPagerAdapter mSectionsPagerAdapter;

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    private ArrayList<User> unverifiedUsersList = new ArrayList<User>();
    private ArrayList<User> usersList = new ArrayList<User>();

    private MenuItem searchMenu;
    private SearchView searchView;
    private static boolean search;

    private ImageView currUserImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_actions);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        supportInvalidateOptionsMenu();
        initializeComponents();
        setEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        databaseReference.addValueEventListener(valueEventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseReference.removeEventListener(valueEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        databaseReference.removeEventListener(valueEventListener);
    }

    private void initializeComponents(){
        databaseReference = FirebaseDatabase.getInstance().getReference();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Пользователи");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        currUserImage = (ImageView)navigationView.getHeaderView(0).findViewById(R.id.userImage);
        TextView userName = (TextView)navigationView.getHeaderView(0).findViewById(R.id.userName);
        TextView userType = (TextView)navigationView.getHeaderView(0).findViewById(R.id.userType);

        currUserImage.setImageBitmap(Globals.ImageMethods.getclip(Globals.ImageMethods.createUserImage(Globals.currentUser.getUserName(), UserActionsActivity.this)));

        mSectionsPagerAdapter = new UserActionsFragments.SectionsPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        userName.setText(Globals.currentUser.getUserName());
        userType.setText("Администратор");
        navigationView.getMenu().findItem(R.id.charts).setVisible(false);

        search = false;
    }

    public static String getLogInMessage(User unVerifiedUser) throws Exception {
        int role = unVerifiedUser.getRole();
        String resultString = "Вы действительно хотите создать аккаунт " + unVerifiedUser.getLogin()
                + " и дать ему права ";
        if (role == User.SIMPLE_USER)
            return resultString + "ПОЛЬЗОВАТЕЛЯ";
        else if (role == User.DEPARTMENT_MEMBER)
            return resultString + "РАБОТНИКА ОТДЕЛА";
        else if (role == User.ADMINISTRATOR)
            return resultString + "АДМИНИСТРАТОРА";
        else if (role == User.DEPARTMENT_CHIEF)
            return resultString + "НАЧАЛЬНИКА ОТДЕЛА";
        else throw new Exception("Передана нулевая ссылка или неверно указаны права пользователя");
    }

    public static String getDatabaseUserPath(User unVerifiedUser) throws Exception {
        int role = unVerifiedUser.getRole();
        if (role == User.SIMPLE_USER)
            return DatabaseVariables.Users.DATABASE_VERIFIED_SIMPLE_USER_TABLE;
        else if (role == User.DEPARTMENT_MEMBER)
            return DatabaseVariables.Users.DATABASE_VERIFIED_WORKER_TABLE;
        else if (role == User.ADMINISTRATOR)
            return DatabaseVariables.Users.DATABASE_VERIFIED_ADMIN_TABLE;
        else if (role == User.DEPARTMENT_CHIEF)
            return DatabaseVariables.Users.DATABASE_VERIFIED_CHIEF_TABLE;
        else throw new Exception("Передана нулевая ссылка или неверно указаны права пользователя");
    }

    private void setEvents() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!search) {
                    unverifiedUsersList = Globals.Downloads.getSpecificVerifiedUserList(dataSnapshot, DatabaseVariables.Users.DATABASE_UNVERIFIED_USER_TABLE);
                    if (!mSectionsPagerAdapter.updateFirstFragment(unverifiedUsersList, UserActionsActivity.this, databaseReference, search))
                        MenuItemCompat.collapseActionView(searchMenu);


                    usersList = Globals.Downloads.getVerifiedUserList(dataSnapshot);
                    mSectionsPagerAdapter.updateSecondFragment(usersList, UserActionsActivity.this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        currUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogFragment bottomSheetDialogFragment = BottomSheetFragment.newInstance(Globals.currentUser.getLogin(), Globals.currentUser.getLogin(), Globals.currentUser);
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (search) {
                    MenuItemCompat.collapseActionView(searchMenu);
                    search = false;

                    mSectionsPagerAdapter.updateFirstFragment(unverifiedUsersList, UserActionsActivity.this, databaseReference, search);
                    mSectionsPagerAdapter.updateSecondFragment(usersList, UserActionsActivity.this);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            finish();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.acceptedTickets) {
            finish();
        } else if (id == R.id.listOfTickets) {
            Intent intent = new Intent(UserActionsActivity.this, ListOfTicketsActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.charts) {
            Intent intent = new Intent(UserActionsActivity.this, ChartsActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.settings) {
            Intent intent = new Intent(this, PreferencesActivity.class);
            startActivity(intent);
        } else if (id == R.id.about) {
            Globals.showAbout(UserActionsActivity.this);
        } else if (id == R.id.logOut) {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.exit) {
            new MaterialDialog.Builder(this)
                    .title("Закрыть приложение")
                    .content("Вы действительно хотите закрыть приложение?")
                    .positiveText(android.R.string.yes)
                    .negativeText(android.R.string.no)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            UserActionsActivity.this.finishAffinity();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.cancel();
                        }
                    })
                    .show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        search = false;
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_actions, menu);
        searchMenu = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenu.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            public boolean onQueryTextChange(String newText) {
                search = true;
                if (viewPager.getCurrentItem() == 0) {
                    ArrayList<User> newUnverifiedUsersList = new ArrayList<User>();

                    for (User unverifiedUser : unverifiedUsersList) {
                        if (unverifiedUser.getUserName().toLowerCase().contains(searchView.getQuery().toString().toLowerCase()))
                            newUnverifiedUsersList.add(unverifiedUser);
                    }

                    mSectionsPagerAdapter.updateFirstFragment(newUnverifiedUsersList, UserActionsActivity.this, databaseReference, search);
                }
                else
                {
                    ArrayList<User> newUsersList = new ArrayList<User>();

                    for (User user: usersList){
                        if (user.getUserName().toLowerCase().contains(searchView.getQuery().toString().toLowerCase()))
                            newUsersList.add(user);
                    }

                    mSectionsPagerAdapter.updateSecondFragment(newUsersList, UserActionsActivity.this);
                }
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchMenu, new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        search = true;

                        return true;
                    }
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        search = false;
                        return true;
                    }
                });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
