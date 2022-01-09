package org.xbatis.spring.boot.autoconfigure.util;

import java.util.ArrayList;
import java.util.HashMap;

public class SqlTableMode {
    private final HashMap<Integer, char[][]> modeMap = new HashMap<Integer, char[][]>(1 << ReplaceMode.values().length) {{
        ReplaceMode[] replaceModes = ReplaceMode.values();
        HashMap<Integer, char[]> tempMap = new HashMap<>(replaceModes.length);
        ArrayList<Integer> modes = new ArrayList<Integer>(replaceModes.length) {{
            for (ReplaceMode replaceMode : replaceModes) {
                add(replaceMode.mode);
                tempMap.put(replaceMode.mode, replaceMode.prefixSuffix);
            }
        }};
        ArrayList<ArrayList<Integer>> allReplaceMode = getAllReplaceMode(modes, 0);
        //遍历所有子集
        for (ArrayList<Integer> list : allReplaceMode) {
            //遍历所有替换模式并装载对应前后缀
            char[][] chars = new char[list.size()][2];
            int sum = 0;
            for (int i = 0; i < list.size(); i++) {
                Integer mode = list.get(i);
                chars[i] = tempMap.get(mode);
                sum += i;
            }
            put(sum, chars);
        }
    }};

    public enum ReplaceMode {

        BTB(0b0001, new char[]{' ', ' '}),
        CTB(0b0010, new char[]{',', ' '}),
        BTC(0b0100, new char[]{' ', ','}),
        CTC(0b1000, new char[]{',', ','});
        private final int mode;
        private final char[] prefixSuffix;

        ReplaceMode(int mode, char[] prefixSuffix) {
            this.mode = mode;
            this.prefixSuffix = prefixSuffix;
        }
    }

    private ArrayList<ArrayList<Integer>> getAllReplaceMode(ArrayList<Integer> integers, int idx) {
        ArrayList<ArrayList<Integer>> all = new ArrayList<>();
        if (idx + 1 < integers.size()) {
            all = getAllReplaceMode(integers, idx + 1);
            Integer integer = integers.get(idx);
            ArrayList<ArrayList<Integer>> subs = new ArrayList<>();
            for (ArrayList<Integer> sub : all) {
                ArrayList<Integer> temp = new ArrayList<>(sub);
                temp.add(integer);
                subs.add(temp);
            }
            all.addAll(subs);
        }
        all.add(new ArrayList<Integer>() {{
            add(integers.get(idx));
        }});
        return all;
    }

}
