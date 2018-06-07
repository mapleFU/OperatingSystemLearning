# Tongji Memory Lab

## QuickStart

运行对应的.jar文件，可以看到如下的界面：

![ui summary](./doc/readme_images/ui summary.png)

我们可以看到下列的按钮：

1. execute : 执行一条指令
2. 5 combo : 执行五条指令
3. execute all : 执行全部指令

同时有以下的选择栏，表示对比的页面置换算法

1. LRU\(默认最初显示\)
2. FIFO
3. RAND

显示的有3项

1. 饼状图 + 小数数字，表示对应的缺页率
2. 指令序号 — 物理序号对应的表，表示对应的指令执行
3. Frame1 ～ Frame4的显示，表示算法对应的Frame

下面是模拟运行：

![executed](./doc/readme_images/executed.png)

可以看到，缺页率、指令序号等对应显示在图中。如果需要对比多种算法，可以按对应算法的按钮，得到这个算法对应的数据。 以下是rand 算法和 FIFO算法对应的数据：

![rand 算法](./doc/readme_images/rand 算法.png)

![fifo 算法](./doc/readme_images/fifo 算法.png)

当你执行完全部指令后，可以自行选择对应重启，燃尽诶面归零，重新启动程序。

## 模块和架构

详细情况可以看文档中对应的[Javadoc](./doc/main.html)

### 内存模拟

#### HardDiskMemory

次级存储器，这里模拟磁盘进行存储，能够进行载入物理Frame、导出物理Frame的功能.

#### PhysicsMemory

用来表示物理内存的类。这里能够存储对应的物理页面，并且能够将页面逐出，放入HardDiskMemory, 也能导入物理Frame。

#### VirtualMemery

虚拟内存，通过MMUTranslator在物理内存寻址。用户通过worker操作虚拟内存来查找对应的指令。

#### Code

表示抽象的代码，可以被直接执行.

#### Frame

物理的Frame, 存有实际的code的信息，能够存储在。

#### PageTable

页表，用于将虚拟内存VPN映射到实体的物理PFN。如果目标不在物理内存中会抛出异常。

#### PageTableEntity

页表项
页表中存储的实体，用于映射到真正的物理空间。
同时根据保护位等信息表示对应的物理信息是否在物理内存中，或者这块内存是否被初始化／使用

#### MMUTranslator

MMU 转换器，能够将虚拟地址转换成物理空间的地址。调用`PageTable` 和`TLB`来实现目标。

这个模块的Translator能够处理异常，并且加载对应的PTE的物理页。

#### TLB

用于优化物理内存寻址的表，这里尚未实现。

### 页面置换算法

#### EvictBase

在这里，缓存置换算法都是EvictBase的子类，EvictBase的信息如下：

```java
/**
 * 基本的替换算法的基类
 * 参考：http://flychao88.iteye.com/blog/1977653
 */
public abstract class EvictBase {
    final int lruSize;

    /**
     * 获得自用的缓存置换算法的算法名称
     * @return 算法名称
     */
    public String getEvictAlgoName() {
        return evictAlgoName;
    }

    protected String evictAlgoName;
    public EvictBase(int lruSize) {
        this.lruSize = lruSize;
    }

    /**
     * 表示对应位置的物理内存被使用
     * @param frameID
     */
    public abstract void codeUse(int frameID);

    /**
     * 返回应该evict的物理内存的ID
     * @return
     */
    public abstract int evictID();

    @Override
    public String toString() {
        return "EvictAlgorithm " + evictAlgoName;
    }
}
```

这里我实现了对应的FIFO LRU RAND三种置换算法。

### 随机数生成

这里实现了两种随机生成指令的算法，效果比较好的采取如下方法：用对应的状态机，生成一定序列的指令：

```java
public class RCodeGenerator implements Iterator<Integer> {
    private Stack<Integer> stack = new Stack<>();
    /**
     * 最多生成的指令
     */
    private final int maxExec;
    private int[] addresses;
    void generateAddress() {
        Random random = new Random();
        // records the last address to be stored
        int last = random.nextInt(maxExec);
        int flag = 0;
        for (int i = 0; i < maxExec; ++i) {
            addresses[i] = last;
            if (flag % 2 == 0) {
                // flag == 0 || flag == 2
                last = (last + 1) % maxExec;
            }
            else if (flag == 1) {
                if (last == 0) {
                    last = 0;
                }
                else {
                    last = random.nextInt(last - 1);
                }
            }
            else {
                // flag == 3
                last = last + 1 + random.nextInt(maxExec - (last + 1));
            }
            flag = (flag + 1) % 4;
        }
    }

    private int curIndex;

    public RCodeGenerator(int maxExecute) {
        maxExec = maxExecute;
        addresses = new int[maxExecute];
        generateAddress();
        curIndex = 0;
    }

    @Override
    public boolean hasNext() {
        return curIndex < maxExec;
    }

    @Override
    public Integer next() {
        return addresses[curIndex++];
    }
}
```

flag 表示状态。这里实现了迭代器的接口，让后面能够像迭代器一样使用这个生成器。 

#### LRU 算法

![img](http://my.csdn.net/uploads/201205/24/1337859321_3597.png)

LRU\(least-recently-use\) 算法利用了程序的局部性，根据数据的历史访问记录来进行淘汰数据，其核心思想是“如果数据最近被访问过，那么将来被访问的几率也更高”。这个算法保证：

**被Evict的元素是其中中最少被使用的**

一般这种算法直接实现时间复杂度相对较高，所以可以用LRU-K等算法来做优化。但是因为这个项目的物理内存空间只有4.所以我才用了链表来实现了真实的LRU算法。这个算法代码对应如下：

```java
public class LRUEvict extends EvictBase {
    private LinkedList<Integer> intList;
    
    public LRUEvict(int size) {
        // fill in lruSize
        super(size);
        intList = new LinkedList<>();
        evictAlgoName = "LRU";
    }

    @Override
    public void codeUse(int frameID) {
        Iterator<Integer> iter = intList.iterator();
        boolean existed = false;
        while (iter.hasNext()) {
            if (iter.next() == frameID) {
                iter.remove();
                existed = true;
                intList.addLast(frameID);
                break;
            }
        }
        if (!existed) {
            if (intList.size() == lruSize) {
                throw new RuntimeException("codeuse size out of range.");
            } else {
                intList.addLast(frameID);
            }
        }
    }

    @Override
    public int evictID() {
        int toRemove = intList.removeFirst();
        return toRemove;
    }
}
```

我用了`java.util.LinkedList`这一链表容器来实现。当一块内存被访问时，对应的`codeUse`会被调用：

1. 程序查看链表中的元素是否达到内存元素的上限，没有达到上限直接插入链表末端：

   ```java
   if (!existed) {
   	if (intList.size() == lruSize) {
   		throw new RuntimeException("codeuse size out of range.");
       } else {
       	intList.addLast(frameID);
       }
   }
   ```

   

2. 如果对应序号在内存中，程序将这个序号从链表中放到链表的表尾

   ```java
   Iterator<Integer> iter = intList.iterator();
   boolean existed = false;
   while (iter.hasNext()) {
       if (iter.next() == frameID) {
           iter.remove();
           existed = true;
           intList.addLast(frameID);
           break;
       }
   }
   ```

那么，当需要evict元素时，链表表头的元素，就是内存块中被最少使用的元素，我们释放链表表头，并且返回对应序号即可

```java
@Override
public int evictID() {
    int toRemove = intList.removeFirst();
    return toRemove;
}
```

#### RAND 算法

Evict算法目的是讲对应的内存块Evict。Rand指的是随机Evict。这种算法部分时候其实效率也不低。

这里我采用Java的伪随机数来完成RAND算法

```java
import java.util.Random;

public class RandEvict extends EvictBase {

    private Random random;
    public RandEvict(int lruSize) {
        super(lruSize);
        evictAlgoName = "Rand";
        random = new Random();
    }

    // 什么都不做
    @Override
    public void codeUse(int frameID) { }

    @Override
    public int evictID() {
        // 返回一个对应大小的数
        return random.nextInt(lruSize);
    }
}

```

使用不会对这个代码产生什么影响，evict的时候只需要生成一个对应的随机数即可。

#### FIFO 算法

FIFO 即first-in-first-out, 最先evict的是最先进入内存的。这里我同样用`java.util.LinkedList`来模拟。这里用链表模拟一个逻辑上的队列。由于要查验元素是否在表内，所以不能单纯使用队列。

这里的evict思路和LRU是一样的，排出表头元素（最早加入链表），每次有新元素插入表尾。与LRU的区别在于，`codeUse`调用时，找到元素则什么都不做，LRU则会将这个元素插入表尾。

```java
Iterator<Integer> iter = fifoUsedQueue.iterator();
boolean existed = false;
while (iter.hasNext()) {
    if (iter.next() == frameID) {
        existed = true;
        break;
    }
}
```

整体的类：

```java
import java.util.LinkedList;

public class FIFOEvict extends EvictBase {
    /**
     * 是FIFO的使用队列
     */
    private LinkedList<Integer> fifoUsedQueue;

    public FIFOEvict(int lruSize) {
        super(lruSize);
        fifoUsedQueue = new LinkedList<>();
        evictAlgoName = "FIFO";
    }

    @Override
    public void codeUse(int frameID) {
        Iterator<Integer> iter = fifoUsedQueue.iterator();
        boolean existed = false;
        while (iter.hasNext()) {
            if (iter.next() == frameID) {
                existed = true;
                break;
            }
        }
        if (!existed) {
            if (fifoUsedQueue.size() == lruSize) {
                throw new RuntimeException("codeuse size out of range.");
            } else {
                fifoUsedQueue.addLast(frameID);
            }
        }
    }

    @Override
    public int evictID() {
        Integer remove = fifoUsedQueue.removeFirst();
        return remove;
    }
}

```

