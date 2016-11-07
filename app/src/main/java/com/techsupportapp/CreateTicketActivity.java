package com.techsupportapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techsupportapp.databaseClasses.Ticket;
import com.techsupportapp.fragments.BottomSheetFragment;
import com.techsupportapp.utility.DatabaseVariables;
import com.techsupportapp.utility.Globals;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateTicketActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //region Fields

    private DatabaseReference databaseReference;

    private int ticketCount;
    private String rightDate;

    //endregion

    //region Composite Controls

    private EditText topicET;
    private EditText messageET;

    private FloatingActionButton fab;

    private ImageView currUserImage;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ticket);

        initializeComponents();

        setEvents();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.acceptedTickets) {
            finish();
        } else if (id == R.id.settings) {
            Intent intent = new Intent(this, PreferencesActivity.class);
            startActivity(intent);
        } else if (id == R.id.about) {
            Globals.showAbout(CreateTicketActivity.this);
            return true;
        } else if (id == R.id.logOut) {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        } else if (id == R.id.exit) {
            new MaterialDialog.Builder(this)
                    .title("Закрыть приложение")
                    .content("Вы действительно хотите закрыть приложение?")
                    .positiveText(android.R.string.yes)
                    .negativeText(android.R.string.no)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            exit();
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

    private void initializeComponents() {
        topicET = (EditText)findViewById(R.id.message_topic_text);
        messageET = (EditText)findViewById(R.id.message_text);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        fab = (FloatingActionButton) findViewById(R.id.fab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Создать заявку");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        currUserImage = (ImageView)navigationView.getHeaderView(0).findViewById(R.id.userImage);
        TextView userName = (TextView)navigationView.getHeaderView(0).findViewById(R.id.userName);
        TextView userType = (TextView)navigationView.getHeaderView(0).findViewById(R.id.userType);

        currUserImage.setImageBitmap(Globals.ImageMethods.getclip(Globals.ImageMethods.createUserImage(Globals.currentUser.getUserName(), CreateTicketActivity.this)));

        userName.setText(Globals.currentUser.getUserName());
        userType.setText("Пользователь");

        Menu nav_menu = navigationView.getMenu();
        nav_menu.findItem(R.id.userActions).setVisible(false);
        nav_menu.findItem(R.id.charts).setVisible(false);
        nav_menu.findItem(R.id.acceptedTickets).setTitle("Список ваших заявок");
        nav_menu.findItem(R.id.listOfTickets).setTitle("Создать заявку");
    }

    private void setEvents() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (topicET.getText().toString().equals("") || messageET.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Заполните поля", Toast.LENGTH_LONG).show();
                } else {
                    String newRightDate = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                    try {
                        if (rightDate == null) {
                            databaseReference.child(DatabaseVariables.Indexes.DATABASE_FIRST_DATE_INDEX).setValue(newRightDate);
                            databaseReference.child(DatabaseVariables.Indexes.DATABASE_LAST_DATE_INDEX).setValue(newRightDate);
                        }
                        else if (!newRightDate.equals(rightDate))
                            databaseReference.child(DatabaseVariables.Indexes.DATABASE_LAST_DATE_INDEX).setValue(newRightDate);
                        Ticket newTicket = new Ticket("ticket" + ticketCount, Globals.currentUser.getLogin(), Globals.currentUser.getUserName(), topicET.getText().toString(), messageET.getText().toString());
                        databaseReference.child(DatabaseVariables.Tickets.DATABASE_UNMARKED_TICKET_TABLE).child("ticket" + ticketCount++).setValue(newTicket);
                        databaseReference.child(DatabaseVariables.Indexes.DATABASE_TICKET_INDEX_COUNTER).setValue(ticketCount);
                        Toast.makeText(getApplicationContext(), "Заявка добалена", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ticketCount = dataSnapshot.child(DatabaseVariables.Indexes.DATABASE_TICKET_INDEX_COUNTER).getValue(int.class);
                rightDate = dataSnapshot.child(DatabaseVariables.Indexes.DATABASE_LAST_DATE_INDEX).getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        currUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogFragment bottomSheetDialogFragment = BottomSheetFragment.newInstance(Globals.currentUser.getLogin(), Globals.currentUser.getLogin(), Globals.currentUser);
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });
    }

    private void exit(){
        this.finishAffinity();
    }

}
