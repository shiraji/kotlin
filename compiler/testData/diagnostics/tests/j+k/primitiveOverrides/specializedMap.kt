// FILE: AbstractSpecializedMap.java
public abstract class AbstractSpecializedMap extends java.util.Map<Integer, Double> {
    public abstract double put(int x, double y);
    public abstract double remove(int k);
    public abstract double get(int k);

    public abstract boolean containsKey(int k);
    public boolean containsKey(Object x) {
        return false;
    }

    public boolean containsValue(double v);
    public boolean containsValue(Object x) {
        return false;
    }
}

// FILE: SpecializedMap.java
public abstract class SpecializedMap extends AbstractSpecializedMap {
    public double put(int x, double y) {
        return 1.0;
    }

    @Override
    public Double put(Integer key, Double value) {
        return super.put(key, value);
    }

    public double remove(int k) {
        return defRetValue;
    }

    public Double remove(Object ok) {
        return null;
    }

    public Double get(Integer ok) {
        return null;
    }

    public double get(int k) {
        return defRetValue;
    }

    public boolean containsKey(int k) {
        return false;
    }

    public boolean containsValue(double v) {
        return false
    }
}

// FILE: main.kt
fun foo(x: SpecializedMap) {
    x.containsKey(1)
    x.containsKey(null)

    x.get(2)
    x.get(null)

    x.remove(3)
    x.remove(null)

    x.put(4, 5.0)
    x.put(4, null)
}
