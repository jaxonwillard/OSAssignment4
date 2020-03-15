import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

public class PI {
    TaskQueue taskQueue = new TaskQueue();
    ResultTable resultTable = new ResultTable();
    int processors = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) {
        try {
            PI piCalculator = new PI();
            ArrayList<Thread> threads = new ArrayList<>();
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < piCalculator.processors; i++) {
                threads.add(new Thread(new WorkerThread(piCalculator.taskQueue, piCalculator.resultTable)));
                threads.get(i).start();
                threads.get(i).join();
            }
            long totalTime = System.currentTimeMillis() - startTime;

            System.out.println(totalTime);
//            for (int i=1; i<10; i++){
//                System.out.println(piCalculator.resultTable.results.get(i));
//            }


        }catch (Exception e){
            System.out.println(e);
        }
    }
}

class TaskQueue{
    LinkedList<Integer> linkedList;
    TaskQueue(){
        this.linkedList = getShuffledLL();
    }
    LinkedList<Integer> getShuffledLL(){
        LinkedList<Integer> linkedList = new LinkedList<>();
        for (int i=1; i<400; i++){
            linkedList.push(i);
        }
        Collections.shuffle(linkedList);
        return linkedList;
    }

    synchronized int getTask(){
        return this.linkedList.pop();
    }


}

class ResultTable{
    HashMap<Integer, String> results = new HashMap<>();
    synchronized void putKeyVal(int key, String value){
        this.results.put(key, value);
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
//                Integer key = this.taskQueue.linkedList.pop();
//                int key = getTask();
                int key = this.taskQueue.getTask();
                Integer value = this.bpp.getDecimal(key);
                String valStr = String.valueOf(value).substring(0,1);
                System.out.print(".");
                this.resultTable.putKeyVal(key, valStr);
//                putIntoTable(key, valStr);
//                this.resultTable.results.put(key, valStr);
        }



    }
    synchronized int getTask(){
        return this.taskQueue.linkedList.pop();
    }
    synchronized void putIntoTable(int key, String value){
        this.resultTable.results.put(key, value);
    }
}