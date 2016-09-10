package eladkay.scanner.misc;

public class Vec2i {
    private final int x;
    private final int y;
    public Vec2i(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public String serialize() {
        return x + "/" + y;
    }

    public static Vec2i deserialize(String s) {
        String[] split = s.split("/");
        return new Vec2i(Integer.valueOf(split[0]), Integer.valueOf(split[1]));
    }

    public Vec2i add(Vec2i vec) {
        return new Vec2i(vec.getX() + x, vec.getY() + y);
    }

    public Vec2i add(int x, int y) {
        return new Vec2i(this.x + x, this.y + y);
    }

    public Vec2i subtract(Vec2i vec) {
        return new Vec2i(vec.getX() - x, vec.getY() - y);
    }

    public Vec2i subtract(int x, int y) {
        return new Vec2i(this.x - x, this.y - y);
    }

    @Override
    public int hashCode() {
        return x * 41234475 + y * 412237852;
    }
}
