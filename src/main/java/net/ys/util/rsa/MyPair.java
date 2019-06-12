package net.ys.util.rsa;

/**
 * User: NMY
 * Date: 17-11-14
 */
public class MyPair<T, K> {
    private T l;
    private K r;

    public MyPair(T l, K r) {
        this.l = l;
        this.r = r;
    }

    public T getL() {
        return l;
    }

    public void setL(T l) {
        this.l = l;
    }

    public K getR() {
        return r;
    }

    public void setR(K r) {
        this.r = r;
    }
}
