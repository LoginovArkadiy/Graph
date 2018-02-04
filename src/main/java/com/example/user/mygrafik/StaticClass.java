package com.example.user.mygrafik;

public class StaticClass {
    static int toFive(int x) {
        while (x % 5 != 0)
            x++;
        return x;
    }

    /**
     * @param num   число
     * @param count количество знаков после запятой
     * @return
     */
    static String roundTo(float num, byte count) {
        String s = Float.toString(num);
        int i = 0;
        for (; i < s.length(); i++) {
            if (s.charAt(i) == '.') break;
        }
        try {
            return s.substring(0, i + count);
        } catch (StringIndexOutOfBoundsException e) {
            return s;
        }
    }

    static final int INTEGRAL = 2;
    static final int GRAPH = 1;

}
