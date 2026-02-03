package main.processers;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class MultiGetArray<A> {
    private final ReentrantLock[] locks;
    private final ArrayList<ArrayList<A>> lists = new ArrayList<>();
    int looking = 0;
    public MultiGetArray(ArrayList<A> list, int size){
        locks = new ReentrantLock[size];
        for (int a = 0; a < size; a++) locks[a] = new ReentrantLock(false);
        for (int a = 0; a < size; a++){
            ArrayList<A> copy = new ArrayList<>();
            copy.addAll(list);
            lists.add(copy);
        }
    }
    //int locked=0;
    private final ReentrantLock getAndReserveListID_Lock = new ReentrantLock(false);
    public int getAndReserveListID(){
        //todo: I don't understand thing.
        //      this can cause thread deadlocks if i use the synchronized keyword here. and I don't understand why...?
        //      I am very confused....
        //      maybe it has something to do with me locking here..? (not the getAndReserveListID_Lock, but the other lock) (it does not. only 1 item gets locked at a time though, kinda strange...?)
        getAndReserveListID_Lock.lock();
        int get = looking;
        looking++;
        if (looking >= locks.length) looking = 0;
        //locked++;
        //System.out.println("locked to: "+locked);
        locks[get].lock();
        //locked--;
        //System.out.println("unlocked to: "+locked);
        //locked++;
        //System.out.println("locked to: "+locked);
        getAndReserveListID_Lock.unlock();
        return get;
    }
    private final ReentrantLock getList_Lock = new ReentrantLock(false);
    public synchronized ArrayList<A> getList(int id){
        return lists.get(id);
    }
    private final ReentrantLock unlockList_Lock = new ReentrantLock(false);
    public synchronized void unlockList(int id){
        unlockList_Lock.lock();
        locks[id].unlock();
        //locked--;
        //System.out.println("unlocked to: "+locked);
        unlockList_Lock.unlock();
    }
}
