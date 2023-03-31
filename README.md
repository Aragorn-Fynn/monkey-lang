# monkey-lang

### 概述
参考[用Go语言自制解释器](https://book.douban.com/subject/35909085/)实现的一门带命令行的语言， 具体特性如下：
1. 类C语法
2. 变量绑定
3. 整型和布尔型
4. 算术表达式
5. 函数和高阶函数
6. 闭包

### 示例
1. 变量绑定
```shell
>>> let a = 1;
1
>>> a;
1
>>> let b = 2;
2
>>> b
2
>>> let a = 1 > 2;
false
>>> a
false
>>> 
```
2. 函数
```shell
>>> let add = fn(a, b) {return a+b};
fn(a,b) {
return (a + b);
}
>>> add(1, 2);
3
>>> 
```
3. 高阶函数
```shell
>>> let twice = fn(func, x) {return func(func(x))};
fn(func,x) {
return func(func(x));
}
>>> let addOne = fn(x) {return x+2}
fn(x) {
return (x + 2);
}
>>> twice(addOne, 2);
6
>>> 

```

### 实现
1. 词法分析器 -> 递归下降
2. 语法分析器 -> Pratt Parser
3. 解释器 -> 树遍历解释器
4. 命令行 -> Jline3