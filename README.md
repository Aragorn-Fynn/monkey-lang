# monkey-lang

### 概述
参考[用Go语言自制解释器](https://book.douban.com/subject/35909085/)实现的一门带命令行的语言， 具体特性如下：
1. 类C语法
2. 变量绑定
3. 整型和布尔型
4. 算术表达式
5. 函数和高阶函数
6. 闭包
7. 字符串
8. 数组
9. 内置函数
   1. len: 求数组和字符串长度
   2. first: 求数组第一个元素
   3. push: 向数组结尾添加一个元素
   4. print: 向标准输出打印数据
   5. time: 返回当前时间戳


### 示例
1. 变量绑定
```shell
>>> let a = 1;
>>> a
1
>>> let b = 2
>>> b
2
>>> let a = 1 > 2
>>> a
false
>>> 
```
2. 函数
```shell
>>> let fact = fn(x) {if (x==0) {return 0} else {if (x==1) {return 1} else{return fact(x-1) + fact(x-2)}}};
>>> fact(3);
2
>>> fact(4);
3
>>> fact(5);
5
>>> fact(6);
8
>>> fact(7);
13
>>> 
```
3. 高阶函数
```shell
>>> let twice = fn(func, x) {return func(func(x))};
>>> let addOne = fn(x) {return x+1}
>>> twice(addOne, 2);
4
```
4. 字符串
```shell
>>> let hello = "hello there, fellow monkey users and fans"
>>> hello
hello there, fellow monkey users and fans
>>> "hello world"
hello world
>>> let sayHello = fn() {"Hello!"}
>>> sayHello();
Hello!
>>> let greeter = fn(greeting) {fn(name) {greeting + " " + name +"!"}};
>>> let hello = greeter("hello")
>>> hello("feng")
hello feng!
```
5. 数组
```shell
>>> let myArr = [1+2, 2+3, fn(x){return x*x}]
>>> myArr[0]
3
>>> myArr[1]
5
>>> myArr[2](2)
4
>>> len(myArr)
3
>>> first(myArr)
3
>>> let another = push(myArr, 4)
>>> another
[3,5,fn(x) {
return (x * x);
},4]
>>>
```
5. 内置函数
```shell
>>> len("1234")
4
>>> let four = "2"
>>> len(four)
1
>>>
>>> print("hello world");
hello world
```

### 实现
1. 词法分析器 -> 递归下降
2. 语法分析器 -> Pratt Parser
3. 解释器 -> 树遍历解释器
4. 命令行 -> Jline3