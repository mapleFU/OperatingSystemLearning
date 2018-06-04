# Tongji Memory Lab

## QuickStart



## 模块和架构

### Memory

#### Code

表示抽象的代码，可以被直接执行

#### Frame

物理的Frame, 存有实际的code。

#### PageTable

页表，用于将虚拟内存VPN映射到实体的物理PFN。如果目标不在物理内存中会抛出异常。

#### PageTableEntity

页表中存储的实体，用于映射到真正的物理空间。

#### MMUTranslator

MMU 转换器，能够将虚拟地址转换成物理空间的地址。调用`PageTable` 和`TLB`来实现目标。

这个模块的Translator能够处理异常，并且加载对应的PTE的物理页。




