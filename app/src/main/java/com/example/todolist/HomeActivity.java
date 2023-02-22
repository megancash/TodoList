//Student Name: Megan Cash
//Student Number: C19317723
package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class HomeActivity extends AppCompatActivity {

    //Widgets
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private ProgressDialog progress;
    private DatabaseReference reference;
    private EditText searchBar;

    //Create instances of the Firebase Database
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    //Initialising Global Variables
    private String id;
    private String key ="";
    private String category;
    private String description;
    private String deadline;
    private String status;
    private ArrayList <ToDoItem> taskDataSet = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().setTitle("Home");
        mAuth = FirebaseAuth.getInstance();

        searchBar = findViewById(R.id.searchBar);
        recyclerView = findViewById(R.id.todolist);
        floatingActionButton = findViewById(R.id.addTask);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        progress= new ProgressDialog(this);

        mUser=mAuth.getCurrentUser();
        id = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("To do list tasks").child(id);

        //Search Task of current user by category and status, and display them in RCV
/*        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                FirebaseRecyclerAdapater.filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              addTask();  //addTask method
            }
        });
    }
    //Add task method - To allow user to add a new task to the to-do list.
    private void addTask() {
        AlertDialog.Builder taskDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View taskView = inflater.inflate(R.layout.add_task, null);
        taskDialog.setView(taskView);

        final AlertDialog dialog = taskDialog.create();
        dialog.setCancelable(false);

        final EditText category = taskView.findViewById(R.id.inputCategory);
        final EditText description = taskView.findViewById(R.id.inputDescription);
        final EditText deadline = taskView.findViewById(R.id.inputDeadline);
        final EditText status = taskView.findViewById(R.id.inputStatus);
        Button save = taskView.findViewById(R.id.addTaskButton);
        Button cancel = taskView.findViewById(R.id.cancelButton);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mCategory = category.getText().toString().trim();
                String mDescription = description.getText().toString().trim();
                String mDeadline = deadline.getText().toString().trim();
                String mStatus = status.getText().toString().trim();


                if (TextUtils.isEmpty(mCategory)) {
                    category.setError("Error! Input task"); //Validation - if category is empty
                    return;
                }
                if (TextUtils.isEmpty(mDescription)) { //Validation - if description is empty
                    description.setError("Error! Input description");
                    return;
                }
                if (TextUtils.isEmpty(mDeadline)) { //Validation - if deadline is empty
                    deadline.setError("Error! Input deadline");
                    return;
                }
                if (TextUtils.isEmpty(mStatus)) { //Validation - if status is empty
                    status.setError("Error! Input status. Must be incomplete or done");
                    return;
                }
                if (mStatus.equalsIgnoreCase("Incomplete") ||   mStatus.equalsIgnoreCase("done")) {
                    progress.setMessage("Adding your task");
                    progress.setCanceledOnTouchOutside(false);
                    progress.show();

                    ToDoItem toDoItem = new ToDoItem(mCategory, mDescription, mDeadline, mStatus);
                    reference.child(id).setValue(toDoItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //taskDataSet.add(toDoItem);
                                Toast.makeText(HomeActivity.this, "Your task has been added to the To do list!", Toast.LENGTH_SHORT).show();
                                progress.dismiss();
                            } else {
                                String error = task.getException().toString();
                                Toast.makeText(HomeActivity.this, "Error! Task could not be added to the To do List.", Toast.LENGTH_SHORT).show();
                                progress.dismiss();
                            }
                        }
                    });
                }
                else {
                    status.setError("Error! Input status. Must be incomplete or done");
                    return;
                }
                dialog.dismiss();
            }
        });
        dialog.show();
        }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<ToDoItem> options = new FirebaseRecyclerOptions.Builder<ToDoItem>()
                .setQuery(reference, ToDoItem.class)
                .build();

        FirebaseRecyclerAdapter<ToDoItem, taskViewHolder> adapter = new FirebaseRecyclerAdapter<ToDoItem, taskViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull taskViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull ToDoItem toDoItem) {
                holder.setCategory(toDoItem.getCategory());
                holder.setDescription(toDoItem.getDescription());
                holder.setDeadline(toDoItem.getDeadline());
                holder.setStatus(toDoItem.getStatus());

                //Creating an OnClickListener to be able to update or delete a task when a task is selected.
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        key = getRef(position).getKey();
                        category = toDoItem.getCategory();
                        description = toDoItem.getDescription();
                        deadline = toDoItem.getDeadline();
                        status = toDoItem.getStatus();

                        updateTask();
                    }
                });

            }

            @NonNull
            @Override
            public taskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.get_tasks,parent, false);
                return new taskViewHolder(v);
            }

    /*        ArrayList<ToDoItem> arrayList;
            public void filter(CharSequence charSequence){
                ArrayList<ToDoItem> taskDataSet = new ArrayList<>();
                if (!TextUtils.isEmpty(charSequence)) {
                    for (ToDoItem toDoItem : arrayList) {
                        if (toDoItem.getTitle().toLowerCase().contains(charSequence)) {
                            taskDataSet.add(toDoItem);
                        }
                    }
                }
            }*/
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }


    //Creating an adapter which will allow us to retrieve task data
        public static class taskViewHolder extends RecyclerView.ViewHolder{
            View view;
            //Constructor matching super
            public taskViewHolder(@NonNull View itemView) {
                super(itemView);
                view = itemView;
            }
            public void setCategory(String category) {
                TextView taskTextView = view.findViewById(R.id.category1);
                taskTextView.setText(category);
            }
            public void setDescription(String description) {
                TextView taskTextView = view.findViewById(R.id.description1);
                taskTextView.setText(description);
            }
            public void setDeadline(String deadline) {
                TextView taskTextView = view.findViewById(R.id.deadline1);
                taskTextView.setText(deadline);
            }
            public void setStatus(String status) {
                TextView taskTextView = view.findViewById(R.id.status1);
                taskTextView.setText(status);
            }
        }
        //To allow user to update their tasks.
        private void updateTask() {
            AlertDialog.Builder taskDialog = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            View v = inflater.inflate(R.layout.update_task, null);
            taskDialog.setView(v);

            final AlertDialog dialog = taskDialog.create();

            final EditText category1 = v.findViewById(R.id.updateCategoryField);
            final EditText description1 = v.findViewById(R.id.updateDescriptionField);
            final EditText deadline1 = v.findViewById(R.id.updateDeadlineField);
            final EditText status1 = v.findViewById(R.id.updateStatusField);



            category1.setText(category);
            category1.setSelection(category.length());

            description1.setText(description);
            description1.setSelection(description.length());

            deadline1.setText(deadline);
            deadline1.setSelection(deadline.length());

            status1.setText(status);
            status1.setSelection(status.length());

            //Update Button
            Button updateButton = v.findViewById(R.id.updateButton);
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   category = category1.getText().toString().trim();
                   description = description1.getText().toString().trim();
                   deadline = deadline1.getText().toString().trim();
                   status = status1.getText().toString().trim();
                    //Validation
                    /*if (TextUtils.isEmpty(task)) { //Validation - if task is empty
                        task1.setError("Error! Input task");
                        return;
                    }
                    if (TextUtils.isEmpty(category)) {
                        category1.setError("Error! Input task"); //Validation - if category is empty
                        return;
                    }
                    if (TextUtils.isEmpty(description)) { //Validation - if description is empty
                        description1.setError("Error! Input description");
                        return;
                    }
                    if (TextUtils.isEmpty(deadline)) { //Validation - if deadline is empty
                        deadline1.setError("Error! Input deadline");
                        return;
                    }
                    if (TextUtils.isEmpty(status)) { //Validation - if status is empty
                        status1.setError("Error! Input status. Must be incomplete or done");
                        return;
                    }
                    if (status.equalsIgnoreCase("Incomplete") ||   status.equalsIgnoreCase("done")) {
                        progress.setMessage("Adding your task");
                        progress.setCanceledOnTouchOutside(false);
                        progress.show();*/

                        ToDoItem toDoItem = new ToDoItem(category, description, deadline, status);

                        reference.child(key).setValue(toDoItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(HomeActivity.this, "Your task has been updated successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    String error = task.getException().toString();
                                    Toast.makeText(HomeActivity.this, "Error! Task could not be updated." + error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    /*}else {
                        status1.setError("Error! Input status. Must be incomplete or done");
                        return;*/
                   dialog.dismiss();
                }
            });

            //Delete Button
            Button deleteButton = v.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful()) {
                               Toast.makeText(HomeActivity.this, "Task deleted from To do List.", Toast.LENGTH_SHORT).show();
                           } else {
                               String error = task.getException().toString();
                               Toast.makeText(HomeActivity.this, "Error! Task not deleted" + error, Toast.LENGTH_SHORT).show();
                           }
                        }
                    });
                   dialog.dismiss();
                }
            });
            dialog.show();
        }


    //Using suitable menu in app - Option Menu
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.home) {
            Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
            startActivity(intent);
        } else if (id == R.id.profile) { //To bring user to profile page when checkout profile icon is selected.
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.logout) { //To log user out when the logout item is selected.
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(HomeActivity.this, "You have successfully logged out!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
        }
        return true;
    }

    //Option Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_icon, menu);
       return super.onCreateOptionsMenu(menu);

    }
}
