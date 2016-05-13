[![](https://jitpack.io/v/Yumenokanata/AvocadoViews.svg)](https://jitpack.io/#Yumenokanata/AvocadoViews)

# AvocadoViews
一个小自定义控件的小集合，整合了以往项目中比较具有通用性的小自定义控件

1. DateSelectPicker：日期选择器控件  
2. DoubleRefreshLayout：具有下拉刷新和上拉加载功能的容器，提供异常处理  
3. 陆续扩展中...  

## 添加到Android studio
Step1: 在根build.gradle中添加仓库：
```groovy
allprojects {
	repositories {
        jcenter()
		maven { url "https://jitpack.io" }
	}
}
```

Step2: 在工程中添加依赖：
```groovy
dependencies {
    compile 'com.github.Yumenokanata:AvocadoViews:x.y.z'
}
```

## 

## 一、DateSelectPicker

点击后弹出Dialog选择日期的控件，在4.4及以上系统中有统一的样式，并提供上下限的设置接口。

## 二、DoubleRefreshLayout

具有下拉刷新和上拉加载功能的容器，提供异常处理。

```java
RendererAdapter<String> rendererAdapter = new RendererAdapter<>(new ArrayList<>(), this, TestItemRenderer.class);
SubPageAdapter<String> adapter = new SubPageAdapter<>(rendererAdapter,
        pageNum -> {
            if(pageNum >= 4)
                return Observable.<List<String>>error(new NoMoreDataException())
                        .subscribeOn(Schedulers.io());
            else
                return Observable.just(provideTestData(pageNum))
                        .subscribeOn(Schedulers.io());
        });
doubleRefreshLayout.initData(adapter);
doubleRefreshLayout.refreshData();
```


###License
<pre>
Copyright 2015 Yumenokanata

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>
