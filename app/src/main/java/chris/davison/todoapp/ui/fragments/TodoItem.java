package chris.davison.todoapp.ui.fragments;

public class TodoItem {
    private String owner;
    private String imageReference;
    private String description;
    private String markedDone;

    public TodoItem() {}

    public TodoItem(String owner, String imageReference, String description, String markedDone) {
        this.owner = owner;
        this.imageReference = imageReference;
        this.description = description;
        this.markedDone = markedDone;
    }

    public String getOwner() { return owner; }

    public String getImageReference() {
        return imageReference;
    }

    public String getDescription() {
        return description;
    }

    public String getMarkedDone() {
        return markedDone;
    }
}