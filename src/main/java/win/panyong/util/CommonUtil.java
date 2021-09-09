package win.panyong.util;

import org.springframework.boot.system.ApplicationHome;
import org.springframework.web.multipart.MultipartFile;
import win.panyong.utils.StringUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class CommonUtil {

    public static String sortString(String s) {
        char[] ar = s.toCharArray();
        Arrays.sort(ar);
        return String.valueOf(ar);
    }

    public static String[] reverse(String[] arr) {
        for (int i = arr.length - 1; i >= arr.length / 2; i--) {
            String temp = arr[i]; // 0
            int a = arr.length - 1 - i; // 5
            arr[i] = arr[a]; // 将末位赋值给首位
            arr[a] = temp;
        }
        return arr;
    }

    public static <T> Consumer<T> consumerWithIndex(BiConsumer<T, Integer> consumer) {
        class Obj {
            int i;
        }
        Obj obj = new Obj();
        return t -> {
            int index = obj.i++;
            consumer.accept(t, index);
        };
    }

    public static <T> Predicate<T> predicateWithIndex(BiPredicate<T, Integer> predicate) {
        class Obj {
            int i;
        }
        Obj obj = new Obj();
        return t -> {
            int index = obj.i++;
            return predicate.test(t, index);
        };
    }

    public static <T, R> Function<T, R> functionWithIndex(BiFunction<T, Integer, R> function) {
        class Obj {
            int i;
        }
        Obj obj = new Obj();
        return t -> {
            int index = obj.i++;
            return function.apply(t, index);
        };
    }

    public static Integer[] getRandomArray(List<Integer> list, int size) {
        Set<Integer> set = new HashSet<>(size);
        while (set.size() < size) {
            int offset = size - set.size();
            list.removeIf(set::contains);
            for (int i = 0; i < offset; i++) {
                Integer integer = list.get(new Random().nextInt(list.size()));
                set.add(integer);
            }
        }
        Integer[] arr = set.toArray(new Integer[size]);
        CommonUtil.integerSort(arr);
        return arr;
    }

    public static File uploadFile(String filePath, MultipartFile multipartFile) throws IOException {
        String fileFullPath = new ApplicationHome(CommonUtil.class) + "/fileupload" + filePath;
        File file = new File(fileFullPath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        multipartFile.transferTo(file);
        return file;
    }

    public static List<String> getPPTImageList(String pptFilePath) {
        String pptFileDir = pptFilePath.substring(0, pptFilePath.lastIndexOf("/"));
        String imageDir = pptFilePath.substring(pptFilePath.lastIndexOf("/"), pptFilePath.lastIndexOf("."));
        File imageListPath = new File(new ApplicationHome(CommonUtil.class) + pptFileDir + imageDir);
        String[] list = imageListPath.list();
        if (list != null) {
            return Arrays.stream(list).sorted(Comparator.comparing(s -> Integer.parseInt(s.split("-")[1].split("\\.")[0]))).map(s -> pptFileDir + imageDir + File.separator + s).collect(Collectors.toList());
        } else {
            return null;
        }
    }

    public static String getCellStringValue(Object o) {
        return o != null ? o.toString() : null;
    }

    public static String stringSort(String s) {
        char[] tempArray = s.toCharArray();
        int maxNum = tempArray[0];
        for (int temp : tempArray) {
            if (temp > maxNum) {
                maxNum = temp;
            }
        }
        int[] bucket = new int[maxNum + 1];
        bucket[0] = 0;
        for (int temp : tempArray) {
            bucket[temp]++;
        }
        int index = 0;
        for (int i = 0; i < bucket.length; i++) {
            while (bucket[i]-- > 0) {
                tempArray[index++] = (char) i;
            }
        }
        return String.copyValueOf(tempArray);
    }

    public static void integerSort(Integer[] tempArray) {
        int maxNum = tempArray[0];
        for (int temp : tempArray) {
            if (temp > maxNum) {
                maxNum = temp;
            }
        }
        int[] bucket = new int[maxNum + 1];
        bucket[0] = 0;
        for (int temp : tempArray) {
            bucket[temp]++;
        }
        int index = 0;
        for (int i = 0; i < bucket.length; i++) {
            while (bucket[i]-- > 0) {
                tempArray[index++] = i;
            }
        }
    }

    public static Map<String, Integer> stringCount(String s) {
        Map<String, Integer> map = new HashMap<>();
        if (!StringUtil.invalid(s)) {
            char[] tempArray = s.toCharArray();
            int maxNum = tempArray[0];
            for (int temp : tempArray) {
                if (temp > maxNum) {
                    maxNum = temp;
                }
            }
            int[] bucket = new int[maxNum + 1];
            bucket[0] = 0;
            for (int temp : tempArray) {
                bucket[temp]++;
            }
            for (int i = 0; i < bucket.length; i++) {
                if (bucket[i] > 0) {
                    map.put(String.valueOf((char) i), bucket[i]);
                }
            }
        }
        return map;
    }

    public static Map<Integer, Integer> integerCount(Integer[] tempArray) {
        Map<Integer, Integer> map = new HashMap<>();
        int maxNum = tempArray[0];
        for (int temp : tempArray) {
            if (temp > maxNum) {
                maxNum = temp;
            }
        }
        int[] bucket = new int[maxNum + 1];
        bucket[0] = 0;
        for (int temp : tempArray) {
            bucket[temp]++;
        }
        for (int i = 0; i < bucket.length; i++) {
            if (bucket[i] > 0) {
                map.put(i, bucket[i]);
            }
        }
        return map;
    }


}
