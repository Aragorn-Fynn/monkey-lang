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
9. Hash
10. 内置函数
    1. len: 求数组和字符串长度
    2. first: 求数组第一个元素
    3. push: 向数组结尾添加一个元素
    4. print: 向标准输出打印数据
    5. time: 返回当前时间戳
11. 宏


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
>>> let fib = fn(x) {
        if (x==0) {
            return 0;
        } else {
            if (x==1) {
                return 1;
            } else {
                return fib(x-1) +fib(x-2);
            }
        }
    }
>>> fib(1)
1
>>> fib(2)
1
>>> fib(10)
55
>>> fib(20)
6765
>>> 
```
3. 高阶函数
```shell
>>> let twice = fn(func, x) {
        return func(func(x));
    }
>>> let addOne = fn(x) {
        return x+1;
    }
>>> twice(addOne, 1)
3
>>> twice(addOne, 2)
4
>>>
```
4. 字符串
```shell
>>> let hello = "hello there, fellow monkey users and fans"
>>> hello
hello there, fellow monkey users and fans
>>> "hello world"
hello world
>>> let sayHello = fn() {
        "Hello!";
    }
>>> sayHello()
Hello!
>>> let greeter = fn(greeting) {
        fn(name) {
            greeting + " " + name +"!";
        }
    }
>>> let hello = greeter("hello")
>>> hello("feng")
hello feng!
>>>
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
6. Hash
```shell
>>> let myHash = {true:"yes, boolean", 99:"correct, an integer"};
>>> myHash[true]
yes, boolean
>>> myHash[99]
correct, an integer
>>> myHash[5>1]
yes, boolean
>>> myHash[100-1]
correct, an integer
>>> let people = [{"name":"alice", "age":24}, {"name": "anna", "age": 28}];
>>> people[0]["name"]
alice
>>> people[0]["age"]
24
>>> people[1]["age"]
28
>>> people[1]["name"]
anna
>>> 
>>> let getName = fn(person) {return person["name"];};
>>> getName(people[0])
alice
>>> getName(people[1])
anna
>>>
>>> let newPerson = {};
>>> let newPerson = put(newPerson, "name", "bob")
>>> let newPerson = put(newPerson, "age", 25)
>>> let people = push(people, newPerson)
>>> len(people)
3
>>> people[2]["name"]
bob
>>> people[2]["age"]
25
>>>
```
7. 内置函数
```shell
>>> len("1234")
4
>>> let four = "2"
>>> len(four)
1
>>>
>>> print("hello world");
hello world
>>> time()
2023-04-03 21:42:42
>>> 
```
8. 宏
```shell
>>> let reverse = macro(a, b) {
        quote(unquote(b)-unquote(a));
    };
>>> reverse(2+2, 10-5)
1
>>> let evalSecond = macro(a, b){
        quote(unquote(b));
    };
>>> evalSecond(print("not printed"), print("printed"))
printed
>>>
>>> let unless = macro(condition, consequence, alternative) {
        quote(if (!(unquote(condition))) {
            unquote(consequence);
        } else {
            unquote(alternative);
        });
    };
>>>  unless(10<5, print("not greater"), print("greater"))
not greater
>>> unless(10>5, print("not greater"), print("greater"))
greater
>>>
```
9. 循环
```shell
>>> let loop = macro(cond, step, statements) {
        quote(if (unquote(cond)) {
                unquote(statements);
                unquote(step);
            });
    };
>>> let i=0;
>>> loop(i<2, i+1, print(i));
0
1
>>>
```

### 实现
1. 词法分析器 -> 递归下降
2. 语法分析器 -> Pratt Parser
3. 解释器 -> 树遍历解释器
4. 命令行 -> Jline3