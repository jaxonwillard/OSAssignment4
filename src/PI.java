
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

public class PI {
    volatile TaskQueue taskQueue;
    volatile ResultTable resultTable = new ResultTable();
    int processors = Runtime.getRuntime().availableProcessors();
    PI(int size){
        this.taskQueue = new TaskQueue(size);
    }

    public static void main(String[] args) {
            try {
                PI piCalculator = new PI(1000);
                ArrayList<Thread> threads = new ArrayList<>();
                long startTime = System.currentTimeMillis();
                for (int j = 0;  j< piCalculator.processors; j++) {
                    threads.add(new Thread(new WorkerThread(piCalculator.taskQueue, piCalculator.resultTable)));
                    threads.get(j).start();
                }
                for (int j = 0; j<piCalculator.processors; j++){
                    threads.get(j).join();
                }long totalTime = System.currentTimeMillis() - startTime;
                System.out.println();
                for (int i=1; i<1000; i++){
                    System.out.print(piCalculator.resultTable.getVal(i));
                }
                System.out.printf("\nPi Computation took %d ms", totalTime);
            } catch (Exception e) {
                System.out.println(e);
            }

    }
}

class TaskQueue{
    volatile LinkedList<Integer> linkedList;
    int size;
    int index = 0;
    TaskQueue(int size){
        this.size = size;
        this.linkedList = getShuffledLL();
    }
    LinkedList<Integer> getShuffledLL(){
        LinkedList<Integer> linkedList = new LinkedList<>();
        for (int i=1; i<size; i++){
            linkedList.push(i);
        }
        Collections.shuffle(linkedList);
        return linkedList;
    }

    synchronized int getTask()  {
        index++;
        if (index % 10 ==0){
            System.out.flush();
            System.out.print(".");
        }
        return this.linkedList.pop();
    }
}

class ResultTable{
    private volatile HashMap<Integer, String> results = new HashMap<>();
    synchronized void putKeyVal(int key, String value)  {
        if (this.results.containsKey(key)){
            System.out.println("i already have a key!");}
        this.results.put(key, value);
    }
    public String getVal(Integer key){
        return this.results.get(key);
    }
}

class WorkerThread implements Runnable{
    private volatile TaskQueue taskQueue;
    private volatile ResultTable resultTable;
    Bpp bpp = new Bpp();

    WorkerThread(TaskQueue tq, ResultTable rt){
        this.taskQueue = tq;
        this.resultTable = rt;
    }

    @Override
    public void run(){
            while(!this.taskQueue.linkedList.isEmpty()) {
                int key = this.taskQueue.getTask();
                Integer value = this.bpp.getDecimal(key);
                String valStr = String.valueOf(value).substring(0,1);
                this.resultTable.putKeyVal(key, valStr);
            }
    }

}