package main;

public class Utils {

    public static int binPower(int x) {
        return (int) Math.pow(2, x);
    }

    public static int binLogFloor(int x) {
        return (int) Math.floor(Math.log(x) / Math.log(2));
    }

    public static int binLogCeil(int x) {
        return (int) Math.ceil(Math.log(x) / Math.log(2));
    }

}
