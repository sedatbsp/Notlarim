package com.sdtbsp.notdefterim.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.muddzdev.styleabletoast.StyleableToast;
import com.sdtbsp.notdefterim.R;
import com.sdtbsp.notdefterim.adapters.NotesAdapter;
import com.sdtbsp.notdefterim.database.NotesDatabase;
import com.sdtbsp.notdefterim.entities.Note;
import com.sdtbsp.notdefterim.listeners.NotesListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesListener,NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_CODE_ADD_NOTE=1;
    public static final int REQUEST_CODE_UPDATE_NOTE=2;
    public static final int REQUEST_CODE_SHOW_NOTES=3;
    private RecyclerView notesRecyclerView;
    private ImageView imageAddNoteMain;
    private EditText inputSearch;
    private List<Note> noteList;
    private NotesAdapter notesAdapter;
    private int noteClickedPosition = -1;
    private AlertDialog dialogShowAbout;
    DrawerLayout drawerLayout;
    private TextView welcomeTextAddNote;
    private ImageView welcomeImgAddNote;
    private NavigationView navigationView;

    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        constraintLayout = findViewById(R.id.activityMainLayout);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view = getCurrentFocus();
                if(view!=null){
                    InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                }
            }
        });


        drawerLayout = findViewById(R.id.drawerLayout);
        welcomeTextAddNote = findViewById(R.id.welcomeTextAddNote);
        welcomeImgAddNote = findViewById(R.id.welcomeImgAddNote);
        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        imageAddNoteMain  = findViewById(R.id.imgAddNoteMain);
        imageAddNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(getApplicationContext(), CreateNoteActivity.class),
                        REQUEST_CODE_ADD_NOTE
                );
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
            }
        });


        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));

        noteList = new ArrayList<>();


        notesAdapter = new NotesAdapter(noteList,this);
        notesRecyclerView.setAdapter(notesAdapter);



        getNotes(REQUEST_CODE_SHOW_NOTES,false);
        inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                notesAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(noteList.size() != 0){
                    notesAdapter.searchNotes(editable.toString());
                }
            }
        });




    }



    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(),CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate",true);
        intent.putExtra("note",note);
        startActivityForResult(intent,REQUEST_CODE_UPDATE_NOTE);

    }

    private void getNotes(final int requestCode,final boolean isNoteDeleted){
        @SuppressLint("StaticFieldLeak")
        class GetNotesTask extends AsyncTask<Void,Void, List<Note>>{
            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NotesDatabase.getDatabase(getApplicationContext()).noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);

                if(requestCode == REQUEST_CODE_SHOW_NOTES){
                    noteList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();
                }else if(requestCode == REQUEST_CODE_ADD_NOTE){
                    noteList.add(0,notes.get(0));
                    notesAdapter.notifyItemInserted(0);
                    notesRecyclerView.smoothScrollToPosition(0);
                }else if(requestCode == REQUEST_CODE_UPDATE_NOTE){
                    noteList.remove(noteClickedPosition);
                    if(isNoteDeleted){
                        notesAdapter.notifyItemRemoved(noteClickedPosition);
                    }else {
                        noteList.add(noteClickedPosition,notes.get(noteClickedPosition));
                        notesAdapter.notifyItemChanged(noteClickedPosition);
                    }

                }

                if(noteList.size()==0){
                    welcomeTextAddNote.setVisibility(View.VISIBLE);
                    welcomeImgAddNote.setVisibility(View.VISIBLE);
                }else{
                    welcomeTextAddNote.setVisibility(View.GONE);
                    welcomeImgAddNote.setVisibility(View.GONE);
                }

                Log.d("My notes",notes.toString());



            }
        }

        new GetNotesTask().execute();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK){ // resultCode RESULT_OK , createNoteActivity den geliyor.
            getNotes(REQUEST_CODE_ADD_NOTE,false);
        }else if(requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK){
            if(data != null){
                getNotes(REQUEST_CODE_UPDATE_NOTE,data.getBooleanExtra("isNoteDeleted",false));
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        //to prevent current item select over and over
        if (item.isChecked()){
            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        }

        switch (id){
            case R.id.menuNotes:
                //Toast.makeText(this, R.string.su_an_notlar_sayfasindasin, Toast.LENGTH_SHORT).show();
                StyleableToast.makeText(this,"Zaten Notlar sayfasındasın.", Toast.LENGTH_LONG, R.style.mytoast).show();
                break;
            case R.id.menuCreateNote:
                startActivity(new Intent(this,CreateNoteActivity.class));
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
                break;
            case R.id.menuNewPassword:
                startActivity(new Intent(this,UpdatePasswordActivity.class));
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
                break;

            case R.id.menuRateApp:
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                alertBuilder.setTitle("Uygulamamızı Değerlendirin");
                alertBuilder.setMessage("Uygulamayı desteklemek isterseniz google play store üzerinden puan ve yorum bırakabilirsiniz.");
                alertBuilder.setNegativeButton("İptal",null);
                alertBuilder.setPositiveButton("Değerlendir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Uri uri = Uri.parse("market://details?id="+getPackageName());
                        Intent intent = new Intent(Intent.ACTION_VIEW,uri);

                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        try {
                            startActivity(intent);
                        }catch (ActivityNotFoundException e){
                            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("http://play.google.com/store/apps/details?id="+getPackageName())));

                        }
                    }
                });

                final AlertDialog dialog = alertBuilder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg0) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorDelete));
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorIcons));
                    }
                });
                dialog.show();

                break;


            case R.id.menuAppShare:

                String textTitle = "Güvenli Not Uygulaması";
                String textSubject = "Bu uygulamayı sana tavsiye ediyorum. İndirip destek olur musun? ";
                String textNote = "http://play.google.com/store/apps/details?id="+getPackageName();

                Intent mSharingIntent = new Intent(Intent.ACTION_SEND);
                mSharingIntent.setType("text/plain");
                mSharingIntent.putExtra(Intent.EXTRA_TITLE,textTitle);
                mSharingIntent.putExtra(Intent.EXTRA_SUBJECT,textSubject);
                mSharingIntent.putExtra(Intent.EXTRA_TEXT,textSubject+"\n"+textNote);
                startActivity(Intent.createChooser(mSharingIntent,getString(R.string.app_name)));

                break;


            case R.id.menuAboutUs:
                showMenuAboutUs();
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showMenuAboutUs(){
        if(dialogShowAbout == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_show_about,(ViewGroup) findViewById(R.id.layoutShowAboutContainer));

            builder.setView(view);

            dialogShowAbout = builder.create();
            if(dialogShowAbout.getWindow() != null){
                dialogShowAbout.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }


            view.findViewById(R.id.textOK).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StyleableToast.makeText(MainActivity.this,"Bizi değerlendirmeyi unutmayın :)", Toast.LENGTH_LONG, R.style.mytoast).show();
                    dialogShowAbout.dismiss();
                }
            });

        }

        dialogShowAbout.show();

    }



}