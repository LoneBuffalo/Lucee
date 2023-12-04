![Lucee](https://raw.githubusercontent.com/lucee/Lucee/6.0/images/lucee-white.png#gh-dark-mode-only)
![Lucee](https://raw.githubusercontent.com/lucee/Lucee/6.0/images/lucee-black.png#gh-light-mode-only)

Lucee 6 comes with a lot of new features and functionality that improve your interaction with Lucee both directly, through new features, and indirectly, by enhancing the existing ones. The focus, as always, is not simply to add functionality that you can achieve yourself with CFML code, but to enhance the language itself.

Stay tuned as we explore the exciting world of Lucee 6. Get ready to elevate your CFML game with the latest and greatest.


# Java

Lucee now offers an array of enhanced functionalities for a more seamless integration between Lucee and Java applications and code.

## Java Code inside CFML!

In Lucee 6 you have the flexibility to incorporate Java code directly within your CFML code opening up new possibilities for seamless integration.

#### Within a User Defined Function (UDF):
```java
int function echoInt(int i) type="java" {
    if(i==1) throw new Exception("Oopsie-daisy!!!");
    return i*2;
}
```

Simply add the attribute `[type="java"]` and you can effortlessly embed Java code within your CFML template.

#### Or in a Closure:
```java
to_string = function (String str1, String str2) type="java" {
    return new java.lang.StringBuilder(str1).append(str2).toString();
}
```

Please note that this feature isn't supported for Lambda Functions as the Lambda Syntax conflicts with the attribute "type=java".


#### Of course, these functions can also be part of a component:
```java
component { 
   int function echoInt(int i) type="java" {
       if(i==1)throw new Exception("Oopsie-daisy!!!");
       return i*2;
   }
   to_string = function (String str1, String str2) type="java" {
        return new java.lang.StringBuilder(str1).append(str2).toString();
   }
 }
```
With Lucee 6, you can seamlessly blend Java and CFML to unlock new dimensions of versatility.

### Java Functions Implement Java Functional Interfaces

In Lucee 6, if the interface of a function matches one of the Java Functional Interfaces found in the [Java Standard Library](https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html), Lucee automatically implements that interface. Consequently, the function can be seamlessly passed to Java as a Java Function.

For instance, let's revisit our previous example, which implements the ["IntUnaryOperator"](https://docs.oracle.com/javase/8/docs/api/java/util/function/IntUnaryOperator.html) interface. You can verify this implementation in the function's metadata or its dump.

But why does Lucee implement these interfaces? Lucee goes the extra mile by matching the function's signature, including its arguments and return type, to any of the existing Java Functional Interfaces. And guess what? It all happens automatically!

This feature empowers you to use these functions seamlessly in your Java code, bridging the gap between CFML and Java effortlessly.

## CFML Code in Java! 
Lucee 6 takes CFML to a whole new level by allowing you to seamlessly integrate CFML components and functions with specific Java classes that implement certain interfaces or Java Functions.

### Components to Classes

When you pass a CFML component to a Java method, and that method expects a specific class, Lucee performs automatic conversion or wrapping of the component into that class. You don't need to follow any specific steps; Lucee handles it all behind the scenes.

All you have to do is create a component that implements all the methods of a Java interface as functions. For instance, consider a component that implements the [CharSequence](https://docs.oracle.com/javase/8/docs/api/java/lang/CharSequence.html) interface:
```java
// component that implements all methods from interface CharSequence
component {


   function init(String str) {
       variables.str=reverse(arguments.str);
   }
  
   function length() {
       SystemOutput("MyString.length:"&str.length(),1,1);
       return str.length();
   }


   function  charAt( index) {
       SystemOutput("MyString.charAt("&index&"):"&str.charAt( index),1,1);
       return str.charAt( index);
   }


   function subSequence(start, end) {
       SystemOutput("MyString.subSequence("&start&", "&end&"):"&str.subSequence(start, end),1,1);
       return str.subSequence(start, end);
   }


   function toString() {
       SystemOutput("MyString.toString():"&str.toString(),1,1);
       return str.toString();
   }
}
```

#### With this component in place, you can effortlessly use it as a CharSequence like this:
```java
// this class has a method that takes as an argument a CharSequence, that way we can force Lucee to convert/wrap our component to that interface.
HashUtil =  createObject("java","lucee.commons.digest.HashUtil");


// this component implements all necessary functions for the CharSequence interface
cfc=new MyString("Susi Sorglos");


// calling the method HashUtil.create64BitHashAsString(CharSequence cs) with our component as argument
hash=HashUtil.create64BitHashAsString(cfc);
dump(hash);
```
#### You can also explicitly define the interface you're implementing, as shown here:
```java
component implementsJava="java.util.List" {
   function onMissingMethod(name,args) {
       if(name=="size") return 10;
       throw "method #name# is not supported!";
   }
}
```
In this case, we explicitly implement the ["java.util.List"](https://docs.oracle.com/javase/8/docs/api/java/util/List.html) interface, allowing Lucee to handle it as an array. With this approach, you don't need to define all the methods, as Lucee doesn't enforce a strict function match due to the "implementsJava" attribute.

## UDF to Java Lambda Function

When you pass a CFML Function (UDF/Closure/Lambda) to Java like this
and the Java interface expects a Java Function (Lambda), Lucee automatically converts or wraps that function into a Java function of the corresponding type.

This seamless integration allows you to harness the full power of your CFML functions in your Java code.
In the following example, we demonstrate how Lucee implicitly implements the ["IntUnaryOperator"](https://docs.oracle.com/javase/8/docs/api/java/util/function/IntUnaryOperator.html) interface. You can then pass it to Java and use it as such, making the integration between CFML and Java even smoother.
```java
int function echoInt(int i) type="java" {
       if(i==1)throw new Exception("Oopsie-daisy!!!");
       return i*2;
}
```

# Components

In Lucee 6, we've expanded your options by introducing sub-components, a powerful addition to your CFML toolkit. Unlike traditional components defined in separate .cfc files, sub-components reside within the same template as the main component, offering you increased flexibility and improved code organization.

## Why Use Sub Components?

Sub-components bring several benefits to the table:

- **Modularization**: Sub-components enable you to break down complex components into smaller, more manageable pieces. This modular approach simplifies development, testing, and maintenance.
- **Reusability**: Components that are used across multiple templates can be encapsulated as sub-components. This reusability reduces code duplication and promotes a DRY (Don't Repeat Yourself) coding practice.
- **Scoped Variables**: Sub-components can access variables within the parent component's scope, simplifying data sharing between related components.
- **Improved Collaboration**: In team projects, sub-components can make it easier for developers to work on specific parts of a component without affecting the entire structure. This promotes collaboration and concurrent development.
- **Code Organization**: Sub-components help maintain a clean and organized codebase by keeping related functionality together within a single template.
- **Testing Isolation**: When unit testing components, sub-components can be tested in isolation, allowing for more targeted and granular testing.
- **Easier Debugging**: Smaller, focused sub-components can make debugging more straightforward since you can pinpoint issues within specific sections of your code.
- **Version Control**: Sub-components can be managed and version-controlled independently, providing more control over changes and updates.
- **Performance Optimization**: In certain cases, splitting functionality into sub-components can lead to more efficient code execution, especially when components are conditionally loaded based on specific use cases.
- **Simplified Component Management**: With sub-components, you can manage related code within a single template, making it easier to locate and edit all associated functionality.

These additional benefits highlight the versatility and advantages of using sub-components in your CFML applications.

#### Example of Sub Component Definition:
```java
component {
   function mainTest() {
       return "main";
   }
}
component name="Sub" {  
   function subTest() {
       return "sub";
   }
}
```

#### In the example above, the sub component is given a name attribute for easy referencing. You can invoke sub components as follows:
```java
// Usage example
cfc = new MyCFC();
echo("main -> " & cfc.mainTest());
echo("<br>");

cfc = new MyCFC$Sub();
echo("sub -> " & cfc.subTest());
echo("<br>");
```
Sub components expand your horizons and provide new avenues for structuring your CFML code effectively.

## Inline (Anonymous) Components
In Lucee 6, we not only introduce sub components but also unleash the power of inline components. These components are defined directly within your code, eliminating the need for extra files or templates.

### Why Use Inline Components?
Inline components bring several advantages to your CFML code:

- **Simplicity**: Inline components simplify your code by eliminating the need for separate component files. This can make your codebase more straightforward and easier to manage.
- **Local Scoping**: Inline components are ideal for situations where you need a component with a limited scope. They exist only within the context where they are defined, reducing global namespace clutter.
- **Custom Configuration**: You can configure inline components with specific properties or behaviors tailored to a particular context, providing flexibility in your code.
- **One-time Use**: Use inline components when you require a component for a single, specific task or operation, avoiding the overhead of creating separate files.
- **Encapsulation**: Inline components encapsulate related functionality, making it more self-contained and easier to understand.
- **Code Isolation**: With inline components, you can isolate specific logic or features, making it easier to maintain, update, or remove them as needed.
- **Dynamic Generation**: Inline components allow for dynamic component generation based on runtime conditions, providing flexibility in component creation.
- **Reduced Maintenance**: Since inline components are closely tied to the code where they are used, updates and changes are localized, reducing the potential impact on other parts of the application.
- **Event Handling**: Inline components are excellent for defining listeners and event handlers, enhancing the modularity of your application's event-driven architecture.
- **Improved Readability**: By embedding components directly within your code, you make your code more self-documenting, as the component's purpose and usage are evident within the context.

These additional benefits highlight how inline components can streamline your CFML code, making it more concise, efficient, and maintainable while enhancing code organization and flexibility.


#### Example of Inline Component:
```java
   inline=new component {  
       function subTest() {
           return "inline<br>";
       } 
   };  
   dump("inline->"&inline.subTest());
   dump(inline);
```

In the example above, we create an inline component that defines a subTest function. This inline component is perfect for situations where you need a component temporarily, enhancing code efficiency and maintainability.

Inline components offer a powerful way to enhance the structure and readability of your CFML code while efficiently managing local components.


# Query super power 

The humble query tag was among the pioneers in CFML, and it remains a cornerstone of the language's functionality. In Lucee 6, we've taken a deep dive into enhancing this critical component, unlocking its full potential to empower your data manipulation and retrieval tasks. Let's explore the remarkable advancements that make querying in CFML an even more formidable superpower.

## Query listeners

In Lucee 6, we introduce the concept of query listeners, a powerful tool that enables you to monitor and manipulate every query execution within your CFML application. By defining query listeners in your Application.cfc, you gain fine-grained control over the query lifecycle, from before execution to after completion.

### How to Define Query Listeners:
You can define query listeners using the following syntax in your Application.cfc:
```java
  this.query.listener= {
     before: function (caller,args) {
        dump(label:"before",var:arguments);
     },
     after: function (caller,args,result,meta) {
        dump(label:"after",var:arguments);
     }
  }
```

With this configuration, you have access to two listener functions:

- **Before Function**: This function is executed before the query is executed. It allows you to inspect and potentially modify query parameters or settings.
- **After Function**: After the query execution is complete, the "after" function provides access to the query result and metadata. You can use this function to perform post-processing tasks or log query results.

### Using a Query Listener Component:

Alternatively, you can encapsulate query listener functionality within a dedicated component. Here's how you can do it:
```java
  this.query.listener = new QueryListener();
```

By using a dedicated component, you can organize and encapsulate your query listener logic, making it easier to manage and maintain, especially in larger applications like this:
```java
    component {
     function before(caller,args) {
        args.sql="SELECT TABLE_NAME as abc FROM INFORMATION_SCHEMA.TABLES"; args.maxrows=2;
        return arguments;
     }
     function after(caller,args,result,meta) {
        var row=queryAddRow(result);
        result.setCell("abc","123",row);
        return arguments;
     }
     function error(args,caller,meta,exception) {
        //handle exception
     }
  }
```

### Why Use Query Listeners?
Query listeners offer several benefits:

- **Real-time Monitoring**: You can gain insights into query execution as it happens, allowing you to detect and respond to issues promptly.
- **Dynamic Query Modification**: With the "before" function, you can dynamically modify queries based on specific conditions or requirements.
- **Logging and Debugging**: The "after" function is invaluable for logging query results or conducting further analysis.
- **Customization**: You can tailor query listeners to suit your application's unique needs, enhancing its flexibility and adaptability.

Query listeners provide a valuable toolset for optimizing query execution, troubleshooting issues, and customizing query behavior to meet your application's demands.

## Query Async: Enhancing Query Responsiveness

In Lucee 6, we introduce the powerful "async" attribute for queries, allowing you to boost the responsiveness of your applications. With "async," you can instruct Lucee not to wait for the query's execution to complete. This asynchronous approach is particularly useful when you want to monitor exceptions or retrieve the query result without causing delays in your application's flow.

### How to Use the "async" Attribute:

To leverage the "async" attribute, simply add it to your query tag, like this:
```java
  query datasource="mysql" async=true listener={
        error:function (args,caller,meta,exception){
           systemOutput(exception,true,true);
     }
     } {
     ```update user set lastAccess=now()```
     }
```
In this example, we've set the "async" attribute to "true" for the query, indicating that Lucee should execute the query asynchronously.
Using a Local Listener:
If you want to capture exceptions or retrieve the query result when using "async," you can define a local listener within the query tag. The local listener is responsible for handling any exceptions that may occur during the asynchronous query execution.
Why Use "async" Queries?
Async queries offer several advantages:

- **Enhanced Responsiveness**: Async queries prevent your application from waiting for potentially time-consuming database operations to complete. This responsiveness can significantly improve user experience.
- **Exception Handling**: By utilizing a local listener, you can promptly detect and handle exceptions that occur during the query execution.
- **Non-blocking**: Asynchronous queries do not block the execution flow of your application, allowing other tasks to proceed simultaneously.
- **Parallel Processing**: You can leverage async queries to perform multiple database operations in parallel, optimizing performance.

Async queries provide a valuable tool for enhancing the responsiveness of your applications while efficiently handling database operations and exceptions. By incorporating "async" into your query strategy, you can strike a balance between speed and robust error handling.

## Query Lazy: Efficient Handling of Large Result Sets

In Lucee 6, we introduce the powerful "queryLazy" function, a game-changer when dealing with queries that return enormous result sets. Traditional queries might consume excessive memory when handling millions of records, but with "queryLazy," you can efficiently read queries in manageable blocks, ensuring that memory constraints are never a concern.

### How "queryLazy" Works:

The "queryLazy" function allows you to process queries in blocks, making it ideal for scenarios where you expect a large result set. Consider this example:
```java
	queryLazy(
    	sql:"select * from user",
    	listener: function (rows){
    		dump(rows);
    	},
     	options:{datasource:datasource,blockfactor:1000}
    );

```

In this example, we retrieve records from the "user" table in blocks of a maximum of 1000 records at a time. The "listener" function is invoked for each block of records, enabling you to process them efficiently. By doing so, you prevent memory exhaustion, even when dealing with massive data sets.

### Why Use "queryLazy" Queries?

"queryLazy" offers several advantages:
- **Memory Efficiency**: By reading queries in smaller blocks, "queryLazy" ensures that memory usage remains controlled, even for queries with millions of records.
- **Improved Performance**: Processing smaller chunks of data at a time can lead to improved query performance, as the database doesn't need to fetch the entire result set at once.
- **Real-time Processing**: With the "listener" function, you can process and analyze data as it's retrieved, enabling real-time insights and actions.
- **Scalability**: "queryLazy" is scalable and well-suited for applications that need to handle substantial data volumes without sacrificing performance or memory.
- **Reduced Latency**: Smaller data blocks reduce the time it takes to retrieve and process results, minimizing latency in your application.
- **Robustness**: Handling large data sets with "queryLazy" enhances the robustness and reliability of your applications, preventing potential memory-related issues.

"queryLazy" is your go-to solution for efficiently handling large result sets, ensuring optimal memory management, and maintaining the responsiveness of your CFML applications.

## Query Indexes: Enhancing Query Data Retrieval

In Lucee 6, we introduce the capability to set an index for a query result, providing you with a powerful tool for efficient data retrieval. By defining an index for your query, you can access specific rows, row data, or individual cells with ease, streamlining your data manipulation tasks.

### How to Set an Index for a Query:

You can set an index for a query result using the "indexName" attribute in your <cfquery> tag. Consider this example:
```cfml
<cfquery name="qry" datasource="mysql" indexName="id">
  select 1 as id, 'Susi' as name
  union all
  select 2 as id, 'Peter' as name
</cfquery>
```














