import java.util.*;
import static java.lang.Math.ceil;

/**
 * @author Danila Shulepin
 */
public class MainTaskA {


    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        String line = scan.nextLine();

        int n = Integer.parseInt(line.split(" ")[0]);
        int d = Integer.parseInt(line.split(" ")[1]);

        long max = Integer.MIN_VALUE;
        long min = Integer.MAX_VALUE;

        ArrayList<Date> dateList = new ArrayList<>();

        //Inserting all the elements inside the list
        for (int i = 0; i < n; i++) {
            line = scan.nextLine();
            String date = line.split(" \\$")[0];
            double value = Double.parseDouble(line.split(" \\$")[1]);

            Date dateObject = new Date(date, value);

            if (min > dateObject.numberOfDays) min = dateObject.numberOfDays;
            if (max < dateObject.numberOfDays) max = dateObject.numberOfDays;
            dateList.add(dateObject);
        }

        //Sorting list with dates using radix sort
        RadixSort radixSort = new RadixSort();
        radixSort.sort(dateList, dateList.size());

        int count = 0;

        ArrayDeque<Double> queue = new ArrayDeque<>();
        ArrayList<Double> elements = new ArrayList<>();

        long lastDate = 0;
        int k = 1;
        double accumulator = 0;

        if (!dateList.isEmpty()) {
            //Filling queue and auxiliary array with first d days
            lastDate = dateList.get(0).numberOfDays;
            accumulator += (dateList.get(0).value);
            while (queue.size() != d) {
                //If current day is the same as the last one, add its value to accumulator
                if (dateList.get(k).numberOfDays == lastDate) {
                    accumulator += (dateList.get(k).value);
                    k++;
                }
                //If current day is the next day for the last one,
                // add accumulator value to the queue and list and renew accumulator
                else if (dateList.get(k).numberOfDays == lastDate + 1) {
                    queue.add(accumulator);
                    elements.add(accumulator);
                    accumulator = (dateList.get(k).value);
                    if (queue.size() == d) break;
                    k++;
                    lastDate++;
                }
                //Zero day
                else {
                    queue.add(accumulator);
                    elements.add(accumulator);
                    accumulator = 0;
                    lastDate++;
                }

            }
        }


        lastDate = min + d - 1;
        accumulator = 0;

        while (true) {
            //If current day is the same as the last one, compare value with doubled median
            if (dateList.get(k).numberOfDays == lastDate + 1) {
                Median m = new Median(elements, d);
                double median = m.getDoubleMedian();
                accumulator += dateList.get(k).value;
                if (accumulator >= median) count++;
                k++;
            }
            //Renew day inside queue and array
            else {
                lastDate++;
                double y = queue.poll();
                elements.remove((Object) y);
                queue.add(accumulator);
                elements.add(accumulator);
                accumulator = 0;

            }
            if (k >= dateList.size()) break;
        }
        System.out.println(count);

    }
}

/**
 * Class of median finding
 *
 * @author Danila Shulepin
 */
class Median {
    double m;

    /**
     * Constructor
     * @param list - to find median of this array
     * @param d - size of array
     */
    Median(ArrayList<Double> list, int d) {
        //Sorting array with insertion sort
        InsertionSort obj = new InsertionSort();
        obj.sort(list);
        if (d % 2 == 0) {
            m = (list.get(d / 2) + list.get(d / 2 - 1));
        } else {
            m = 2 * list.get(d / 2);
        }
    }

    /**
     * @return doubled median
     */
    double getDoubleMedian() {
        return m;
    }
}

/**
 * Class of Date
 *
 * @author Danila Shulepin
 */
class Date {
    int year;
    int month;
    int day;
    double value;
    long numberOfDays;

    /**
     * Constructor
     * @param date - string version YYYY-MM-DD
     * @param value
     */
    Date(String date, double value) {
        String temp = date;
        year = Integer.parseInt(temp.split("-")[0]);
        month = Integer.parseInt(temp.split("-")[1]);
        day = Integer.parseInt(temp.split("-")[2]);
        this.value = value;
        numberOfDays = dateToNum(year, month, day);
    }

    /**
     * Checking if the year is a leap one
     * @param year
     * @return true - it's leap, false - otherwise
     */
    public static boolean isLeapYear(int year) {
        return (year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0);
    }

    /**
     * Converting date to number of days
     * @param year
     * @param month
     * @param day
     * @return number of days
     */
    public static int dateToNum(int year, int month, int day) {
        int[] currentDaysInMonths = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
        
        if (month < 1)
            month = 1;
        else if (month > 12) {
            year += (month - 1) / 12;
            month = ((month - 1) % 12) + 1;
        }
        
        int num = (int) (365 * year + ceil(year / 4.0) - ceil(year / 100.0) + ceil(year / 400.0) + currentDaysInMonths[month - 1] + day);

        if (month > 2 && isLeapYear(year))
            ++num;

        return num;
    }
}

/**
 * Class of radix sort
 *
 * @author Danila Shulepin
 */
class RadixSort {

    /**
     * Radix sort using array
     *
     * Time complexity: O(n*k)
     * k - number of digits
     * n - number of numbers
     *
     * @param arr - array
     * @param n - number of elements
     */
    void sort(int[] arr, int n) {
        int m = max(arr, n);
        CountingSort countingSort = new CountingSort();

        int count = 1;
        while (m / count > 0) {
            countingSort.sort(arr, n, count, 10);
            count *= 10;
        }
    }

    /**
     * Maximum value
     *
     * @param arr - array
     * @param n - number of elements
     * @return max value from array
     */
    int max(int[] arr, int n) {
        int m = Integer.MIN_VALUE;

        for (int i = 0; i < n; i++) {
            if (arr[i] > m) m = arr[i];
        }
        return m;
    }

    /**
     * Radix sort using array list
     *
     * Time complexity: O(n*k)
     * k - number of digits
     * n - number of numbers
     *
     * @param arr - array list
     * @param n - number of elements
     */
    void sort(ArrayList<Date> arr, int n) {
        int m = max(arr, n);
        CountingSort countingSort = new CountingSort();

        int count = 1;
        while (m / count > 0) {
            countingSort.sort(arr, n, count, 10);
            count *= 10;
        }
    }


    /**
     * Maximum value
     *
     * @param arr - array list
     * @param n - number of elements
     * @return max value from array
     */
    int max(ArrayList<Date> arr, int n) {
        int m = Integer.MIN_VALUE;

        for (int i = 0; i < n; i++) {
            if (arr.get(i).numberOfDays > m) m = (int) arr.get(i).numberOfDays;
        }
        return m;
    }
}

/**
 * Class of counting sort
 *
 * @author Danila Shulepin
 */
class CountingSort {
    /**
     * Counting sort using array
     *
     * Time complexity: O(n+k)
     * k - range of elements
     * n - number of numbers
     *
     * @param arr - array
     * @param n - number of elements
     * @param count - divider
     * @param x - range
     */
    void sort(int[] arr, int n, int count, int x) {
        int[] temp = new int[x];
        int[] output = new int[n];
        for (int i = 0; i < x; i++) {
            temp[i] = 0;
        }

        for (int i = 0; i < n; i++) {
            temp[(arr[i] / count) % 10]++;
        }

        for (int i = 0; i < x - 1; i++)
            temp[i + 1] += temp[i];

        for (int i = n - 1; i >= 0; i--) {
            output[temp[(arr[i] / count) % 10] - 1] = arr[i];
            temp[(arr[i] / count) % 10]--;
        }

        for (int i = 0; i < n; i++)
            arr[i] = output[i];
    }

    /**
     * Counting sort using array list
     *
     * Time complexity: O(n+k)
     * k - range of elements
     * n - number of numbers
     *
     * @param arr - array list
     * @param n - number of elements
     * @param count - divider
     * @param x - range
     */
    void sort(ArrayList<Date> arr, int n, int count, int x) {
        int[] temp = new int[x];
        Date[] output = new Date[n];
        for (int i = 0; i < x; i++) {
            temp[i] = 0;
        }

        for (int i = 0; i < n; i++) {
            temp[(int) (arr.get(i).numberOfDays / count) % 10]++;
        }

        for (int i = 0; i < x - 1; i++)
            temp[i + 1] += temp[i];

        for (int i = n - 1; i >= 0; i--) {
            output[temp[(int) (arr.get(i).numberOfDays / count) % 10] - 1] = arr.get(i);
            temp[(int) (arr.get(i).numberOfDays / count) % 10]--;
        }

        for (int i = 0; i < n; i++)
            arr.set(i, output[i]);
    }
}

/**
 * Class of insertion sort
 *
 * @author Danila Shulepin
 */
class InsertionSort {
    /**
     * Insertion sort for array
     *
     * Time complexity: Worst -> O(n^2); Best -> O(n)
     * n - number of elements
     *
     * @param arr - array
     */
    void sort(double arr[]) {
        int n = arr.length;
        for (int i = 1; i < n; ++i) {
            double key = arr[i];
            int j = i - 1;

            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j = j - 1;
            }
            arr[j + 1] = key;
        }
    }

    /**
     * Insertion sort for array list
     *
     * Time complexity: Worst -> O(n^2); Best -> O(n)
     * n - number of elements
     *
     * @param arr - array list
     */
    void sort(ArrayList<Double> arr) {
        int n = arr.size();
        for (int i = 1; i < n; ++i) {
            double key = arr.get(i);
            int j = i - 1;

            while (j >= 0 && arr.get(j) > key) {
                arr.set(j + 1, arr.get(j));
                j = j - 1;
            }
            arr.set(j + 1, key);
        }
    }
}