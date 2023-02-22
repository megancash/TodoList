//Student Name: Megan Cash
//Student Number: C19317723
package com.example.todolist;

public class ToDoItem {

    //Variables
    public String task, category, description, deadline, status,id;

    //Constructor
    public ToDoItem() {

    }

    public ToDoItem(String category, String description, String deadline, String status) {
        this.category = category;
        this.description = description;
        this.deadline = deadline;
        this.status = status;
        this.id=id;
    }

    //Getter and Setter Methods
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public  String getId () { return status; }

    public void setId(String id) {this.id=id;}
}
