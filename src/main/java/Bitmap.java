public class Bitmap {
    public int w;
    public int h;
    public int size;
    public int[] data;

    public Bitmap(int w, int h) {
        this.w = w;
        this.h = h;
        this.size = w * h;
        this.data = new int[this.size];
    }

    public boolean at(int x, int y) {
        return (x >= 0 && x < this.w && y >=0 && y < this.h) &&
                this.data[this.w * y + x] == 1;
    }

    public Point index(int i) {
        Point point = new Point();
        point.y = i / this.w;
        point.x = i - point.y * this.w;
        return point;
    }

    public void flip(int x, int y) {
        if (this.at(x, y)) {
            this.data[this.w * y + x] = 0;
        } else {
            this.data[this.w * y + x] = 1;
        }
    }

    public Bitmap copy() {
        Bitmap bm = new Bitmap(this.w, this.h);
        for (int i = 0; i < this.size; i++) {
            bm.data[i] = this.data[i];
        }
        return bm;
    }

}
