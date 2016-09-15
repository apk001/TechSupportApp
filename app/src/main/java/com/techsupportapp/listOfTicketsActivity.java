package com.techsupportapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListOfTicketsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ListView viewOfTickets;
    private TextView problemBut;
    private DatabaseReference databaseRef;
    private ArrayList<Ticket> listOfTickets = new ArrayList<Ticket>();
    private ArrayAdapter<Ticket> adapter;

    private String mAppId;
    private String mUserId;
    private String mNickname;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_tickets);

        mAppId = getIntent().getExtras().getString("appKey");
        mUserId = getIntent().getExtras().getString("uuid");
        mNickname = getIntent().getExtras().getString("nickname");
        isAdmin = getIntent().getExtras().getBoolean("isAdmin");

        initializeComponents();
        setEvents();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.listOfChannels) {
            Intent intent = new Intent(ListOfTicketsActivity.this, ListOfChannelsActivity.class);
            intent.putExtra("appKey", mAppId);
            intent.putExtra("uuid", mUserId);
            intent.putExtra("nickname", mNickname);
            intent.putExtra("isAdmin", isAdmin);
            startActivity(intent);
        } else if (id == R.id.settings) {

        } else if (id == R.id.about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ListOfTicketsActivity.this);
            builder.setTitle("О программе");
            String str = String.format("Tech Support App V1.0");
            builder.setMessage(str);
            builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {

                }
            });
            builder.setCancelable(false);
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        } else if (id == R.id.exit) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initializeComponents() {
        viewOfTickets = (ListView)findViewById(R.id.listOfTickets);

        databaseRef = FirebaseDatabase.getInstance().getReference();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Список заявок");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setEvents() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listOfTickets.clear();
                for (DataSnapshot ticketRecord : dataSnapshot.child(DatabaseVariables.DATABASE_UNMARKED_TICKET_TABLE).getChildren()) {
                    Ticket ticket = ticketRecord.getValue(Ticket.class);
                    listOfTickets.add(ticket);
                }
                adapter = new ArrayAdapter<Ticket>(getApplicationContext(), android.R.layout.simple_list_item_1, listOfTickets);
                viewOfTickets.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        viewOfTickets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listOfTickets.get(position).addAdmin(mUserId);
                databaseRef.child(DatabaseVariables.DATABASE_MARKED_TICKET_TABLE).child(listOfTickets.get(position).ticketId).setValue(listOfTickets.get(position));
                databaseRef.child(DatabaseVariables.DATABASE_UNMARKED_TICKET_TABLE).child(listOfTickets.get(position).ticketId).removeValue();

                Intent intent = new Intent(ListOfTicketsActivity.this, ChatActivity.class);
                Bundle args = ChatActivity.makeMessagingStartArgs(mAppId, mUserId, mNickname, listOfTickets.get(position).userId);
                intent.putExtras(args);

                startActivityForResult(intent, 210);
            }
        });
    }

}
