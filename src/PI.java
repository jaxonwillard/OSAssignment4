import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class PI {
    volatile TaskQueue taskQueue;
    volatile ResultTable resultTable = new ResultTable();
    int processors = Runtime.getRuntime().availableProcessors();
    PI(int size){
        this.taskQueue = new TaskQueue(size);
    }

    public static void main(String[] args) {
        for (int i=0; i<5; i++) {
            try {
                PI piCalculator = new PI(i * 200);
                ArrayList<Thread> threads = new ArrayList<>();
                long startTime = System.currentTimeMillis();
                for (int j = 0;  j< piCalculator.processors; j++) {
                    threads.add(new Thread(new WorkerThread(piCalculator.taskQueue, piCalculator.resultTable)));
                    threads.get(j).start();
                    threads.get(j).join();
                }
                long totalTime = System.currentTimeMillis() - startTime;

                System.out.println("size: " + i*200 + " totalTime: " + totalTime);
//            for (int i=1; i<10; i++){
//                System.out.println(piCalculator.resultTable.results.get(i));
//            }


            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}

class TaskQueue{
    public Semaphore lock = new Semaphore(1);
    volatile LinkedList<Integer> linkedList;
    int size;
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

    int getTask() throws InterruptedException {
        this.lock.acquire();
        int task = this.linkedList.pop();
        this.lock.release();
        return task;
    }


}

class ResultTable{
    public Semaphore lock = new Semaphore(1);
    volatile HashMap<Integer, String> results = new HashMap<>();
    void putKeyVal(int key, String value) throws InterruptedException {
        this.lock.acquire();
        this.results.put(key, value);
        this.lock.release();
    }
}

class WorkerThread implements Runnable{
    private TaskQueue taskQueue;
    private ResultTable resultTable;
    Bpp bpp = new Bpp();

    WorkerThread(TaskQueue tq, ResultTable rt){
        this.taskQueue = tq;
        this.resultTable = rt;
    }

    @Override
    public void run(){
            while(!this.taskQueue.linkedList.isEmpty()) {
                int key = 0;
                try {
                    key = this.taskQueue.getTask();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Integer value = this.bpp.getDecimal(key);
                String valStr = String.valueOf(value).substring(0,1);
                try {
                    this.resultTable.putKeyVal(key, valStr);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
    }

}