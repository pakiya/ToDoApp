package singh.pk.todoappdemo.ui.pojo;

public class Category {

    public String category_name;
    public String todo_count;

    public Category(){  }

    public Category(String categoryName, String todo_count) {
        this.category_name = categoryName;
        this.todo_count = todo_count;
    }

    public String getCategoryName() {
        return category_name;
    }

    public void setCategoryName(String categoryName) {
        this.category_name = categoryName;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getTodo_count() {
        return todo_count;
    }

    public void setTodo_count(String todo_count) {
        this.todo_count = todo_count;
    }
}
