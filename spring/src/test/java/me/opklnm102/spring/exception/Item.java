package me.opklnm102.spring.exception;

public class Item {

    public static final Item NOT_FOUND = new Item(0);

    private Integer id;

    public Item(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                '}';
    }
}
