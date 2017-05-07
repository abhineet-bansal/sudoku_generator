package core;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class Board {
    private int[] values;

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        String str = "";
        for (int val : values) str += val;
        return str;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Board other = (Board) obj;
        return this.toString().equals(other.toString());
    }
}
