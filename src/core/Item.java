package core;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {
    private int row;
    private int col;
    private int num;

    public Item(int r, int c, int n) {
        row = r;
        col = c;
        num = n;
    }
}
