package singh.pk.todoappdemo.ui.add_todo_item;

public class TodoPojo {

    public String todo_name;
    public String todo_description;
    public String todo_image;
    public String thumb_image;
    public String todo_status;


    public TodoPojo() {
    }

    public TodoPojo(String todo_name, String todo_description, String todo_image, String thumb_image, String todo_status) {
        this.todo_name = todo_name;
        this.todo_description = todo_description;
        this.todo_image = todo_image;
        this.thumb_image = thumb_image;
        this.todo_status = todo_status;
    }

    public String getTodo_name() {
        return todo_name;
    }

    public void setTodo_name(String todo_name) {
        this.todo_name = todo_name;
    }

    public String getTodo_description() {
        return todo_description;
    }

    public void setTodo_description(String todo_description) {
        this.todo_description = todo_description;
    }

    public String getTodo_image() {
        return todo_image;
    }

    public void setTodo_image(String todo_image) {
        this.todo_image = todo_image;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getTodo_status() {
        return todo_status;
    }

    public void setTodo_status(String todo_status) {
        this.todo_status = todo_status;
    }
}
