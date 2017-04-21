[![](https://jitpack.io/v/Yumenokanata/AvocadoViews.svg)](https://jitpack.io/#Yumenokanata/AvocadoViews)

# AvocadoViews
一个小自定义控件的小集合，整合了自己以往做的项目中比较具有通用性的小自定义控件

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
    // 上、下滑动载入的整合控件
    compile 'com.github.Yumenokanata.AvocadoViews:loadinglayout:2.0.1'

    // DSL式的RecyclerViewAdapter构建器
    compile 'com.github.Yumenokanata.AvocadoViews:dsladapter:2.0.1'
    // DSL式的RecyclerViewAdapter构建器的DataBinding支持
    compile 'com.github.Yumenokanata.AvocadoViews:adapterdatabinding:2.0.1'

    // 轻量级的状态切换容器
    compile 'com.github.Yumenokanata.AvocadoViews:statuslayout:2.0.1'
}
```

## 2.x 全新来袭
2.x版本进行了完全的重构和整理，强势更新

### 1. LoadingLayout
上、下滑动载入的整合控件，采用Kotlin编写、RxJava2、改进的Redux架构为核心，架构清晰、简单、稳定
```java
LoadingLayout loadingLayout = (LoadingLayout) findViewById(R.id.loading_layout);
LayoutInitializer initializer = LayoutInitializer
        .<RecyclerView.ViewHolder, ItemModel>builder()
        .provider(this::dataSupplier)
        .adapter(adapter)
        .layoutManager(new LinearLayoutManager(this))
        .doForLoadMoreView(loadMoreStatus -> {
            render(state.withStatus(loadMoreStatus));
            if(loadMoreStatus == LoadMoreStatus.NORMAL)
                loadingLayout.loadData();
        })
        .showData(data -> render(state.withData(data)))
        .build();
loadingLayout.init(initializer);

loadingLayout.refresh();
```

### 2. DslAdapter (__NEW__)
DSL式的RecyclerViewAdapter构建器，并支持DataBinding。核心小巧，适合二次开发。
```java
RendererAdapter adapter = RendererAdapter.repositoryAdapter()
        .addLayout(layout(R.layout.list_header))
        .add(() -> provideData(),
                rendererOf(ItemModel.class)
                        .layout(R.layout.simple_item)
                        .stableIdForItem(ItemModel::getId)
                        .bindWith((m, v) -> ((TextView)v.findViewById(R.id.simple_text_view)).setText(m.getTitle()))
                        .recycleWith(v -> ((TextView)v.findViewById(R.id.simple_text_view)).setText(""))
                        .forList())
        .add(() -> provideData(),
                dataBindingRepositoryPresenterOf(ItemModel.class)
                        .layout(R.layout.item_layout)
                        .itemId(BR.model)
                        .stableIdForItem(ItemModel::getId)
                        .forList())
        .addItem(DateFormat.getInstance().format(new Date()),
                dataBindingRepositoryPresenterOf(String.class)
                        .layout(R.layout.list_footer)
                        .itemId(BR.text)
                        .forItem())
        .build();
```

### 3. StatusLayout
轻量级的状态切换容器

```xml
<indi.yume.view.avocadoviews.statuslayout.StatusLayout
    android:id="@+id/result_loading_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:sl_loadingView="@layout/loading_layout"
    app:sl_emptyView="@layout/no_search_result_layout"
    app:sl_defaultShowMode="loading"
    app:sl_toolShowMode="empty"/>
```

```java
statusLayout.showContentView();

statusLayout.showLoadingView();

...
```

### License
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
