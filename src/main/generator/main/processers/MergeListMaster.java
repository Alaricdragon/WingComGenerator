package main.processers;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public abstract class MergeListMaster<A> implements Runnable{
    private final ArrayList<ArrayList<A>> lists;
    private boolean complete = false;
    private ThreadGroup pGroup;
    private static int used = 0;

    private int completed = 0;
    private int totalLists;

    private ReentrantLock lock = new ReentrantLock(false);
    private ReentrantLock completeLock = new ReentrantLock(false);
    public MergeListMaster(ArrayList<ArrayList<A>> lists ){
        this.lists = lists;
        pGroup = new ThreadGroup("organizer_MergeList_"+used);
        used++;
        totalLists = lists.size();
    }
    public abstract Runnable getRunnable(ArrayList<A> listA, ArrayList<A> listB, MergeListMaster<A> This);
    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(2);//fast sleep because this stage needs it I think
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (pGroup.activeCount() == 0 && lists.size() == 1){
                completeLock.lock();
                complete = true;
                completeLock.unlock();
                return;
            }
            //System.out.println("cound and lists: "+pGroup.activeCount()+", "+lists.size());
            lock.lock();
            for (int a = lists.size()-1; a > 0; a-=2){
                new Thread(pGroup,getRunnable(lists.get(a),lists.get(a-1),this)).start();
                lists.removeLast();
                lists.removeLast();
            }
            lock.unlock();
        }
    }
    public boolean isComplete(){
        completeLock.lock();
        boolean out = complete;
        completeLock.unlock();
        return out;
    }
    public String getStatus(){
        lock.lock();
        String out = completed+" / "+totalLists;
        lock.unlock();
        return out;
    }
    public void addList(ArrayList<A> list){
        lock.lock();
        lists.add(list);
        completed+=2;
        totalLists++;
        lock.unlock();
    }
    public synchronized ArrayList<A> getFinalLists(){
        return lists.get(0);
    }
}
