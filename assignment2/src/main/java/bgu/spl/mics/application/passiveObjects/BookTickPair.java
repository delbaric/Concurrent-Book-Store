package bgu.spl.mics.application.passiveObjects;

public class BookTickPair {

    private  String bookTitle;
    private  int tick;

    public BookTickPair(String bookTitle, int tick){
        this.bookTitle = bookTitle;
        this.tick = tick;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public int getTick() {
        return tick;
    }
}
