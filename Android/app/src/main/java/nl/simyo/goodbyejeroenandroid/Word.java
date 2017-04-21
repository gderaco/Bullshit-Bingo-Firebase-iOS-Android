package nl.simyo.goodbyejeroenandroid;

/**
 * Created by giuseppederaco on 04/02/2017.
 */

class Word {
    private String name;
    private Integer count;

    public Word(String name, Integer count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
